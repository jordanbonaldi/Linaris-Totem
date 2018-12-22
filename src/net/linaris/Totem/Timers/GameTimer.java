package net.linaris.Totem.Timers;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Listeners.Motd;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.ScoreBoard;
import net.linaris.Totem.Managers.TeamsManager.TeamColor;
import net.linaris.Totem.Totem.GameState;
import net.linaris.Totem.Utils.Data;
import net.linaris.Totem.Utils.Utils;

public class GameTimer {
	private static GameTimer instance;
	public static GameTimer getInstance() {
		if(instance == null) instance = new GameTimer();
		return instance;
	}
	
	private List<GameTask> m_tasks = new LinkedList<GameTask>();
	
	private int taskId = -1;
	private long m_time;
	
	private GameTimer() {
		m_time = 9999L;
	}
	
	public void start() {
		if(taskId != -1) return;
		
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Totem.getInstance(), new Runnable() {
			@Override
			public void run() {
				// TASKS
				for(int i = 0; i < m_tasks.size(); i++) {
					if(m_tasks.get(i).m_time-- <= 0) {
						m_tasks.get(i).m_task.run();
						m_tasks.remove(i);
						i--;
					}
				}
				// -----
				
				// int minutes = (int) (m_time / 60);
				int secondes = (int) (m_time % 60);
				
                for(Player p : Bukkit.getOnlinePlayers()){
                        ScoreBoard.getInstance().updateScoreboard(GameTimer.this.m_time);

                }
				
				if(Totem.getGameState() == GameState.LOBBY) {
					// CORRECTION BUG NON-TP
					Location lobby = Data.getInstance().getLobby();
					for(Player player : Utils.m_playersJustConnected) {
						if(!player.getLocation().getWorld().getUID().equals(lobby.getWorld().getUID()) ||
								player.getLocation().distance(lobby) > 15) {
							player.teleport(lobby);
						}
					}
					Utils.m_playersJustConnected.clear();
					// ---------------------
					
					
					if(m_time > Totem.getInt("lobby-time")) Motd.set("§aDisponible");
					else {
						String motd;
						if(Bukkit.getOnlinePlayers().length >= Totem.getPlayerMax()) motd = "§cFull";
						else motd = "§b<sec>s";
						
						Motd.set(motd.replaceAll("<sec>", Long.toString(m_time))
								.replaceAll("<SECOND>", secondes > 1 ? "secondes" : "seconde"));
					}
					
					if(m_time <= 0) {
						int teams_alive = PlayersManager.getInstance().getPlayersByTeam(null).size();
						for(TeamColor color : TeamColor.values()) {
							if(PlayersManager.getInstance().getPlayersByTeam(color).size() > 0) teams_alive++;
						}
						if(teams_alive < 2) { // 2 teams minimum
							m_time = 9999L;
							return;
						}
						
						Totem.getInstance().setGameState(GameState.GAME);
						m_time = Totem.getInt("game-time");
						return;
					}
				}
				else if(Totem.getGameState() == GameState.GAME) {
					if(m_time < 0) {
						m_time = 7L;
						Totem.getInstance().setGameState(GameState.END);
					}
				}
				else if(Totem.getGameState() == GameState.END) {
					if(m_time <= 0) {
						Totem.getInstance().setGameState(GameState.RESTART);
						m_time = 5L;
						return;
					}
				}
				else if(Totem.getGameState() == GameState.RESTART) {
					if(m_time <= 0) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
					}
				}
								
				m_time--;
				
				if(Totem.getBoolean("always-day-lobby")) {
					Data.getInstance().getLobby().getWorld().setTime(5000L);
				}
				if(Totem.getBoolean("always-day-game")) {
					Totem.getWorldGame().setTime(5000L);
				}
			}
		}, 20, 20); // toutes les secondes
	}
	
	public long getTime() { return m_time; }
	public void setTime(long time) { m_time = time; }
	public void addTask(Runnable task, long time) { m_tasks.add(new GameTask(task, time)); }
	
	
	public class GameTask {
		private Runnable m_task;
		private long m_time;
		
		public GameTask(Runnable task, long time) {
			m_task = task;
			m_time = time;
		}
	}
}

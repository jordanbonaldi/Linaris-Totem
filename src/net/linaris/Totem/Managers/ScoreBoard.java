package net.linaris.Totem.Managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.TeamsManager.TeamColor;
import net.linaris.Totem.Managers.TeamsManager.TucTeam;
import net.linaris.Totem.Totem.GameState;

public class ScoreBoard {
	private Objective m_objective;
	
	private GameState m_boardType;
	
	private Map<String, String> m_uniqueText = new HashMap<String, String>();
	private int m_wait = 0;
	
	private static ScoreBoard instance;
	public static ScoreBoard getInstance() {
		if(instance == null) instance = new ScoreBoard();
		return instance;
	}
	
	private ScoreBoard() {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		m_objective = board.registerNewObjective("totem", "dummy");
		m_objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		m_boardType = GameState.CONFIG;
	}
	
	public Scoreboard getScoreboard() { return m_objective.getScoreboard(); }
	
	public void updateScoreboard(long time) {
		GameState gameState = Totem.getGameState();
		
		if(m_boardType != gameState) {
			m_boardType = gameState;
			
			for(ObjectiveInfo obj : getUsedObjectives()) {
				for(OfflinePlayer entry : Bukkit.getOfflinePlayers()) {
					obj.getScoreboard().resetScores(entry);
				}
				
				// On initialise juste les valeurs
				if(gameState == GameState.LOBBY) {
					obj.getObjective().setDisplayName("§6§l\u271a §b§lTotem §6§l\u271a");
				}
			}
		}
		
		if(gameState == GameState.LOBBY) {
			int nbPlayers = Bukkit.getOnlinePlayers().length;
			setText("lobby_player", ("Joueurs §a<x>/<max>")
					.replaceAll("<x>", Integer.toString(nbPlayers))
					.replaceAll("<max>", Integer.toString(Totem.getPlayerMax())), 2);
			
			if(nbPlayers >= Totem.getInt("player-min")) {
				setText("lobby_time", "Début dans §a<sec>s"
						.replaceAll("<sec>", Long.toString(time))
						.replaceAll("<SECOND>", time > 1 ? "secondes" : "seconde"), 1);
			}
			else {
				StringBuilder sb = new StringBuilder();
                sb.append("§aAttente");
				for(int i = 0; i < m_wait; i++) sb.append(".");
				m_wait = m_wait >= 3 ? 0 : m_wait + 1;
				setText("lobby_time", sb.toString(), 1);
			}
		}
		else if(gameState == GameState.GAME) {
			int min = (int) (time / 60);
			int sec = (int) (time % 60);
			for(ObjectiveInfo obj : getUsedObjectives()) {
				obj.getObjective().setDisplayName(("§6§l\u271a §e<min>:<sec> §b§lTotem §6§l\u271a")
						.replaceAll("<team_color>", obj.getTeamColor().getChatColor().toString())
						.replaceAll("<team>", obj.getTeamColor().getTeamName())
						.replaceAll("<min>", getVarWithZero(min))
						.replaceAll("<sec>", getVarWithZero(sec))
						.replaceAll("<MINUTE>", min > 1 ? "minutes" : "minute")
						.replaceAll("<SECONDE>", sec > 1 ? "secondes" : "seconde"));
			}
			
			for(TucTeam team : TeamsManager.getInstance().getTeams()) {
				setText(team.getColor().name(), ("<team_color>§l<team> §f:")
						.replaceAll("<team_color>", team.getColor().getChatColor().toString())
						.replaceAll("<team>", team.getColor().getTeamName()),
						team.getPlayers().size());
			}
		}
	}
	
	private void setText(String id, String text, int pos) {
		if(m_uniqueText.containsKey(id)) {
			String last_text = m_uniqueText.get(id);
			
			if(last_text.equalsIgnoreCase(text)) {
				for(ObjectiveInfo obj : getUsedObjectives()) {
					int last_score = obj.getObjective().getScore(Bukkit.getOfflinePlayer(last_text)).getScore();
					if(pos != last_score) obj.getObjective().getScore(Bukkit.getOfflinePlayer(last_text)).setScore(pos);
				}
				return;
			}
			for(ObjectiveInfo obj : getUsedObjectives()) {
				obj.getScoreboard().resetScores(Bukkit.getOfflinePlayer(last_text));
			}
		}
		
		if(text.length() > 16) text = text.substring(0, 16);
		m_uniqueText.put(id, text);
		for(ObjectiveInfo obj : getUsedObjectives()) {
			obj.getObjective().getScore(Bukkit.getOfflinePlayer(text)).setScore(pos);
		}
	}
	
	public void removeTeam(TucTeam team) {
		if(m_uniqueText.containsKey(team.getColor().name())) {
			for(ObjectiveInfo obj : getUsedObjectives()) {
				obj.getScoreboard().resetScores(Bukkit.getOfflinePlayer(m_uniqueText.get(team.getColor().name())));
			}
		}
	}
	
	public static String getVarWithZero(int var) {
		return var > 9 ? Integer.toString(var) : new StringBuilder().append("0").append(var).toString();
	}
	
	private List<ObjectiveInfo> getUsedObjectives() {
		List<ObjectiveInfo> objs = new LinkedList<ObjectiveInfo>();
		if(Totem.getGameState() == GameState.LOBBY) objs.add(new ObjectiveInfo(m_objective, null));
		else {
			for(TucTeam team : TeamsManager.getInstance().getTeams()) {
				objs.add(new ObjectiveInfo(team.getObjective(), team.getColor()));
			}
		}
		
		return objs;
	}
	
	private class ObjectiveInfo {
		private Objective m_obj;
		private TeamColor m_teamColor;
		
		private ObjectiveInfo(Objective obj, TeamColor teamColor) {
			m_obj = obj;
			m_teamColor = teamColor;			
		}
		
		public Scoreboard getScoreboard() { return m_obj.getScoreboard(); }
		public Objective getObjective() { return m_obj; }
		public TeamColor getTeamColor() { return m_teamColor; }
	}
}

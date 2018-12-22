package net.linaris.Totem.Managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager.TucPlayer;
import net.linaris.Totem.Timers.GameTimer;
import net.linaris.Totem.Totem.GameState;
import net.linaris.Totem.Utils.Data;
import net.linaris.Totem.Utils.Lang;
import net.linaris.Totem.Utils.Utils;
import net.linaris.Totem.Utils.Data.TeamData;

public class TeamsManager {
	private List<TucTeam> m_teams = new LinkedList<TucTeam>();
	private boolean m_isWinnerSay = false;
	
	private static TeamsManager instance = null;
	public static TeamsManager getInstance() {
		if(instance == null) instance = new TeamsManager();
		return instance;
	}
    private Map<String, String> m_lang = new LinkedHashMap<String, String>();
	private TeamsManager() {
		for(TeamColor color : TeamColor.values()) {
			m_teams.add(new TucTeam(color));
		}
		
		for(TeamColor color : TeamColor.values()) {
			createBukkitTeam(color); // On fais cela après car toutes les teams doivent être créé
		}



	}
	
	public List<TucTeam> getTeams() { return m_teams; }
	
	public TucTeam getTeam(TeamColor color) {
		for(TucTeam tteam : m_teams) {
			if(tteam.getColor() == color) return tteam;
		}
		return null;
	}
	
	public List<TucTeam> getTeamNotFull() {
		List<TucTeam> teams = new ArrayList<TucTeam>();
		
		for(TucTeam team : m_teams) {
			if(!team.isFull()) teams.add(team);
		}
		
		Utils.sortList(teams, new Comparator<TucTeam>() {
			@Override
			public int compare(TucTeam team1, TucTeam team2) {
				return Integer.compare(team1.getPlayers().size(), team2.getPlayers().size());
			}
		});
		
		return teams;
	}
	
	public boolean allTeamFull() {
		for(TucTeam team : m_teams) {
			if(!team.isFull()) return false;
		}
		return true;
	}
	
	public void refreshTeam(TucTeam team, Location breaked) { // retourne true si il y a un gagnant		
		int team_size = 0;
		TucTeam winner = null;
		for(TucTeam ateam : m_teams) {
			if(ateam.isTeamAlive(breaked)) {
				team_size++;
				winner = ateam;
			}
		}
		
		if(team_size == 1 && !m_isWinnerSay) { // ON A UN GAGNANT
			List<Player> players = new LinkedList<Player>();
			for(TucPlayer tplayer : winner.getPlayers()) {
				players.add(tplayer.getPlayer());
			}
			Totem.getGameAPI().win(players);
			for(Player p : Bukkit.getOnlinePlayers()){
            		p.sendMessage(("§b§lL'équipe <team_color>§l<team> §b§la gagné la partie!")
        					.replaceAll("<team>", winner.getColor().getTeamName())
        					.replaceAll("<team_color>", winner.getColor().getChatColor().toString()));

			}

			
			if(breaked != null) breaked.getBlock().setType(Material.AIR);
		}
		
		if(team_size <= 1 && !m_isWinnerSay) {
			Totem.getInstance().setGameState(GameState.END);
			GameTimer.getInstance().setTime(7L);
			m_isWinnerSay = true;
		}
	}
	
	private void createBukkitTeam(TeamColor color) {
		for(TucTeam team : m_teams) {
			//System.out.println(team.getColor() + " scoreboard, team " + color + " created");
			Team bukkitTeam = team.getScoreboard().registerNewTeam(color.name());
			bukkitTeam.setDisplayName(color.getColoredTeamName());
			bukkitTeam.setCanSeeFriendlyInvisibles(true);
			bukkitTeam.setAllowFriendlyFire(false);
			bukkitTeam.setPrefix(color.getChatColor().toString());
		}
	}
	
	private void addPlayerInBukkitTeam(TeamColor color, Player player) {
		for(TucTeam team : m_teams) {
			
			
			Team bukkitTeam = team.getScoreboard().getTeam(color.name());
			bukkitTeam.addPlayer(player);
		}
	}
	
	private void removePlayerInBukkitTeam(TeamColor color, Player player) {
		for(TucTeam team : m_teams) {
			Team bukkitTeam = team.getScoreboard().getTeam(color.name());
			bukkitTeam.removePlayer(player);
		}
	}
	
	public boolean isTotem(Location loc) {
		for(TucTeam team : m_teams) {
			if(team.isTotem(loc)) return true;
		}
		return false;
	}
	
	public TucTeam getTeamByTotem(Location loc) {
		for(TucTeam team : m_teams) {
			if(team.isTotem(loc)) return team;
		}
		return null;
	}
	
	public class TucTeam {
		private TeamColor m_color;
		private Location m_totemLocation;
		private Location m_spawn;
		private List<TucPlayer> m_players = new LinkedList<TucPlayer>();
		private Objective m_objective;
		
		private TucTeam(TeamColor color) {
			m_color = color;
			
			TeamData data = Data.getInstance().getTeamData(m_color);
			m_spawn = data.getSpawn();
			m_totemLocation = data.getTotemLocation();
			
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			m_objective = board.registerNewObjective("totem", "dummy");
			m_objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			m_totemLocation.clone().add(0, 0, 0).getBlock().setType(Material.OBSIDIAN);
			m_totemLocation.clone().add(0, 1, 0).getBlock().setType(Material.OBSIDIAN);
			m_totemLocation.clone().add(0, 2, 0).getBlock().setType(Material.OBSIDIAN);
			m_totemLocation.clone().add(0, 3, 0).getBlock().setType(Material.BEACON);
		}
		
		public void prepareToDelete() {
			
			ScoreBoard.getInstance().removeTeam(this);
		}
		
		public boolean isTeamAlive(Location breaked) {
			if(m_players.size() <= 0) return false;
					
			for(int y = 0; y < 4; y++) {
				Location loc = m_totemLocation.clone().add(0, y, 0);
				if(loc.getBlock().getType() != Material.AIR && !Utils.locEquals(loc, breaked)) return true;
			}
			return false;
		}
		public Location getTotemLocation() { return m_totemLocation.clone(); }
		
		public boolean isTotem(Location loc) {
			for(int y = 0; y < 4; y++) {
				Location l = m_totemLocation.clone().add(0, y, 0);
				if(Utils.locEquals(l, loc)) {
					return true;
				}
			}
			return false;
		}
		
		public void addPlayer(TucPlayer tplayer) {
			addPlayerInBukkitTeam(m_color, tplayer.getPlayer());
			m_players.add(tplayer);
		}
		
		public void removePlayer(TucPlayer tplayer) {
			removePlayerInBukkitTeam(m_color, tplayer.getPlayer());
			
			for(int i = 0; i < m_players.size(); i++) {
				if(m_players.get(i).getUUID().equals(tplayer.getUUID())) {
					m_players.remove(i);
					return;
				}
			}
		}
		
		public void sendMessage(String message) {
			for(TucPlayer player : getPlayers()) {
				player.getPlayer().sendMessage(message);
			}
		}
		
		public TeamColor getColor() { return m_color; }
		public Location getSpawn() { return m_spawn; }
		
		public Scoreboard getScoreboard() { return m_objective.getScoreboard(); }
		public Objective getObjective() { return m_objective; }
		
		public List<TucPlayer> getPlayers() { return m_players; }
		
		public boolean isFull() {
			return getPlayers().size() >= Totem.getInt("players-by-team");
		}
	}
	
	public enum TeamColor {
		BLUE(DyeColor.BLUE, ChatColor.BLUE),
		RED(DyeColor.RED, ChatColor.RED);
		
		private DyeColor m_dyeColor;
		private ChatColor m_chatColor;
		TeamColor(DyeColor dyeColor, ChatColor chatColor) {
			m_dyeColor = dyeColor;
			m_chatColor = chatColor;
		}
		

		
		public DyeColor getDyeColor() { return m_dyeColor; }
		public ChatColor getChatColor() { return m_chatColor; }
		public String getTeamName() { return Lang.get("TEAM_NAME_" + name()); }
		public String getColoredTeamName() { return m_chatColor + getTeamName(); }
		
		public static TeamColor getByDyeColor(DyeColor dyeColor) {
			for(TeamColor color : values()) {
				if(color.m_dyeColor == dyeColor) return color;
			}
			return null;
		}
		
		
		
		public static TeamColor getByString(String str) {
			for(TeamColor color : values()) {
				if(color.name().equalsIgnoreCase(str)) return color;
			}
			return null;
		}
		
		public static TeamColor[] valuesExpect(TeamColor expect) {
			TeamColor[] colors = new TeamColor[3];
			int i = 0;
			for(TeamColor acolor : TeamColor.values()) {
				if(!acolor.equals(expect)) {
					colors[i] = acolor;
					i++;
				}
			}
			return colors;
		}
	}
}

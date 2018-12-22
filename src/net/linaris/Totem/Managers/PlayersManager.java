package net.linaris.Totem.Managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.linaris.Totem.Managers.TeamsManager.TeamColor;
import net.linaris.Totem.Managers.TeamsManager.TucTeam;


public class PlayersManager {
	private Map<UUID, TucPlayer> m_players = new HashMap<UUID, TucPlayer>();
	
	private static PlayersManager instance;
	public static PlayersManager getInstance() {
		if(instance == null) instance = new PlayersManager();
		return instance;
	}
	
	private PlayersManager() {
	}
		
	public void removePlayer(Player player) {
		TucPlayer tplayer = getPlayer(player);
		TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
		if(team != null) team.removePlayer(tplayer);
		m_players.remove(player.getUniqueId());
	}
	
	public TucPlayer getPlayer(Player player) {
		if(m_players.containsKey(player.getUniqueId())) return m_players.get(player.getUniqueId());
		
		TucPlayer tplayer = new TucPlayer(player);
		m_players.put(player.getUniqueId(), tplayer);
		
        for(Player p : Bukkit.getOnlinePlayers()){
                p.setScoreboard(ScoreBoard.getInstance().getScoreboard());

        }
		return tplayer;
	}
	
	public Collection<TucPlayer> getPlayers() { return m_players.values(); }
	
	public List<TucPlayer> getPlayersByTeam(TeamColor color) {
		List<TucPlayer> players = new LinkedList<TucPlayer>();
		
		for(TucPlayer player : m_players.values()) {
			if(player.getTeamColor() == color) players.add(player);
		}
		
		return players;
	}
	
	
	public class TucPlayer {
		private Player m_player;
		private TeamColor m_teamColor;
		
		private TucPlayer(Player player) {
			m_player = player;
			m_teamColor = null;
		}
		
		public Player getPlayer() { return m_player; }		
		
		public void setTeamColor(TeamColor color) {
			m_teamColor = color;
		}
		public TeamColor getTeamColor() { return m_teamColor; }
		
		public UUID getUUID() { return m_player.getUniqueId(); }
	}
}

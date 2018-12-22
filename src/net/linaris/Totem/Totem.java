package net.linaris.Totem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.skelerex.LinarisKits.api.GameAPI;
import com.skelerex.LinarisKits.api.GameType;

import net.linaris.Totem.Commands.CommandAllChat;
import net.linaris.Totem.Commands.CommandMessage;
import net.linaris.Totem.Commands.CommandTotem;
import net.linaris.Totem.Listeners.BasicPlayerListener;
import net.linaris.Totem.Listeners.ChatListener;
import net.linaris.Totem.Listeners.GameListener;
import net.linaris.Totem.Listeners.LobbyListener;
import net.linaris.Totem.Listeners.Motd;
import net.linaris.Totem.Listeners.WorldListener;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.ScoreBoard;
import net.linaris.Totem.Managers.TeamsManager;
import net.linaris.Totem.Managers.PlayersManager.TucPlayer;
import net.linaris.Totem.Managers.TeamsManager.TeamColor;
import net.linaris.Totem.Managers.TeamsManager.TucTeam;
import net.linaris.Totem.Timers.GameTimer;
import net.linaris.Totem.Utils.Data;
import net.linaris.Totem.Utils.FileUtils;
import net.linaris.Totem.Utils.Utils;

public class Totem extends JavaPlugin {
	private World m_worldGame;
	private GameState m_gameState;
	private GameAPI m_gameAPI = new GameAPI(GameType.TOTEM);
	public static GameAPI getGameAPI() { return instance.m_gameAPI; }
	
	private static Totem instance;
	public static Totem getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();
		reloadConfig();
		
		if(getBoolean("enable-bungeecord")) {
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}
		
		// --- GESTION DU MONDE DE JEU ---
		File srcDir = new File("world_totem");
		File destDir = new File("world_in_progress");
		
		FileUtils.deleteDirectory(destDir);
		FileUtils.copyDirectory(srcDir, destDir);
		
		m_worldGame = Bukkit.createWorld(new WorldCreator("world_in_progress"));
		// -------------------------------
		
		Data.getInstance();
		ScoreBoard.getInstance();

		TeamsManager.getInstance();
		
		m_gameState = Totem.getBoolean("config-mode") ? GameState.CONFIG : GameState.LOBBY;
		
		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : world.getEntities()) {
				if((entity instanceof LivingEntity) && entity.getType() != EntityType.PLAYER) entity.remove();
			}
		}
		
		PluginManager pm = getServer().getPluginManager();
		if(!getBoolean("config-mode")) {
			pm.registerEvents(new BasicPlayerListener(), this);
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new GameListener(), this);
			pm.registerEvents(new LobbyListener(), this);
		}
		pm.registerEvents(new WorldListener(), this);
		
		GameTimer.getInstance().start();
		getWorldGame().setStorm(false);
		
		getCommand("totem").setExecutor(new CommandTotem());
		getCommand("all").setExecutor(new CommandAllChat());
		getCommand("message").setExecutor(new CommandMessage());
	}
	
	@Override
	public void onDisable() {
		// SI GAME STATE EN CONFIG : on save "world_in_progress" dans le "world_totem"
		if(Totem.getGameState() == GameState.CONFIG) {
			File srcDir = new File("world_in_progress");
			File destDir = new File("world_totem");
			
			FileUtils.deleteDirectory(destDir);
			FileUtils.copyDirectory(srcDir, destDir);
		}
	}
	
	public static World getWorldGame() { return instance.m_worldGame; }
	public static GameState getGameState() { return instance.m_gameState; }
	
	public void setGameState(GameState gameState) {
		m_gameState = gameState;
		
		if(m_gameState == GameState.GAME) {
			Motd.set("§cIn game");
						
			// On attribut les joueurs à une team
			List<TucPlayer> withoutTeam = new LinkedList<TucPlayer>();
			
			for(TucPlayer tplayer : PlayersManager.getInstance().getPlayers()) {
				if(tplayer.getTeamColor() == null) withoutTeam.add(tplayer);
				else TeamsManager.getInstance().getTeam(tplayer.getTeamColor()).addPlayer(tplayer);
			}
			
			while(!TeamsManager.getInstance().allTeamFull() && !withoutTeam.isEmpty()) {
				TucTeam team = TeamsManager.getInstance().getTeamNotFull().get(0);
				team.addPlayer(withoutTeam.get(0));
				withoutTeam.get(0).setTeamColor(team.getColor());
				withoutTeam.remove(0);
			}
			
			// Téléportation au spawn
			for(TucTeam team : TeamsManager.getInstance().getTeams()) {
				for(TucPlayer tplayer : team.getPlayers()) {					
					Utils.resetPlayer(tplayer.getPlayer());
					tplayer.getPlayer().teleport(team.getSpawn());
					Utils.setInventory(tplayer.getPlayer());
					tplayer.getPlayer().setScoreboard(team.getScoreboard());
				}
			}
			// ---
			
			for(TeamColor color : TeamColor.values()) {
				TeamsManager.getInstance().refreshTeam(TeamsManager.getInstance().getTeam(color), null);
			}
			
            for(Player p : Bukkit.getOnlinePlayers()){
            		p.sendMessage("§6§lLa partie vient de commencer !");

		}
		}
		else if(m_gameState == GameState.END) {
			// Rq : fin du jeu par victoire d'une team ou fin du temps (pas forcement de vainqueur)
		}
		else if(m_gameState == GameState.RESTART) {
			// KICK DES JOUEURS
			if(TeamsManager.getInstance().getTeams().size() == 1) {
				TucTeam winner = TeamsManager.getInstance().getTeams().get(0);
				for(Player player : Bukkit.getOnlinePlayers()) {
    					Utils.kick(player, "§6§kMM §b§lVous avez gagné la partie ! §6§kMM");

				}
			}
			else {
				for(Player player : Bukkit.getOnlinePlayers()) {
    					Utils.kick(player, "§7La partie est terminé ! §eRetour au hub :D");

				}
			}
			// ----------------
		}
	}
	
	// RACOURCI
    public static int getInt(String key) { return instance.getConfig().getInt(key); }
    public static boolean getBoolean(String key) { return instance.getConfig().getBoolean(key); }
    public static String getString(String key) { return instance.getConfig().getString(key); }
    public static int getPlayerMax() { return getInt("players-by-team") * TeamColor.values().length; }
    
    public enum GameState {
    	CONFIG,
    	LOBBY,
    	GAME,
    	END,
    	RESTART;
    }
}

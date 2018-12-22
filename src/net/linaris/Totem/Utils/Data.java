package net.linaris.Totem.Utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.TeamsManager.TeamColor;

public class Data {
	private File m_file;
	private FileConfiguration m_yaml;
	
	private static Data instance = null;
	public static Data getInstance() {
		if(instance == null) instance = new Data();
		return instance;
	}
	
	private Data() {
		try {
			File dirs = new File("plugins/" + Totem.getInstance().getName() + "/data");
			dirs.mkdirs();
			
			// Valeurs par defaut			
			m_file = new File("plugins/" + Totem.getInstance().getName() + "/data/data.yml");
			if(!m_file.exists()) m_file.createNewFile();
			m_yaml = YamlConfiguration.loadConfiguration(m_file);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public TeamData getTeamData(TeamColor color) {
		ConfigurationSection team = m_yaml.getConfigurationSection("teams." + color.name());
		
		if(team == null || !team.contains("spawn") || !team.contains("totem")) {
			Location zero = Totem.getWorldGame().getSpawnLocation();
			return new TeamData(color, zero.clone(), zero.clone());
		}
		
		Location spawn = Utils.toLocation(team.getString("spawn"), false);
		Location totem = Utils.toLocation(team.getString("totem"), true);
		
		return new TeamData(color, spawn, totem);
	}
	
	public void createTeamData(TeamColor color, Location spawn, Location totem) {
		try {
			ConfigurationSection team = m_yaml.createSection("teams." + color.name());
			team.set("spawn", Utils.toString(spawn, false));
			team.set("totem", Utils.toString(totem, true));
			m_yaml.save(m_file);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public Location getLobby() {
		if(m_yaml.contains("lobby")) return Utils.toLocation(m_yaml.getString("lobby"), false);
		else return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	public void setLobby(Location lobby) {
		try {
			m_yaml.set("lobby", Utils.toString(lobby, false));
			m_yaml.save(m_file);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public class TeamData {
		private TeamColor m_color;
		private Location m_spawn;
		private Location m_totem;
		
		private TeamData(TeamColor color, Location spawn, Location totem) {
			m_color = color;
			m_spawn = spawn;
			m_totem = totem;
		}
		
		public TeamColor getColor() { return m_color; }
		public Location getSpawn() { return m_spawn; }
		public Location getTotemLocation() { return m_totem; }
	}
}

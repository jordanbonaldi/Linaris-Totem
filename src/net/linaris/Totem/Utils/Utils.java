package net.linaris.Totem.Utils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skelerex.LinarisKits.api.LinarisKitsAPI;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.PlayersManager.TucPlayer;
import net.linaris.Totem.Managers.TeamsManager.TeamColor;
import net.linaris.Totem.Totem.GameState;

public abstract class Utils {
	public static List<Player> m_playersJustConnected = new LinkedList<Player>();
	
	public static Location toLocation(final String string, final boolean block) {
        final String[] splitted = string.split(";");
        final World world = Bukkit.getWorld(splitted[0]);
        
        if (world == null || splitted.length < 4) return null;
        
        Location location = new Location(world, Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
        if(!block) {
        	if(splitted.length >= 6) {
        		location.setYaw(Float.parseFloat(splitted[4]));
        		location.setPitch(Float.parseFloat(splitted[5]));
        	}
        }
        return location;
    }

    public static String toString(final Location l, final boolean block) {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(l.getWorld().getName()).append(";");
    	sb.append(l.getBlockX()).append(";").append(l.getBlockY()).append(";").append(l.getBlockZ());
    	
    	if(!block) {
	    	sb.append(";").append(l.getYaw()).append(";").append(l.getPitch());
    	}
    	
        return sb.toString();
    }
    
    @SuppressWarnings("deprecation")
	public static void setInventory(Player player) {
		TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
		PlayerInventory inv = player.getInventory();
		
		if(Totem.getGameState() == GameState.LOBBY) {
			inv.clear();
			
			for(int i = 0; i < TeamColor.values().length; i++) {
				TeamColor color = TeamColor.values()[i];
				
				ItemStack wool = new ItemStack(Material.WOOL);
				wool.setDurability((short) color.getDyeColor().getWoolData());
				
				ItemMeta meta = wool.getItemMeta();
				meta.setDisplayName(color.getColoredTeamName());
				wool.setItemMeta(meta);
				
				inv.setItem(i, wool);
			}
			
			ItemStack is_kit = new ItemStack(Material.NETHER_STAR);
			
			ItemMeta is_kit_meta = is_kit.getItemMeta();
			is_kit_meta.setDisplayName(LinarisKitsAPI.ITEM_CHOOSE_KIT);
			is_kit.setItemMeta(is_kit_meta);
			
			inv.setItem(8, is_kit);
			
			player.updateInventory();
		}
		else if(Totem.getGameState() == GameState.GAME) {
			Totem.getGameAPI().applyKit(player);
		}
	}
    
    @SuppressWarnings("deprecation")
	public static void resetPlayer(Player player) {
		player.setGameMode(GameMode.SURVIVAL);	
		player.setMaxHealth(20D);
		player.setHealth(20D);
		player.setLevel(0);
		player.setExp(0F);
		player.setFoodLevel(20);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		
		List<PotionEffect> effects = new LinkedList<PotionEffect>(player.getActivePotionEffects());
		for(PotionEffect effect : effects) { player.removePotionEffect(effect.getType()); }
		
		player.updateInventory();
	}
    
    public static <T> void sortList(List<T> list, Comparator<T> c) {
		for(int i = 0; i < list.size() - 1; i++) {
			T obj1 = list.get(i);
			T obj2 = list.get(i+1);
			if(c.compare(obj1, obj2) > 0) {
				list.set(i, obj2);
				list.set(i+1, obj1);
				i -= 2;
				if(i <= -2) i++;
			}
		}
	}
    
    public static MetadataValue getMetadataValue(Metadatable meta, String key) {
		if(!meta.hasMetadata(key)) return null;
		
		for(MetadataValue value : meta.getMetadata(key)) {
			if(value.getOwningPlugin().getName().equalsIgnoreCase(Totem.getInstance().getName())) {
				return value;
			}
		}
		return null;
	}
    
    public static String toString(ItemStackAndSlot is_slot) {
    	// FORMAT : <material>:<amount>:<durability>:<display_name>:<echantements>:<slot>
    	// ENCHANTEMENTS FORMAT : <type>,<level>;<type>,<level>;<type>,<level> ...
    	ItemStack is = is_slot.getItemStack();
    	StringBuilder sb = new StringBuilder();
    	sb.append(is.getType().name()).append(":").append(is.getAmount()).append(":");
    	sb.append(is.getDurability()).append(":");
    	
    	ItemMeta im = is.getItemMeta();
    	if(im.hasDisplayName()) sb.append(im.getDisplayName());
    	sb.append(":");
    	
    	int i = 0;
    	Map<Enchantment, Integer> enchants = is.getEnchantments();
    	for(Enchantment ench : enchants.keySet()) {
    		if(i > 0) sb.append(";");
    		int level = enchants.get(ench);
    		sb.append(ench.getName()).append(",").append(level);
    		i++;
    	}
    	sb.append(":").append(is_slot.getSlot());
    	
    	return sb.toString();
    }
    
    public static ItemStackAndSlot toItemStack(String data) {
    	try {
	    	// FORMAT : <material>:<amount>:<durability>:<display_name>:<echantements>:<slot>
	    	// ENCHANTEMENTS FORMAT : <type>,<level>;<type>,<level>;<type>,<level> ...
	    	String[] infos = data.split(":");
	    	if(infos.length < 4) return null;
	    	
	    	Material material = Material.getMaterial(infos[0]);
	    	int amount = Integer.parseInt(infos[1]);
	    	String display_name = ChatColor.translateAlternateColorCodes('&', infos[3]);
	    	String echants_data = infos[4];
	    	int slot = Integer.parseInt(infos[5]);
	    	
	    	if(material == null) return null;
	    	
	    	ItemStack is = new ItemStack(material, amount);
	    	if(!infos[2].isEmpty()) {
	    		is.setDurability(Short.parseShort(infos[2]));
	    	}
	    	
	    	if(!display_name.isEmpty()) {
	    		ItemMeta im = is.getItemMeta();
	    		im.setDisplayName(display_name);
	    		is.setItemMeta(im);
	    	}
	    	
	    	if(!echants_data.isEmpty()) {
	    		String[] enchants_infos = echants_data.split(";");
	    		for(String enchant_infos : enchants_infos) {
	    			String[] enchant_infos_split = enchant_infos.split(",");
	    			if(enchant_infos_split.length == 2) {
	    				Enchantment ench = Enchantment.getByName(enchant_infos_split[0]);
	    				int level = Integer.parseInt(enchant_infos_split[1]);
	    				
	    				if(ench != null) is.addEnchantment(ench, level);
	    			}
	    		}
	    	}
	    	
	    	return new ItemStackAndSlot(is, slot);
    	} catch(Exception e) { return null; }
    }
    
    public static class ItemStackAndSlot {
    	private ItemStack m_is;
    	private int m_slot;
    	
    	public ItemStackAndSlot(ItemStack is, int slot) {
    		m_is = is;
    		m_slot = slot;
    	}
    	
    	public ItemStack getItemStack() { return m_is; }
    	public int getSlot() { return m_slot; }
    }
    
    public static void tpToLobby(Player player) {
		if(Totem.getBoolean("enable-bungeecord")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(Totem.getString("bungeecord-lobby"));
			player.sendPluginMessage(Totem.getInstance(), "BungeeCord", out.toByteArray());
		}
	}
    
    public static void kick(final Player player, final String message) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(Totem.getInstance(), new Runnable() {
		@Override
		public void run() {
				
	    	if(Totem.getBoolean("enable-bungeecord")) {
	    		player.sendMessage(message);
	    		tpToLobby(player);
	    	}
	    	else {
	    		player.kickPlayer(message);
	    	}
			
		}
	}, 50L);
    }
    
    public static boolean locEquals(Location l1, Location l2) {
    	if(l1 == null && l2 == null) return true;
    	else if(l1 == null || l2 == null) return false;
    	
    	return l1.getWorld().getUID().equals(l2.getWorld().getUID()) && 
    			l1.getBlockX() == l2.getBlockX() &&
    			l1.getBlockY() == l2.getBlockY() &&
    			l1.getBlockZ() == l2.getBlockZ();
    }
}

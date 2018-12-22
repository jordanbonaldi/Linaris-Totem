
package net.linaris.Totem.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Timers.GameTimer;
import net.linaris.Totem.Utils.Data;
import net.linaris.Totem.Utils.Utils;

public class BasicPlayerListener
implements Listener {
    private int max_build_height = Totem.getInt("max-build-height");

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getLocation().getBlockY() > this.max_build_height) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getLocation().getBlockY() > this.max_build_height) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (Totem.getGameState() != Totem.GameState.CONFIG && Totem.getGameState() != Totem.GameState.LOBBY) {
            for(Player p : Bukkit.getOnlinePlayers()){
            		p.sendMessage("§cLa partie est en cours !");

            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "");
        } else if (Bukkit.getOnlinePlayers().length >= Totem.getPlayerMax()) {
            for(Player p : Bukkit.getOnlinePlayers()){
            		p.sendMessage("§cLa partie est pleine !");

            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "");
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
	        for(Player p : Bukkit.getOnlinePlayers()){
	    		if(player.getName().equals("Neferett")){
		    		p.sendMessage("§f§o[§c§oFondateur§f§o] §b§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.megavip")) {
		    		p.sendMessage("§f§o[§a§oMegaVip§f§o] §a§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.vip")){
		    		p.sendMessage("§f§o[§e§oVip§f§o] §e§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.modo")){
		    		p.sendMessage("§f§o[§6§oModo§f§o] §6§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.admin")){
		    		p.sendMessage("§f§o[§c§oAdmin§f§o] §c§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.vipelite")) { 
		    		p.sendMessage("§f§o[§b§oVIPElite§f§o] §b§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
	    		}else if(player.hasPermission("game.yt")) { 
    	    		p.sendMessage("§f§o[§c§oYouTuber§f§o] §b§o" + event.getPlayer().getName() + "§7§o a quitté le jeu !");
        		}else{	
		    		p.sendMessage("§6" + event.getPlayer().getName() + "§7§o a a quitté le jeu !");
	    		}

	        }
            event.setQuitMessage(null);
        } else {
            event.setQuitMessage(null);
        }
        PlayersManager.getInstance().removePlayer(event.getPlayer());
        if (Bukkit.getOnlinePlayers().length < Totem.getInt("player-min") && Totem.getGameState() == Totem.GameState.LOBBY) {
            GameTimer.getInstance().setTime(9999);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        PlayersManager.getInstance().removePlayer(event.getPlayer());
        if (Bukkit.getOnlinePlayers().length < Totem.getInt("player-min") && Totem.getGameState() == Totem.GameState.LOBBY) {
            GameTimer.getInstance().setTime(9999);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            event.setRespawnLocation(Data.getInstance().getLobby());
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        Utils.setInventory(event.getPlayer());
    }
}


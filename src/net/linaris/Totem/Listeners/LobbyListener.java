
package net.linaris.Totem.Listeners;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.TeamsManager;
import net.linaris.Totem.Timers.GameTimer;
import net.linaris.Totem.Utils.Data;
import net.linaris.Totem.Utils.Utils;

public class LobbyListener
implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayersManager.getInstance().getPlayer(player);
        Utils.resetPlayer(player);
        for(Player p : Bukkit.getOnlinePlayers()){
    		if(player.getName().equals("Neferett")){
	    		p.sendMessage("§f§o[§c§oFondateur§f§o] §b§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.megavip")) {
	    		p.sendMessage("§f§o[§a§oMegaVip§f§o] §a§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.vip")){
	    		p.sendMessage("§f§o[§e§oVip§f§o] §e§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.modo")){
	    		p.sendMessage("§f§o[§6§oModo§f§o] §6§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.admin")){
	    		p.sendMessage("§f§o[§c§oAdmin§f§o] §c§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.vipelite")) { 
	    		p.sendMessage("§f§o[§b§oVIPElite§f§o] §b§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else if(player.hasPermission("game.yt")) { 
	    		p.sendMessage("§f§o[§c§oYouTuber§f§o] §b§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}else{	
	    		p.sendMessage("§6§o" + event.getPlayer().getName() + "§7§o a rejoint le jeu !");
    		}
        }
        event.setJoinMessage(null);        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            PlayersManager.getInstance().getPlayer(player);
            player.teleport(Data.getInstance().getLobby());
            Utils.m_playersJustConnected.add(player);
            if (Bukkit.getOnlinePlayers().length >= Totem.getPlayerMax()) {
                if (GameTimer.getInstance().getTime() > 10) {
                    GameTimer.getInstance().setTime(10);
                }
            } else if (Bukkit.getOnlinePlayers().length >= Totem.getInt("player-min")) {
                if (GameTimer.getInstance().getTime() > (long)Totem.getInt("lobby-time")) {
                    GameTimer.getInstance().setTime(Totem.getInt("lobby-time"));
                }
            } else {
                GameTimer.getInstance().setTime(9999);
            }
            Utils.setInventory(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player;
        if (Totem.getGameState() == Totem.GameState.LOBBY && (player = event.getPlayer()).getLocation().getY() <= Data.getInstance().getLobby().getY() - 8.0) {
            player.teleport(Data.getInstance().getLobby());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        if (inHand == null) {
            return;
        }
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            if (inHand.getType() == Material.WOOL) {
                TeamsManager.TeamColor lastColor = tplayer.getTeamColor();
                TeamsManager.TeamColor color = TeamsManager.TeamColor.getByDyeColor(DyeColor.getByWoolData((byte)((byte)inHand.getDurability())));
                if (color.equals((Object)lastColor)) {
                    return;
                }
                int nbPlayers = PlayersManager.getInstance().getPlayersByTeam(color).size();
                if (Totem.getBoolean("force-player-team")) {
                    LinkedList<TeamsManager.TeamColor> teamAllow = new LinkedList<TeamsManager.TeamColor>();
                    HashMap<TeamsManager.TeamColor, Integer> playersNb = new HashMap<TeamsManager.TeamColor, Integer>();
                    TeamsManager.TeamColor[] arrteamColor = TeamsManager.TeamColor.values();
                    int n = arrteamColor.length;
                    int n2 = 0;
                    while (n2 < n) {
                        TeamsManager.TeamColor acolor = arrteamColor[n2];
                        playersNb.put(acolor, PlayersManager.getInstance().getPlayersByTeam(acolor).size());
                        ++n2;
                    }
                    int nbMin = Integer.MAX_VALUE;
                    for (Integer aint : playersNb.values()) {
                        if (aint >= nbMin) continue;
                        nbMin = aint;
                    }
                    for (TeamsManager.TeamColor acolor : playersNb.keySet()) {
                        int nbPlayer = (Integer)playersNb.get((Object)acolor);
                        if (nbPlayer != nbMin) continue;
                        teamAllow.add(acolor);
                    }
                    if (!teamAllow.contains((Object)color)) {
                                player.sendMessage("§cTrop de joueurs dans cette team !");

                        
                        return;
                    }
                }
                if (nbPlayers < Totem.getInt("players-by-team")) {
                    tplayer.setTeamColor(color);
                            player.sendMessage(("§bVous avez rejoint l'équipe <team_color><team> §b!").replaceAll("<team>", color.getTeamName()).replaceAll("<team_color>", color.getChatColor().toString()));

                    
                } else {
                                player.sendMessage("§cTrop de joueurs dans cette team !");

                        
                    
                }
            } else if (inHand.getType() == Material.NETHER_STAR) {
                player.openInventory(Totem.getGameAPI().getKitInventory(player));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            Totem.getGameAPI().onInventoryClick(event);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME) {
            if (event.getFoodLevel() < 20) {
                event.setFoodLevel(20);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME && Totem.getGameState() != Totem.GameState.CONFIG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME && Totem.getGameState() != Totem.GameState.CONFIG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME) {
            event.setCancelled(true);
        }
    }
}



package net.linaris.Totem.Listeners;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.TeamsManager;
import net.linaris.Totem.Utils.Utils;

public class GameListener
implements Listener {
    private int fortress_protection = Totem.getInt("fortress-build-protection");
    private List<Material> container = new LinkedList<Material>();

    public GameListener() {
        this.container.add(Material.CHEST);
        this.container.add(Material.FURNACE);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        TeamsManager.TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
        if (Totem.getBoolean("player-death-lightning")) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        if (player.getKiller() != null) {
            Totem.getGameAPI().kill(player.getKiller());
        }
        PlayersManager.TucPlayer killer = player.getKiller() == null ? null : PlayersManager.getInstance().getPlayer(player.getKiller());
        for(Player p1 : Bukkit.getOnlinePlayers()){
        		p1.sendMessage(" §e" + player.getName() + (Object)ChatColor.YELLOW + " " + (player.getKiller() == null ? "§7a succombé." : new StringBuilder("§7a été tué par §e").append((Object)player.getKiller().getName()).toString()));

        }
        event.setDeathMessage(null);
        TeamsManager.getInstance().refreshTeam(team, null);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        TeamsManager.TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
        event.setRespawnLocation(team.getSpawn());
        Utils.setInventory(player);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        TeamsManager.TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
            TeamsManager.getInstance().refreshTeam(team, null);
        
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (Totem.getGameState() != Totem.GameState.GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        TeamsManager.TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
        if (team != null) {
            TeamsManager.getInstance().refreshTeam(team, null);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.breakBlock(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.breakBlock(event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!this.breakBlock(event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        int i = 0;
        while (i < event.blockList().size()) {
            if (!this.breakBlock(((Block)event.blockList().get(i)).getLocation(), null)) {
                event.blockList().remove(i);
                --i;
            }
            ++i;
        }
    }

    public boolean breakBlock(Location loc, Player who) {
        if (!TeamsManager.getInstance().isTotem(loc)) {
            for (TeamsManager.TucTeam team : TeamsManager.getInstance().getTeams()) {
                int x = Math.abs(loc.getBlockX() - team.getTotemLocation().getBlockX());
                int z = Math.abs(loc.getBlockZ() - team.getTotemLocation().getBlockZ());
                if (x > this.fortress_protection || z > this.fortress_protection) continue;
                return false;
            }
        }
        if (who != null) {
            PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(who);
            for (TeamsManager.TucTeam team : TeamsManager.getInstance().getTeams()) {
                if (!team.isTotem(loc)) continue;
                if (team.getColor().equals((Object)tplayer.getTeamColor())) {
                    return false;
                }
                TeamsManager.TucTeam team_breaked = TeamsManager.getInstance().getTeamByTotem(loc);
                TeamsManager.getInstance().refreshTeam(team_breaked, loc);
                for(Player p : Bukkit.getOnlinePlayers()){
                		p.sendMessage(("§b§lL'équipe <team_color>§l<team>§b§l a perdu un bloc sur son §6§lTOTEM §b§l!").replaceAll("<team_color>", team.getColor().getChatColor().toString()).replaceAll("<team>", team.getColor().getTeamName()));

                }
                TeamsManager.getInstance().refreshTeam(team, loc);
                return true;
            }
        } else {
            for (TeamsManager.TucTeam team : TeamsManager.getInstance().getTeams()) {
                if (!team.isTotem(loc)) continue;
                return false;
            }
        }
        return true;
    }
}


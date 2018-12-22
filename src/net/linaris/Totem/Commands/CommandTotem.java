
package net.linaris.Totem.Commands;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.TeamsManager;
import net.linaris.Totem.Timers.GameTimer;
import net.linaris.Totem.Utils.Data;

public class CommandTotem
implements CommandExecutor,
Listener {
    private HashMap<UUID, TeamConfig> m_teamConfig = new HashMap();

    public CommandTotem() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Totem.getInstance());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "You must be a player !");
            return true;
        }
        if (!sender.isOp()) {
            sender.sendMessage((Object)ChatColor.RED + "You must be an admin !");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setlobby")) {
                Location l = player.getLocation();
                Data.getInstance().setLobby(l);
                l.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                player.sendMessage((Object)ChatColor.GREEN + "Lobby define !");
                return true;
            }
            if (args[0].equalsIgnoreCase("lobby")) {
                player.teleport(Data.getInstance().getLobby());
                return true;
            }
            if (args[0].equalsIgnoreCase("gameworld")) {
                player.teleport(Totem.getWorldGame().getSpawnLocation());
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (Totem.getGameState() == Totem.GameState.CONFIG) {
                    Totem.getInstance().getConfig().set("config-mode", (Object)false);
                    Totem.getInstance().saveConfig();
                    player.sendMessage((Object)ChatColor.GREEN + "Config mode disable, please restart the server");
                    return true;
                }
                if (Totem.getGameState() == Totem.GameState.LOBBY) {
                    GameTimer.getInstance().setTime(2);
                    return true;
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("configteam")) {
            TeamsManager.TeamColor color = TeamsManager.TeamColor.getByString(args[1]);
            if (color == null) {
                player.sendMessage((Object)ChatColor.RED + "Use /totem configteam <BLUE | RED>");
            } else {
                this.m_teamConfig.put(player.getUniqueId(), new TeamConfig(player, color));
                player.setItemInHand(new ItemStack(Material.STICK, 1));
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getItemInHand().getType().equals((Object)Material.STICK)) {
            return;
        }
        if (this.m_teamConfig.containsKey(player.getUniqueId()) && this.m_teamConfig.get(player.getUniqueId()).getState() == 0 && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            this.m_teamConfig.get(player.getUniqueId()).setState(player.getLocation());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!event.getBlock().getType().equals((Object)Material.STONE)) {
            return;
        }
        if (this.m_teamConfig.containsKey(player.getUniqueId()) && this.m_teamConfig.get(player.getUniqueId()).getState() == 1) {
            this.m_teamConfig.get(player.getUniqueId()).setState(event.getBlock().getLocation());
            event.setCancelled(true);
        }
    }

    private class TeamConfig {
        private Player m_player;
        private TeamsManager.TeamColor m_color;
        private int m_state;
        private Location m_spawn;
        private Location m_totem;

        public TeamConfig(Player player, TeamsManager.TeamColor color) {
            this.m_player = player;
            this.m_color = color;
            this.m_state = 0;
            this.m_player.sendMessage((Object)ChatColor.YELLOW + "Right click with STICK to set team spawn");
        }

        public int getState() {
            return this.m_state;
        }

        public void setState(Location loc) {
            if (this.m_state == 0) {
                this.m_player.sendMessage((Object)ChatColor.YELLOW + "Ok! Place STONE to set totem location");
                this.m_spawn = loc;
                ++this.m_state;
                this.m_player.setItemInHand(new ItemStack(Material.STONE));
            } else if (this.m_state == 1) {
                this.m_player.sendMessage((Object)ChatColor.GREEN + "Team config !");
                this.m_totem = loc;
                Data.getInstance().createTeamData(this.m_color, this.m_spawn, this.m_totem);
                CommandTotem.this.m_teamConfig.remove(this.m_player.getUniqueId());
            }
        }
    }

}


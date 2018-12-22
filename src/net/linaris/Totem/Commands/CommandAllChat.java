
package net.linaris.Totem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAllChat
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "You must be a player !");
            return true;
        }
        Player player = (Player)sender;
        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            message.append("!");
            String[] arrstring = args;
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String arg = arrstring[n2];
                message.append(arg).append(" ");
                ++n2;
            }
            player.chat(message.toString());
            return true;
        }
        return false;
    }
}


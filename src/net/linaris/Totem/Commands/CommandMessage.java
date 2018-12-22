package net.linaris.Totem.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMessage implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player !");
			return true;
		}
		
		Player player = (Player) sender;
				
		if(args.length > 1) {
			Player receiver = Bukkit.getPlayer(args[0]);
			
			if(receiver == null) {
	    				player.sendMessage(("§cLe joueur §e<player> §cn'est pas connecté !").replaceAll("<player>", args[0]));

	            
				return true;
			}
			
			StringBuilder message = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				message.append(args[i]).append(" ");
			}
        			player.sendMessage(("§7vers §e<receiver> §f: §e<message>")
        					.replaceAll("<receiver>", receiver.getName())
        					.replaceAll("<sender>", player.getName())
        					.replaceAll("<message>", message.toString()));
        			
        			receiver.sendMessage(("§7de §e<sender> §f: §e<message>E")
        					.replaceAll("<receiver>", receiver.getName())
        					.replaceAll("<sender>", player.getName())
        					.replaceAll("<message>", message.toString()));

            
			return true;
		}
		
		return false;
	}
}

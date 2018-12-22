
package net.linaris.Totem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.linaris.Totem.Totem;
import net.linaris.Totem.Managers.PlayersManager;
import net.linaris.Totem.Managers.TeamsManager;
import net.linaris.Totem.Managers.TeamsManager.TucTeam;

public class ChatListener
implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        String message = event.getMessage();
        if (Totem.getGameState() == Totem.GameState.LOBBY) {
        	

        		
        		
			TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
			if(team == null) {
				
        		if(player.getName().equals("Neferett")){
    				
				event.setFormat(" §f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage());
    			}
    			else if(player.hasPermission("game.megavip")) {
    				event.setFormat(" §f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage());
    			}else if(player.hasPermission("game.vip")){
    				event.setFormat(" §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage());
    				
    			}else if(player.hasPermission("game.modo")){
    				event.setFormat(" §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage());
    				
    			}else if(player.hasPermission("game.admin")){
    				event.setFormat(" §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage());
    			}else if(player.hasPermission("game.vipelite")) {
    				event.setFormat(" §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
    			}else{
    				event.setFormat(" §7" + player.getName()+"§f: "+ event.getMessage());	
    			}
			}
		
			else {
				
        		if(player.getName().equals("Neferett")){
    				
				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] "+ "§f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage());
    			}
    			else if(player.hasPermission("game.megavip")) {
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + "§f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage());
    			}else if(player.hasPermission("game.vip")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage());
    				
    			}else if(player.hasPermission("game.modo")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage());
    				
    			}else if(player.hasPermission("game.admin")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage());
    			}else if(player.hasPermission("game.vipelite")) {
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
    			}else{
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f]" + " §7" + player.getName()+"§f: "+ event.getMessage());	
    			}
			}
			if(message.startsWith("!")) event.setMessage(message.substring(1));

			return;

            
        }
        if (Totem.getGameState() == Totem.GameState.GAME) {
        	if(!message.startsWith("!")){
            TeamsManager.TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());
			
    		if(player.getName().equals("Neferett")){
				
    			team.sendMessage("§f["+team.getColor().getChatColor().toString()+"Team§f] "+ "§f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage());
			}
			else if(player.hasPermission("game.megavip")) {
				team.sendMessage("§f["+team.getColor().getChatColor().toString()+"Team§f] " + "§f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage());
			}else if(player.hasPermission("game.vip")){
				team.sendMessage("§f["+team.getColor().getChatColor().toString()+"Team§f] " + " §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage());
				
			}else if(player.hasPermission("game.modo")){
				team.sendMessage("§f["+team.getColor().getChatColor().toString() +"Team§f] " + " §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage());
				
			}else if(player.hasPermission("game.admin")){
				team.sendMessage("§f["+team.getColor().getChatColor().toString() +"Team§f] " + " §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage());
			}else if(player.hasPermission("game.vipelite")) {
				team.sendMessage("§f["+team.getColor().getChatColor().toString() +"Team§f] " + " §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
			}else{
				team.sendMessage("§f["+team.getColor().getChatColor().toString() +"Team§f]" + " §7" + player.getName()+"§f: "+ event.getMessage());	
			}
        	}else if(message.startsWith("!")){
    			TucTeam team = TeamsManager.getInstance().getTeam(tplayer.getTeamColor());

        		if(player.getName().equals("Neferett")){
    				
				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] "+ "§f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
    			}
    			else if(player.hasPermission("game.megavip")) {
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + "§f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));
    			}else if(player.hasPermission("game.vip")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));
    				
    			}else if(player.hasPermission("game.modo")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
    				
    			}else if(player.hasPermission("game.admin")){
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
    			}else if(player.hasPermission("game.vipelite")) {
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f] " + " §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage().replaceFirst("!", ""));
    			}else{
    				event.setFormat("§f["+team.getColor().getChatColor().toString() + team.getColor().getTeamName()+"§f]" + " §7" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));	
    			}
    			return;
    		}
    			
    		}
            
        
        event.setCancelled(true);
    }
}


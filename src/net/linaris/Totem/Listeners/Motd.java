
package net.linaris.Totem.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

import net.linaris.Totem.Totem;

public class Motd
implements Listener {
    private static Motd instance;
    private String motd;

    public static void set(String motd) {
        if (instance == null) {
            instance = new Motd();
        }
        Motd.instance.motd = motd;
    }

    private Motd() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Totem.getInstance());
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (this.motd == null) {
            return;
        }
        event.setMotd(this.motd);
    }
}


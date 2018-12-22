
package net.linaris.Totem.Utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.linaris.Totem.Totem;

public class Lang {
    private Map<String, String> m_lang = new LinkedHashMap<String, String>();
    private File m_file;
    private FileConfiguration m_yaml;
    private static Lang instance = null;

    public static String get(String key) {
        if (instance == null) {
            instance = new Lang();
        }
        if (!Lang.instance.m_lang.containsKey(key)) {
            System.out.println("Key \"" + key + "\" unfound !");
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', Lang.instance.m_lang.get(key).replaceAll("<egrave>", "\u00e8").replaceAll("<eacute>", "é").replaceAll("<ecirc>", "ê").replaceAll("<euml>", "\u00eb").replaceAll("<uacute>", "\u00f9").replaceAll("<agrave>", "\u00e0").replaceAll("<check>", "\u2713").replaceAll("<check_bold>", "\u2714").replaceAll("<cross>", "\u2715").replaceAll("<cross_bold>", "\u2716").replaceAll("<plus>", "\u271a").replaceAll("<star>", "\u272a"));
    }

    private Lang() {
        try {
            File dirs = new File("plugins/" + Totem.getInstance().getName());
            dirs.mkdirs();
            this.m_lang.put("TEAM_NAME_RED", "Rouge");
            this.m_lang.put("TEAM_NAME_BLUE", "Bleu");
            this.m_file = new File("plugins/" + Totem.getInstance().getName() + "/lang.yml");
            if (!this.m_file.exists()) {
                this.m_file.createNewFile();
            }
            this.m_yaml = YamlConfiguration.loadConfiguration(this.m_file);
            LinkedHashMap<String, String> tmpLang = new LinkedHashMap<String, String>();
            for (String key : this.m_lang.keySet()) {
                if (this.m_yaml.contains(key)) {
                    tmpLang.put(key, this.m_yaml.getString(key));
                    continue;
                }
                this.m_yaml.set(key, this.m_lang.get(key));
                tmpLang.put(key, this.m_lang.get(key));
            }
            this.m_lang = tmpLang;
            this.m_yaml.save(this.m_file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


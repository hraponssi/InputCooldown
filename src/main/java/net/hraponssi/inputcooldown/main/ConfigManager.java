package net.hraponssi.inputcooldown.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
    public ConfigManager(Main plugin) {
        super();
        this.plugin = plugin;
    }

    public FileConfiguration datacfg;
    public FileConfiguration langcfg;
    public File datafile;
    public File langfile;
    Main plugin;

    String dataname = "icdata.yml";
    String langname = "lang.yml";

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        datafile = new File(plugin.getDataFolder(), dataname);
        langfile = new File(plugin.getDataFolder(), langname);

        if (!datafile.exists()) {
            try {
                datafile.createNewFile();
                plugin.getLogger().info(ChatColor.GREEN + "The " + dataname + " file has been created");
            } catch (IOException e) {
                plugin.getLogger().info(ChatColor.RED + "Could not create the " + dataname + " file");
            }
        }
        if (!langfile.exists()) {
            try {
                langfile.createNewFile();
                try (InputStream defConfigStream = plugin.getResource("lang.yml");
                        InputStreamReader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)) {
                    FileConfiguration deflang = YamlConfiguration.loadConfiguration(reader);
                    deflang.save(langfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                plugin.getLogger().info(ChatColor.GREEN + "The " + langname + " file has been created");
            } catch (IOException e) {
                plugin.getLogger().info(ChatColor.RED + "Could not create the " + langname + " file");
            }
        }

        datacfg = YamlConfiguration.loadConfiguration(datafile);
        langcfg = YamlConfiguration.loadConfiguration(langfile);
    }

    public FileConfiguration getData(String name) {
        if (name == "lang") return langcfg;
        return datacfg;
    }

    public void saveData() {
        try {
            datacfg.save(datafile);
            plugin.getLogger().info(ChatColor.AQUA + "The " + dataname + " file has been saved");
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + "Could not save the " + dataname + " file");
        }
    }

    public void reloadData() {
        datacfg = YamlConfiguration.loadConfiguration(datafile);
        plugin.getLogger().info(ChatColor.BLUE + "The " + dataname + " file has been reloaded");
    }

    public void reloadLang() {
        langcfg = YamlConfiguration.loadConfiguration(langfile);
        plugin.getLogger().info(ChatColor.BLUE + "The " + langname + " file has been reloaded");
    }

}

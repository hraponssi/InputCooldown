package net.hraponssi.inputcooldown.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
    public ConfigManager(Main plugin) {
        super();
        this.plugin = plugin;
    }
	public FileConfiguration icdatacfg;
	public File icdatafile;
	Main plugin;

	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		icdatafile = new File(plugin.getDataFolder(), "icdata.yml");

		if (!icdatafile.exists()) {
			try {
				icdatafile.createNewFile();
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The icdata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the icdata.yml file");
			}
		}

		icdatacfg = YamlConfiguration.loadConfiguration(icdatafile);
	}

	public FileConfiguration geticdata() {
		return icdatacfg;
	}

	public void saveicdata() {
		try {
			icdatacfg.save(icdatafile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The icdata.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the icdata.yml file");
		}
	}

	public void reloadicdata() {
		icdatacfg = YamlConfiguration.loadConfiguration(icdatafile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The icdata.yml file has been reload");

	}
}

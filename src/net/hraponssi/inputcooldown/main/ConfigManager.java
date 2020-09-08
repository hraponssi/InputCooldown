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
	public FileConfiguration datacfg;
	public File datafile;
	Main plugin;
	
	String filename = "icdata.yml";

	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		datafile = new File(plugin.getDataFolder(), filename);

		if (!datafile.exists()) {
			try {
				datafile.createNewFile();
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The " + filename + " file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the " + filename + " file");
			}
		}

		datacfg = YamlConfiguration.loadConfiguration(datafile);
	}

	public FileConfiguration getdata() {
		return datacfg;
	}

	public void savedata() {
		try {
			datacfg.save(datafile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The " + filename + " file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the " + filename + " file");
		}
	}

	public void reloaddata() {
		datacfg = YamlConfiguration.loadConfiguration(datafile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The " + filename + " file has been reload");

	}
}

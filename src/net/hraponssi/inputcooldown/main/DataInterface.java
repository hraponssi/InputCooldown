package net.hraponssi.inputcooldown.main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DataInterface {

	Main plugin;
	ConfigManager configManager;
	Utils utils;
	
	public DataInterface(Main plugin, ConfigManager cManager) {
		this.plugin = plugin;
		this.configManager = cManager;
		this.utils = new Utils();
	}
	
	public void saveData() {
		List<String> cb = configManager.getData().getStringList("cooldownBlocks");
		configManager.getData().set("cooldownBlocks", cb);
		cb.clear();
		for(Location loc : plugin.cooldownBlocks.keySet()) {
			int time = plugin.cooldownBlocks.get(loc);
			cb.add(time +  ":" +  loc.getBlockX() + "~"  + loc.getBlockY() +"~"+  loc.getBlockZ() +"~"+  loc.getWorld().getName());
		}
		configManager.saveData();
	}
	
	public void loadData() {
		List<String> cb = configManager.getData().getStringList("cooldownBlocks");
		for(String key: cb) {
			String[] splitted = key.split(":");
			String[] locsplitted = splitted[1].split("~");
			plugin.addCooldownBlock(utils.newLocation(utils.toInt(locsplitted[0]), utils.toInt(locsplitted[1]), utils.toInt(locsplitted[2]), utils.getWorld(locsplitted[3])).getBlock(), utils.toInt(splitted[0]));
		}
	}
	
}

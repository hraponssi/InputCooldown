package net.hraponssi.inputcooldown.main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DataInterface {

	Main plugin;
	ConfigManager configManager;
	
	public DataInterface(Main plugin, ConfigManager cManager) {
		this.plugin = plugin;
		this.configManager = cManager;
	}
	
	public void saveData() {
		List<String> cb = configManager.getdata().getStringList("gameids");
		configManager.getdata().set("cooldownBlocks", cb);
		cb.clear();
		for(Location loc : plugin.cooldownBlocks.keySet()) {
			int time = plugin.cooldownBlocks.get(loc);
			cb.add(time +  ":" +  loc.getBlockX() + "~"  + loc.getBlockY() +"~"+  loc.getBlockZ() +"~"+  loc.getWorld().getName());
		}
		configManager.savedata();
	}
	
	public void loadData() {
//		List<String> gi = configManager.getdata().getStringList("gameids");
//		for(String key: sl) {
//			String[] splitted = key.split(":");
//			String[] locsplitted = splitted[1].split("~");
//			signs.put(newLocation(toInt(locsplitted[0]), toInt(locsplitted[1]), toInt(locsplitted[2]), getWorld(locsplitted[3])), toInt(splitted[0]));
//		}
	}
	
}

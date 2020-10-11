package net.hraponssi.inputcooldown.main;

import java.util.HashMap;
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
		List<String> cb = configManager.getData("data").getStringList("cooldownBlocks");
		configManager.getData("data").set("cooldownBlocks", cb);
		cb.clear();
		for(Location loc : plugin.cooldownBlocks.keySet()) {
			int time = plugin.cooldownBlocks.get(loc);
			cb.add(time +  ":" +  loc.getBlockX() + "~"  + loc.getBlockY() +"~"+  loc.getBlockZ() +"~"+  loc.getWorld().getName());
		}
		configManager.saveData();
	}
	
	public void loadData() {
		List<String> cb = configManager.getData("data").getStringList("cooldownBlocks");
		for(String key: cb) {
			String[] splitted = key.split(":");
			String[] locsplitted = splitted[1].split("~");
			plugin.addCooldownBlock(utils.newLocation(utils.toInt(locsplitted[0]), utils.toInt(locsplitted[1]), utils.toInt(locsplitted[2]), utils.getWorld(locsplitted[3])).getBlock(), utils.toInt(splitted[0]));
		}
	}
	
	public String read(String s) {
		if(s == null) {
			plugin.getLogger().warning("Tried to read null lang string");
			return "";
		}
		String result = s;
		plugin.getLogger().info("result " + result + " . " + result.length() + " l");
		if(result.length()>0) if(result.charAt(result.length()-1) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>1) result = result.substring(0, result.length()-1);
		}
		if(result.length()>0) if(result.charAt(0) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>0) result = result.substring(1, result.length());
		}
		plugin.getLogger().info("result " + result + " . " + result.length() + " l");
		return result;
	}
	
	public void loadLang() {
		configManager.reloadLang();
		List<String> ls = configManager.getData("lang").getStringList("Strings");
		HashMap<String, String> Strings = new HashMap<>();
		for(String key: ls) {
			String[] splitted = key.split(":", 2);
			Strings.put(read(splitted[0]), read(splitted[1]));
		}
		plugin.getLogger().info(ls.size() + " Lang strings loaded");
		Lang.setMap(Strings);
	}
	
}

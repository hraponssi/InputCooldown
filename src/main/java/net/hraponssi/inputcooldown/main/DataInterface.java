package net.hraponssi.inputcooldown.main;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

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
		List<String> cp = configManager.getData("data").getStringList("cooldownPlots");
		configManager.getData("data").set("cooldownPlots", cp);
		cp.clear();
		for(String id : plugin.cooldownPlots.keySet()) {
			int time = plugin.cooldownPlots.get(id);
			cp.add(id +  ":" +  "DEFAULT" + "~"  + time);
		}
		for(String id : plugin.cooldownPlotBlocks.keySet()) {
			int time = plugin.cooldownPlotBlocks.get(id);
			cp.add(id + "~"  + time);
		}
		List<String> cbs = configManager.getData("data").getStringList("cooldownBlockSaves");
		configManager.getData("data").set("cooldownBlockSaves", cbs);
		cbs.clear();
		for(Location loc : plugin.cooldowns.keySet()) {
			Cooldown cooldown = plugin.cooldowns.get(loc);
			String stringLoc = loc.getBlockX() + "~"  + loc.getBlockY() +"~"+  loc.getBlockZ() +"~"+  loc.getWorld().getName();
			cbs.add(cooldown.time + "~" + cooldown.age + ":" +  stringLoc + ":" + cooldown.user.getUniqueId().toString());
		}
		configManager.saveData();
	}
	
	public void loadData() {
		List<String> bc = configManager.getData("data").getStringList("cooldownBlockSaves");
		for(String key: bc) {
			String[] splitted = key.split(":");
			String[] timesplitted = splitted[0].split("~");
			String[] locsplitted = splitted[1].split("~");
			Location loc = utils.newLocation(utils.toInt(locsplitted[0]), utils.toInt(locsplitted[1]), utils.toInt(locsplitted[2]), utils.getWorld(locsplitted[3]));
			UUID uuid = UUID.fromString(splitted[2]);
			plugin.addCooldown(loc.getBlock(), Bukkit.getOfflinePlayer(uuid).getPlayer(), utils.toInt(timesplitted[0]), utils.toInt(timesplitted[1]));
		}
		List<String> cb = configManager.getData("data").getStringList("cooldownBlocks");
		for(String key: cb) {
			String[] splitted = key.split(":");
			String[] locsplitted = splitted[1].split("~");
			if(Bukkit.getWorld(locsplitted[3]) != null){
				plugin.addCooldownBlock(utils.newLocation(utils.toInt(locsplitted[0]), utils.toInt(locsplitted[1]), utils.toInt(locsplitted[2]), utils.getWorld(locsplitted[3])).getBlock(), utils.toInt(splitted[0]));
			}else {
				plugin.getLogger().severe("Tried to load block cooldown in invalid world '"+ locsplitted[3] + "'");
			}
		}
		List<String> cp = configManager.getData("data").getStringList("cooldownPlots");
		for(String key: cp) {
			String[] splitted = key.split(":");
			String[] datasplitted = splitted[1].split("~");
			if(datasplitted[0].equals("DEFAULT")) {
				plugin.addCooldownPlot(splitted[0], utils.toInt(datasplitted[1]));
			}else {
				Material mat = Material.getMaterial(datasplitted[0]);
				if(utils.isInput(mat)) {
					plugin.addCooldownPlotBlock(splitted[0], mat, utils.toInt(datasplitted[1]));
				}else{
					plugin.getLogger().warning("Tried to parse plot wide block cooldown with invalid input type " + datasplitted[0]);
				}
				//specific block default plot cooldown
			}
		}
	}
	
	public String read(String s) {
		if(s == null) {
			plugin.getLogger().warning("Tried to read null lang string");
			return "";
		}
		String result = s;
		if(result.length()>0) if(result.charAt(result.length()-1) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>1) result = result.substring(0, result.length()-1);
		}
		if(result.length()>0) if(result.charAt(0) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>0) result = result.substring(1, result.length());
		}
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

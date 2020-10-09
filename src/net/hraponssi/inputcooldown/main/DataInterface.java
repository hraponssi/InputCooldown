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
		if(result.length()>0) if(result.charAt(result.length()-1) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>1) result = result.substring(0, result.length()-2);
		}
		if(result.length()>0) if(result.charAt(0) == '"' || result.charAt(result.length()-1) == '\'') {
			if(result.length()>0) result = result.substring(1, result.length()-1);
		}
		return result;
	}
	
	public void loadLang() {
		configManager.reloadLang();
		Lang.prefix = read(configManager.getData("lang").getString("PREFIX"));
		Lang.chatPrefix = read(configManager.getData("lang").getString("CHATPREFIX"));
		Lang.nolongerSetter = read(configManager.getData("lang").getString("NOLONGERSETTER"));
		Lang.invalidFormat = read(configManager.getData("lang").getString("INVALIDFORMAT"));
		Lang.nowSetting = read(configManager.getData("lang").getString("NOWSETTING"));
		Lang.plotError = read(configManager.getData("lang").getString("PLOTERROR"));
		Lang.invalidArguments = read(configManager.getData("lang").getString("INVALIDARGUMENTS"));
		Lang.noPermission = read(configManager.getData("lang").getString("NOPERMISSION"));
		Lang.configReloaded = read(configManager.getData("lang").getString("CONFIGRELOADED"));
		Lang.checkingCooldowns = read(configManager.getData("lang").getString("CHECKINGCOOLDOWNS"));
		Lang.notCheckingCooldowns = read(configManager.getData("lang").getString("NOTCHECKINGCOOLDOWNS"));
		Lang.checkedCooldown = read(configManager.getData("lang").getString("CHECKEDCOOLDOWN"));
		Lang.noCooldown = read(configManager.getData("lang").getString("NOCOOLDOWN"));
		Lang.reload();
	}
	
}

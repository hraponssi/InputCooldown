package net.hraponssi.inputcooldown.main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	ConfigManager configManager;
	Commands commands;
	EventHandlers eventHandlers;
	
	HashMap<Location, Integer> cooldownBlocks = new HashMap<>();
	HashMap<Location, Integer> cooldowns = new HashMap<>();
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		configManager = new ConfigManager(this);
		commands = new Commands(this);
		eventHandlers = new EventHandlers(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(eventHandlers, this);
		getCommand("ic").setExecutor(commands);
		getCommand("inputcooldown").setExecutor(commands);
	}
	
	public void removeCooldownBlock(Block b) {
		if(cooldownBlocks.containsKey(b.getLocation())) cooldownBlocks.remove(b.getLocation());
	}
	
	public void addCooldownBlock(Block b, int t) {
		if(cooldownBlocks.containsKey(b.getLocation())) {
			cooldownBlocks.replace(b.getLocation(), t);
		}else {
			cooldownBlocks.put(b.getLocation(), t);
		}
	}
	
	public boolean isCooldown(Block b) {
		return cooldowns.containsKey(b.getLocation());
	}
	
	public void cooldown(Block b) {
		if(cooldownBlocks.containsKey(b.getLocation())) {
			cooldowns.put(b.getLocation(), cooldownBlocks.get(b.getLocation()));
		}
	}
	
}

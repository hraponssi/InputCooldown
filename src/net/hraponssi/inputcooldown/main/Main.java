package net.hraponssi.inputcooldown.main;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	ConfigManager configManager;
	Commands commands;
	
	HashMap<Player, Integer> CooldownBlocks = new HashMap<>();
	HashMap<Player, Integer> Cooldowns = new HashMap<>();
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		configManager = new ConfigManager(this);
		commands = new Commands(this);
		getCommand("ic").setExecutor(commands);
		getCommand("inputcooldown").setExecutor(commands);
	}
	
	public void removeCooldownBlock(Block b) {
		
	}
	
	public void addCooldownBlock(Block b, int t) {
		
	}
	
	public void cooldown(Block b) {
		
	}
	
}

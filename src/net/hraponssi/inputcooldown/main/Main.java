package net.hraponssi.inputcooldown.main;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	ConfigManager configManager;
	Commands commands;
	EventHandlers eventHandlers;
	DataInterface dataInterface;
	
	HashMap<Location, Integer> cooldownBlocks = new HashMap<>();
	HashMap<Location, Cooldown> cooldowns = new HashMap<>();
	
	HashMap<Player, Integer> players = new HashMap<>();
	
	@Override
	public void onDisable() {
		dataInterface.saveData();
	}
	
	@Override
	public void onEnable() {
		configManager = new ConfigManager(this);
		commands = new Commands(this);
		eventHandlers = new EventHandlers(this);
		dataInterface = new DataInterface(this, configManager);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(eventHandlers, this);
		getCommand("ic").setExecutor(commands);
		getCommand("inputcooldown").setExecutor(commands);
		configManager.setup();
		dataInterface.loadData();
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				scheduler();
			}
		}, 1L, 1L);
	}
	
	public void scheduler() {
		for(Location l : cooldowns.keySet()) {
			Cooldown cooldown = cooldowns.get(l);
			cooldown.age++;
			if(cooldown.age>=cooldown.time) {
				cooldowns.remove(l);
				continue;
			}
			cooldowns.replace(l, cooldown);
		}
	}
	
	public void removeCooldownBlock(Block b) {
		if(cooldownBlocks.containsKey(b.getLocation())) cooldownBlocks.remove(b.getLocation());
	}
	
	public void removePlayer(Player p) {
		if(players.containsKey(p)) players.remove(p);
	}
	
	public void setPlayer(Player p, int timeout) {
		if(players.containsKey(p)) {
			players.replace(p, timeout);
		}else players.put(p, timeout);
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
	
	public Integer getCooldown(Block b) {
		if(!cooldowns.containsKey(b.getLocation())) return 0;
		Cooldown cooldown = cooldowns.get(b.getLocation());
		int age = cooldown.age;
		int time = cooldown.time;
		int left = time-age;
		return left/20;
	}
	
	public void cooldown(Block b, Player p) {
		if(cooldownBlocks.containsKey(b.getLocation())) {
			cooldowns.put(b.getLocation(), new Cooldown(cooldownBlocks.get(b.getLocation()), b.getLocation(), p));
		}
	}
	
}

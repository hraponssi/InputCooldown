package net.hraponssi.inputcooldown.main;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.plotsquared.core.plot.PlotId;

public class Main extends JavaPlugin{

	ConfigManager configManager;
	Commands commands;
	EventHandlers eventHandlers;
	DataInterface dataInterface;
	Utils utils;
	
	HashMap<Location, Integer> cooldownBlocks = new HashMap<>();
	HashMap<String, Integer> cooldownPlots = new HashMap<>();
	HashMap<String, Integer> cooldownPlotBlocks = new HashMap<>();
	HashMap<Location, Cooldown> cooldowns = new HashMap<>();
	
	HashMap<Player, Integer> players = new HashMap<>();
	HashMap<Player, Integer> plotPlayers = new HashMap<>();
	
	ArrayList<Player> admins = new ArrayList<>();
	
	ArrayList<Player> checkers = new ArrayList<>();
	ArrayList<Player> reseters = new ArrayList<>();
	HashMap<Player, String> removers = new HashMap<>();
	
	//Config values
	int minimumAccess = 1;
	
	int maxTime = 3600;
	int minTime = 3;
	int maxPlotCooldowns = -1;
	
	boolean adminJoinMsg = false;
	boolean adminLeaveDisable = false;
	
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
		utils = new Utils();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(eventHandlers, this);
		getCommand("ic").setExecutor(commands);
		getCommand("inputcooldown").setExecutor(commands);
		configManager.setup();
		dataInterface.loadLang();
		setConfig();
		loadConfig();
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				dataInterface.loadData();
			}
		}, 1L);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				scheduler();
			}
		}, 1L, 1L);
	}
	
	public void scheduler() {
		ArrayList<Location> removeList = new ArrayList<Location>();
		for(Location l : cooldowns.keySet()) {
			Cooldown cooldown = cooldowns.get(l);
			if(!l.getWorld().isChunkLoaded(l.getBlockX()/16, l.getBlockZ()/16)) continue; //Checks if chunk isnt loaded without loading chunk
			cooldown.age++;
			if(cooldown.age>=cooldown.time) {
				removeList.add(l);
				continue;
			}
			cooldowns.replace(l, cooldown);
		}
		for(Location l : removeList) cooldowns.remove(l);
	}
	
	public void reloadCfg() {
		dataInterface.loadLang();
		loadConfig();
	}
	
	public void loadConfig() {
		reloadConfig();
		FileConfiguration config = this.getConfig();
		if(config.getString("minimumAccess").equalsIgnoreCase("Owner")) minimumAccess = 3;
		if(config.getString("minimumAccess").equalsIgnoreCase("Trusted")) minimumAccess = 2;
		if(config.getString("minimumAccess").equalsIgnoreCase("Member")) minimumAccess = 1;
		maxPlotCooldowns = config.getInt("maxPlotCooldowns");
		maxTime = config.getInt("maxTime");
		minTime = config.getInt("minTime");
		adminJoinMsg = config.getBoolean("adminModeJoinMsg");
		adminLeaveDisable = config.getBoolean("disableAdminOnQuit");
	}
	
	public void setConfig() {
		File f = new File(this.getDataFolder() + File.separator + "config.yml");
		if (f.exists()) {
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		try (InputStream defConfigStream = this.getResource("config.yml");
				InputStreamReader reader = new InputStreamReader(defConfigStream,StandardCharsets.UTF_8)){
			FileConfiguration defconf = YamlConfiguration.loadConfiguration(reader);
			config.addDefaults(defconf);
			config.setDefaults(defconf);
			this.saveDefaultConfig();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean inAdminMode(Player p) {
		return admins.contains(p);
	}
	
	public void removeCooldownBlock(Block b) {
		if(cooldownBlocks.containsKey(b.getLocation())) cooldownBlocks.remove(b.getLocation());
	}
	
	public void removeCooldownPlot(String id) {
		if(cooldownPlots.containsKey(id)) cooldownPlots.remove(id);
	}
	
	public void removeCooldownPlotBlock(String id, Material mat) {
		cooldownPlotBlocks.remove(id + ":" + mat.name());
	}
	
	public void resetCooldown(Block b) {
		if(cooldowns.containsKey(b.getLocation())) cooldowns.remove(b.getLocation());
	}
	
	public void removePlayer(Player p) {
		if(players.containsKey(p)) players.remove(p);
		if(plotPlayers.containsKey(p)) plotPlayers.remove(p);
	}
	
	public void removeRemover(Player p) {
		if(removers.containsKey(p)) removers.remove(p);
	}
	
	public void setRemover(Player p, String type) {
		if(!removers.containsKey(p)) {
			removers.put(p, type);
		}else {
			removers.replace(p, type);
		}
	}
	
	public void setPlayer(Player p, int timeout) {
		if(players.containsKey(p)) {
			players.replace(p, timeout);
		}else players.put(p, timeout);
	}
	
	public void setPlotPlayer(Player p, int timeout) {
		if(plotPlayers.containsKey(p)) {
			plotPlayers.replace(p, timeout);
		}else plotPlayers.put(p, timeout);
	}
	
	public void addCooldownBlock(Block b, int t) {
		cooldownBlocks.put(b.getLocation(), t);
	}
	
	public void addCooldownPlotBlock(String id, Material mat, int t) {
		cooldownPlotBlocks.put(id + ":" + mat.name(), t);
	}
	
	public void addCooldownPlot(String id, int t) {
		cooldownPlots.put(id, t);
	}
	
	public boolean isCooldown(Block b) {
		return cooldownBlocks.containsKey(b.getLocation());
	}
	
	public boolean hasCooldown(Block b) {
		return cooldowns.containsKey(b.getLocation());
	}
	
	public boolean hasCooldown(String id, Material mat) {
		return cooldownPlotBlocks.containsKey(id + ":" + mat.name());
	}
	
	public boolean hasCooldown(String id) {
		return cooldownPlots.containsKey(id);
	}
	
	public Integer getSetCooldown(Block b) {
		if(!cooldownBlocks.containsKey(b.getLocation())) return 0;
		int cooldown = cooldownBlocks.get(b.getLocation());
		return cooldown;
	}
	
	public Integer getCooldown(Block b) {
		if(!cooldowns.containsKey(b.getLocation())) return 0;
		Cooldown cooldown = cooldowns.get(b.getLocation());
		int age = cooldown.age;
		int time = cooldown.time;
		int left = time-age;
		return left/20;
	}
	
	public HashMap<String, Integer> getPlotCooldowns(String id){
		HashMap<String, Integer> cooldowns = new HashMap<String, Integer>();
		for(Entry<String, Integer> entry : cooldownPlotBlocks.entrySet()) {
			String key = entry.getKey();
			int time = entry.getValue();
			if(key.startsWith(id)) {
				String[] splitKey = key.split(":");
				cooldowns.put(splitKey[1], time);
			}
		}
		if(cooldownPlots.containsKey(id)) cooldowns.put("DEFAULT" , cooldownPlots.get(id));
		return cooldowns;
	}
	
	public int plotCooldownCount(String id) {
		return getPlotCooldowns(id).size();
	}
	
	public void cooldown(Block b, Player p) {
		PlotId id = utils.getPlot(b.getLocation());
		if(cooldownBlocks.containsKey(b.getLocation())) {
			cooldowns.put(b.getLocation(), new Cooldown(cooldownBlocks.get(b.getLocation()), b.getLocation(), p));
		}else if(cooldownPlotBlocks.containsKey(id.getX() + ";" + id.getY() + ":" + b.getType().name())) {
			getLogger().info("Tried to cooldown block but only found plot material specific cooldown " + id.getX() + ";" + id.getY() + " " + b.getType().name());
			cooldowns.put(b.getLocation(), new Cooldown(cooldownPlotBlocks.get(id.getX() + ";" + id.getY() + ":" + b.getType().name()), b.getLocation(), p));
		}else if(cooldownPlots.containsKey(id.getX() + ";" + id.getY())) {
			getLogger().info("Tried to cooldown block but only found plot cooldown " + id.getX() + ";" + id.getY());
			cooldowns.put(b.getLocation(), new Cooldown(cooldownPlots.get(id.getX() + ";" + id.getY()), b.getLocation(), p));
		}
	}
	
	public void addCooldown(Block b, Player p, int time, int age) {
		Cooldown cooldown = new Cooldown(time, b.getLocation(), p);
		cooldown.age = age;
		cooldowns.put(b.getLocation(), cooldown);
	}

	public boolean toggleAdmin(Player p) {
		if(admins.contains(p)) {
			admins.remove(p);
			return false;
		}else {
			admins.add(p);
			return true;
		}
	}
	
}
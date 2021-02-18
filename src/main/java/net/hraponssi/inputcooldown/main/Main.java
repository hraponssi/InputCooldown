package net.hraponssi.inputcooldown.main;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.plotsquared.core.plot.PlotId;

import net.hraponssi.inputcooldown.commands.Commands;
import net.hraponssi.inputcooldown.commands.completion.InputCooldownCompletion;
import net.hraponssi.inputcooldown.events.EventHandlers;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{

	//Classes
	ConfigManager configManager;
	Commands commands;
	EventHandlers eventHandlers;
	DataInterface dataInterface;
	Utils utils;
	
	//Data
	HashMap<Location, Integer> cooldownBlocks = new HashMap<>();
	HashMap<String, Integer> cooldownPlots = new HashMap<>();
	HashMap<String, Integer> cooldownPlotBlocks = new HashMap<>();
	HashMap<Location, Cooldown> cooldowns = new HashMap<>();
	
	HashMap<UUID, Integer> players = new HashMap<>();
	HashMap<UUID, Integer> plotPlayers = new HashMap<>();
	
	ArrayList<UUID> admins = new ArrayList<>();
	ArrayList<UUID> debugers = new ArrayList<>();
	
	ArrayList<UUID> checkers = new ArrayList<>();
	ArrayList<UUID> reseters = new ArrayList<>();
	HashMap<UUID, String> removers = new HashMap<>();
	
	//Config values
	int minimumAccess = 1;
	
	int maxTime = 3600;
	int minTime = 3;
	int maxPlotCooldowns = -1;
	
	boolean adminJoinMsg = false;
	boolean adminLeaveDisable = false;
	
	//Dependencies
	boolean pSquared = false;
	
	@Override
	public void onDisable() {
		dataInterface.saveData();
	}
	
	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("PlotSquared").isEnabled()) {
			pSquared = true;
			getLogger().info("PlotSquared detected!");
		}
		configManager = new ConfigManager(this);
		commands = new Commands(this);
		eventHandlers = new EventHandlers(this);
		dataInterface = new DataInterface(this, configManager);
		utils = new Utils(pSquared);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(eventHandlers, this);
		getCommand("ic").setExecutor(commands);
		getCommand("ic").setTabCompleter(new InputCooldownCompletion());
		getCommand("inputcooldown").setExecutor(commands);
		getCommand("inputcooldown").setTabCompleter(new InputCooldownCompletion());
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
		return admins.contains(p.getUniqueId());
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
		if(getPlayers().containsKey(p.getUniqueId())) getPlayers().remove(p.getUniqueId());
		if(getPlotPlayers().containsKey(p.getUniqueId())) getPlotPlayers().remove(p.getUniqueId());
	}
	
	public void removeRemover(Player p) {
		if(getRemovers().containsKey(p.getUniqueId())) getRemovers().remove(p.getUniqueId());
	}
	
	public void setRemover(Player p, String type) {
		if(!getRemovers().containsKey(p.getUniqueId())) {
			getRemovers().put(p.getUniqueId(), type);
		}else {
			getRemovers().replace(p.getUniqueId(), type);
		}
	}
	
	public void setPlayer(Player p, int timeout) {
		if(getPlayers().containsKey(p.getUniqueId())) {
			getPlayers().replace(p.getUniqueId(), timeout);
		}else getPlayers().put(p.getUniqueId(), timeout);
	}
	
	public void setPlotPlayer(Player p, int timeout) {
		if(getPlotPlayers().containsKey(p.getUniqueId())) {
			getPlotPlayers().replace(p.getUniqueId(), timeout);
		}else getPlotPlayers().put(p.getUniqueId(), timeout);
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
		int cooldown = 0;
		if(pSquared) {
			PlotId id = utils.getPlot(b.getLocation());
			if(cooldownPlots.containsKey(id.getX() + ";" + id.getY())) cooldown = cooldownPlots.get(id.getX() + ";" + id.getY());
			if(cooldownPlotBlocks.containsKey(id.getX() + ";" + id.getY() + ":" + b.getType().name())) cooldown = cooldownPlotBlocks.get(id.getX() + ";" + id.getY() + ":" + b.getType().name());
		}
		if(cooldownBlocks.containsKey(b.getLocation())) cooldown = cooldownBlocks.get(b.getLocation());
		return cooldown; //checks done in order of priority
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
		if(pSquared) {
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
		}else {
			if(cooldownBlocks.containsKey(b.getLocation())) {
				cooldowns.put(b.getLocation(), new Cooldown(cooldownBlocks.get(b.getLocation()), b.getLocation(), p));
			}
		}
	}
	
	public void addCooldown(Block b, Player p, int time, int age) {
		Cooldown cooldown = new Cooldown(time, b.getLocation(), p);
		cooldown.age = age;
		cooldowns.put(b.getLocation(), cooldown);
	}

	public boolean toggleAdmin(Player p) {
		if(admins.contains(p.getUniqueId())) {
			admins.remove(p.getUniqueId());
			return false;
		}else {
			admins.add(p.getUniqueId());
			return true;
		}
	}
	
	public boolean toggleDebug(Player p) {
		if(debugers.contains(p.getUniqueId())) {
			debugers.remove(p.getUniqueId());
			return false;
		}else {
			debugers.add(p.getUniqueId());
			return true;
		}
	}
	
	public void debug(String msg, Player p) {
		if(debugers.contains(p.getUniqueId())) p.sendMessage(ChatColor.GRAY + "<"
											+ ChatColor.YELLOW + "IC Debug"
											+ ChatColor.GRAY + "> "
											+ ChatColor.YELLOW + msg);
	}
	
	public boolean getPSquared() {
		return pSquared;
	}

	public boolean getAdminJoinMsg() {
		return adminJoinMsg;
	}

	public boolean getAdminLeaveDisable() {
		return adminLeaveDisable;
	}

	public HashMap<UUID, String> getRemovers() {
		return removers;
	}

	public int getMinimumAccess() {
		return minimumAccess;
	}

	public ArrayList<UUID> getReseters() {
		return reseters;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public int getMinTime() {
		return minTime;
	}

	public int getMaxPlotCooldowns() {
		return maxPlotCooldowns;
	}

	public ArrayList<UUID> getCheckers() {
		return checkers;
	}

	public HashMap<UUID, Integer> getPlayers() {
		return players;
	}

	public HashMap<UUID, Integer> getPlotPlayers() {
		return plotPlayers;
	}
	
}

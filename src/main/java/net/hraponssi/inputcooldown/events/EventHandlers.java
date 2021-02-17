package net.hraponssi.inputcooldown.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.hraponssi.inputcooldown.main.Lang;
import net.hraponssi.inputcooldown.main.Main;
import net.hraponssi.inputcooldown.main.Utils;

public class EventHandlers implements Listener {

	Main plugin;
	Utils utils;
	InputHandler inputHandler;
	
	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils(plugin.getPSquared());
		this.inputHandler = new InputHandler(plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		inputHandler.onPlayerInteract(event);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(plugin.getAdminJoinMsg() && plugin.inAdminMode(event.getPlayer())) event.getPlayer().sendMessage(Lang.get("ADMINBYPASS"));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if(plugin.getAdminLeaveDisable() && plugin.inAdminMode(event.getPlayer())) plugin.toggleAdmin(event.getPlayer()); 
	}
	
}

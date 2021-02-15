package net.hraponssi.inputcooldown.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventHandlers implements Listener {

	Main plugin;
	Utils utils;
	InputHandler inputHandler;
	
	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils(plugin.pSquared);
		this.inputHandler = new InputHandler(plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		inputHandler.onPlayerInteract(event);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(plugin.adminJoinMsg && plugin.inAdminMode(event.getPlayer())) event.getPlayer().sendMessage(Lang.get("ADMINBYPASS"));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if(plugin.adminLeaveDisable && plugin.inAdminMode(event.getPlayer())) plugin.toggleAdmin(event.getPlayer()); 
	}
	
}

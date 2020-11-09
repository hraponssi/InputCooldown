package net.hraponssi.inputcooldown.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.plotsquared.core.plot.PlotId;

public class EventHandlers implements Listener {

	Main plugin;
	Utils utils;
	InputHandler inputHandler;
	
	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils();
		this.inputHandler = new InputHandler(plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		inputHandler.onPlayerInteract(event);
	}
	
}

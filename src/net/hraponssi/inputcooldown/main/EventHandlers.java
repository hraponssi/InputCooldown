package net.hraponssi.inputcooldown.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventHandlers implements Listener {

	Main plugin;
	
	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player p = event.getPlayer();
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        Block clicked = event.getClickedBlock();
	        if (clicked.getType() == Material.STONE_BUTTON) {
	            p.sendMessage("button click");
	        }
	    }
	}
	
}

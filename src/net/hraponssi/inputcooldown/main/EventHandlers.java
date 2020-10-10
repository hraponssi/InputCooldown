package net.hraponssi.inputcooldown.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventHandlers implements Listener {

	Main plugin;
	Utils utils;
	
	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player p = event.getPlayer();
	    Block b = event.getClickedBlock();
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        if (utils.isInput(b.getType())) {
	        	if(plugin.players.containsKey(p)) {
	        		event.setCancelled(true);
	        		if(!utils.inOwnPlot(p)) {
	        			p.sendMessage(Lang.get("PLOTERROR"));
	        			return;
	        		}
	        		p.sendMessage("Set timeout of " + plugin.players.get(p));
	        		plugin.addCooldownBlock(b, plugin.players.get(p));
	        	}else if(plugin.checkers.contains(p)){
	        		if(plugin.isCooldown(b)) {
	        			p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s"));
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else {
	        		p.sendMessage("input click");
		            if(plugin.hasCooldown(b)) {
		            	event.setCancelled(true);
		            	p.sendMessage(Lang.get("COOLDOWN",plugin.getCooldown(b) + "s"));
		            }else {
		            	plugin.cooldown(b, p);
		            }
	        	}
	        }
	    }
	}
	
}

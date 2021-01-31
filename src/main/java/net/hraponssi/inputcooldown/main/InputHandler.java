package net.hraponssi.inputcooldown.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.plotsquared.core.plot.PlotId;

public class InputHandler {

	Main plugin;
	Utils utils;
	
	public InputHandler(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils();
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player p = event.getPlayer();
	    Block b = event.getClickedBlock();
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        if (utils.isInput(b.getType())) {
	        	if(plugin.players.containsKey(p)) {
	        		event.setCancelled(true);
	        		if(utils.plotAccessLevel(p) < plugin.minimumAccess && !plugin.inAdminMode(p)) {
	        			p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        			return;
	        		} else if(plugin.inAdminMode(p)) {
	        			p.sendMessage(Lang.get("ADMINBYPASS"));
	        		}
	        		if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.maxPlotCooldowns && plugin.maxPlotCooldowns > -1) {
	        			p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.maxPlotCooldowns));
	        			return;
	        		}
	        		p.sendMessage(Lang.get("SETCOOLDOWN", plugin.players.get(p)/20 + "s"));
	        		plugin.addCooldownBlock(b, plugin.players.get(p));
	        		plugin.resetCooldown(b); //Make this configurable?
	        	}else if(plugin.plotPlayers.containsKey(p)) {
	        		event.setCancelled(true);
	        		if(utils.plotAccessLevel(p) < plugin.minimumAccess && !plugin.inAdminMode(p)) {
	        			p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        			return;
	        		} else if(plugin.inAdminMode(p)) {
	        			p.sendMessage(Lang.get("ADMINBYPASS"));
	        		}
	        		if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.maxPlotCooldowns && plugin.maxPlotCooldowns > -1) {
	        			p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.maxPlotCooldowns));
	        			return;
	        		}
	        		p.sendMessage(Lang.get("SETPLOTBLOCKCOOLDOWN", plugin.plotPlayers.get(p)/20 + "s"));
	        		PlotId id = utils.getPlot(b.getLocation());
	        		plugin.addCooldownPlotBlock(id.getX() + ";" + id.getY(), b.getType(), plugin.plotPlayers.get(p));
	        	}else if(plugin.checkers.contains(p)){ //TODO update for non block specific cooldowns
	        		if(plugin.isCooldown(b)) {
	        			p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s"));
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.reseters.contains(p)){
	        		if(plugin.hasCooldown(b)) {
	        			int time = plugin.getCooldown(b);
	        			plugin.resetCooldown(b);
	        			p.sendMessage(Lang.get("COOLDOWNRESET", time + "s"));
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.removers.containsKey(p)) {
	        		if(plugin.isCooldown(b)) {
	        			if(plugin.removers.get(p).equals("click")) {
	        				if(utils.inOwnPlot(p)) {
		        				plugin.removeCooldownBlock(b);
		        				plugin.resetCooldown(b); //Make this configurable?
		        				p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " ("+plugin.getSetCooldown(b)+"s)"));
		        			}else {
		        				p.sendMessage(Lang.get("PLOTACCESSERROR"));
		        			}
	        			}else if(plugin.removers.get(p).equals("block")) {
	        				if(utils.inOwnPlot(p)) {
	        					String plotid = utils.getPlot(b.getLocation()).getX() + ";" + utils.getPlot(b.getLocation()).getY();
	        					p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " default ("+plugin.getSetCooldown(b)+"s)"));
	        					plugin.removeCooldownPlotBlock(plotid, b.getType());
		        			}else {
		        				p.sendMessage(Lang.get("PLOTACCESSERROR"));
		        			}
	        			}
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
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
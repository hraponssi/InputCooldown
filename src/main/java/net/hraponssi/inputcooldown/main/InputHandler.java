package net.hraponssi.inputcooldown.main;

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
		this.utils = new Utils(plugin.pSquared);
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player p = event.getPlayer();
	    Block b = event.getClickedBlock();
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        if (utils.isInput(b.getType())) {
	        	if(plugin.players.containsKey(p.getUniqueId())) {
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
	        		p.sendMessage(Lang.get("SETCOOLDOWN", plugin.players.get(p.getUniqueId())/20 + "s"));
	        		plugin.addCooldownBlock(b, plugin.players.get(p.getUniqueId()));
	        		plugin.resetCooldown(b); //Make this configurable?
	        	}else if(plugin.plotPlayers.containsKey(p.getUniqueId())) {
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
	        		p.sendMessage(Lang.get("SETPLOTBLOCKCOOLDOWN", plugin.plotPlayers.get(p.getUniqueId())/20 + "s"));
	        		PlotId id = utils.getPlot(b.getLocation());
	        		plugin.addCooldownPlotBlock(id.getX() + ";" + id.getY(), b.getType(), plugin.plotPlayers.get(p.getUniqueId()));
	        	}else if(plugin.checkers.contains(p.getUniqueId())){
	        		PlotId id = utils.getPlot(b.getLocation());
	        		if(plugin.isCooldown(b)) {
	        			p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s"));
	        		}else if(plugin.hasCooldown(id.getX() + ";" + id.getY(), b.getType())){
	        			p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s " + "(" + "Plot, " + b.getType().name() + ")"));
	        		}else if(plugin.hasCooldown(id.getX() + ";" + id.getY())){
	        			p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s " + "(" + "Plot" + ")"));
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.reseters.contains(p.getUniqueId())){
	        		if(plugin.hasCooldown(b)) {
	        			int time = plugin.getCooldown(b);
	        			plugin.resetCooldown(b);
	        			p.sendMessage(Lang.get("COOLDOWNRESET", time + "s"));
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.removers.containsKey(p.getUniqueId())) {
	        		if(plugin.isCooldown(b)) {
	        			if(plugin.removers.get(p.getUniqueId()).equals("click")) {
	        				if(utils.plotAccessLevel(p) < plugin.minimumAccess  && !plugin.inAdminMode(p)) {
	        					p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        					return;
	        				} else if(plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("ADMINBYPASS"));
							}
		        			plugin.removeCooldownBlock(b);
		        			plugin.resetCooldown(b); //Make this configurable?
		        			p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " ("+plugin.getSetCooldown(b)+"s)"));
	        			}else if(plugin.removers.get(p.getUniqueId()).equals("block")) {
	        				if(utils.plotAccessLevel(p) < plugin.minimumAccess  && !plugin.inAdminMode(p)) {
	        					p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        					return;
	        				}  else if(plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("ADMINBYPASS"));
							}
	        				String plotid = utils.getPlot(b.getLocation()).getX() + ";" + utils.getPlot(b.getLocation()).getY();
	        				p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " default ("+plugin.getSetCooldown(b)+"s)"));
	        				plugin.removeCooldownPlotBlock(plotid, b.getType());
	        			}
	        		}else {
	        			p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        	}else {
	        		plugin.debug("input click", p);
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

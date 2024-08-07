package net.hraponssi.inputcooldown.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.plotsquared.core.plot.PlotId;

import net.hraponssi.inputcooldown.main.Lang;
import net.hraponssi.inputcooldown.main.Main;
import net.hraponssi.inputcooldown.main.Utils;

public class InputHandler {

	Main plugin;
	Utils utils;
	
	public InputHandler(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils(plugin.getPSquared());
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player p = event.getPlayer();
	    Block b = event.getClickedBlock();
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
	        if (utils.isInput(b.getType())) {
	        	boolean msg = !plugin.msgCooldown(p, b.getLocation());
	        	if(plugin.getPlayers().containsKey(p.getUniqueId())) {
	        		event.setCancelled(true);
	        		if(utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
	        			if(msg) p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        			return;
	        		} else if(plugin.inAdminMode(p)) {
	        			if(msg) p.sendMessage(Lang.get("ADMINBYPASS"));
	        		}
	        		if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.getMaxPlotCooldowns() && plugin.getMaxPlotCooldowns() > -1) {
	        			if(msg) p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.getMaxPlotCooldowns()));
	        			return;
	        		}
	        		if(msg) p.sendMessage(Lang.get("SETCOOLDOWN", plugin.getPlayers().get(p.getUniqueId())/20 + "s"));
	        		plugin.addCooldownBlock(b, plugin.getPlayers().get(p.getUniqueId()));
	        		plugin.resetCooldown(b); //Make this configurable?
	        	}else if(plugin.getPlotPlayers().containsKey(p.getUniqueId())) {
	        		event.setCancelled(true);
	        		if(utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
	        			if(msg) p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        			return;
	        		} else if(plugin.inAdminMode(p)) {
	        			if(msg) p.sendMessage(Lang.get("ADMINBYPASS"));
	        		}
	        		if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.getMaxPlotCooldowns() && plugin.getMaxPlotCooldowns() > -1) {
	        			if(msg) p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.getMaxPlotCooldowns()));
	        			return;
	        		}
	        		if(msg) p.sendMessage(Lang.get("SETPLOTBLOCKCOOLDOWN", plugin.getPlotPlayers().get(p.getUniqueId())/20 + "s"));
	        		PlotId id = utils.getPlot(b.getLocation());
	        		plugin.addCooldownPlotBlock(id.getX() + ";" + id.getY(), b.getType(), plugin.getPlotPlayers().get(p.getUniqueId()));
	        	}else if(plugin.getCheckers().contains(p.getUniqueId())){
	        		PlotId id = utils.getPlot(b.getLocation());
	        		if(plugin.isCooldown(b)) {
	        			if(msg) p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s"));
	        		}else if(plugin.hasCooldown(id.getX() + ";" + id.getY(), b.getType())){
	        			if(msg) p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s " + "(" + "Plot, " + b.getType().name() + ")"));
	        		}else if(plugin.hasCooldown(id.getX() + ";" + id.getY())){
	        			if(msg) p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b)/20 + "s " + "(" + "Plot" + ")"));
	        		}else {
	        			if(msg) p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.getReseters().contains(p.getUniqueId())){
	        		if(plugin.hasCooldown(b)) {
	        			int time = plugin.getCooldown(b);
	        			plugin.resetCooldown(b);
	        			if(msg) p.sendMessage(Lang.get("COOLDOWNRESET", time + "s"));
	        		}else {
	        			if(msg) p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        		event.setCancelled(true);
	        	}else if(plugin.getRemovers().containsKey(p.getUniqueId())) {
	        		if(plugin.isCooldown(b) || plugin.isBlockCooldown(b)) {
	        			if(plugin.getRemovers().get(p.getUniqueId()).equals("click") && plugin.isCooldown(b)) {
	        				if(utils.plotAccessLevel(p) < plugin.getMinimumAccess()  && !plugin.inAdminMode(p)) {
	        					if(msg) p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        					return;
	        				} else if(plugin.inAdminMode(p)) {
	        					if(msg) p.sendMessage(Lang.get("ADMINBYPASS"));
							}
		        			plugin.removeCooldownBlock(b);
		        			plugin.resetCooldown(b); //Make this configurable?
		        			if(msg) p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " ("+plugin.getSetCooldown(b)+"s)"));
	        			}else if(plugin.getRemovers().get(p.getUniqueId()).equals("block") && plugin.isBlockCooldown(b)) {
	        				if(utils.plotAccessLevel(p) < plugin.getMinimumAccess()  && !plugin.inAdminMode(p)) {
	        					if(msg) p.sendMessage(Lang.get("PLOTACCESSERROR"));
	        					return;
	        				}  else if(plugin.inAdminMode(p)) {
	        					if(msg) p.sendMessage(Lang.get("ADMINBYPASS"));
							}
	        				String plotid = utils.getPlot(b.getLocation()).getX() + ";" + utils.getPlot(b.getLocation()).getY();
	        				if(msg) p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString() + " default ("+plugin.getSetCooldown(b)+"s)"));
	        				plugin.removeCooldownPlotBlock(plotid, b.getType());
	        			}
	        		}else {
	        			if(msg) p.sendMessage(Lang.get("NOCOOLDOWN"));
	        		}
	        	}else {
	        		plugin.debug("input click", p);
	        		boolean ownPlot = utils.inOwnPlot(p);
	        		boolean bypassOwn = ownPlot && plugin.bypassOnOwnPlot;
	        		boolean bypassAll = plugin.getBypassers().contains(p.getUniqueId()) && plugin.inAdminMode(p);
	        		boolean bypassOn = ownPlot && plugin.getBypassers().contains(p.getUniqueId());
		            if(plugin.hasCooldown(b)) {
		            	event.setCancelled(true);
		            	if(msg) p.sendMessage(Lang.get("COOLDOWN",plugin.getCooldown(b) + "s"));
		            }else if(bypassOwn || bypassAll || bypassOn){
		            	
		            }else {
		            	plugin.cooldown(b, p);
		            }
	        	}
	        }
	    }
	}
	
}

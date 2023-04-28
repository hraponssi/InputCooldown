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

                if (plugin.getPlayers().containsKey(p.getUniqueId())) { // Player is a block click cooldown setter

                    event.setCancelled(true);
                    PlotId plotId = utils.getPlot(p); // TODO make a new system to block null values (roads etc) unless admin. Maybe use admin mode and also add a config option to always have admin mode on for admin users
                    if (utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
                        if (msg) {
                            p.sendMessage(Lang.get("PLOTACCESSERROR"));
                        }
                        return;
                    } else if (plugin.inAdminMode(p) && msg) {
                        p.sendMessage(Lang.get("ADMINBYPASS"));
                    }
                    if (plugin.plotCooldownCount(utils.toStringId(plotId)) >= plugin.getMaxPlotCooldowns()
                            && plugin.getMaxPlotCooldowns() > -1) {
                        if (msg) {
                            p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.getMaxPlotCooldowns()));
                        }
                        return;
                    }
                    if (msg) {
                        p.sendMessage(Lang.get("SETCOOLDOWN", plugin.getPlayers().get(p.getUniqueId()) / 20 + "s"));
                    }
                    plugin.addCooldownBlock(b, plugin.getPlayers().get(p.getUniqueId()));
                    plugin.resetCooldown(b); // Make this configurable?

                } else if (plugin.getPlotPlayers().containsKey(p.getUniqueId())) { // Player is a plot block cooldown setter

                    event.setCancelled(true);
                    if (utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
                        if (msg) {
                            p.sendMessage(Lang.get("PLOTACCESSERROR"));
                        }
                        return;
                    } else if (plugin.inAdminMode(p) && msg) {
                        p.sendMessage(Lang.get("ADMINBYPASS"));
                    }
                    if (plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.getMaxPlotCooldowns()
                            && plugin.getMaxPlotCooldowns() > -1) {
                        if (msg) {
                            p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.getMaxPlotCooldowns()));
                        }
                        return;
                    }
                    if (msg) {
                        p.sendMessage(Lang.get("SETPLOTBLOCKCOOLDOWN",
                                plugin.getPlotPlayers().get(p.getUniqueId()) / 20 + "s"));
                    }
                    PlotId id = utils.getPlot(b.getLocation());
                    plugin.addCooldownPlotBlock(id.getX() + ";" + id.getY(), b.getType(),
                            plugin.getPlotPlayers().get(p.getUniqueId()));

                } else if (plugin.getCheckers().contains(p.getUniqueId())) { // Player is a checker
                    
                    PlotId id = utils.getPlot(b.getLocation());
                    if (msg) {
                        if (plugin.isCooldown(b)) {
                            p.sendMessage(Lang.get("CHECKEDCOOLDOWN", plugin.getSetCooldown(b) / 20 + "s"));
                        } else if (plugin.hasCooldown(id.getX() + ";" + id.getY(), b.getType())) {
                            p.sendMessage(Lang.get("CHECKEDCOOLDOWN",
                                    plugin.getSetCooldown(b) / 20 + "s " + "(" + "Plot, " + b.getType().name() + ")"));
                        } else if (plugin.hasCooldown(id.getX() + ";" + id.getY())) {
                            p.sendMessage(Lang.get("CHECKEDCOOLDOWN",
                                    plugin.getSetCooldown(b) / 20 + "s " + "(" + "Plot" + ")"));
                        } else {
                            p.sendMessage(Lang.get("NOCOOLDOWN"));
                        }
                    }
                    event.setCancelled(true);
	        		
                } else if (plugin.getReseters().contains(p.getUniqueId())) { // Player is a reseter
                    
                    if (plugin.hasCooldown(b)) {
                        int time = plugin.getCooldown(b);
                        plugin.resetCooldown(b);
                        if (msg) {
                            p.sendMessage(Lang.get("COOLDOWNRESET", time + "s"));
                        }
                    } else if (msg) {
                        p.sendMessage(Lang.get("NOCOOLDOWN"));
                    }
                    event.setCancelled(true);

                } else if (plugin.getRemovers().containsKey(p.getUniqueId())) { // Player is a remover

                    if (plugin.isCooldown(b) || plugin.isBlockCooldown(b)) {
                        if (plugin.getRemovers().get(p.getUniqueId()).equals("click") && plugin.isCooldown(b)) { // Click remover
                            if (utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
                                if (msg) {
                                    p.sendMessage(Lang.get("PLOTACCESSERROR"));
                                }
                                return;
                            } else if (plugin.inAdminMode(p)) {
                                if (msg) {
                                    p.sendMessage(Lang.get("ADMINBYPASS"));
                                }
                            }
                            plugin.removeCooldownBlock(b);
                            plugin.resetCooldown(b); // Make this configurable?
                            if (msg) {
                                p.sendMessage(Lang.get("REMOVED", b.getX() + " " + b.getY() + " " + b.getZ() + " "
                                        + b.getType().toString() + " (" + plugin.getSetCooldown(b) + "s)"));
                            }
                        } else if (plugin.getRemovers().get(p.getUniqueId()).equals("block")
                                && plugin.isBlockCooldown(b)) { // Block remover
                                    if (utils.plotAccessLevel(p) < plugin.getMinimumAccess()
                                            && !plugin.inAdminMode(p)) {
                                        if (msg) {
                                            p.sendMessage(Lang.get("PLOTACCESSERROR"));
                                        }
                                        return;
                                    }
                                    if (plugin.inAdminMode(p)) {
                                        if (msg) {
                                            p.sendMessage(Lang.get("ADMINBYPASS"));
                                        }
                                    }
                                    String plotid = utils.getPlot(b.getLocation()).getX() + ";"
                                            + utils.getPlot(b.getLocation()).getY();
                                    if (msg) {
                                        p.sendMessage(Lang.get("REMOVED",
                                                b.getX() + " " + b.getY() + " " + b.getZ() + " "
                                                        + b.getType().toString() + " default ("
                                                        + plugin.getSetCooldown(b) + "s)"));
                                    }
                                    plugin.removeCooldownPlotBlock(plotid, b.getType());
                                }
                    } else {
                        if (msg) {
                            p.sendMessage(Lang.get("NOCOOLDOWN"));
                        }
                    }

                } else { // Basic click, not in any special click mode

                    plugin.debug("input click", p);
                    boolean ownPlot = utils.inOwnPlot(p);
                    boolean bypassOwn = ownPlot && plugin.bypassOnOwnPlot;
                    boolean bypassAll = plugin.getBypassers().contains(p.getUniqueId()) && plugin.inAdminMode(p);
                    boolean bypassOn = ownPlot && plugin.getBypassers().contains(p.getUniqueId());
                    if (plugin.hasCooldown(b)) {
                        event.setCancelled(true);
                        if (msg) {
                            p.sendMessage(Lang.get("COOLDOWN", plugin.getCooldown(b) + "s"));
                        }
                    } else if (bypassOwn || bypassAll || bypassOn) {
                        // TODO what was this supposed to be used for
                    } else {
                        plugin.cooldown(b, p);
                    }

                }
	        }
	    }
	}
	
}

package net.hraponssi.inputcooldown.main;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

	Main plugin;
	Utils utils;

	public Commands(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(command.equalsIgnoreCase("ic") || command.equalsIgnoreCase("inputcooldown")){
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Input cooldown commands can only be run by a player!");
				return true;
			}
			Player p = (Player) sender;
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("remove")) { //TODO improve this
					if(p.hasPermission("ic.user")){
						if(args.length>1) {
							if(args[1].equalsIgnoreCase("click")) {
								if(plugin.removers.containsKey(p)) plugin.removeRemover(p);
								plugin.setRemover(p, "click");
								p.sendMessage(Lang.get("REMOVERSET"));
							}else if(args[1].equalsIgnoreCase("block")) {
								if(plugin.removers.containsKey(p)) plugin.removeRemover(p);
								p.sendMessage(Lang.get("REMOVERSETTYPE"));
								plugin.setRemover(p, "block");
							}else if(args[1].equalsIgnoreCase("plot")) {
								if (utils.plotAccessLevel(p) < plugin.minimumAccess  && !plugin.inAdminMode(p)) {
									p.sendMessage(Lang.get("PLOTACCESSERROR"));
									return true;
								} else if(plugin.inAdminMode(p)) {
									p.sendMessage(Lang.get("ADMINBYPASS"));
								}
								if(!plugin.getPlotCooldowns(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY()).containsKey(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY())) {
									p.sendMessage(Lang.get("NOPLOTCOOLDOWN", "default"));
								}
								int num = plugin.getPlotCooldowns(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY()).get("DEFAULT");
								plugin.removeCooldownPlot(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY());
								p.sendMessage(Lang.get("REMOVED", utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY() + " Default (" + num + "s)"));
							}else if(args[1].equalsIgnoreCase("cancel")) {
								if(plugin.removers.containsKey(p)) plugin.removeRemover(p);
								p.sendMessage(Lang.get("REMOVERUNSET"));
							}
						}else {
							p.sendMessage(Lang.get("INVALIDFORMAT", "remove <click/block/plot/cancel>"));
						}
						return true;
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
						return true;
					}
				} if(args[0].equalsIgnoreCase("reset")) {
					if(p.hasPermission("ic.user")) {
						if(plugin.reseters.contains(p)) {
							p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.RED + "off"));
							plugin.reseters.remove(p);
							return true;
						}else {
							p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.GREEN + "on"));
							plugin.reseters.add(p);
							return true;
						}
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
						return true;
					}
				} else if(args[0].equalsIgnoreCase("unset")) {
					if(p.hasPermission("ic.user")) {
						plugin.removePlayer(p);
						p.sendMessage(Lang.get("NOLONGERSETTER"));
						return true;
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
						return true;
					}
				}else if(args[0].equalsIgnoreCase("set")) { //TODO perm check
					if(args.length<2) {
						p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
						return true;
					} else if(args.length<3) {
						p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
						return true;
					}
					if(!args[1].equalsIgnoreCase("click") && !args[1].equalsIgnoreCase("block") && !args[1].equalsIgnoreCase("plot")) {
						p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
						return true;
					}
					if(utils.isInteger(args[2])) {
						int num = Integer.parseInt(args[2]);
						if(num == 0) {
							plugin.removePlayer(p);
							p.sendMessage(Lang.get("NOLONGERSETTER"));
							return true;
						}
						if(num > plugin.maxTime || num < plugin.minTime) {
							if(num > plugin.maxTime) p.sendMessage(Lang.get("MAXCOOLDOWNERROR", plugin.maxTime + "s"));
							if(num < plugin.minTime) p.sendMessage(Lang.get("MINCOOLDOWNERROR", plugin.minTime + "s"));
							return true;
						}
						if(args[1].equalsIgnoreCase("click")) {
							plugin.setPlayer(p, num*20);
							p.sendMessage(Lang.get("NOWSETTING", num + "s"));
						}else if(args[1].equalsIgnoreCase("block")) {
							p.sendMessage(Lang.get("SETTINGPLOTBLOCKCOOLDOWN", num + "s"));
							plugin.setPlotPlayer(p, num*20);
						}else if(args[1].equalsIgnoreCase("plot")) {
							if(utils.plotAccessLevel(p) < plugin.minimumAccess  && !plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("PLOTACCESSERROR"));
								return true;
							}else if(plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("ADMINBYPASS"));
							}
							if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.maxPlotCooldowns && plugin.maxPlotCooldowns > -1) {
								p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.maxPlotCooldowns));
								return true;
							}
							plugin.addCooldownPlot(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY(), num*20);
							p.sendMessage(Lang.get("SETCOOLDOWN", num + "s"));
						}
					}else {
						p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
						return true;
					}
					return true;
				} else if(args[0].equalsIgnoreCase("reload")) {
					if(p.hasPermission("ic.reload")) {
						plugin.reloadCfg();
						p.sendMessage(Lang.get("CONFIGRELOADED"));
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
					}
					return true;
				} else if(args[0].equalsIgnoreCase("admin")) {
					if(p.hasPermission("ic.admin")) {
						if(plugin.toggleAdmin(p)) {
							p.sendMessage(Lang.get("ADMINENABLED"));
						}else {
							p.sendMessage(Lang.get("ADMINDISABLED"));
						}
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
					}
					return true;
				} else if(args[0].equalsIgnoreCase("list")) {
					if(p.hasPermission("ic.user")) {
						if(utils.getPlot(p) != null) {
							if(utils.plotAccessLevel(p) < plugin.minimumAccess && !plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("PLOTACCESSERROR"));
								return true;
							}else if(plugin.inAdminMode(p)) {
								p.sendMessage(Lang.get("ADMINBYPASS"));
							}
							String plotID = utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY();
							p.sendMessage(Lang.get("PLOTCOOLDOWNLIST", plotID));
							for(Entry<String, Integer> entry : plugin.getPlotCooldowns(plotID).entrySet()) {
								p.sendMessage(ChatColor.GREEN + entry.getKey().toLowerCase() + ChatColor.GRAY +" - " + ChatColor.GREEN + entry.getValue()/20 + "s");
							}
						}else {
							p.sendMessage(Lang.get("PLOTERROR"));
						}
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
					}
					return true;
				} else if(args[0].equalsIgnoreCase("check")) {
					if(p.hasPermission("ic.user")) {
						if(!plugin.checkers.contains(p)) {
							plugin.checkers.add(p);
							p.sendMessage(Lang.get("CHECKINGCOOLDOWNS"));
						}else {
							plugin.checkers.remove(p);
							p.sendMessage(Lang.get("NOTCHECKINGCOOLDOWNS"));
						}
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
					}
					return true;
				} else {
					p.sendMessage(Lang.get("INVALIDARGUMENTS"));
					return true;
				}
			}else {
				p.sendMessage(Lang.get("TITLE"));
				p.sendMessage("remove");
				p.sendMessage("reset");
				p.sendMessage("unset");
				p.sendMessage("set");
				p.sendMessage("reload");
				p.sendMessage("admin");
				p.sendMessage("list");
				p.sendMessage("check");
				if(utils.plotAccessLevel(p) >= plugin.minimumAccess && !plugin.inAdminMode(p)) {
					p.sendMessage("you own that plot.");
				}else {
					p.sendMessage(Lang.get("PLOTACCESSERROR"));
				}
				if(utils.getPlot(p) != null) {
					p.sendMessage("plot id: " + utils.getPlot(p).getX() + "," + utils.getPlot(p).getY());
				}else {
					p.sendMessage("Invalid plot");
				}
				return true;
			}
		}else {
			return false;
		}
	}

}
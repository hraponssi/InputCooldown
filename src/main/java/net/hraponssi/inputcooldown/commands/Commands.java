package net.hraponssi.inputcooldown.commands;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.hraponssi.inputcooldown.main.Lang;
import net.hraponssi.inputcooldown.main.Main;
import net.hraponssi.inputcooldown.main.Utils;
import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

	Main plugin;
	Utils utils;

	public Commands(Main plugin) {
		super();
		this.plugin = plugin;
		this.utils = new Utils(plugin.getPSquared());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		String scmd = cmd.getName();
		if(scmd.equalsIgnoreCase("ic") || scmd.equalsIgnoreCase("inputcooldown")){
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
								if(plugin.getRemovers().containsKey(p.getUniqueId())) plugin.removeRemover(p);
								plugin.setRemover(p, "click");
								p.sendMessage(Lang.get("REMOVERSET"));
							}else if(args[1].equalsIgnoreCase("block")) {
								if(plugin.getRemovers().containsKey(p.getUniqueId())) plugin.removeRemover(p);
								p.sendMessage(Lang.get("REMOVERSETTYPE"));
								plugin.setRemover(p, "block");
							}else if(args[1].equalsIgnoreCase("plot")) {
								if (utils.plotAccessLevel(p) < plugin.getMinimumAccess()  && !plugin.inAdminMode(p)) {
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
								if(plugin.getRemovers().containsKey(p.getUniqueId())) plugin.removeRemover(p);
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
						if(plugin.getReseters().contains(p.getUniqueId())) {
							p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.RED + "off"));
							plugin.getReseters().remove(p.getUniqueId());
							return true;
						}else {
							p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.GREEN + "on"));
							plugin.getReseters().add(p.getUniqueId());
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
				}else if(args[0].equalsIgnoreCase("set")) {
					if(p.hasPermission("ic.user")) {
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
							if(num > plugin.getMaxTime() || num < plugin.getMinTime()) {
								if(num > plugin.getMaxTime()) p.sendMessage(Lang.get("MAXCOOLDOWNERROR", plugin.getMaxTime() + "s"));
								if(num < plugin.getMinTime()) p.sendMessage(Lang.get("MINCOOLDOWNERROR", plugin.getMinTime() + "s"));
								return true;
							}
							if(args[1].equalsIgnoreCase("click")) {
								plugin.setPlayer(p, num*20);
								p.sendMessage(Lang.get("NOWSETTING", num + "s"));
							}else if(args[1].equalsIgnoreCase("block")) {
								p.sendMessage(Lang.get("SETTINGPLOTBLOCKCOOLDOWN", num + "s"));
								plugin.setPlotPlayer(p, num*20);
							}else if(args[1].equalsIgnoreCase("plot")) {
								if(utils.plotAccessLevel(p) < plugin.getMinimumAccess()  && !plugin.inAdminMode(p)) {
									p.sendMessage(Lang.get("PLOTACCESSERROR"));
									return true;
								}else if(plugin.inAdminMode(p)) {
									p.sendMessage(Lang.get("ADMINBYPASS"));
								}
								if(plugin.plotCooldownCount(utils.toStringId(utils.getPlot(p))) >= plugin.getMaxPlotCooldowns() && plugin.getMaxPlotCooldowns() > -1) {
									p.sendMessage(Lang.get("MAXCOOLDOWNCOUNT", "" + plugin.getMaxPlotCooldowns()));
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
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
						return true;
					}
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
							if(utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
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
						if(!plugin.getCheckers().contains(p.getUniqueId())) {
							plugin.getCheckers().add(p.getUniqueId());
							p.sendMessage(Lang.get("CHECKINGCOOLDOWNS"));
						}else {
							plugin.getCheckers().remove(p.getUniqueId());
							p.sendMessage(Lang.get("NOTCHECKINGCOOLDOWNS"));
						}
					}else {
						p.sendMessage(Lang.get("NOPERMISSION"));
					}
					return true;
				} else if(args[0].equalsIgnoreCase("debug")) {
					if(p.hasPermission("ic.admin")) {
						if(plugin.toggleDebug(p)) {
							p.sendMessage(Lang.getChatPrefix() + ChatColor.YELLOW + " Debug enabled.");
						}else {
							p.sendMessage(Lang.getChatPrefix() + ChatColor.YELLOW + " Debug disabled.");
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
				p.sendMessage(ChatColor.GREEN + "/ic remove <> - Remove input cooldowns");
				p.sendMessage(ChatColor.GREEN + "/ic set <> - Set input cooldowns");
				p.sendMessage(ChatColor.GREEN + "/ic unset - Stop setting input cooldowns");
				p.sendMessage(ChatColor.GREEN + "/ic reset - Reset input cooldowns");
				p.sendMessage(ChatColor.GREEN + "/ic list - List input cooldowns on a plot");
				p.sendMessage(ChatColor.GREEN + "/ic check - Check an input for a cooldown");
				if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic admin - Toggle admin mode");
				if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic reload - Reload the config & lang file");
				if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic debug - Toggle debug messages");
				plugin.debug("Debug plot info:", p);
				if(utils.plotAccessLevel(p) >= plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
					plugin.debug(ChatColor.GREEN + "You can access this plot", p);
				}else {
					plugin.debug(ChatColor.RED + "You cannot access this plot", p);
				}
				if(utils.getPlot(p) != null) {
					plugin.debug("plot id: " + utils.getPlot(p).getX() + "," + utils.getPlot(p).getY(), p);
				}else {
					plugin.debug("That plot is invalid", p);
				}
				return true;
			}
		}else {
			return false;
		}
	}

}

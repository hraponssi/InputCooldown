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
            if (args.length == 0) {
                sendHelp(p);
                return true;
            }
            switch(args[0].toLowerCase()) {
            case "remove":
                if(p.hasPermission("ic.user")){
                    if(args.length>1) {
                        switch(args[1].toLowerCase()) {
                        case "click":
                            plugin.removePlayer(p);
                            plugin.setRemover(p, "click");
                            p.sendMessage(Lang.get("REMOVERSET"));
                            break;
                        case "block":
                            plugin.removePlayer(p);
                            p.sendMessage(Lang.get("REMOVERSETTYPE"));
                            plugin.setRemover(p, "block");
                            break;
                        case "plot":
                            if (utils.plotAccessLevel(p) < plugin.getMinimumAccess() && !plugin.inAdminMode(p)) {
                                p.sendMessage(Lang.get("PLOTACCESSERROR"));
                                return true;
                            } else if(plugin.inAdminMode(p)) {
                                p.sendMessage(Lang.get("ADMINBYPASS"));
                            }
                            if(!plugin.getPlotCooldowns(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY()).containsKey("DEFAULT")) {
                                p.sendMessage(Lang.get("NOPLOTCOOLDOWN", "default"));
                                return true;
                            }
                            plugin.removePlayer(p);
                            int num = plugin.getPlotCooldowns(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY()).get("DEFAULT");
                            plugin.removeCooldownPlot(utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY());
                            p.sendMessage(Lang.get("REMOVED", utils.getPlot(p).getX() + ";" + utils.getPlot(p).getY() + " Default (" + num/20 + "s)"));
                            break;
                        case "cancel":
                            plugin.removePlayer(p);
                            p.sendMessage(Lang.get("REMOVERUNSET"));
                            break;
                        default:
                            p.sendMessage(Lang.get("INVALIDFORMAT", "remove <click/block/plot/cancel>"));
                            break;
                        }
                    }else {
                        p.sendMessage(Lang.get("INVALIDFORMAT", "remove <click/block/plot/cancel>"));
                    }
                    return true;
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                    return true;
                }
            case "reset":
                if(p.hasPermission("ic.user")) {
                    if(plugin.getReseters().contains(p.getUniqueId())) {
                        p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.RED + "off"));
                        plugin.getReseters().remove(p.getUniqueId());
                        return true;
                    }else {
                        p.sendMessage(Lang.get("RESETERTOGGLE", ChatColor.GREEN + "on"));
                        plugin.removePlayer(p);
                        plugin.getReseters().add(p.getUniqueId());
                        return true;
                    }
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                    return true;
                }
            case "cancel":
            case "unset":
                if(p.hasPermission("ic.user")) {
                    plugin.removePlayer(p);
                    p.sendMessage(Lang.get("NOLONGERSETTER"));
                    return true;
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                    return true;
                }
            case "set":
                if(p.hasPermission("ic.user")) {
                    if(args.length<2) {
                        p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
                        return true;
                    } else if(args.length<3) {
                        p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
                        return true;
                    }
                    if(utils.isInteger(args[2]) || (args[2].length() >= 2 && utils.isInteger(args[2].substring(0, args[2].length()-1)))) {
                        int num = -1;
                        if (args[2].length() >= 2 && utils.isInteger(args[2].substring(0, args[2].length()-1)) && !utils.isInteger(args[2])) {
                            String multiplier = args[2].substring(args[2].length()-1, args[2].length());
                            if(multiplier.equals("s")) { //seconds
                                num = Integer.parseInt(args[2].substring(0, args[2].length()-1))*1;
                            } else if(multiplier.equals("m")) { //minutes
                                num = Integer.parseInt(args[2].substring(0, args[2].length()-1))*60;
                            } else if(multiplier.equals("h")) { //hours
                                num = Integer.parseInt(args[2].substring(0, args[2].length()-1))*3600;
                            } else { //invalid
                                p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
                                return true;
                            }
                        }else {
                            num = Integer.parseInt(args[2]);
                        }
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
                        plugin.removePlayer(p);
                        switch(args[1].toLowerCase()) {
                        case "click":
                            plugin.setPlayer(p, num*20);
                            p.sendMessage(Lang.get("NOWSETTING", num + "s"));
                            break;
                        case "block":
                            p.sendMessage(Lang.get("SETTINGPLOTBLOCKCOOLDOWN", num + "s"));
                            plugin.setPlotPlayer(p, num*20);
                            break;
                        case "plot":
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
                            break;
                        default:
                            p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
                            break;
                        }
                        return true;
                    }else {
                        p.sendMessage(Lang.get("INVALIDFORMAT", "set <click/block/plot> <cooldown in seconds>"));
                        return true;
                    }
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                    return true;
                }
            case "reload":
                if(p.hasPermission("ic.reload")) {
                    plugin.reloadCfg();
                    p.sendMessage(Lang.get("CONFIGRELOADED"));
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                }
                return true;
            case "admin":
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
            case "list":
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
            case "check":
                if(p.hasPermission("ic.user")) {
                    if(!plugin.getCheckers().contains(p.getUniqueId())) {
                        plugin.removePlayer(p);
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
            case "debug":
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
            case "bypass":
                if(p.hasPermission("ic.user")) {
                    if(plugin.bypassOnOwnPlot) {
                        p.sendMessage(Lang.get("ALREADYBYPASSING"));
                        return true;
                    }
                    if(!plugin.getBypassers().contains(p.getUniqueId())) {
                        plugin.addBypasser(p);
                        p.sendMessage(Lang.get("BYPASSINGOWNPLOT"));
                    }else {
                        plugin.removeBypasser(p);
                        p.sendMessage(Lang.get("NOTBYPASSINGOWNPLOT"));
                    }
                }else {
                    p.sendMessage(Lang.get("NOPERMISSION"));
                }
                return true;
            default:
                p.sendMessage(Lang.get("INVALIDARGUMENTS"));
                return true;
            }
        }else {
            return false;
        }
    }

    public void sendHelp(Player p) {
        p.sendMessage(Lang.get("TITLE"));
        p.sendMessage(ChatColor.GREEN + "/ic remove <type> " + ChatColor.GRAY + "- Remove input cooldowns");
        p.sendMessage(ChatColor.GREEN + "/ic set <type> <seconds> " + ChatColor.GRAY + "- Set input cooldowns");
        p.sendMessage(ChatColor.GREEN + "/ic unset/cancel " + ChatColor.GRAY + "- Cancel an action (setting, removing)");
        p.sendMessage(ChatColor.GREEN + "/ic reset " + ChatColor.GRAY + "- Reset input cooldowns");
        p.sendMessage(ChatColor.GREEN + "/ic list " + ChatColor.GRAY + "- List input cooldowns on a plot");
        p.sendMessage(ChatColor.GREEN + "/ic check " + ChatColor.GRAY + "- Check an input for a cooldown");
        if(!plugin.bypassOnOwnPlot) {
            p.sendMessage(ChatColor.GREEN + "/ic bypass " + ChatColor.GRAY + "- Bypass cooldowns on your plot");
        }
        if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic admin " + ChatColor.GRAY + "- Toggle admin mode");
        if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic reload " + ChatColor.GRAY + "- Reload the config & lang file");
        if(p.hasPermission("ic.admin")) p.sendMessage(ChatColor.GREEN + "/ic debug " + ChatColor.GRAY + "- Toggle debug messages");
        if(plugin.cmdUnset) {
            p.sendMessage(Lang.get("UNSETWITHCMD"));
            if(plugin.getPlayers().containsKey(p.getUniqueId()) || plugin.getPlotPlayers().containsKey(p.getUniqueId())) {
                plugin.removePlayer(p);
                p.sendMessage(Lang.get("NOLONGERSETTER"));
            }
        }
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
    }

}

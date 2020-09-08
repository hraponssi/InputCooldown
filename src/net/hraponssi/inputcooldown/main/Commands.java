package net.hraponssi.inputcooldown.main;

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
				if(args[0].equalsIgnoreCase("remove")) {
					plugin.removePlayer(p);
					p.sendMessage("remove from setters");
					if(p.hasPermission("ic.admin")) {
						return true;
					}else {
						return true;
					}
				} else if(args[0].equalsIgnoreCase("set")) {
					if(args.length<2) {
						p.sendMessage("Wrong format");
						return true;
					}
					if(utils.isInteger(args[1])) {
						int num = Integer.parseInt(args[1]);
						plugin.setPlayer(p, num*20);
					}else {
						p.sendMessage("Wrong format");
						return true;
					}
					return true;
				} else {
					p.sendMessage(ChatColor.RED + "Invalid arguments!");
					return true;
				}
			}else {
				p.sendMessage("test running");
				if(utils.inOwnPlot(p)) {
					p.sendMessage("you own that plot.");
				}else {
					p.sendMessage("not your plot");
				}
				return true;
			}
		}else {
			return false;
		}
	}
	
}

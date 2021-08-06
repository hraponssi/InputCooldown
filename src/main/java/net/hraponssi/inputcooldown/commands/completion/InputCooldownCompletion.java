package net.hraponssi.inputcooldown.commands.completion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class InputCooldownCompletion implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args){

		List<String> completions = new ArrayList<>();
		Player p = (Player) sender;
		if(args.length == 1){
			completions.add("remove");
			completions.add("set");
			completions.add("unset");
			completions.add("cancel");
			completions.add("reset");
			completions.add("list");
			completions.add("check");
			completions.add("help");
			completions.add("bypass");
			if(p.hasPermission("ic.admin")) completions.add("admin");
			if(p.hasPermission("ic.admin")) completions.add("reload");
			return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("set")) {
				completions.add("click");
				completions.add("block");
				completions.add("plot");
			} else if(args[0].equalsIgnoreCase("remove")) {
				completions.add("click");
				completions.add("block");
				completions.add("plot");
				completions.add("cancel");
			}
			return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
		}
		
		return null;
	}

}

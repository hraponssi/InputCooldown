package net.hraponssi.inputcooldown.main;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

public class Lang {
	
	public static HashMap<String, String> langs = new HashMap<>();
	
	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&',get("PREFIX"));
	}
	
	public static String get(String name) {
		if(langs.containsKey(name)) {
			String msg = langs.get(name);
			if(msg.contains("%prefix%")) msg = msg.replaceAll("%prefix%", get("CHATPREFIX"));
			return ChatColor.translateAlternateColorCodes('&', msg);
		}
		return null;
	}
	
	public static String get(String name, String arg) {
		if(langs.containsKey(name)) {
			String msg = langs.get(name);
			if(msg.contains("%prefix%")) msg = msg.replaceAll("%prefix%", get("CHATPREFIX"));
			msg = msg.replaceAll("%args%", arg);
			return ChatColor.translateAlternateColorCodes('&', msg);
		}
		return null;
	}

	public static void setMap(HashMap<String, String> strings) {
		langs = strings;
	}
	
}

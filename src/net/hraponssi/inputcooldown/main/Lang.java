package net.hraponssi.inputcooldown.main;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

public class Lang {

	public static String prefix = "&aInput&eCooldown";
	public static String chatPrefix = "&aInput&eCooldown &7>";
	public static String nolongerSetter = "%prefix% &ayou are no longer setting a cooldown!";
	public static String invalidFormat = "%prefix% &cinvalid format, use %args%!";
	public static String nowSetting = "%prefix% &ayou are now setting a cooldown of %args%!";
	public static String plotError = "%prefix% &cYou can't set cooldowns on that plot!";
	public static String invalidArguments = "%prefix% &cInvalid arguments!";
	public static String noPermission = "%prefix% &cYou are lacking permissions for that.";
	public static String configReloaded = "%prefix% &aThe config has been reloaded.";
	public static String checkingCooldowns = "%prefix% &You are now checking cooldowns you click.";
	public static String checkedCooldown = "%prefix% &That input has a cooldown of %args%.";
	public static String noCooldown = "%prefix% &That input doesn't have a cooldown.";
	public static String notCheckingCooldowns = "%prefix% &aYou are no longer checking cooldowns you click.";
	
	public static HashMap<String, String> langs = new HashMap<>();
	
	public static void reload() {
		langs.put("PREFIX", prefix);
		langs.put("CHATPREFIX", chatPrefix);
		langs.put("NOLONGERSETTER", nolongerSetter);
		langs.put("INVALIDFORMAT", invalidFormat);
		langs.put("NOWSETTING", nowSetting);
		langs.put("PLOTERROR", plotError);
		langs.put("INVALIDARGUMENTS", invalidArguments);
		langs.put("NOPERMISSION", noPermission);
		langs.put("CONFIGRELOADED", configReloaded);
		langs.put("CHECKINGCOOLDOWNS", checkingCooldowns);
		langs.put("CHECKEDCOOLDOWN", checkedCooldown);
		langs.put("NOCOOLDOWN", noCooldown);
		langs.put("NOTCHECKINGCOOLDOWNS", notCheckingCooldowns);
	}
	
	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}
	
	public static String get(String name) {
		if(langs.containsKey(name)) {
			String msg = langs.get(name);
			msg = msg.replaceAll("%prefix%", chatPrefix);
			return ChatColor.translateAlternateColorCodes('&', msg);
		}
		return null;
	}
	
	public static String get(String name, String arg) {
		if(langs.containsKey(name)) {
			String msg = langs.get(name);
			msg = msg.replaceAll("%prefix%", chatPrefix);
			msg = msg.replaceAll("%args%", arg);
			return ChatColor.translateAlternateColorCodes('&', msg);
		}
		return null;
	}
	
}

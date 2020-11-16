package net.hraponssi.inputcooldown.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.world.PlotAreaManager;

import net.md_5.bungee.api.ChatColor;

public class Utils {

	public final static Collection<Material> inputMaterials = new ArrayList<>();
	static {
		inputMaterials.add(Material.STONE_BUTTON);
		inputMaterials.add(Material.POLISHED_BLACKSTONE_BUTTON);
		inputMaterials.add(Material.OAK_BUTTON);
		inputMaterials.add(Material.BIRCH_BUTTON);
		inputMaterials.add(Material.JUNGLE_BUTTON);
		inputMaterials.add(Material.SPRUCE_BUTTON);
		inputMaterials.add(Material.DARK_OAK_BUTTON);
		inputMaterials.add(Material.ACACIA_BUTTON);
		inputMaterials.add(Material.WARPED_BUTTON);
		inputMaterials.add(Material.CRIMSON_BUTTON);
		inputMaterials.add(Material.LEVER);
	}

	public boolean inOwnPlot(Player p) { //TODO add config option to choose allowing trusted or members to add
		PlotPlayer<?> player = PlotPlayer.wrap(p);
		Plot plot = player.getCurrentPlot();
		if(plot == null) return false;
		if(plot.hasOwner()) if(plot.getOwner().equals(p.getUniqueId())) {
			return true;
		}
		return false;
	}
	
	public int plotAccessLevel(Player p) {
		PlotPlayer<?> player = PlotPlayer.wrap(p);
		Plot plot = player.getCurrentPlot();
		UUID pUUID = p.getUniqueId();
		if(plot == null || player == null || pUUID == null) return 99;
		if(inOwnPlot(p)) return 3;
		if(plot.getTrusted().contains(pUUID)) return 2;
		if(plot.getMembers().contains(pUUID)) return 1;
		return 0;
	}
	
	public PlotId getPlot(Player p) {
		PlotPlayer<?> player = PlotPlayer.wrap(p);
		Plot plot = player.getCurrentPlot();
		if(plot == null) return null;
		return plot.getId();
	}
	
	public PlotId getPlot(Location l) {
		com.plotsquared.core.location.Location pl = new com.plotsquared.core.location.Location(l.getWorld().getName(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
		if(pl.getPlot() == null) return null;
		return pl.getPlot().getId();
	}

	public String toStringId(PlotId id) {
		return id.getX() + ";" + id.getY();
	}
	
	public boolean isInteger(String s) { //TODO dont acccept negative numbers
		boolean isValidInteger = false;
		try
		{
			Integer.parseInt(s);
			isValidInteger = true;
		}
		catch (NumberFormatException ex)
		{

		}
		return isValidInteger;
	}

	public boolean isInput(Material mat) {
		return inputMaterials.contains(mat);
	}

	public int toInt(String s) {
		return Integer.parseInt(s);
	}
	
	public World getWorld(String s) {
		return Bukkit.getWorld(s);
	}
	
	public Location newLocation(int x, int y, int z, World world) {
		return new Location(world, x, y, z);
	}
	
}

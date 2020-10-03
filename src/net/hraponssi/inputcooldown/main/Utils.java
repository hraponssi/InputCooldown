package net.hraponssi.inputcooldown.main;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

public class Utils {

	private static Collection<Material> inputMaterials = new ArrayList<>();
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

	public boolean inOwnPlot(Player p) {
		PlotPlayer<?> player = PlotPlayer.wrap(p);
		Plot plot = player.getCurrentPlot();
		if(plot == null) return false;
		if(plot.hasOwner()) if(plot.getOwner().equals(p.getUniqueId())) {
			return true;
		}
		return false;
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

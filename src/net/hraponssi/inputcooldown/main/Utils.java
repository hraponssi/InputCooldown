package net.hraponssi.inputcooldown.main;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

public class Utils {

	private static Collection<Material> inputMaterials = new ArrayList<>();
	static {
		inputMaterials.add(Material.STONE_BUTTON);
		inputMaterials.add(Material.OAK_BUTTON);
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

}

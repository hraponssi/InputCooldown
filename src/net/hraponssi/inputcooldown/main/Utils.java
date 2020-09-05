package net.hraponssi.inputcooldown.main;

import org.bukkit.entity.Player;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

public class Utils {

	public boolean inOwnPlot(Player p) {
		PlotPlayer<?> player = PlotPlayer.wrap(p);
		Plot plot = player.getCurrentPlot();
		if(plot == null) return false;
		if(plot.hasOwner()) if(plot.getOwner().equals(p.getUniqueId())) {
			return true;
		}
		return false;
	}
	
}

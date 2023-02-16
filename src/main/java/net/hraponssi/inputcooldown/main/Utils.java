package net.hraponssi.inputcooldown.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import com.sk89q.worldedit.math.BlockVector3;

public class Utils {

    boolean pSquared = false;

    public Utils(boolean p) {
        this.pSquared = p;
    }

    public final static Collection<String> inputEnds = new ArrayList<>();
    static {
        inputEnds.add("_button");
        inputEnds.add("_plate");
        inputEnds.add("_pressure_plate");
    }

    public boolean inOwnPlot(Player p) {
        PlotPlayer<?> player = PlotSquared.platform().playerManager().getPlayer(p.getUniqueId());
        Plot plot = player.getCurrentPlot();
        if (plot == null) return false;
        if (plot.hasOwner()) if (plot.getOwner().equals(p.getUniqueId())) {
            return true;
        }
        return false;
    }

    public int plotAccessLevel(Player p) {
        PlotPlayer<?> player = PlotSquared.platform().playerManager().getPlayer(p.getUniqueId());
        Plot plot = player.getCurrentPlot();
        UUID pUUID = p.getUniqueId();
        if (plot == null || player == null || pUUID == null) return 99;
        if (inOwnPlot(p)) return 3;
        if (plot.getTrusted().contains(pUUID)) return 2;
        if (plot.getMembers().contains(pUUID)) return 1;
        return 0;
    }

    public PlotId getPlot(Player p) {
        PlotPlayer<?> player = PlotSquared.platform().playerManager().getPlayer(p.getUniqueId());
        Plot plot = player.getCurrentPlot();
        if (plot == null) return null;
        return plot.getId();
    }

    public PlotId getPlot(Location l) {
        com.plotsquared.core.location.Location pl = com.plotsquared.core.location.Location.at(l.getWorld().getName(),
                BlockVector3.at(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        if (pl.getPlot() == null) return null;
        return pl.getPlot().getId();
    }

    public String toStringId(PlotId id) {
        return id.getX() + ";" + id.getY();
    }

    public boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s);
            isValidInteger = true;
        } catch (NumberFormatException ex) {

        }
        return isValidInteger;
    }

    public boolean isInput(Material mat) {
        boolean input = false;
        if (mat.name().toLowerCase() == "lever") input = true;
        for (String ending : inputEnds) {
            if (mat.name().toLowerCase().endsWith(ending)) input = true;
        }
        return input;
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

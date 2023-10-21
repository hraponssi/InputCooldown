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

    private final static Collection<String> inputEnds = new ArrayList<>();
    static {
        inputEnds.add("_button");
        inputEnds.add("_plate");
        inputEnds.add("_pressure_plate");
    }

    /** 
     * Check if a player is in their own plot
     * 
     * @param player to check
     * @return whether the player is in their own plot
     */
    public boolean inOwnPlot(Player player) { //TODO check references for what location this is used with, so plot borders cant be cheated
        PlotPlayer<?> pPlayer = PlotSquared.platform().playerManager().getPlayer(player.getUniqueId());
        Plot plot = pPlayer.getCurrentPlot();
        if (plot == null) return false;
        if (plot.hasOwner() && plot.getOwner().equals(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    /** 
     * Check a player's plot access level in the plot they currently stand in
     * 
     * @return 
     * <ul>
     * <li>99 = encountered a null value
     * <li>3 = own plot
     * <li>2 = trusted
     * <li>1 = member
     * </ul>
     * 
     * @NotNull
     * @param player to check
    */
    public int plotAccessLevel(Player player) { //TODO check what location is interacted with, so plot borders cant be cheated
        PlotPlayer<?> pPlayer = PlotSquared.platform().playerManager().getPlayer(player.getUniqueId());
        Plot plot = pPlayer.getCurrentPlot();
        UUID pUUID = player.getUniqueId();
        if (plot == null || pPlayer == null || pUUID == null) return 99;
        if (inOwnPlot(player)) return 3;
        if (plot.getTrusted().contains(pUUID)) return 2;
        if (plot.getMembers().contains(pUUID)) return 1;
        return 0;
    }

    /**
     * Get the plot a player is standing in
     * 
     * @param player to check
     * @return PlotId
     */
    public PlotId getPlot(Player player) { //TODO either deprecate or rename, can lead to oversight of player not standing in input location's plot
        PlotPlayer<?> pPlayer = PlotSquared.platform().playerManager().getPlayer(player.getUniqueId());
        Plot plot = pPlayer.getCurrentPlot();
        if (plot == null) return null;
        return plot.getId();
    }

    /**
     * Get the plot at a location
     * 
     * @param location to check
     * @return PlotId
     */
    public PlotId getPlot(Location location) {
        com.plotsquared.core.location.Location pl = com.plotsquared.core.location.Location.at(location.getWorld().getName(),
                BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (pl.getPlot() == null) return null;
        return pl.getPlot().getId();
    }

    /**
     * Check if a string is an int
     * 
     * @param string to check
     * @return whether the string is an integer
     */
    public boolean isInt(String string) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(string);
            isValidInteger = true;
        } catch (NumberFormatException ex) {

        }
        return isValidInteger;
    }

    /**
     * Check if a material is an input type
     * 
     * @param material to check
     * @return whether the material is an input type
     */
    public boolean isInput(Material material) {
        boolean input = false;
        if (material.name().toLowerCase().equals("lever")) input = true;
        for (String ending : inputEnds) {
            if (material.name().toLowerCase().endsWith(ending)) input = true;
        }
        return input;
    }

}

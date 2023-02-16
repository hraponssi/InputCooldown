package net.hraponssi.inputcooldown.main;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Cooldown {

    public Cooldown(Integer time, Location loc, Player p) {
        this.time = time;
        this.loc = loc;
        this.user = p;
    }

    Player user;
    Location loc;
    int age, time;

}

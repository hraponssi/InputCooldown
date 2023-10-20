package net.hraponssi.inputcooldown.main;

import java.util.UUID;

import org.bukkit.Location;

public class Cooldown {

    public Cooldown(Integer time, Location loc, UUID uuid) {
        this.time = time;
        this.loc = loc;
        this.userUUID = uuid;
    }

    UUID userUUID;
    Location loc;
    int age, time;

}

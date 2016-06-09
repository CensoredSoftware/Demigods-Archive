package com.WildAmazing.marinating.Demigods.Utilities;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class WriteLocation implements Serializable {

    private static final long serialVersionUID = 8201132625259394712L;

    int X, Y, Z;
    String WORLD;

    public WriteLocation(String world, int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
        WORLD = world;
    }

    public String getWorld() {
        return WORLD;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getZ() {
        return Z;
    }

    public Location toLocationNewWorld(World w) {
        return new Location(w, X, Y, Z);
    }
}

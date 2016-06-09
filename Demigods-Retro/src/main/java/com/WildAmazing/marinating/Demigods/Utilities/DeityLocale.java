package com.WildAmazing.marinating.Demigods.Utilities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Lawrence
 *         Stores an area as a set of Locations and the Deity associated with it.
 */
public class DeityLocale implements Serializable {
    private static final long serialVersionUID = 1995559454583141372L;

    ArrayList<Cuboid> LOCS;
    Divine DEITY;

    public DeityLocale(Divine d) {
        LOCS = new ArrayList<Cuboid>();
        DEITY = d;
    }

    public DeityLocale(Divine d, ArrayList<Cuboid> aw) {
        LOCS = aw;
        DEITY = d;
    }

    public void addToLocale(Cuboid c) {
        LOCS.add(c);
    }

    public void addToLocale(ArrayList<Cuboid> c) {
        for (Cuboid w : c) {
            if (!LOCS.contains(w)) LOCS.add(w);
        }
    }

    public void removeCuboid(Cuboid c) {
        if (LOCS.contains(c)) {
            LOCS.remove(LOCS.indexOf(c));
        }
    }

    public ArrayList<Cuboid> getLocale() {
        return LOCS;
    }

    public Divine getDeity() {
        return DEITY;
    }
}

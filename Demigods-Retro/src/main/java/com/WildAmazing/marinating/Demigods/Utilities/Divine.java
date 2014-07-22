package com.WildAmazing.marinating.Demigods.Utilities;

import java.io.Serializable;

public enum Divine implements Serializable {
    APOLLO(true, false), ARES(true, false), ATHENA(true, false), HADES(true, true), HEPHAESTUS(true, false), POSEIDON(true, true), ZEUS(true, true), HYPERION(false, false), CRONUS(false, true), OCEANUS(false, false), RHEA(false, true), STYX(false, false), TYPHON(false, false), OMNI(false, false), PROMETHEUS(false, true), GOD(true, false), TITAN(false, false);

    private boolean god;
    private boolean tier1;

    private Divine(boolean god, boolean tier1) {
        this.god = god;
        this.tier1 = tier1;
    }

    public boolean isGod() {
        return god;
    }

    public boolean isTier1() {
        return tier1;
    }
}

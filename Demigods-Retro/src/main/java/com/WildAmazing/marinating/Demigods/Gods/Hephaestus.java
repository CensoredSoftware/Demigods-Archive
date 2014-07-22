package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;

import java.util.UUID;

public class Hephaestus extends Deity {
    /**
     *
     */
    private static final long serialVersionUID = 1836286842611372087L;
    private boolean OUTFIT = false;

    public Hephaestus(UUID p) {
        super(Divine.HEPHAESTUS, p);
    }

    public boolean getOutfit() {
        return OUTFIT;
    }

    public void setOutfit(boolean b) {
        OUTFIT = b;
    }
}
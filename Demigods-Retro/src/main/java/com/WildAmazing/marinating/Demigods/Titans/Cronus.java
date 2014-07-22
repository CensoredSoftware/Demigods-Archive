package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;

import java.util.UUID;

public class Cronus extends Deity {
    /**
     *
     */
    private static final long serialVersionUID = -8478696197132571826L;
    boolean REAP = false;
    boolean FREEZE = false;

    public Cronus(UUID p) {
        super(Divine.CRONUS, p);
    }

    public void setReap(boolean b) {
        REAP = b;
    }

    public boolean getReap() {
        return REAP;
    }

    public void setFreeze(boolean b) {
        FREEZE = b;
    }

    public boolean getFreeze() {
        return FREEZE;
    }
}
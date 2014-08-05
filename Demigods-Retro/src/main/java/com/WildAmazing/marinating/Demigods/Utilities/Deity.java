package com.WildAmazing.marinating.Demigods.Utilities;

import java.io.Serializable;
import java.util.UUID;

public abstract class Deity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3835404655799787382L;
    private Divine NAME;
    private UUID PLAYER;

    /**
     * Control class. Extended by other AEsir.
     */
    public Deity() {
        NAME = Divine.OMNI;
        PLAYER = null;
    }

    public Deity(Divine deityName, UUID playerId) {
        NAME = deityName;
        PLAYER = playerId;
    }

    public UUID getPlayerId() {
        return PLAYER;
    }

    public Divine getName() {
        return NAME;
    }
}

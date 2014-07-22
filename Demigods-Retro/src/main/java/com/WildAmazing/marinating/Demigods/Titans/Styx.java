package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Styx extends Deity {
    /**
     *
     */
    private static final long serialVersionUID = -5452083554070420482L;
    private long timeInvincible;
    private long reviveTime;
    private UUID invinciblePlayer;

    public Styx(UUID p) {
        super(Divine.STYX, p);
        timeInvincible = System.currentTimeMillis();
        reviveTime = System.currentTimeMillis();
    }

    public void setInvinciblePlayer(Player player) {
        invinciblePlayer = player.getUniqueId();
    }

    public UUID getInvinciblePlayerId() {
        return invinciblePlayer;
    }

    public void setTime(long l) {
        timeInvincible = l;
    }

    public long getTime() {
        return timeInvincible;
    }

    public void setReviveTime(long l) {
        reviveTime = l;
    }

    public long getReviveTime() {
        return reviveTime;
    }
}
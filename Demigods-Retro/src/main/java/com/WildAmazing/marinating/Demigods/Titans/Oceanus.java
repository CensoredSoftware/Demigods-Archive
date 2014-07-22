package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

import java.util.UUID;

public class Oceanus extends Deity {
    /**
     *
     */
    private static final long serialVersionUID = 8051336014433800334L;
    boolean Squid = false;
    Material SquidITEM;

    public Oceanus(UUID p) {
        super(Divine.OCEANUS, p);
    }

    public void setSquid(boolean b) {
        Squid = b;
    }

    public boolean getSquid() {
        return Squid;
    }

    public void setSquidItem(Material m) {
        SquidITEM = m;
    }

    public Material getSquidItem() {
        return SquidITEM;
    }
}

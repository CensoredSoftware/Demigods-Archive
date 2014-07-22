package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.WriteLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Rhea extends Deity {
    /**
     *
     */
    private static final long serialVersionUID = 4926492490352420958L;
    Player PLAYER;
    ArrayList<WriteLocation> MINE;
    Material PLANTITEM, DETONATEITEM;

    public Rhea(UUID p) {
        super(Divine.RHEA, p);
        MINE = new ArrayList<WriteLocation>();
    }

    public void addTree(WriteLocation l) {
        MINE.add(l);
    }

    public void resetTree() {
        MINE.clear();
    }

    public ArrayList<WriteLocation> getTree() {
        return MINE;
    }

    public Material getPlantItem() {
        return PLANTITEM;
    }

    public void setPlantItem(Material m) {
        PLANTITEM = m;
    }

    public Material getDetonateItem() {
        return DETONATEITEM;
    }

    public void setDetonateItem(Material m) {
        DETONATEITEM = m;
    }
}

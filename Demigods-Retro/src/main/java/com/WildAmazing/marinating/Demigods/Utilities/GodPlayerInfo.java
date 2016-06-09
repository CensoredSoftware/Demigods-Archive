package com.WildAmazing.marinating.Demigods.Utilities;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class GodPlayerInfo extends PlayerInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3173900521751531355L;
    private int FAVOR, BLESSING;
    private boolean CHAT;

    public GodPlayerInfo(Player player, Divine firstDeity) {
        super(player);
        super.addToAllegiance(firstDeity);
        FAVOR = 0;
        BLESSING = 0;
        CHAT = false;
    }

    public GodPlayerInfo(Player p) {
        super(p);
    }

    public GodPlayerInfo(Player p, ArrayList<Divine> deities, int favor, int blessing) {
        super(p);
        for (Divine d : deities)
            super.addToAllegiance(d);
        FAVOR = favor;
        BLESSING = blessing;
        CHAT = false;
    }

    public int getBlessing() {
        return BLESSING;
    }

    public void setBlessing(int amount) {
        BLESSING = amount;
    }

    public boolean hasDeity(Divine deity) {
        if (deity == Divine.GOD) return true;
        if (getAllegiance().contains(deity)) return true;
        return false;
    }

    public int getFavor() {
        return FAVOR;
    }

    public void setFavor(int amount) {
        FAVOR = amount;
    }

    public boolean isChat() {
        return CHAT;
    }

    public void setChat(boolean b) {
        CHAT = b;
    }

    public String getRank() {
        if (super.getAllegiance().size() == 1) return "Acolyte";
        else if (super.getAllegiance().size() == 2) return "Zealot";
        else if (super.getAllegiance().size() == 3) return "demigods";
        else if (super.getAllegiance().size() >= 4) return "Olympian";
        return "Mortal";
    }

    public boolean isEligible() {
        if (BLESSING < 30000 && super.getAllegiance().size() == 1) return false;
        else if (BLESSING < 65000 && super.getAllegiance().size() == 2) return false;
        else if (BLESSING < 150000 && super.getAllegiance().size() == 3) return false;
        else if (super.getAllegiance().size() == 4) return false;
        else return true;
    }

    public int blessingTillNext() {
        if (BLESSING < 30000) return 30000 - BLESSING;
        else if (BLESSING < 65000) return 65000 - BLESSING;
        else if (BLESSING < 150000) return 150000 - BLESSING;
        return -1;
    }

    public int getRanking() {
        return (FAVOR * 2) + (BLESSING) + (super.getAllegiance().size() * 15000);
    }
}

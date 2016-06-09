package com.WildAmazing.marinating.demigods.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class Shrine implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6919813251729490836L;

    private int IRONDURABILITY = 15;
    private int GOLDDURABILITY = 45;
    private int OBSIDIANDURABILITY = 20;
    private int DIAMONDDURABILITY = 75;
    private int BEDROCKDURABILITY = 100;

    private WriteLocation CENTER; //the center block
    private WriteLocation TELEPORTTO; //where to teleport safely
    private int MAXEXTENSIONS = 100; //maximum number of extensions
    private int RANGE; //range of extensions
    private int RANGEMAX = 40;
    private int RADIUS = 5; //blocks directly protected since they are part of shrine
    private int INVINCIBLERADIUS = 5; //radius that same alliance is invincible
    private int INVINCIBLEMAX = 20;
    private int DURABILITY; //how many hits the center block can take
    private int SECONDSINVINCIBLE = 2; //how many seconds between hits
    private int SECONDSMAX = 10;
    private long LASTDAMAGETIME; //records last time shrine was hit
    private String ALLIANCE; //alliance of the shrine
    private String NAME; //name of the shrine
    private ArrayList<WriteLocation> EXTENSIONS;

    private String[] prefix = {"The", ""};
    private String[] adjective = {"Sacred", "Mighty", "Eternal", "Twilight", "Divine", "Holy", "Venerable", "Sanctified", "Dark", "Ancient"};
    private String[] name = {"Ground", "Chamber", "Pantheon", "Land", "Palace", "Temple", "Council", "Shrine", "Archives"};

    /*
     * ---
     * Protection rules
     * ---
     * Blocks within RADIUS of the CENTER can never be altered (except worldedit)
     * A sign may be placed with the text "<shrine name> protection" within the RADIUS of the shrine.
     * As long as it is within the protection of the shrine, it becomes an extender
     * for the shrine. This means that blocks that are within <range> of the sign have
     * protection. Another sign can then be placed within the protected zone of that sign, and a
     * chain of protected bubbles can be created this way. If a sign is destroyed, the area is no
     * longer protected. However, placed signs do not lose their protections unless they themselves are
     * destroyed. Having neighboring areas signs destroyed does not remove the protection of a sign.
     * There is a limit to the number of extensions a single shrine can have.
     * ---
     * To place one of these signs, the player must pay favor equal to the distance from the CENTER
     * according to a slightly exponential formula (discouraging protection > 1000 blocks)
     * -
     * To check if a block is protected, iterate through all shrines and all their extensions, checking
     * distance and comparing it to RANGE and RADIUS.
     */
    public Shrine(Location l) {
        CENTER = DUtil.toWriteLocation(l);
        TELEPORTTO = CENTER;
        RANGE = 10;
        DURABILITY = 0;
        ALLIANCE = null;
        String newname;
        int count = 0;
        do {
            count++;
            newname = "";
            newname += prefix[(new Random()).nextInt(prefix.length)];
            newname += " " + adjective[(new Random()).nextInt(adjective.length)];
            newname += " " + name[(new Random()).nextInt(name.length)];
            newname = newname.trim();
            if (count >= (prefix.length * adjective.length * name.length - 1)) {
                newname = CENTER.getWorld() + "-" + CENTER.getX() + "-" + CENTER.getY() + "-" + CENTER.getZ();
            }
        } while (doesNameExist(newname));
        NAME = newname;
        EXTENSIONS = new ArrayList<WriteLocation>();
        LASTDAMAGETIME = System.currentTimeMillis();
        createShrine(l.getBlock().getRelative(BlockFace.DOWN).getLocation());
        DSave.getShrines().add(this);
        Logger.getLogger("Minecraft").info("A new shrine was created at (" +
                CENTER.getX() + ", " + CENTER.getY() + ", " + CENTER.getZ() + ") in the world " + CENTER.getWorld());
    }

    public boolean claim(Player p, Material block) {
        if (!DSave.hasData(p, "ALLEGIANCE")) {
            p.sendMessage("You cannot claim a shrine if you are not in an allegiance.");
            return false;
        }
        int count = 0;
        int allycount = 0;
        switch (block) {
            case IRON_BLOCK:
                DURABILITY = IRONDURABILITY;
                break;
            case GOLD_BLOCK:
                DURABILITY = GOLDDURABILITY;
                break;
            case DIAMOND_BLOCK:
                DURABILITY = DIAMONDDURABILITY;
                break;
            case OBSIDIAN:
                DURABILITY = OBSIDIANDURABILITY;
                break;
            case BEDROCK:
                DURABILITY = BEDROCKDURABILITY;
                break;
            default:
                p.sendMessage(ChatColor.DARK_RED + "Only iron, obsidian, gold, or diamond blocks can claim a shrine.");
                return false;
        }
        for (Shrine s : DSave.getShrines()) {
            if (s.getAlliance() != null)
                if (s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
                    allycount++;
                    continue;
                }
            count++;
        }
        if ((count == 0) && (allycount != 0)) {
            p.sendMessage("One alliance cannot claim every shrine on the map.");
            return false;
        }
        if (allycount == 0) {
            DUtil.toLocation(getCenter()).getBlock().setType(Material.BEDROCK);
            DURABILITY = BEDROCKDURABILITY;
        }
        LASTDAMAGETIME = System.currentTimeMillis();
        ALLIANCE = (String) DSave.getData(p, "ALLEGIANCE");
        TELEPORTTO = DUtil.toWriteLocation(p.getLocation());
        DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + " has claimed a shrine for the " + ALLIANCE + " alliance.");
        return true;
    }

    public int getInvincibleSeconds() {
        return SECONDSINVINCIBLE;
    }

    public int getInvincibleRadius() {
        return INVINCIBLERADIUS;
    }

    public void setInvincibleRadius(int n) {
        if (n > INVINCIBLEMAX)
            INVINCIBLERADIUS = INVINCIBLEMAX;
        else INVINCIBLERADIUS = n;
    }

    public Location getTeleportSpot() {
        return DUtil.toLocation(TELEPORTTO);
    }

    public void setInvincibleSeconds(int t) {
        if (t > SECONDSMAX)
            SECONDSINVINCIBLE = SECONDSMAX;
        else SECONDSINVINCIBLE = t;
    }

    public long getTime() {
        return LASTDAMAGETIME;
    }

    public void setDamageTime(long l) {
        LASTDAMAGETIME = l;
    }

    public String getName() {
        return NAME;
    }

    public void setName(String s) {
        NAME = s;
    }

    public WriteLocation getCenter() {
        return CENTER;
    }

    public void setCenter(Location l) {
        CENTER = DUtil.toWriteLocation(l);
    }

    public int getRange() {
        return RANGE;
    }

    public void setRadius(int r) {
        RADIUS = r;
    }

    public void setRange(int r) {
        if (r > RANGEMAX)
            RANGE = RANGEMAX;
        else RANGE = r;
    }

    public int getDurability() {
        return DURABILITY;
    }

    public void setDurability(int d) {
        DURABILITY = d;
    }

    public ArrayList<WriteLocation> getExtensions() {
        return EXTENSIONS;
    }

    public int getMaxExtensions() {
        return MAXEXTENSIONS;
    }

    public int getRadius() {
        return RADIUS;
    }

    public String getAlliance() {
        return ALLIANCE;
    }

    public void setAlliance(String s) {
        ALLIANCE = s;
    }

    private void createShrine(Location l) {
        if ((l.getBlockY() > 120) || (l.getBlockY() < 5))
            return;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -2; y <= 6; y++) {
                    l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z).setType(Material.AIR);
                }
            }
        }
        l.getBlock().setType(Material.FENCE);
        l.setX(l.getBlockX());
        l.setY(l.getBlockY());
        l.setZ(l.getBlockZ());
        l.setY(l.getY() - 2);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Block b = (new Location(l.getWorld(), l.getX() + x, l.getY(), l.getZ() + z)).getBlock();
                if (b.getLocation().distance(l) == 1)
                    b.setType(Material.BEDROCK);
                else if ((b.getLocation().distance(l) > 1) && (b.getLocation().distance(l) < 2))
                    b.setType(Material.GLOWSTONE);
                else if ((b.getLocation().distance(l) >= 2) && (b.getLocation().distance(l) < 3)) {
                    b.setTypeId(98);
                    int type = (new Random()).nextInt(10);
                    if (type < 2)
                        b.setData((byte) 1);
                    else if (type > 6)
                        b.setData((byte) 2);
                } else b.setType(Material.BEDROCK);
            }
        }
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 1);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Block b = new Location(l.getWorld(), l.getX() + x, l.getY(), l.getZ() + z).getBlock();
                if (b.getLocation().distance(l) == 1) {
                    b.setType(Material.STEP);
                    b.setData((byte) (0x5));
                } else if ((b.getLocation().distance(l) > 2.8) && (b.getLocation().distance(l) < 3))
                    b.setType(Material.BEDROCK);
                else if ((b.getLocation().distance(l) >= 3) && (b.getLocation().distance(l) < 3.5)) {
                    b.setTypeId(98);
                    int type = (new Random()).nextInt(10);
                    if (type < 2)
                        b.setData((byte) 1);
                    else if (type > 6)
                        b.setData((byte) 2);
                } else if ((b.getLocation().distance(l) >= 3.5) && (b.getLocation().distance(l) < 4.2))
                    b.setType(Material.GLOWSTONE);
                else b.setType(Material.AIR);
            }
        }
        l.getBlock().setType(Material.OBSIDIAN);
    }

    private boolean doesNameExist(String name) {
        for (Shrine sh : DSave.getShrines()) {
            if (name.equals(sh.getName()))
                return true;
        }
        return false;
    }
}

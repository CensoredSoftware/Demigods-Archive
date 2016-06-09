package com.WildAmazing.marinating.demigods.Utilities;

import com.WildAmazing.marinating.demigods.Demigods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class DUtil {
    private static Demigods plugin; //obviously needed
    private static int dist = 100; //maximum range on targeting

    public DUtil(Demigods d) {
        plugin = d;
    }

    public static Player getPlayer(String name) {
        return plugin.getServer().getPlayer(name);
    }

    /**
     * Gets the Location a Player is looking at.
     *
     * @param p
     * @return
     */
    public static Location getTargetLocation(Player p) {
        return p.getTargetBlock((Set) null, dist).getLocation();
    }

    /**
     * Gets the Entity a Player is looking at.
     * Returns null if none found.
     * Will not return the player.
     *
     * @param p
     * @param offset
     * @return
     */
    public static Entity getTargetEntity(Player p, int offset) {
        Entity e = null;
        for (Block b : (List<Block>) p.getLineOfSight((Set) null, dist)) {
            for (Entity t : b.getChunk().getEntities()) {
                if ((t.getLocation().distance(b.getLocation()) <= offset) && !t.equals(p))
                    e = t;
            }
        }
        return e;
    }

    /**
     * Gets the LivingEntity a Player is looking at.
     *
     * @param p
     * @return
     */
    public static LivingEntity getTargetLivingEntity(Player p, int offset) {
        LivingEntity e = null;
        for (Block b : (List<Block>) p.getLineOfSight((Set) null, dist)) {
            for (Entity t : b.getChunk().getEntities()) {
                if (t instanceof LivingEntity)
                    if ((t.getLocation().distance(b.getLocation()) <= offset) && !t.equals(p))
                        e = (LivingEntity) t;
            }
        }
        return e;
    }

    /**
     * Gets the LivingEntity a Player is looking at, with given range.
     *
     * @param p
     * @return
     */
    public static LivingEntity getTargetLivingEntity(Player p, int offset, int range) {
        LivingEntity e = null;
        for (Block b : (List<Block>) p.getLineOfSight((Set) null, range)) {
            for (Entity t : b.getChunk().getEntities()) {
                if (t instanceof LivingEntity)
                    if ((t.getLocation().distance(b.getLocation()) <= offset) && !t.equals(p))
                        e = (LivingEntity) t;
            }
        }
        return e;
    }

    /**
     * Converts Locations to WriteLocation.
     *
     * @param L
     * @return
     */
    public static ArrayList<WriteLocation> toWriteLocations(List<Location> L) {
        ArrayList<WriteLocation> aw = new ArrayList<WriteLocation>();
        for (Location l : L) {
            aw.add(new WriteLocation(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        }
        return aw;
    }

    /**
     * Converts a Location to WriteLocation.
     *
     * @param l
     * @return
     */
    public static WriteLocation toWriteLocation(Location l) {
        return new WriteLocation(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Converts WriteLocations to Locations.
     *
     * @param L
     * @return
     */
    public static ArrayList<Location> toLocations(List<WriteLocation> L) {
        ArrayList<Location> al = new ArrayList<Location>();
        for (WriteLocation l : L) {
            al.add(new Location(plugin.getServer().getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ()));
        }
        return al;
    }

    /**
     * Converts a WriteLocation to Location.
     *
     * @param l
     * @return
     */
    public static Location toLocation(WriteLocation l) {
        return new Location(plugin.getServer().getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
    }

    /**
     * Checks if a player has the given permission or is OP.
     *
     * @param p
     * @param pe
     * @return
     */
    public static boolean hasPermissionOrOP(Player p, String pe) {//convenience method for permissions
        return p.isOp() || p.hasPermission(pe);
    }

    /**
     * Checks is a player has the given permission.
     *
     * @param p
     * @param pe
     * @return
     */
    public static boolean hasPermission(Player p, String pe) {//convenience method for permissions
        return p.hasPermission(pe);
    }

    /**
     * Checks if a player is a God and has the amount of favor.
     *
     * @param p
     * @param favor
     * @return
     */
    public static boolean isGodHasFavor(Player p, int favor) {
        if (isGod(p))
            return (getFavor(p) >= favor);
        return false;
    }

    /**
     * Checks if a player is a Titan and has the amount of power.
     *
     * @param p
     * @param power
     * @return
     */
    public static boolean isTitanHasPower(Player p, int power) {
        if (isTitan(p))
            return (getFavor(p) >= power);
        return false;
    }

    /**
     * Sets a player's allegiance to Titan.
     *
     * @param p
     */
    public static void setTitan(Player p) {
        DSave.saveData(p, "ALLEGIANCE", "titan");
    }

    /**
     * Sets a player's allegiance to God.
     *
     * @param p
     */
    public static void setGod(Player p) {
        DSave.saveData(p, "ALLEGIANCE", "god");
    }

    /**
     * Sets a player's allegiance.
     *
     * @param p
     * @param allegiance
     */
    public static void setAllegiance(Player p, String allegiance) {
        DSave.saveData(p, "ALLEGIANCE", allegiance);
    }

    /**
     * Checks if a player is a Titan.
     *
     * @param p
     * @return
     */
    public static boolean isTitan(Player p) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return getAllegiance(p).equalsIgnoreCase("titan");
        return false;
    }

    /**
     * Checks if a player is a Titan.
     *
     * @param p
     * @return
     */
    public static boolean isTitan(String p) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return getAllegiance(p).equalsIgnoreCase("titan");
        return false;
    }

    /**
     * Checks if a player is a God.
     *
     * @param p
     * @return
     */
    public static boolean isGod(String p) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return getAllegiance(p).equalsIgnoreCase("god");
        return false;
    }

    /**
     * Checks if a player is a God.
     *
     * @param p
     * @return
     */
    public static boolean isGod(Player p) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return getAllegiance(p).equalsIgnoreCase("god");
        return false;
    }

    /**
     * Gets the String representation of a player's allegiance.
     *
     * @param p
     * @return
     */
    public static String getAllegiance(Player p) {
        return getAllegiance(p.getName());
    }

    /**
     * Gets the String representation of a player's allegiance.
     *
     * @param p
     * @return
     */
    public static String getAllegiance(String p) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return ((String) DSave.getData(p, "ALLEGIANCE"));
        return null;
    }

    /**
     * Checks if a player is in a certain allegiance.
     *
     * @param p
     * @param type
     * @return
     */
    public static boolean is(Player p, String type) {
        if (DSave.hasData(p, "ALLEGIANCE"))
            return ((String) DSave.getData(p, "ALLEGIANCE")).equals(type);
        return false;
    }

    /**
     * Gives a player a deity, even if they have none.
     *
     * @param p
     * @param d
     */
    public static void addDeity(Player p, Deity d) {
        if (DSave.hasData(p, "DEITIES"))
            getDeities(p).add(d);
        else {
            ArrayList<Deity> ad = new ArrayList<Deity>();
            ad.add(d);
            DSave.saveData(p, "DEITIES", ad);
        }
    }

    /**
     * Removes a player's deity if they have it.
     *
     * @param p
     * @param name
     */
    public static void removeDeity(Player p, String name) {
        ArrayList<Deity> temp = getDeities(p);
        if (DSave.hasData(p, "DEITIES")) {
            for (Deity de : getDeities(p)) {
                if (de.getName().equalsIgnoreCase(name))
                    temp.remove(getDeities(p).indexOf(de));
            }
        }
        DSave.saveData(p, "DEITIES", temp);
    }

    /**
     * Checks if a player has a named deity.
     *
     * @param p
     * @param name
     * @return
     */
    public static boolean hasDeity(Player p, String name) {
        if (DSave.hasData(p, "DEITIES")) {
            for (Deity de : getDeities(p)) {
                if (de.getName().equalsIgnoreCase(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the deity named if the player has it, otherwise null.
     *
     * @param p
     * @param deityname
     * @return
     */
    public static Deity getDeity(Player p, String deityname) {
        if (DSave.hasData(p, "DEITIES")) {
            for (Deity de : getDeities(p)) {
                if (de.getName().equalsIgnoreCase(deityname))
                    return de;
            }
        }
        return null;
    }

    /**
     * Gives the list of all the player's deities.
     *
     * @param p
     * @return
     */
    public static ArrayList<Deity> getDeities(Player p) {
        if (DSave.hasData(p, "DEITIES")) {
            @SuppressWarnings("unchecked")
            ArrayList<Deity> returnit = (ArrayList<Deity>) DSave.getData(p, "DEITIES");
            return returnit;
        }
        return null;
    }

    /**
     * Gives the list of all the player's deities.
     *
     * @param p
     * @return
     */
    public static ArrayList<Deity> getDeities(String p) {
        if (DSave.hasData(p, "DEITIES")) {
            @SuppressWarnings("unchecked")
            ArrayList<Deity> returnit = (ArrayList<Deity>) DSave.getData(p, "DEITIES");
            return returnit;
        }
        return null;
    }

    /**
     * Set a player's favor.
     *
     * @param p
     * @param amt
     */
    public static void setFavor(Player p, int amt) {
        if (amt > getFavorCap(p))
            amt = getFavorCap(p);
        DSave.saveData(p, "FAVOR", new Integer(amt));
    }

    /**
     * Get a player's favor.
     *
     * @param p
     * @return
     */
    public static int getFavor(Player p) {
        if (DSave.hasData(p, "FAVOR"))
            return (Integer) DSave.getData(p, "FAVOR");
        return -1;
    }

    /**
     * Get a player's favor.
     *
     * @param p
     * @return
     */
    public static int getFavor(String p) {
        if (DSave.hasData(p, "FAVOR"))
            return (Integer) DSave.getData(p, "FAVOR");
        return -1;
    }

    /**
     * Set a player's level.
     *
     * @param p
     * @param amt
     */
    public static void setLevel(Player p, int amt) {
        DSave.saveData(p, "LEVEL", new Integer(amt));
    }

    /**
     * Set a player's level.
     *
     * @param p
     * @param amt
     */
    public static void setLevel(String p, int amt) {
        DSave.saveData(p, "LEVEL", new Integer(amt));
    }

    /**
     * Get a player's level.
     *
     * @param p
     * @return
     */
    public static int getLevel(Player p) {
        if (DSave.hasData(p, "LEVEL"))
            return (Integer) DSave.getData(p, "LEVEL");
        return -1;
    }

    /**
     * Get a player's level.
     *
     * @param p
     * @return
     */
    public static int getLevel(String p) {
        if (DSave.hasData(p, "LEVEL"))
            return (Integer) DSave.getData(p, "LEVEL");
        return -1;
    }

    /**
     * Set the number of kills a player has.
     *
     * @param p
     * @param amt
     */
    public static void setKills(Player p, int amt) {
        DSave.saveData(p, "KILLS", amt);
    }

    /**
     * Set the number of deaths a player has.
     *
     * @param p
     * @param amt
     */
    public static void setDeaths(Player p, int amt) {
        DSave.saveData(p, "DEATHS", amt);
    }

    /**
     * Get the number of kills a player has.
     *
     * @param p
     * @return
     */
    public static int getKills(Player p) {
        if (DSave.hasData(p, "KILLS"))
            return (Integer) DSave.getData(p, "KILLS");
        return -1;
    }

    /**
     * Get the number of kills a player has.
     *
     * @param p
     * @return
     */
    public static int getKills(String p) {
        if (DSave.hasData(p, "KILLS"))
            return (Integer) DSave.getData(p, "KILLS");
        return -1;
    }

    /**
     * Get the number of deaths a player has.
     *
     * @param p
     * @return
     */
    public static int getDeaths(Player p) {
        if (DSave.hasData(p, "DEATHS"))
            return (Integer) DSave.getData(p, "DEATHS");
        return -1;
    }

    /**
     * Get the number of deaths a player has.
     *
     * @param p
     * @return
     */
    public static int getDeaths(String p) {
        if (DSave.hasData(p, "DEATHS"))
            return (Integer) DSave.getData(p, "DEATHS");
        return -1;
    }

    /**
     * Checks if player is a titan with Tier 1 deities.
     *
     * @param p
     * @return
     */
    public static boolean isTierOne(Player p) {
        if (isTitan(p)) {
            if (hasDeity(p, "Cronus") || hasDeity(p, "Rhea") || hasDeity(p, "Prometheus"))
                return true;
        }
        return false;
    }

    /**
     * Gets the cost in levels for the next Deity.
     *
     * @param p
     * @return
     */
    public static int costForNextDeity(Player p) {
        switch (getDeities(p).size()) {
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 15;
            case 4:
                return 20;
            case 5:
                return 27;
            case 6:
                return 35;
            case 7:
                return 45;
            case 8:
                return 50;
            case 9:
                return 60;
            case 10:
                return 70;
            case 11:
                return 80;
            case 12:
                return 90;
        }
        return -1;
    }

    /**
     * Gets the cost in Favor for the next level.
     *
     * @param p
     * @return
     */
    public static int costForNextLevel(Player p) {
        return (int) Math.ceil(18.8211797 * Math.pow(getLevel(p), 2.514268));
    }

    /**
     * Gets the maximum Favor a player can have at a certain level.
     *
     * @param p
     * @return
     */
    public static int getFavorCap(Player p) {
        return (int) Math.ceil(20 * Math.pow(getLevel(p), 2.57));
    }

    /**
     * Checks if player is eligible to level up.
     *
     * @param p
     * @return
     */
    public static boolean canLevelUp(Player p) {
        if (getLevel(p) < getPlugin().getLevelCap())
            return ((getLevel(p) <= getKills(p) + 10) && (costForNextLevel(p) <= getFavor(p)));
        return false;
    }

    /**
     * Gets the name rank of a player.
     *
     * @return
     */
    public static String getRank(Player p) {
        if (isTitan(p)) {
            if (getDeities(p).size() == 1)
                return "Fallen";
            else if (getDeities(p).size() == 2)
                return "Lord";
            else if (getDeities(p).size() == 3)
                return "King";
            else if (getDeities(p).size() == 4)
                return "Reborn";
            else if (getDeities(p).size() == 5)
                return "Legion";
            else if (getDeities(p).size() >= 6)
                return "Immortal";
            return "Spawn";
        } else if (isGod(p)) {
            if (getDeities(p).size() == 1)
                return "Acolyte";
            else if (getDeities(p).size() == 2)
                return "Zealot";
            else if (getDeities(p).size() == 3)
                return "demigods";
            else if (getDeities(p).size() >= 4)
                return "Olympian";
            return "Mortal";
        } else if (is(p, "Omni"))
            return "Admin";
        return "Error";
    }

    /**
     * Gets the arbitrary numeric ranking
     * of a player.
     *
     * @param p
     * @return
     */
    public static long getRanking(String p) {
        int FAVOR = getFavor(p);
        return (FAVOR * 2) + (getLevel(p) * 800) + (getDeities(p).size() * 15000) + (getKills(p) * 200) - (getDeaths(p) * 200);
    }

    /**
     * Checks if a certain material is bound to a skill by the player.
     *
     * @param p
     * @param material
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isBound(Player p, org.bukkit.Material material) {
        if (DSave.hasData(p, "BINDINGS")) {
            ArrayList<Material> check = (ArrayList<Material>) DSave.getData(p, "BINDINGS");
            for (Material ma : check) {
                if (ma == material)
                    return true;
            }
        }
        return false;
    }

    /**
     * Registers a material to the player's list of bound items.
     *
     * @param p
     * @param m
     */
    @SuppressWarnings("unchecked")
    public static void registerBind(Player p, Material m) {
        ArrayList<Material> used = new ArrayList<Material>();
        if (DSave.hasData(p, "BINDINGS")) {
            used = (ArrayList<Material>) DSave.getData(p, "BINDINGS");
            if (!used.contains(m))
                used.add(m);
        } else {
            used.add(m);
        }
        DSave.saveData(p, "BINDINGS", used);
    }

    /**
     * Removes a material from the player's list of bound items.
     */
    @SuppressWarnings("unchecked")
    public static void removeBind(Player p, Material m) {
        ArrayList<Material> used;
        if (DSave.hasData(p, "BINDINGS")) {
            used = (ArrayList<Material>) DSave.getData(p, "BINDINGS");
            if (used.contains(m))
                used.remove(m);
            DSave.saveData(p, "BINDINGS", used);
        }
    }

    /**
     * Grab the plugin.
     */
    public static Demigods getPlugin() {
        return plugin;
    }

    /**
     * Check if a location is the center of an established shrine.
     *
     * @param l
     * @return
     */
    public static boolean isShrineCenter(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld()))
                if (l.equals(DUtil.toLocation(s.getCenter())))
                    return true;
        }
        return false;
    }

    /**
     * Check if a block is part of a shrine's inner radius.
     *
     * @param l
     * @return
     */
    public static boolean isProtectedShrine(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld()))
                if (l.distance(DUtil.toLocation(s.getCenter())) <= s.getRadius())
                    return true;
        }
        return false;
    }

    /**
     * Gets a shrine that protects the location. Returns null if none found.
     *
     * @param l
     * @return
     */
    public static Shrine getShrine(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld()))
                if (l.distance(DUtil.toLocation(s.getCenter())) <= s.getRadius())
                    return s;
        }
        return null;
    }

    /**
     * Gets a shrine that makes the location invincible. Returns null if none found.
     *
     * @param l
     * @return
     */
    public static Shrine getShrineInvincible(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld()))
                if (l.distance(DUtil.toLocation(s.getCenter())) <= s.getInvincibleRadius())
                    return s;
        }
        return null;
    }

    /**
     * Check if a block is protected by extension of a shrine or factions.
     *
     * @param l
     * @return
     */
    public static boolean isProtected(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld())) {
                if (l.distance(DUtil.toLocation(s.getCenter())) <= s.getRadius())
                    return true;
                for (WriteLocation w : s.getExtensions())
                    if (l.distance(DUtil.toLocation(w)) <= s.getRange())
                        return true;
            }
        }
        return false;
    }

    /**
     * Checks if a location is protected under Demigods.
     *
     * @param l
     * @return
     */
    public static boolean isProtectedDemigodsOnly(Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld())) {
                if (l.distance(DUtil.toLocation(s.getCenter())) <= s.getRadius())
                    return true;
                for (WriteLocation w : s.getExtensions())
                    if (l.distance(DUtil.toLocation(w)) <= s.getRange())
                        return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player has permission for a location. (Shrine extension)
     *
     * @param p
     * @param l
     * @return
     */
    public static boolean canPlayerManipulate(Player p, Location l) {
        for (Shrine s : DSave.getShrines()) {
            if (l.getWorld().getName().equals(s.getCenter().getWorld())) {
                if ((s.getAlliance() != null) && !s.getAlliance().equalsIgnoreCase(getAllegiance(p)))
                    for (WriteLocation w : s.getExtensions()) {
                        if (l.distance(DUtil.toLocation(w)) < 0.01)
                            return true;
                        if (l.distance(DUtil.toLocation(w)) <= s.getRange())
                            return false;
                    }
            }
        }
        return true;
    }

    /**
     * Checks if a player has all required attributes (should not give nulls).
     *
     * @param p
     * @return
     */
    public static boolean isFullParticipant(Player p) {
        return isFullParticipant(p.getName());
    }

    /**
     * Check if a player has all required attributes.
     *
     * @param p
     * @return
     */
    public static boolean isFullParticipant(String p) {
        if (DUtil.getAllegiance(p) == null) return false;
        if (DUtil.getDeaths(p) == -1) return false;
        if (DUtil.getKills(p) == -1) return false;
        if ((DUtil.getDeities(p) == null) || (DUtil.getDeities(p).size() == 0)) return false;
        if (DUtil.getFavor(p) == -1) return false;
        if (DUtil.getLevel(p) == -1) return false;
        return true;
    }

    /**
     * Checks if one team has an advantage over the other by greater than the given %.
     *
     * @param alliance
     * @return
     */
    public static boolean hasAdvantage(String alliance, double advantagepercent) {
        HashMap<String, Integer> alliances = new HashMap<String, Integer>();
        for (String player : DSave.getCompleteData().keySet()) {
            if (DSave.hasData(player, "ALLEGIANCE")) {
                if (DSave.hasData(player, "LASTLOGINTIME"))
                    if ((Long) DSave.getData(player, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000)
                        continue;
                if (alliances.containsKey(DSave.getData(player, "ALLEGIANCE"))) {
                    int put = alliances.remove(DSave.getData(player, "ALLEGIANCE")) + 1;
                    alliances.put((String) DSave.getData(player, "ALLEGIANCE"), put);
                } else alliances.put((String) DSave.getData(player, "ALLEGIANCE"), 1);
            }
        }
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> clone = (HashMap<String, Integer>) alliances.clone();
        HashMap<String, Integer> talliances = clone;
        ArrayList<String> alliancerank = new ArrayList<String>();
        Logger.getLogger("Minecraft").info("Total alliances: " + alliances.size());
        Logger.getLogger("Minecraft").info(alliances + "");
        for (int i = 0; i < alliances.size() + 1; i++) {
            String newleader = "";
            int leadamt = -1;
            for (String all : alliances.keySet()) {
                if (alliances.get(all) > leadamt) {
                    leadamt = alliances.get(all);
                    newleader = all;
                }
            }
            alliancerank.add(newleader);
            leadamt = -1;
            alliances.remove(newleader);
        }
        if (alliancerank.size() == 1) return false;
        if (alliancerank.get(0).equalsIgnoreCase(alliance)) {
            if (advantagepercent <= ((double) talliances.get(alliancerank.get(0)) / talliances.get(alliancerank.get(1))))
                return true;
        }
        return false;
    }

    /**
     * Calculates the value of the item.
     *
     * @param ii
     * @return
     */
    public static int getValue(ItemStack ii) {
        int val = 0;
        switch (ii.getType()) {
            case STONE:
                val += ii.getAmount() * 0.8;
                break;
            case COBBLESTONE:
                val += ii.getAmount() * 0.6;
                break;
            case DIRT:
                val += ii.getAmount() * 0.1;
                break;
            case LOG:
                val += ii.getAmount() * 2;
                break;
            case WOOD:
                val += ii.getAmount() * 0.5;
                break;
            case STICK:
                val += ii.getAmount() * 0.25;
                break;
            case GLASS:
                val += ii.getAmount() * 1.8;
                break;
            case LAPIS_BLOCK:
                val += ii.getAmount() * 85;
                break;
            case SANDSTONE:
                val += ii.getAmount() * 1.1;
                break;
            case GOLD_BLOCK:
                val += ii.getAmount() * 170;
                break;
            case IRON_BLOCK:
                val += ii.getAmount() * 120;
                break;
            case BRICK:
                val += ii.getAmount() * 10;
                break;
            case TNT:
                val += ii.getAmount() * 10;
                break;
            case MOSSY_COBBLESTONE:
                val += ii.getAmount() * 10;
                break;
            case OBSIDIAN:
                val += ii.getAmount() * 10;
                break;
            case DIAMOND_BLOCK:
                val += ii.getAmount() * 300;
                break;
            case CACTUS:
                val += ii.getAmount() * 1.7;
                break;
            case YELLOW_FLOWER:
                val += ii.getAmount() * 0.1;
                break;
            case SEEDS:
                val += ii.getAmount() * 0.3;
                break;
            case PUMPKIN:
                val += ii.getAmount() * 0.7;
                break;
            case CAKE:
                val += ii.getAmount() * 42;
                break;
            case APPLE:
                val += ii.getAmount() * 5;
                break;
            case COAL:
                val += ii.getAmount() * 2.5;
                break;
            case DIAMOND:
                val += ii.getAmount() * 30;
                break;
            case IRON_ORE:
                val += ii.getAmount() * 7;
                break;
            case GOLD_ORE:
                val += ii.getAmount() * 13;
                break;
            case IRON_INGOT:
                val += ii.getAmount() * 12;
                break;
            case GOLD_INGOT:
                val += ii.getAmount() * 18;
                break;
            case STRING:
                val += ii.getAmount() * 2.4;
                break;
            case WHEAT:
                val += ii.getAmount() * 0.6;
                break;
            case BREAD:
                val += ii.getAmount() * 2;
                break;
            case RAW_FISH:
                val += ii.getAmount() * 2.4;
                break;
            case PORK:
                val += ii.getAmount() * 2.4;
                break;
            case COOKED_FISH:
                val += ii.getAmount() * 3.4;
                break;
            case GRILLED_PORK:
                val += ii.getAmount() * 3.4;
                break;
            case GOLDEN_APPLE:
                val += ii.getAmount() * 190;
                break;
            case GOLD_RECORD:
                val += ii.getAmount() * 60;
                break;
            case GREEN_RECORD:
                val += ii.getAmount() * 60;
                break;
            case GLOWSTONE:
                val += ii.getAmount() * 1.7;
                break;
            case REDSTONE:
                val += ii.getAmount() * 3.3;
                break;
            case EGG:
                val += ii.getAmount() * 0.3;
                break;
            case SUGAR:
                val += ii.getAmount() * 1.2;
                break;
            case BONE:
                val += ii.getAmount() * 3;
                break;
            case ENDER_PEARL:
                val += ii.getAmount() * 7.7;
                break;
            case ROTTEN_FLESH:
                val += ii.getAmount() * 3;
                break;
            case RAW_CHICKEN:
                val += ii.getAmount() * 2;
                break;
            case COOKED_CHICKEN:
                val += ii.getAmount() * 2.6;
                break;
            case RAW_BEEF:
                val += ii.getAmount() * 2;
                break;
            case COOKED_BEEF:
                val += ii.getAmount() * 2.7;
                break;
            case MELON:
                val += ii.getAmount() * 0.8;
                break;
            case COOKIE:
                val += ii.getAmount() * 6;
                break;
            case VINE:
                val += ii.getAmount() * 1.2;
                break;
            case DRAGON_EGG:
                val += ii.getAmount() * 10000;
                break;
            default:
                val += ii.getAmount() * 1;
                break;
        }
        return val;
    }
}

package com.WildAmazing.marinating.demigods.Utilities;

import com.WildAmazing.marinating.demigods.Gods.Ares;
import com.WildAmazing.marinating.demigods.Gods.Hades;
import com.WildAmazing.marinating.demigods.Gods.Poseidon;
import com.WildAmazing.marinating.demigods.Gods.Zeus;
import com.WildAmazing.marinating.demigods.Titans.Atlas;
import com.WildAmazing.marinating.demigods.Titans.Cronus;
import com.WildAmazing.marinating.demigods.Titans.Prometheus;
import com.WildAmazing.marinating.demigods.Titans.Rhea;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/*
 * HASHMAP OF PLAYER'S NAMES
 * CONTAINS EACH PLAYER'S SAVED INFORMATION (IDENTIFIED BY STRINGS)
 * BE VERY CAREFUL NOT TO SAVE THINGS THAT CAN'T BE WRITTEN
 */
public class DSave {
    Logger log = Logger.getLogger("Minecraft");

    private static HashMap<String, HashMap<String, Object>> SAVEDDATA;
    private static ArrayList<Shrine> SHRINES;
    private static ArrayList<Deity> GLOBALLIST;

    public DSave(String path) {
        SAVEDDATA = new HashMap<String, HashMap<String, Object>>();
        SHRINES = new ArrayList<Shrine>();
        File f1 = new File(path + "Players/");
        File f2 = new File(path + "Shrines/");
        if (!f1.exists()) {
            log.info("[Demigods] Creating a new player save.");
            f1.mkdirs();
        }
        if (!f2.exists()) {
            log.info("[Demigods] Creating a new shrine save.");
            f2.mkdirs();
        }
        File[] list = f1.listFiles();
        for (File element : list) {
            String load = element.getName();
            if (load.endsWith(".dem")) {
                load = load.substring(0, load.length() - 4);
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
                    Object result = ois.readObject();
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> cast = (HashMap<String, Object>) result;
                    SAVEDDATA.put(load, cast);
                    ois.close();
                } catch (Exception error) {
                    log.severe("[Demigods] Could not load player " + load);
                }
            }
        }
        list = f2.listFiles();
        for (File element : list) {
            String load = element.getName();
            if (load.endsWith(".loc")) {
                load = load.substring(0, load.length() - 4);
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
                    Object result = ois.readObject();
                    Shrine cast = (Shrine) result;
                    SHRINES.add(cast);
                    ois.close();
                } catch (Exception error) {
                    log.severe("[Demigods] Could not load shrine " + load);
                }
            }
        }
        loadGlobalList();
        log.info("[Demigods] Loaded " + f1.listFiles().length + " player files.");
        log.info("[Demigods] Loaded " + f2.listFiles().length + " shrines.");
    }

    /*
     * Check if the player has saved information.
     */
    public static boolean hasPlayer(Player p) {
        return hasPlayer(p.getName());
    }

    public static boolean hasPlayer(String p) {
        return SAVEDDATA.containsKey(p);
    }

    /*
     * Add a player's information to be saved, under the player's name.
     */
    public static boolean addPlayer(Player p) {
        return addPlayer(p.getName()); //always use getName();
    }

    public static boolean addPlayer(String p) {
        if (hasPlayer(p)) return false;
        SAVEDDATA.put(p, new HashMap<String, Object>());
        return true;
    }

    /*
     * Check if the player has data saved under a certain id.
     */
    public static boolean hasData(Player p, String id) {
        return hasData(p.getName(), id);
    }

    public static boolean hasData(String p, String id) {
        if (hasPlayer(p)) {
            if (SAVEDDATA.get(p).containsKey(id))
                return true;
        }
        return false;
    }

    /*
     * Save data under a certain id.
     */
    public static boolean saveData(Player p, String id, Object save) {
        return saveData(p.getName(), id, save);
    }

    public static boolean saveData(String p, String id, Object save) {
        if (!hasPlayer(p)) return false;
        if (SAVEDDATA.get(p).containsKey(id))
            SAVEDDATA.get(p).remove(id); //remove if already there, to overwrite
        SAVEDDATA.get(p).put(id, save);
        return true;
    }

    /*
     * Get all of a player's data.
     */
    public static HashMap<String, Object> getAllData(Player p) {
        return getAllData(p.getName());
    }

    public static HashMap<String, Object> getAllData(String p) {
        if (hasPlayer(p))
            return SAVEDDATA.get(p);
        return null;
    }

    /*
     * Get a specific piece of saved data, by id.
     */
    public static Object getData(Player p, String id) {
        return getData(p.getName(), id);
    }

    public static Object getData(String p, String id) {
        if (hasData(p, id))
            return SAVEDDATA.get(p).get(id);
        return null;
    }

    public static ArrayList<Shrine> getShrines() {
        return SHRINES;
    }

    public static void removePlayer(Player p) {
        removePlayer(p.getName());
    }

    public static void removePlayer(String p) {
        if (SAVEDDATA.containsKey(p)) {
            SAVEDDATA.remove(p);
        }
    }

    public static HashMap<String, HashMap<String, Object>> getCompleteData() {
        return SAVEDDATA;
    }

    public static void removeItem(String path) {
        (new File(path)).delete();
    }

    public static void saveItem(String path, Object item) {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(item);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGlobalList() {
        GLOBALLIST = new ArrayList<Deity>();
        GLOBALLIST.add(new Cronus("ADMIN"));
        GLOBALLIST.add(new Prometheus("ADMIN"));
        GLOBALLIST.add(new Rhea("ADMIN"));
        GLOBALLIST.add(new Atlas("ADMIN"));
        //
        GLOBALLIST.add(new Zeus("ADMIN"));
        GLOBALLIST.add(new Ares("ADMIN"));
        GLOBALLIST.add(new Hades("ADMIN"));
        GLOBALLIST.add(new Poseidon("ADMIN"));
        Logger.getLogger("Minecraft").info("[Demigods] Preloaded deities.");
    }

    public static ArrayList<Deity> getGlobalList() {
        return GLOBALLIST;
    }

    /*
     * Saves itself, but must be loaded elsewhere (main plugin).
     */
    public static void save(String path) throws FileNotFoundException, IOException {
        (new File(path + "Players/")).mkdirs();
        (new File(path + "Shrines/")).mkdirs();
        for (String name : SAVEDDATA.keySet()) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/Players/" + name + ".dem"));
            oos.writeObject(SAVEDDATA.get(name));
            oos.flush();
            oos.close();
        }
        for (Shrine shrine : SHRINES) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/Shrines/" + shrine.getName() + ".loc"));
            oos.writeObject(shrine);
            oos.flush();
            oos.close();
        }
    }
}

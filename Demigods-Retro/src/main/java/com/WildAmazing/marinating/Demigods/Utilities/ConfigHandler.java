package com.WildAmazing.marinating.Demigods.Utilities;

import com.WildAmazing.marinating.Demigods.Demigods;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class ConfigHandler {
    private static String PATH;
    private static File FILE;
    private static Properties CONFIG;
    private static Demigods plugin;
    private static ArrayList<String> WORLDS; // ALL THESE WORLDS ARE YOURS EXCEPT EUROPA. ATTEMPT NO LANDING THERE.
    private static boolean MOTD;
    private static boolean PHANTOM;
    private static boolean PROTECT;

    public ConfigHandler(String path, Demigods instance) {
        PATH = path;
        FILE = new File(path);
        CONFIG = new Properties();
        plugin = instance;
        WORLDS = new ArrayList<String>();
        MOTD = true;
        PHANTOM = true;
        PROTECT = false;
    }

    public boolean generateFile() {
        if (FILE.exists()) return false;
        try {
            FILE.createNewFile();
            FileOutputStream out = new FileOutputStream(PATH);
            String worlds = "";
            for (World w : plugin.getServer().getWorlds())
                worlds += w.getName() + ";;";
            CONFIG.put("Worlds", worlds);
            CONFIG.put("motd", "true");
            CONFIG.put("phantom", "true");
            CONFIG.put("skills_in_protected", "false");
            CONFIG.store(out, "Worlds: Which worlds Demigods is active on (separate with double semicolons)\r\n" + "motd: True or false, whether there is a message on logging in\r\n" + "phantom: True or false, whether players become phantoms on death\r\n" + "protected: True or false, whether destructive skills can be used in protected zones");
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            Demigods.log.severe("[Demigods] Config creation error: " + e.getMessage());
        }
        return false;
    }

    public boolean readFile() {
        WORLDS = new ArrayList<String>();
        try {
            FileInputStream in = new FileInputStream(PATH);
            CONFIG.load(in);
            MOTD = Boolean.parseBoolean(CONFIG.getProperty("motd"));
            PHANTOM = Boolean.parseBoolean(CONFIG.getProperty("phantom"));
            PROTECT = Boolean.parseBoolean(CONFIG.getProperty("skills_in_protected"));
            String str = CONFIG.getProperty("Worlds");
            for (String s : str.split(";;"))
                WORLDS.add(s);
            in.close();
            return true;
        } catch (Exception e) {
            Demigods.log.severe("[Demigods] Config loading error: " + e.getMessage());
        }
        return false;
    }

    public boolean getMOTD() {
        return MOTD;
    }

    public boolean canSkillsBeUsedInProtected() {
        return PROTECT;
    }

    public boolean getPhantom() {
        return PHANTOM;
    }

    public boolean isParticipating(World w) {
        return WORLDS.contains(w.getName());
    }
}

package com.WildAmazing.marinating.demigods;

import com.WildAmazing.marinating.demigods.Utilities.DSave;
import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Listeners.DemigodsBlockListener;
import com.WildAmazing.marinating.demigods.Utilities.Listeners.DemigodsEntityListener;
import com.WildAmazing.marinating.demigods.Utilities.Listeners.DemigodsPlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Demigods extends JavaPlugin {
    private final DemigodsPlayerListener playerListener = new DemigodsPlayerListener(this);
    private final DemigodsEntityListener entityListener = new DemigodsEntityListener(this);
    private final DemigodsBlockListener blockListener = new DemigodsBlockListener(this);

    private DSave SAVE;

    private int LEVELCAP = 50;

    public Demigods() {
        super();
    }

    @Override
    public void onEnable() {
        //Counting time
        long firstTime = System.currentTimeMillis();
        //Load save
        loadMaster();
        //Hook listeners
        loadListeners();
        getLogger().info("Plugin listeners hooked.");

		/*
         * Activate utilities (BAD THINGS HAPPEN IF THIS DOESN'T WORK)
		 */
        @SuppressWarnings("unused")
        DUtil util = new DUtil(this);
        /*
		 * Set commands in general (DemigodsCommands) and then for all players' deities
		 */
        DemigodsCommands ce = new DemigodsCommands(this);
        getCommand("dg").setExecutor(ce);
        getCommand("check").setExecutor(ce);
        getCommand("transfer").setExecutor(ce);
        getCommand("alliance").setExecutor(ce);
        getCommand("checkplayer").setExecutor(ce);
        getCommand("givedeity").setExecutor(ce);
        getCommand("removedeity").setExecutor(ce);
        getCommand("forsake").setExecutor(ce);
        getCommand("setfavor").setExecutor(ce);
        getCommand("setallegiance").setExecutor(ce);
        getCommand("setlevel").setExecutor(ce);
        getCommand("setkills").setExecutor(ce);
        getCommand("setdeaths").setExecutor(ce);
        getCommand("setradius").setExecutor(ce);
        getCommand("createshrine").setExecutor(ce);
        getCommand("removeplayer").setExecutor(ce);
        getCommand("removeshrine").setExecutor(ce);
        getCommand("nameshrine").setExecutor(ce);
        getCommand("warpshrine").setExecutor(ce);
        getCommand("offer").setExecutor(ce);
        getCommand("value").setExecutor(ce);
        getCommand("shrine").setExecutor(ce);
        getCommand("shove").setExecutor(ce);
        getCommand("lightning").setExecutor(ce);
        getCommand("storm").setExecutor(ce);
        getCommand("strike").setExecutor(ce);
        getCommand("bloodthirst").setExecutor(ce);
        getCommand("crash").setExecutor(ce);
        getCommand("slow").setExecutor(ce);
        getCommand("cleave").setExecutor(ce);
        getCommand("timestop").setExecutor(ce);
        getCommand("fireball").setExecutor(ce);
        getCommand("defect").setExecutor(ce);
        getCommand("firestorm").setExecutor(ce);
        getCommand("poison").setExecutor(ce);
        getCommand("plant").setExecutor(ce);
        getCommand("detonate").setExecutor(ce);
        getCommand("entangle").setExecutor(ce);
        getCommand("chain").setExecutor(ce);
        getCommand("entomb").setExecutor(ce);
        getCommand("tartarus").setExecutor(ce);
        getCommand("reel").setExecutor(ce);
        getCommand("drown").setExecutor(ce);
        getCommand("earthquake").setExecutor(ce);
        getCommand("blast").setExecutor(ce);
        getCommand("invincible").setExecutor(ce);
        //
        getLogger().info("Loading completed in " + ((double) (System.currentTimeMillis() - firstTime) / 1000) + " seconds.");
    }

    @Override
    public void onDisable() {
        try {
            DSave.save(getDataFolder().getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            getLogger().severe("Save location error. Screenshot the stack trace and send to marinating.");
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Save write error. Screenshot the stack trace and send to marinating.");
        }
        this.getServer().getScheduler().cancelTasks(this);
        getLogger().info("Save completed and tasks cancelled.");
    }

    public void loadMaster() {
        SAVE = new DSave(getDataFolder().getPath());
    }

    public void loadListeners() {
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getPluginManager().registerEvents(blockListener, this);
    }

    public DSave getSave() {
        return SAVE;
    }

    public int getLevelCap() {
        return LEVELCAP;
    }
}


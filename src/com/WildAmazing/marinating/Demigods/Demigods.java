package com.WildAmazing.marinating.Demigods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Apollo;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Ares;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Athena;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Hades;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Hephaestus;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Poseidon;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Zeus;

import com.WildAmazing.marinating.Demigods.Deities.Titans.Atlas;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Cronus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Hyperion;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Oceanus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Prometheus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Rhea;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Themis;

import com.WildAmazing.marinating.Demigods.Listeners.DChatCommands;
import com.WildAmazing.marinating.Demigods.Listeners.DCrafting;
import com.WildAmazing.marinating.Demigods.Listeners.DDamage;
import com.WildAmazing.marinating.Demigods.Listeners.DDeities;
import com.WildAmazing.marinating.Demigods.Listeners.DLevels;
import com.WildAmazing.marinating.Demigods.Listeners.DPvP;
import com.WildAmazing.marinating.Demigods.Listeners.DShrines;

import com.clashnia.ClashniaUpdate.DemigodsUpdate;
import com.clashnia.Demigods.Deities.Giants.Typhon;


import com.massivecraft.factions.P;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Demigods extends JavaPlugin implements Listener
{
	// Soft dependencies
	protected static WorldGuardPlugin WORLDGUARD = null;
	protected static P FACTIONS = null;

	// Define variables
	public static Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/Demigods/";
	DUtil initialize;
	DSave SAVE;

	public static Deity[] deities =
		{
		
		/*
		 *  Titans
		 */
		// Parents to the Olympian Gods
		new Cronus("ADMIN"),
		new Rhea("ADMIN"),
		
		new Prometheus("ADMIN"),
		new Atlas("ADMIN"),
		new Oceanus("ADMIN"),
		new Hyperion("ADMIN"),
		new Themis("ADMIN"),
		
		/*
		 *  Olympian Gods
		 */
		// The Big Three
		new Zeus("ADMIN"),
		new Poseidon("ADMIN"),
		new Hades("ADMIN"),
		
		new Ares("ADMIN"),
		new Athena("ADMIN"),
		new Apollo("ADMIN"),
		new Hephaestus("ADMIN"),
		
		/* 	
		 *  Giants
		 */
		// Only Giant for Demigods 2
		new Typhon("ADMIN")
	};

	public Demigods()
	{
		super();
	}

	@Override
	public void onEnable()
	{
		long firstTime = System.currentTimeMillis();
		oldDownloader(); // #0 (disable our old update method) TODO
		
		log.info("[Demigods] Initializing.");
		
		new DSettings(this); // #1 (needed for DUtil to load)
		initialize = new DUtil(this); // #2 (needed for everything else to work)
		SAVE = new DSave(mainDirectory, deities); // #3 (needed to start save system)
		loadListeners(); // #4
		loadCommands(); // #5 (needed)
		initializeThreads(); // #6 (regen and etc)
		loadDependencies(); // #7 compatibility with protection plugins
		cleanUp(); // #8
		invalidShrines(); // #9
		levelPlayers(); // #10
		
		log.info("[Demigods] Attempting to load Metrics.");
		
		loadMetrics(); // #11
		unstickFireball(); // #12
		
		// Check for updates, and then update if need be		
		new DemigodsUpdate(this);
		Boolean shouldUpdate = DemigodsUpdate.shouldUpdate();
		if(shouldUpdate && DSettings.getSettingBoolean("update"))
		{
			DemigodsUpdate.demigodsUpdate();
		}
		
		log.info("[Demigods] Preparation completed in "+((double)(System.currentTimeMillis()-firstTime)/1000)+" seconds.");
	}

	@Override
	public void onDisable()
	{
		// Try to save files, if it can't, then let the Administrator know
		try
		{
			DSave.save(mainDirectory);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			log.severe("[Demigods] Save location error. Screenshot the stack trace and send to us on BukketDev.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			log.severe("[Demigods] Save write error. Screenshot the stack trace and send to us on BukketDev.");
		}
		
		// Cancel all tasks
		int c = 0;
		for (BukkitWorker bw : getServer().getScheduler().getActiveWorkers())
			if (bw.getOwner().equals(this))
				c++;
		for (BukkitTask bt : getServer().getScheduler().getPendingTasks())
			if (bt.getOwner().equals(this))
				c++;
		this.getServer().getScheduler().cancelAllTasks();
		
		log.info("[Demigods] Save completed and "+c+" tasks cancelled.");
	}
	
	@EventHandler
	public void saveOnExit(PlayerQuitEvent e)
	{
		// Save a player file when they exit, if it can't, let the Administrator know
		if (DUtil.isFullParticipant(e.getPlayer()))
			try 
			{
				DSave.save(mainDirectory);
			} 
			catch (FileNotFoundException er) 
			{
				er.printStackTrace();
				log.severe("[Demigods] Save location error. Screenshot the stack trace and send to us on BukketDev.");
			} 
			catch (IOException er) 
			{
				er.printStackTrace();
				log.severe("[Demigods] Save write error. Screenshot the stack trace and send to us on BukketDev.");
			}
	}

	public void loadDependencies()
	{
		// Check for the WorldGuard plugin
		Plugin pg = getServer().getPluginManager().getPlugin("WorldGuard");
		if ((pg != null) && (pg instanceof WorldGuardPlugin))
		{
			WORLDGUARD = (WorldGuardPlugin)pg;
			if (!DSettings.getSettingBoolean("allow_skills_everywhere"))
				log.info("[Demigods] WorldGuard detected. Skills are disabled in no-PvP zones.");
		}
		
		// Check for the Factions plugin
		pg = getServer().getPluginManager().getPlugin("Factions");
		if (pg != null)
		{
			FACTIONS = ((P)pg);
			if (!DSettings.getSettingBoolean("allow_skills_everywhere"))
				log.info("[Demigods] Factions detected. Skills are disabled in peaceful zones.");
		}
		
		// Check to see if a player has the SimpleNotice client mod installed
		getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");
	}
	
	public void loadMetrics()
	{
		new DMetrics(this);
		DMetrics.allianceStatsPastWeek();
		DMetrics.allianceStatsAllTime();
	}

	public void loadCommands()
	{
		// Register the command manager
		DCommandExecutor ce = new DCommandExecutor(this);
		
		/*
		 *  General commands
		 */
		getCommand("dg").setExecutor(ce);
		getCommand("check").setExecutor(ce);
		getCommand("claim").setExecutor(ce);
		getCommand("alliance").setExecutor(ce);
		getCommand("perks").setExecutor(ce); //TODO: PERKS
		getCommand("value").setExecutor(ce);
		getCommand("bindings").setExecutor(ce);
		getCommand("forsake").setExecutor(ce);
		getCommand("adddevotion").setExecutor(ce);
		
		/*
		 *  Admin Commands
		 */
		getCommand("checkplayer").setExecutor(ce);
		getCommand("removeplayer").setExecutor(ce);
		getCommand("debugplayer").setExecutor(ce);
		getCommand("setallegiance").setExecutor(ce);
		getCommand("getfavor").setExecutor(ce);
		getCommand("setfavor").setExecutor(ce);
		getCommand("addfavor").setExecutor(ce);
		getCommand("getmaxfavor").setExecutor(ce);
		getCommand("setmaxfavor").setExecutor(ce);
		getCommand("addmaxfavor").setExecutor(ce);
		getCommand("givedeity").setExecutor(ce);
		getCommand("removedeity").setExecutor(ce);
		getCommand("addunclaimeddevotion").setExecutor(ce);
		getCommand("getdevotion").setExecutor(ce);
		getCommand("setdevotion").setExecutor(ce);;
		getCommand("addhp").setExecutor(ce);
		getCommand("sethp").setExecutor(ce);
		getCommand("setmaxhp").setExecutor(ce);
		getCommand("getascensions").setExecutor(ce);
		getCommand("setascensions").setExecutor(ce);
		getCommand("addascensions").setExecutor(ce);
		getCommand("setkills").setExecutor(ce);
		getCommand("setdeaths").setExecutor(ce);
		getCommand("exportdata").setExecutor(ce);
		
		//getCommand("setlore").setExecutor(ce);

		/*
		 *  Shrine commands
		 */
		getCommand("shrine").setExecutor(ce);
		getCommand("shrinewarp").setExecutor(ce);
		getCommand("forceshrinewarp").setExecutor(ce);
		getCommand("shrineowner").setExecutor(ce);
		getCommand("removeshrine").setExecutor(ce);
		getCommand("fixshrine").setExecutor(ce);
		getCommand("listshrines").setExecutor(ce);
		getCommand("nameshrine").setExecutor(ce);
		
		/*
		 *  Deity Commands
		 */
		// Zeus
		getCommand("shove").setExecutor(ce);
		getCommand("lightning").setExecutor(ce);
		getCommand("storm").setExecutor(ce);
		
		// Ares
		getCommand("strike").setExecutor(ce);
		getCommand("bloodthirst").setExecutor(ce);
		getCommand("crash").setExecutor(ce);
		
		// Cronus
		getCommand("slow").setExecutor(ce);
		getCommand("cleave").setExecutor(ce);
		getCommand("timestop").setExecutor(ce);
		
		// Prometheus
		getCommand("fireball").setExecutor(ce);
		getCommand("blaze").setExecutor(ce);
		getCommand("firestorm").setExecutor(ce);
		
		// Rhea
		getCommand("poison").setExecutor(ce);
		getCommand("plant").setExecutor(ce);
		getCommand("detonate").setExecutor(ce);
		getCommand("entangle").setExecutor(ce);
		
		// Hades
		getCommand("chain").setExecutor(ce);
		getCommand("entomb").setExecutor(ce);
		getCommand("tartarus").setExecutor(ce);
		
		// Poseidon
		getCommand("reel").setExecutor(ce);
		getCommand("drown").setExecutor(ce);
		//getCommand("waterfall").setExecutor(ce);
		
		// Atlas
		getCommand("unburden").setExecutor(ce);
		getCommand("invincible").setExecutor(ce);
		
		// Athena
		getCommand("flash").setExecutor(ce);
		getCommand("ceasefire").setExecutor(ce);
		
		// Oceanus
		getCommand("squid").setExecutor(ce);
		getCommand("makeitrain").setExecutor(ce);
		
		// Hyperion
		getCommand("starfall").setExecutor(ce);
		getCommand("smite").setExecutor(ce);
		
		// Hephaestus
		getCommand("reforge").setExecutor(ce);
		getCommand("shatter").setExecutor(ce);
		
		// Apollo
		getCommand("cure").setExecutor(ce);
		getCommand("finale").setExecutor(ce);
		
		// Themis
		getCommand("swap").setExecutor(ce);
		getCommand("congregate").setExecutor(ce);
		getCommand("assemble").setExecutor(ce);
		
		// Typhon
		getCommand("charge").setExecutor(ce);
	}

	public void loadListeners()
	{
		getServer().getPluginManager().registerEvents(new DLevels(), this);
		getServer().getPluginManager().registerEvents(new DChatCommands(), this);
		getServer().getPluginManager().registerEvents(new DDamage(), this);
		getServer().getPluginManager().registerEvents(new DPvP(), this);
		getServer().getPluginManager().registerEvents(new DShrines(), this);
		getServer().getPluginManager().registerEvents(new DDeities(), this);
		getServer().getPluginManager().registerEvents(new DCrafting(), this);
		getServer().getPluginManager().registerEvents(new Hephaestus("LISTENER"), this);
	}

	private void initializeThreads()
	{
		// Setup threads for saving, health, and favor
		int startdelay = (int)(DSettings.getSettingDouble("start_delay_seconds")*20);
		int favorfrequency = (int)(DSettings.getSettingDouble("favor_regen_seconds")*20);
		int hpfrequency = (int)(DSettings.getSettingDouble("hp_regen_seconds")*20);
		int savefrequency = DSettings.getSettingInt("save_interval_seconds")*20;
		if (hpfrequency < 0) hpfrequency = 600;
		if (favorfrequency < 0) favorfrequency = 600;
		if (startdelay <= 0) startdelay = 1;
		if (savefrequency <= 0) savefrequency = 300;
		
		// Favor
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for (World w : DSettings.getEnabledWorlds())
				{
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p))
						{
							int regenrate = DUtil.getAscensions(p); //TODO: PERK UPGRADES THIS
							if (regenrate < 1) regenrate = 1;
							DUtil.setFavorQuiet(p.getName(), DUtil.getFavor(p)+regenrate);
						}
				}
			}
		}, startdelay, favorfrequency);
		
		// Health regeneration
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for (World w : DSettings.getEnabledWorlds())
				{
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p))
						{
							if ((p.getHealth() < 1) || (DUtil.getHP(p) < 1)) continue;
							int heal = 1; //TODO: PERK UPGRADES THIS
							if (heal < 1) heal = 1;
							if (DUtil.getHP(p) < DUtil.getMaxHP(p))
								DUtil.setHPQuiet(p.getName(), DUtil.getHP(p)+heal);
						}
				}
			}
		}, startdelay, hpfrequency);
		
		// Health sync
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run() {
				if (DSettings.getEnabledWorlds() == null) return;
				for (World w : DSettings.getEnabledWorlds())
				{
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p))
							if (p.getHealth() > 0)
								DDamage.syncHealth(p);
				}
			}
		}, startdelay, 2);
		
		// Data save
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					DSave.save(mainDirectory);
					log.info("[Demigods] Saved data for "+DUtil.getFullParticipants().size()+" Demigods players. "+DSave.getCompleteData().size()+" files total.");
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
					log.severe("[Demigods] Save location error. Screenshot the stack trace and send to marinating.");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					log.severe("[Demigods] Save write error. Screenshot the stack trace and send to marinating.");
				}
			}
		}, startdelay, savefrequency);
		
		// Information display
		int frequency = (int)(DSettings.getSettingDouble("stat_display_frequency_in_seconds")*20);
		if (frequency > 0)
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				@Override
				public void run()
				{
					for (World w : DSettings.getEnabledWorlds())
					{
						for (Player p : w.getPlayers())
							if (DUtil.isFullParticipant(p))
								if (p.getHealth() > 0) {
									ChatColor color = ChatColor.GREEN;
									if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.25) color = ChatColor.RED;
									else if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.5) color = ChatColor.YELLOW;
									String str = "-- HP "+color+""+DUtil.getHP(p)+"/"+DUtil.getMaxHP(p)+ChatColor.YELLOW+" Favor "+DUtil.getFavor(p)+"/"+
									DUtil.getFavorCap(p);
									p.sendMessage(str);
								}
					}
				}
			}, startdelay, frequency);
		}
	}

	private void oldDownloader()
	{
	    try
	    {
	    	// Define variables
	    	Plugin demigodDownloader = Bukkit.getPluginManager().getPlugin("DemigodDownloader");
			String demigodDownloaderPath = demigodDownloader.getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(5);
			String OS = System.getProperty("os.name");
			
			log.info("[DemigodDownloader] " + demigodDownloaderPath);
	    	
	    	// Disable old downloader plugin
			Bukkit.getServer().getPluginManager().disablePlugin(demigodDownloader);
			
			// Set the downloader to a variable for deletion
	    	File oldDownloader = new File(demigodDownloaderPath);
	    	
	    	// Check file existence and go from there
	    	if(!oldDownloader.exists())
	    	{
	    		// Can't find downloader, let the administrator know
	    		log.severe("[DemigodDownloader] Can't find the correct jar file...");
	    		log.warning("[DemigodDownloader] Please manually remove the DemigodDownloader, it's obsolete.");
	    	}
	    	else
	    	{
	    		// Attempt to delete downloader
	    		boolean success = oldDownloader.delete();
	    		if(success)
	    		{
	    			log.info("[DemigodDownloader] Deleting old download method, just relax. :)");
	    		}
	    		else if (OS.contains("windows") || OS.contains("Windows"))
	    		{
	    			log.warning("[DemigodDownloader] Windows does not allow deletion of files that are in use.");
	    			log.warning("[DemigodDownloader] Please manually remove the DemigodDownloader while the server is off.");
	    		}
	    		else
	    		{
	    			log.severe("[DemigodDownloader] There was an error when deleting the downloader.  Do you have permission?");
	    			log.warning("[DemigodDownloader] Please manually remove the DemigodDownloader, it's obsolete.");
	    		}
	    	}
	    	
		}
	    catch (NullPointerException e)
	    {
			// Plugin doesn't exist, do nothing
		}
	}
	
	private void cleanUp()
	{
		// Clean things that may cause glitches
		for (String player : DUtil.getFullParticipants())
		{
			for (Deity d : DUtil.getDeities(player))
			{
				if (DSave.hasData(player, d.getName().toUpperCase()+"_TRIBUTE_"))
				{
					DSave.removeData(player, d.getName().toUpperCase()+"_TRIBUTE_");
				}
			}
		}
	}
	
	/**
	private void updateSave()
	{
		// Updating to 1.1
		HashMap<String, HashMap<String, Object>> copy = DSave.getCompleteData();
		String updated = "[Demigods] Updated players:";
		boolean yes = false;
		for (String player : DSave.getCompleteData().keySet())
		{
			if (DSave.hasData(player, "dEXP"))
			{ // Coming from pre 1.1
				yes = true;
				copy.get(player).remove("dEXP");
				if (DSave.hasData(player, "LEVEL"))
					copy.get(player).remove("LEVEL");
				for (Deity d : DUtil.getDeities(player))
				{
					copy.get(player).put(d.getName()+"_dvt", (int)Math.ceil((500*Math.pow(DUtil.getAscensions(player), 1.98))/DUtil.getDeities(player).size()));
				}
				if (!DSave.hasData(player, "A_EFFECTS"))
					DUtil.setActiveEffects(player, new HashMap<String, Long>());
				if (!DSave.hasData(player, "P_SHRINES"))
					DUtil.setShrines(player, new HashMap<String, WriteLocation>());
				updated += " "+player;
			}
		}
		if (yes)
			log.info(updated);
		DSave.overwrite(copy);
	}
	*/
	
	private void invalidShrines()
	{
		// Remove invalid shrines
		Iterator<WriteLocation> i = DUtil.getAllShrines().iterator();
		ArrayList<String> worldnames = new ArrayList<String>();
		for (World w : getServer().getWorlds())
			worldnames.add(w.getName());
		int count = 0;
		while (i.hasNext()) {
			WriteLocation n = i.next();
			if (!worldnames.contains(n.getWorld()) || (n.getY() < 0) || (n.getY() > 256)) {
				count++;
				DUtil.removeShrine(n);
			}
		}
		if (count > 0)
			log.info("[Demigods] Removed "+count+" invalid shrines.");
	}
	
	private void levelPlayers()
	{
		// Level players		
		for (String player : DSave.getCompleteData().keySet())
			DLevels.levelProcedure(player);
	}
	
	private void unstickFireball()
	{
		// Unstick Prometheus fireballs
		for (World w : DSettings.getEnabledWorlds())
		{
			Iterator<Entity> it = w.getEntities().iterator();
			while (it.hasNext())
			{
				Entity e = it.next();
				if (e instanceof Fireball)
				{
					e.remove();
					it.remove();
				}
			}
		}
	}
}

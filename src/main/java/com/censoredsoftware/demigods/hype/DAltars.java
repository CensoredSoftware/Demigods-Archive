package com.censoredsoftware.demigods.hype;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;

import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Util.DSave;
import com.WildAmazing.marinating.Demigods.Util.DSettings;

public class DAltars implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(final ChunkLoadEvent event)
	{
		if(!DSettings.getEnabledWorlds().contains(event.getWorld())) return;

		// Define variables
		final Location location = randomChunkLocation(event.getChunk());

		// Return a random boolean based on the chance of Altar generation
		if(!DSave.altarNearby(location) && randomPercentBool(3.14))
		{
			if(AltarDemo.ALTAR.generate(location, true))
			{
				DSave.saveAltar(location);
				DSave.saveAltarCenter(location.clone().add(0, 2, 0));

				location.getWorld().strikeLightningEffect(location);
				location.getWorld().strikeLightningEffect(location);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DMiscUtil.getPlugin(), new Runnable()
				{
					@Override
					public void run()
					{
						for(Entity entity : event.getWorld().getEntities())
						{
							if(entity instanceof Player)
							{
								if(entity.getLocation().distance(location) < 400)
								{
									((Player) entity).sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "A mysterious structure has spawned near you...");
								}
							}
						}
					}
				}, 1);
			}
		}
	}

	@EventHandler
	public void onClickAltar(PlayerInteractEvent event)
	{
		if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if(!DSave.getAllAltarCenters().contains(event.getClickedBlock().getLocation())) return;
		event.setCancelled(true);

		if(DMiscUtil.hasDeity(event.getPlayer(), "?????"))
		{
			// open the tribute inventory
			Inventory ii = DMiscUtil.getPlugin().getServer().createInventory(event.getPlayer(), 27, "Tributes");
			event.getPlayer().openInventory(ii);
			DSave.saveData(event.getPlayer(), "?????" + "_TRIBUTE_", "?????");
		}
		else if(DMiscUtil.isFullParticipant(event.getPlayer()))
		{
			DMiscUtil.giveDeitySilent(event.getPlayer().getName(), new Secret(event.getPlayer().getName()));
			event.getPlayer().sendMessage(ChatColor.YELLOW + "A mysterious energy flows through you.");
			event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
			for(int i = 0; i < 20; i++)
				event.getPlayer().getWorld().spawn(event.getPlayer().getLocation(), ExperienceOrb.class);
		}
	}

	public static Location randomChunkLocation(Chunk chunk)
	{
		Location reference = chunk.getBlock(generateIntRange(1, 16), 64, generateIntRange(1, 16)).getLocation();
		double locX = reference.getX();
		double locY = chunk.getWorld().getHighestBlockYAt(reference);
		double locZ = reference.getZ();
		return new Location(chunk.getWorld(), locX, locY, locZ);
	}

	public static int generateIntRange(int min, int max)
	{
		return new Random().nextInt(max - min + 1) + min;
	}

	public static boolean randomPercentBool(double percent)
	{
		if(percent <= 0.0) return false;
		Random rand = new Random();
		int chance = rand.nextInt(Math.abs((int) Math.ceil(1.0 / (percent / 100.0))) + 1);
		return chance == 1;
	}
}

package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;

import com.WildAmazing.marinating.Demigods.DMiscUtil;
import com.WildAmazing.marinating.Demigods.DSave;
import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.DUpdateUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DDeities implements Listener
{
	/*
	 * Distributes all events to deities
	 */
	public DDeities()
	{
		DMiscUtil.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DMiscUtil.getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				for(String name : DMiscUtil.getFullParticipants())
				{
					Player p = DMiscUtil.getOnlinePlayer(name);
					if((p != null) && p.isOnline())
					{
						if(DSettings.getEnabledWorlds().contains(p.getWorld()))
						{
							for(Deity d : DMiscUtil.getDeities(p))
								d.onTick(System.currentTimeMillis());
						}
					}
				}
			}
		}, 20, 5);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld())) return;
		// Player
		Player p = e.getPlayer();
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld())) return;
		// Player
		Player p = e.getPlayer();
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	public static void onEntityDamage(EntityDamageEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
		for(Player pl : e.getEntity().getWorld().getPlayers())
		{
			if(DMiscUtil.isFullParticipant(pl))
			{
				if((DMiscUtil.getDeities(pl) != null) && (DMiscUtil.getDeities(pl).size() > 0))
				{
					for(Deity d : DMiscUtil.getDeities(pl))
						d.onEvent(e);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
			{
				for(Deity d : DMiscUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
		if(e.isCancelled()) return;
		if(e.getTarget() instanceof Player)
		{
			Player p = (Player) e.getTarget();
			if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
			{
				for(Deity d : DMiscUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if(!DSettings.getEnabledWorlds().contains(e.getPlayer().getWorld())) return;
		Player p = e.getPlayer();
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{ // sync to master file
		final Player p = e.getPlayer();
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if(DSettings.getSettingBoolean("motd"))
		{
			p.sendMessage("This server is running Demigods v" + ChatColor.YELLOW + DMiscUtil.getPlugin().getDescription().getVersion() + ChatColor.WHITE + ".");
			p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "/dg" + ChatColor.GRAY + " for more info.");
		}

		/*
		 * Update Notify
		 */
		if(DSettings.getSettingBoolean("update_notify") && DMiscUtil.hasPermissionOrOP(p, "demigods.admin"))
		{
			String latest = DUpdateUtil.getLatestVersion();
			if(latest.startsWith("3"))
			{
				p.sendMessage(ChatColor.RED + "There is a new, stable" + ChatColor.DARK_GREEN + " upgrade release " + ChatColor.RED + "for Demigods.");
				if(DSettings.getSettingBoolean("update")) p.sendMessage(ChatColor.RED + "You cannot automatically upgrade to version " + latest + ".");
				p.sendMessage(ChatColor.RED + "Visit BukkitDev and download Demigods after reading the special instructions.");
				p.sendMessage(ChatColor.RED + "BukkitDev: " + ChatColor.GREEN + "dev.bukkit.org/server-mods/demigods");
			}
			else if(DUpdateUtil.check())
			{
				p.sendMessage(ChatColor.RED + "There is a new, stable release for Demigods.");
				if(DSettings.getSettingBoolean("update")) p.sendMessage("Please " + ChatColor.YELLOW + "reload the server " + ChatColor.WHITE + "ASAP to finish an auto-update.");
				else p.sendMessage("Please update ASAP by using " + ChatColor.YELLOW + "/dg update");
			}
		}

		if(!DSave.hasPlayer(p))
		{
			Logger.getLogger("Minecraft").info("[Demigods] " + p.getName() + " joined and no save was detected. Creating new file.");
			DSave.addPlayer(p);
		}
		if(DSave.hasData(p, "CHARGE"))
		{
			DSave.saveData(p, "CHARGE", System.currentTimeMillis());
			p.sendMessage(ChatColor.YELLOW + "Your charging attack has been reset.");
		}

		DSave.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e)
	{
		Player p = e.getPlayer();
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		if(DMiscUtil.isFullParticipant(p)) if(DSave.hasData(p, "ALLIANCECHAT"))
		{
			if((Boolean) DSave.getData(p, "ALLIANCECHAT"))
			{
				e.setCancelled(true);
				Logger.getLogger("Minecraft").info("[" + DMiscUtil.getAllegiance(p) + "] " + p.getName() + ": " + e.getMessage());
				for(Player pl : DMiscUtil.getPlugin().getServer().getOnlinePlayers())
				{
					if(DMiscUtil.isFullParticipant(pl) && DMiscUtil.getAllegiance(pl).equalsIgnoreCase(DMiscUtil.getAllegiance(p))) pl.sendMessage(ChatColor.GREEN + "[" + DMiscUtil.getAscensions(p) + "] " + p.getName() + ": " + e.getMessage());
				}
			}
		}
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		if((DMiscUtil.getDeities(p) != null) && (DMiscUtil.getDeities(p).size() > 0))
		{
			for(Deity d : DMiscUtil.getDeities(p))
				d.onEvent(e);
		}
	}
}

package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Prometheus;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class PrometheusCommands
{
	public static void onPlayerInteractPrometheus(PlayerInteractEvent e, Demigods plugin)
	{
		Player player = e.getPlayer();
		if(!plugin.isTitan(player)) return;
		if(!(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) return;
		TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
		if(!tpi.isAlive()) return;
		Prometheus p = (Prometheus) tpi.getDeity(Divine.PROMETHEUS);
		if(player.getItemInHand().getType() != Material.AIR)
		{
			if(player.getItemInHand().getType() == p.getFireballItem())
			{
				fireballcode(tpi, player, p, plugin);
				return;
			}
		}
		if(p.getFireball()) fireballcode(tpi, player, p, plugin);
	}

	public static void onEnablePrometheus(final World w, final Demigods plugin)
	{
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if(w == null) return;
				for(Player p : w.getPlayers())
				{
					if(p.isOnline()) if(plugin.isTitan(p))
					{
						if(plugin.getInfo(p).hasDeity(Divine.PROMETHEUS))
						{
							if(plugin.getInfo(p).isAlive())
							{
								if(p.getHealth() < 20)
								{
									p.setHealth(p.getHealth() + 1);
								}
							}
						}
					}
				}
			}
		}, 120, 120);
	}

	public static void onPlayerCommandPreprocessPrometheus(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.PROMETHEUS))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Prometheus p = (Prometheus) tpi.getDeity(Divine.PROMETHEUS);
				String[] sects = event.getMessage().split(" +", 2);
				String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
				int bind = 0;
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("bind")) bind = 1;
					else if(args[0].equalsIgnoreCase("unbind")) bind = -1;
				}
				Commands cmd;
				try
				{
					cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
				}
				catch(Exception ex)
				{
					return;
				}
				try
				{
					switch(cmd)
					{
						case FIREBALL:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(p.getFireballItem() != player.getItemInHand().getType())
									{
										p.setFireballItem(player.getItemInHand().getType());
										player.sendMessage("Fireball " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Fireball " + ChatColor.YELLOW + "is already bound to " + p.getFireballItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Fireball " + ChatColor.YELLOW + "is no longer bound to " + p.getFireballItem().name() + ".");
									p.setFireballItem(Material.AIR);
								}
								break;
							}
							if(p.getFireball())
							{
								player.sendMessage("Fireball has been toggled off.");
								p.setFireball(false);
							}
							else
							{
								player.sendMessage("Fireball has been turned on.");
								p.setFireball(true);
							}
							break;
						case FIRESTORM:
							if(tpi.getPower() < 1000)
							{
								player.sendMessage("This skill requires 1000 Power.");
								break;
							}
							firestorm(player, plugin);
							player.sendMessage(ChatColor.RED + "Prometheus" + ChatColor.WHITE + " rains fire upon your enemies.");
							tpi.setPower(tpi.getPower() - 1000);
							break;
					}
				}
				catch(NoSuchMethodError ex)
				{
					player.sendMessage("The plugin for " + sects[0].toLowerCase() + " is broken or out of date.");
				}
				catch(Exception ex)
				{
					player.sendMessage("�cError: " + ex.getMessage());
				}
				event.setCancelled(true);
			}
		}
	}

	private enum Commands
	{
		FIREBALL, FIRESTORM
	}

	private static void shootFireball(Location from, Location to, Player player)
	{
		Location blockLoc = to;
		blockLoc.setX(blockLoc.getX() + .5);
		blockLoc.setY(blockLoc.getY() + .5);
		blockLoc.setZ(blockLoc.getZ() + .5);
		Vector path = blockLoc.toVector().subtract(from.toVector());
		Fireball fireball = (Fireball) to.getWorld().spawnEntity(path.toLocation(to.getWorld()), EntityType.FIREBALL);
		Vector v = from.toVector().add(from.getDirection().multiply(2));
		fireball.teleport(v.toLocation(to.getWorld()));
	}

	private static void firestorm(Player pl, Demigods plugin)
	{
		ArrayList<Entity> entitylist = new ArrayList<Entity>();
		Vector ploc = pl.getLocation().toVector();
		for(Entity anEntity : pl.getWorld().getEntities())
		{
			if(anEntity.getLocation().toVector().isInSphere(ploc, 50.0) && anEntity.getUniqueId() != pl.getUniqueId()) entitylist.add(anEntity);
		}
		final Player p = pl;
		final ArrayList<Entity> enList = entitylist;
		for(int i = 0; i <= 100; i += 20)
		{
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					for(Entity eee : enList)
					{
						try
						{
							LivingEntity e1 = (LivingEntity) eee;
							Location up = new Location(e1.getWorld(), e1.getLocation().getX() + Math.random(), 126, e1.getLocation().getZ() + Math.random());
							up.setPitch(90);
							shootFireball(up, new Location(e1.getWorld(), e1.getLocation().getX() + Math.random(), e1.getLocation().getY(), e1.getLocation().getZ() + Math.random()), p);
						}
						catch(Exception notAlive)
						{} // ignore stuff like minecarts
					}
				}
			}, i);
		}
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!plugin.isTitan(p)) return;
			if(!plugin.getInfo(p).hasDeity(Divine.PROMETHEUS)) return;
			if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK)
			{
				if(p.getFireTicks() > 0) p.setFireTicks(0);
				e.setCancelled(true);
			}
		}
	}

	private static void fireballcode(TitanPlayerInfo tpi, Player player, Prometheus p, Demigods plugin)
	{
		if(tpi.getPower() < 15)
		{
			player.sendMessage("You need at least 15 Power to use this ability.");
			p.setFireball(false);
			return;
		}
		Block target = player.getTargetBlock(null, 125);
		if(target == null || target.getType() == Material.AIR || target.getType() == Material.BEDROCK)
		{
			player.sendMessage("Invalid target.");
		}
		else
		{
			shootFireball(player.getEyeLocation(), target.getLocation(), player);
			if(target.getType() != Material.BEDROCK && plugin.isUnprotected(target.getLocation())) target.setType(Material.FIRE);
			tpi.setPower(tpi.getPower() - 15);
		}
	}
}

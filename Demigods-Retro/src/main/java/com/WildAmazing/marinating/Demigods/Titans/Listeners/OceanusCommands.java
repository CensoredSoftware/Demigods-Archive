package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Oceanus;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class OceanusCommands
{
	public static void onEnableOceanus(final World w, final Demigods plugin)
	{
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if(w == null) return;
				if(w.hasStorm()) for(Player p : w.getPlayers())
				{
					if(p.isOnline()) if(plugin.isTitan(p))
					{
						if(plugin.getInfo(p).hasDeity(Divine.OCEANUS))
						{
							if(p.getHealth() < 20) p.setHealth(p.getHealth() + 1);
						}
					}
				}
			}
		}, 40, 40);
	}

	public static void onPlayerInteract(PlayerInteractEvent e, Demigods plugin)
	{
		Player p = e.getPlayer();
		if(!plugin.isTitan(p)) return;
		if(!plugin.getInfo(p).hasDeity(Divine.OCEANUS)) return;
		if(!plugin.getInfo(p).isAlive()) return;
		Oceanus o = (Oceanus) plugin.getInfo(p).getDeity(Divine.OCEANUS);
		if(p.getItemInHand().getType() != Material.AIR)
		{
			if(p.getItemInHand().getType() == o.getSquidItem())
			{
				squidfire(p, plugin);
				return;
			}
		}
		if(o.getSquid()) squidfire(p, plugin);
	}

	public static void onPlayerInteractEntity(PlayerInteractEntityEvent e, Demigods plugin)
	{
		Player p = e.getPlayer();
		if(!plugin.isTitan(p)) return;
		if(!plugin.getInfo(p).hasDeity(Divine.OCEANUS)) return;
		if(!plugin.getInfo(p).isAlive()) return;
		if(p.getItemInHand().getType() != Material.AIR) return;
		try
		{
			LivingEntity le = (LivingEntity) e.getRightClicked();
			if(le.getLocation().getBlock().getType() == Material.AIR)
			{
				le.getLocation().getBlock().setType(Material.WATER);
				le.getLocation().getBlock().setData((byte) (0x8));
			}
		}
		catch(Exception incorrect)
		{}
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		try
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				Player p = (Player) ee.getDamager();
				if(!plugin.isTitan(p)) return;
				if(!plugin.getInfo(p).hasDeity(Divine.OCEANUS)) return;
				if(!plugin.getInfo(p).isAlive()) return;
				if(p.getItemInHand().getType() != Material.AIR) return;
				LivingEntity le = (LivingEntity) e.getEntity();
				if(le.getLocation().getBlock().getType() == Material.WATER || le.getLocation().getBlock().getType() == Material.STATIONARY_WATER)
				{
					e.setDamage(5);
					p.sendMessage(ChatColor.YELLOW + "Critical hit!");
				}
			}
		}
		catch(Exception ex)
		{}
	}

	public static void onPlayerCommandPreprocessOceanus(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.OCEANUS))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Oceanus p = (Oceanus) tpi.getDeity(Divine.OCEANUS);
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
						case SQUID:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(p.getSquidItem() != player.getItemInHand().getType())
									{
										p.setSquidItem(player.getItemInHand().getType());
										player.sendMessage("Squid " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Squid " + ChatColor.YELLOW + "is already bound to " + p.getSquidItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Squid " + ChatColor.YELLOW + "is no longer bound to " + p.getSquidItem().name() + ".");
									p.setSquidItem(Material.AIR);
								}
								break;
							}
							if(p.getSquid())
							{
								player.sendMessage("Squid has been toggled off.");
								p.setSquid(false);
							}
							else
							{
								player.sendMessage("Squid has been turned on.");
								p.setSquid(true);
							}
							break;
						case MAKEITRAIN:
							if(!player.getWorld().hasStorm())
							{
								if(tpi.getPower() < 250)
								{
									player.sendMessage("This ability costs 250 Power.");
									break;
								}
								player.getWorld().setStorm(true);
								for(Player pl : player.getWorld().getPlayers())
								{
									pl.sendMessage(ChatColor.DARK_AQUA + "Oceanus" + ChatColor.GRAY + " has started a rainstorm in this world.");
								}
								tpi.setPower(tpi.getPower() - 250);
							}
							else player.sendMessage("It's already raining in this world.");
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
		SQUID, MAKEITRAIN
	}

	private static void squidfire(Player p, final Demigods plugin)
	{
		TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
		if(tpi.getPower() < 75)
		{
			p.sendMessage("This skill costs 75 Power.");
			return;
		}
		LivingEntity target = null;
		for(Block b : p.getLineOfSight(null, 200))
		{
			for(Entity eee : b.getChunk().getEntities())
			{
				try
				{
					LivingEntity e1 = (LivingEntity) eee;
					if(e1.getLocation().toVector().distance(b.getLocation().toVector()) < 3) // jumps to the nearest entity
					{
						if(e1 instanceof Player)
						{
							Player ptemp = (Player) e1;
							if(!ptemp.equals(p))
							{
								target = e1;
								break;
							}
						}
						else target = e1;
					}
				}
				catch(Exception notAlive)
				{} // ignore stuff like minecarts
			}
		}
		if(target == null || target.equals(p))
		{
			p.sendMessage("No valid target found.");
			return;
		}
		if(!plugin.isUnprotected(target.getLocation()))
		{
			p.sendMessage("That is a protected area.");
			return;
		}
		p.getWorld().spawnCreature(p.getEyeLocation(), CreatureType.SQUID);
		Squid s = null;
		for(LivingEntity e : p.getWorld().getLivingEntities())
		{
			if(e instanceof Squid) if(e.getLocation().toVector().distance(p.getEyeLocation().toVector()) < 1) s = (Squid) e;
		}
		if(s == null) return;
		s.setTarget(target);
		Vector v = p.getLocation().toVector();
		Vector victor = target.getLocation().toVector().subtract(v);
		s.setVelocity(victor);
		p.sendMessage(ChatColor.DARK_AQUA + "Squid missile fired. " + ChatColor.WHITE + "Detonation in 3 seconds.");
		final Squid ss = s;
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				if(plugin.isUnprotected(ss.getLocation()))
				{
					if(ss != null) ss.getWorld().createExplosion(ss.getLocation(), 3);
				}
			}
		}, 60);
		tpi.setPower(tpi.getPower() - 75);
	}
}

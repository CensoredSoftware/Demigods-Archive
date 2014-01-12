package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Typhon;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TyphonCommands
{
	public static void onPlayerInteractTyphon(PlayerInteractEvent e, Demigods plugin)
	{
		Player player = e.getPlayer();
		if(!plugin.isTitan(player)) return;
		TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
		if(!tpi.isAlive())
		{
			return;
		}
		Typhon t = (Typhon) tpi.getDeity(Divine.TYPHON);
		if(e.getAction() != Action.PHYSICAL)
		{
			if(player.getItemInHand().getType() != Material.AIR)
			{
				if(player.getItemInHand().getType() == t.getChargeItem())
				{
					charge(player, tpi, plugin);
					return;
				}
			}
			if(t.getCharge()) charge(player, tpi, plugin);
		}
	}

	public static void onPlayerCommandPreprocessTyphon(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.TYPHON))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Typhon t = (Typhon) tpi.getDeity(Divine.TYPHON);
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
						case CHARGE:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(t.getChargeItem() != player.getItemInHand().getType())
									{
										t.setChargeItem(player.getItemInHand().getType());
										player.sendMessage("Charge " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Charge " + ChatColor.YELLOW + "is already bound to " + t.getChargeItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Charge " + ChatColor.YELLOW + "is no longer bound to " + t.getChargeItem().name() + ".");
									t.setChargeItem(Material.AIR);
								}
								break;
							}
							if(t.getCharge())
							{
								t.setCharge(false);
								player.sendMessage("Charge has been disabled.");
							}
							else
							{
								t.setCharge(true);
								player.sendMessage("Charge was enabled.");
							}
							break;
						case FURY:
							fury(player, plugin, t);
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
		CHARGE, FURY
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		try
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				Player p = (Player) ee.getDamager();
				LivingEntity le = (LivingEntity) e.getEntity();
				if(plugin.isTitan(p))
				{
					if(!plugin.getInfo(p).isAlive()) return;
					if(plugin.getInfo(p).hasDeity(Divine.TYPHON))
					{
						Vector v = p.getLocation().toVector();
						Vector victor = le.getLocation().toVector().subtract(v);
						victor.multiply(0.6);
						if(le instanceof Player)
						{
							Player pl = (Player) le;
							if(plugin.isTitan(pl) || plugin.isGod(pl))
							{
								if(!plugin.getInfo(pl).isAlive()) return;
							}
						}
						le.setVelocity(victor); // super kb
					}
				}
			}
		}
		catch(Exception notthatevent)
		{}
		/*
		 * if (e.getEntity() instanceof Player){
		 * if (plugin.isTitan((Player)e.getEntity())){
		 * Player p = (Player)e.getEntity();
		 * if (plugin.getInfo(p).hasDeity(Divine.TYPHON)){
		 * if (p.getHealth()==20){
		 * if (e.getDamage()<4)
		 * e.setDamage(0); //immune to small damage if at full hp
		 * }
		 * }
		 * }
		 * }
		 */
	}

	public static void onEntityDeath(EntityDeathEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!plugin.isTitan(p)) return;
			if(!plugin.getInfo(p).hasDeity(Divine.TYPHON)) return;
			if(!plugin.getInfo(p).isAlive()) return;
			if(plugin.isUnprotected(p.getLocation())) p.getWorld().createExplosion(p.getLocation(), 7);
		}
	}

	private static void charge(Player p, TitanPlayerInfo tpi, Demigods plugin)
	{
		LivingEntity target = null;
		if(tpi.getPower() >= 20)
		{
			for(Block b : p.getLineOfSight(null, 200))
			{
				for(Entity ee : b.getWorld().getLivingEntities())
				{
					LivingEntity e1 = (LivingEntity) ee;
					if(e1.getLocation().toVector().distance(b.getLocation().toVector()) < 3) // jumps to the nearest entity
					{
						if(e1 instanceof Player)
						{
							Player ptemp = (Player) e1;
							if(!plugin.isTitan(ptemp))
							{
								target = ptemp;
								break;
							}
						}
						else target = e1;
						break;
					}
				}
			}
			if(target == null) return;
			if(p.getLocation().toVector().distance(target.getLocation().toVector()) > 2)
			{
				try
				{
					float pitch = p.getLocation().getPitch();
					float yaw = p.getLocation().getYaw();
					Location tar = target.getLocation();
					tar.setPitch(pitch);
					tar.setYaw(yaw);
					p.teleport(tar);
					target.damage(2, p);
					tpi.setPower(tpi.getPower() - 20);
					p.sendMessage("You now have " + tpi.getPower() + " Power.");
				}
				catch(Exception nullpointer)
				{} // ignore it if something went wrong
			}
			else p.sendMessage("Your target is too far away or too close to you.");
		}
		else p.sendMessage("Charge costs 20 Power. You have " + tpi.getPower() + ".");
	}

	private static void fury(final Player p, Demigods plugin, final Typhon t)
	{
		if(t.getFuryTime() > System.currentTimeMillis())
		{
			p.sendMessage("You cannot use Fury again for " + (((t.getFuryTime() / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
			return;
		}
		t.setFuryTime(System.currentTimeMillis() + 60000);
		final TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
		final int save = p.getHealth();
		if(tpi.getPower() < 750)
		{
			p.sendMessage("This ability costs 750 Power.");
			return;
		}
		final ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		final Location startloc = p.getLocation();
		for(LivingEntity e : p.getWorld().getLivingEntities())
		{
			if(e.getLocation().toVector().isInSphere(p.getLocation().toVector(), 35)) // jumps to the nearest entity
			{
				if(e instanceof Player)
				{
					Player ptemp = (Player) e;
					if(!plugin.isTitan(ptemp))
					{
						if(!targets.contains(ptemp)) targets.add(ptemp);
					}
				}
				else
				{
					if(!targets.contains(e)) targets.add(e);
				}
			}
		}
		if(targets.size() == 0)
		{
			p.sendMessage("There are no targets to attack.");
			return;
		}
		for(int i = 0; i < targets.size(); i++)
		{
			final int ii = i;
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					p.teleport(targets.get(ii));
					p.getLocation().setPitch(targets.get(ii).getLocation().getPitch());
					p.getLocation().setYaw(targets.get(ii).getLocation().getYaw());
					targets.get(ii).damage(10, p);
					p.setHealth(save);
				}
			}, i * 10);
		}
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				p.teleport(startloc);
				p.setHealth(save);
				p.sendMessage(targets.size() + " targets were struck with the fury of " + ChatColor.GOLD + "Typhon" + ChatColor.WHITE + ".");
				tpi.setPower(tpi.getPower() - 750);
				p.sendMessage(ChatColor.GRAY + "You now have " + tpi.getPower() + " Power.");
			}
		}, targets.size() * 10);
	}
}

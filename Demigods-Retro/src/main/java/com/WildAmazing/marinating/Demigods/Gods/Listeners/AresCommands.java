package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Ares;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AresCommands
{
	public static void onPlayerInteractAres(PlayerInteractEvent e, Demigods plugin)
	{
		Player p = e.getPlayer();
		if(!plugin.isGod(p)) return;
		GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
		if(!gpi.hasDeity(Divine.ARES)) return;
		if(!gpi.isAlive())
		{
			return;
		}
		Ares a = (Ares) gpi.getDeity(Divine.ARES);
		if(p.getItemInHand().getType() != Material.AIR)
		{
			if(p.getItemInHand().getType() == a.getFlingItem())
			{
				flingSword(gpi, p, a, plugin, e);
				return;
			}
		}
		if(a.getFling())
		{
			flingSword(gpi, p, a, plugin, e);
		}
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		try
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				Player p = (Player) ee.getDamager();
				if(!plugin.isGod(p)) return;
				GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
				if(!gpi.hasDeity(Divine.ARES)) return;
				if(!gpi.isAlive())
				{
					return;
				}
				try
				{
					LivingEntity le = (LivingEntity) e.getEntity();
					if(p.getHealth() < 20 && ((Ares) gpi.getDeity(Divine.ARES)).getBloodthirst())
					{
						if(e.getDamage() + p.getHealth() < 20) p.setHealth(p.getHealth() + e.getDamage());
						else p.setHealth(20);
					}
					if(le.getHealth() - e.getDamage() <= 0)
					{
						if((int) (Math.random() * 3) == 1)
						{
							int reward = 1 + (int) (Math.random() * 3);
							p.sendMessage(ChatColor.RED + "Finishing bonus: +" + reward);
							gpi.setBlessing(gpi.getBlessing() + reward);
							gpi.setFavor(gpi.getFavor() + reward);
						}
					}
				}
				catch(Exception notliving)
				{}
			}
		}
		catch(Exception notthatevent)
		{}
	}

	public static void onPlayerCommandPreprocessAres(PlayerCommandPreprocessEvent event, final Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isGod(player))
		{
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
			if(gpi.hasDeity(Divine.ARES))
			{
				if(!gpi.isAlive())
				{
					return;
				}
				final Ares a = (Ares) gpi.getDeity(Divine.ARES);
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
						case BLOODTHIRST:
							if(a.getBloodthirst())
							{
								player.sendMessage("You are already in Bloodthirst mode.");
								break;
							}
							else
							{
								if(gpi.getFavor() < 50)
								{
									player.sendMessage("Bloodthirst costs 50 Favor.");
									break;
								}
								gpi.setFavor(gpi.getFavor() - 50);
								a.setBloodthirst(true);
								player.sendMessage("Bloodthirst is enabled.");
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										player.sendMessage(ChatColor.RED + "There are 10 seconds of Bloodthirst remaining.");
									}
								}, 1000);
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										a.setBloodthirst(false);
										player.sendMessage(ChatColor.RED + "Bloodthirst is no longer in effect.");
									}
								}, 1200);
							}
							break;
						case FLING:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(a.getFlingItem() != player.getItemInHand().getType())
									{
										a.setFlingItem(player.getItemInHand().getType());
										player.sendMessage("Fling " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Fling " + ChatColor.YELLOW + "is already bound to " + a.getFlingItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Fling " + ChatColor.YELLOW + "is no longer bound to " + a.getFlingItem().name() + ".");
									a.setFlingItem(Material.AIR);
								}
								break;
							}
							if(a.getFling())
							{
								player.sendMessage("Fling is no longer enabled.");
								a.setFling(false);
								break;
							}
							else
							{
								if(gpi.getFavor() < 4)
								{
									player.sendMessage("Fling costs 4 Favor.");
									break;
								}
								a.setFling(true);
								player.sendMessage("Fling is enabled.");
							}
							break;
						case POUND:
							if(gpi.getFavor() < 100)
							{
								player.sendMessage("You have not enough Favor.");
								break;
							}
							int count = 0;
							for(int x = -3; x <= 3; x++)
							{
								for(int y = -3; y <= 3; y++)
								{
									for(int z = -3; z <= 3; z++)
									{
										Block b = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
										if(b.getType() == Material.AIR && (count * 50 < gpi.getFavor()) && count < 20)
										{
											player.getWorld().spawnCreature(b.getLocation(), CreatureType.WOLF);
											count++;
										}
									}
								}
							}
							for(Entity en : player.getNearbyEntities(4, 4, 4))
							{
								if(en instanceof Wolf)
								{
									final Wolf w = (Wolf) en;
									w.setTamed(true);
									w.setOwner(player);
									w.setSitting(false);
									plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
									{
										public void run()
										{
											w.setHealth(0);
										}
									}, 4000);// /
								}
							}
							if(count > 0)
							{
								player.sendMessage(count + " wolves were spawned at the cost of " + (count * 50) + " Favor.");
								gpi.setFavor(gpi.getFavor() - count * 50);
							}
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
		BLOODTHIRST, FLING, POUND
	}

	private static void flingSword(GodPlayerInfo gpi, Player p, Ares a, Demigods plugin, PlayerInteractEvent e)
	{
		if(e.getAction() == Action.PHYSICAL) return;
		if(gpi.getFavor() < 4)
		{
			a.setFling(false);
			p.sendMessage("Fling requires 4 Favor.");
			return;
		}
		if(!(p.getItemInHand().getType() == Material.WOOD_SWORD || p.getItemInHand().getType() == Material.STONE_SWORD || p.getItemInHand().getType() == Material.IRON_SWORD || p.getItemInHand().getType() == Material.DIAMOND_SWORD))
		{
			p.sendMessage("You must be holding a sword to use this ability.");
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
							if(!plugin.isGod(ptemp) && !ptemp.equals(p))
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
		if(target != null)
		{
			target.damage(2, p);
			gpi.setFavor(gpi.getFavor() - 4);
		}
		else
		{
			p.sendMessage("No target found.");
		}
	}
}

package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Cronus;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Vector;

public class CronusCommands
{
	public static void onPlayerCommandPreprocessCronus(PlayerCommandPreprocessEvent event, final Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.CRONUS))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Cronus c = (Cronus) tpi.getDeity(Divine.CRONUS);
				String[] sects = event.getMessage().split(" +", 2);
				String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
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
						case CTIME:
							if(args.length == 0)
							{
								String print = "";
								double mins = ((0.06) * (player.getWorld().getTime() % 1000));
								if(mins < 10) print += "0";
								print += (int) (Math.round(mins));
								player.sendMessage("The current time is " + player.getWorld().getTime() / 1000 + ":" + print);
								return;
							}
							else if(args.length == 1)
							{
								if(tpi.getPower() < 50)
								{
									player.sendMessage("You need at least 50 Power to do that.");
									break;
								}
								if(args[0].equalsIgnoreCase("dawn")) player.getWorld().setTime(23000);
								else if(args[0].equalsIgnoreCase("dusk")) player.getWorld().setTime(12000);
								else if(args[0].equalsIgnoreCase("day")) player.getWorld().setTime(100);
								else if(args[0].equalsIgnoreCase("night")) player.getWorld().setTime(15000);
								else
								{
									player.sendMessage("Correct usage: " + ChatColor.YELLOW + "/ctime <day/night/dusk/dawn>");
									break;
								}
								player.sendMessage(ChatColor.RED + "With a mighty effort, you bend time to your will.");
								tpi.setPower(tpi.getPower() - 50);
								player.sendMessage("You now have " + tpi.getPower() + " power.");
							}
							break;
						case SLOW:
							if(c.getFreeze())
							{
								player.sendMessage("Time is already slowed.");
							}
							else
							{
								if(tpi.getPower() >= 800)
								{
									player.getServer().broadcastMessage(ChatColor.RED + "Cronus" + ChatColor.GRAY + " has slowed time on world '" + player.getWorld().getName() + "'.");
									c.setFreeze(true);
									tpi.setPower(tpi.getPower() - 800);
									for(int i = 1; i < 1200; i += 2)
									{
										final int delay = i;
										plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
										{
											public void run()
											{
												for(Entity e : player.getWorld().getEntities())
												{
													try
													{
														LivingEntity le = (LivingEntity) e;
														if(le instanceof Player)
														{
															if(!plugin.isTitan((Player) le)) le.setVelocity(new Vector(0, 0, 0));
														}
														else le.setVelocity(new Vector(0, 0, 0));
													}
													catch(Exception error)
													{}
												}
											}
										}, delay);
									}
									plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
									{
										public void run()
										{
											c.setFreeze(false);
											player.getServer().broadcastMessage(ChatColor.GRAY + "Time has returned to normal speed.");
										}

									}, 1200);
								}
								else player.sendMessage("This ability requires 800 Power.");
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
		CTIME, SLOW
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		try
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				Player player = (Player) ee.getDamager();
				if(plugin.isTitan(player))
				{
					TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
					if(tpi.hasDeity(Divine.CRONUS))
					{
						if(!tpi.isAlive())
						{
							return;
						}
						if(player.getItemInHand().getType().equals(Material.WOOD_HOE) || player.getItemInHand().getType().equals(Material.STONE_HOE) || player.getItemInHand().getType().equals(Material.IRON_HOE) || player.getItemInHand().getType().equals(Material.DIAMOND_HOE))
						{
							switch(player.getItemInHand().getType())
							{
								case WOOD_HOE:
									e.setDamage(4);
									break;
								case STONE_HOE:
									e.setDamage(6);
									break;
								case IRON_HOE:
									e.setDamage(8);
									break;
								case DIAMOND_HOE:
									e.setDamage(13);
									break;
							}
						}
					}
				}
			}
		}
		catch(Exception notThatKindOfEvent)
		{}
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!plugin.isTitan(p)) return;
			if(plugin.getInfo(p).hasDeity(Divine.CRONUS))
			{
				e.setDamage(e.getDamage() / 2);
			}
		}
	}

	public static void onEntityDeath(EntityDeathEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(plugin.isTitan(p))
			{
				if(!plugin.getInfo(p).isAlive()) return;
				TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
				int amt;
				if(tpi.getAllegiance().contains(Divine.TYPHON)) amt = tpi.getPower() - (tpi.getPower() / 8);
				else amt = tpi.getPower() - tpi.getPower() / 10;
				p.sendMessage("Your Power has been reduced to " + amt + " from " + tpi.getPower() + ".");
				p.sendMessage(ChatColor.RED + "You have failed your Titan brethren.");
				tpi.setPower(amt);
				if((int) (Math.random() * 3) == 1)
				{
					Divine choice = tpi.getAllegiance().get((int) (Math.random() * tpi.getAllegiance().size()));
					if(!tpi.isTierOne(choice) && choice != Divine.TYPHON)
					{
						p.sendMessage(ChatColor.RED + choice.name() + " is displeased with your defeat.");
						p.sendMessage(ChatColor.YELLOW + choice.name() + " is no longer in your allegiance.");
						tpi.removeFromAllegiance(choice);
					}
				}
			}
		}
	}
}

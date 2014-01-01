package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Zeus;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ZeusCommands
{
	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!(plugin.isGod(p))) return;
			if(plugin.getInfo(p).hasDeity(Divine.ZEUS))
			{
				if(!plugin.getInfo(p).isAlive())
				{
					return;
				}
				if(e.getCause() == DamageCause.FALL)
				{
					p.sendMessage(ChatColor.GOLD + "Zeus" + ChatColor.WHITE + " has protected you from " + ((double) e.getDamage() / 2) + " falling damage.");
					e.setCancelled(true);
				}
				else if(e.getCause() == DamageCause.LIGHTNING)
				{
					e.setCancelled(true);
				}
			}
		}
	}

	public static void onPlayerInteract(PlayerInteractEvent e, Demigods plugin)
	{
		Player player = e.getPlayer();
		if(plugin.isGod(player))
		{
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
			if(gpi.hasDeity(Divine.ZEUS))
			{
				if(!gpi.isAlive())
				{
					return;
				}
				Zeus z = (Zeus) gpi.getDeity(Divine.ZEUS);
				if(player.getItemInHand().getType() != Material.AIR)
				{
					if(player.getItemInHand().getType() == z.getLiftItem())
					{
						lift(gpi, player, plugin);
						return;
					}
					else if(player.getItemInHand().getType() == z.getLightningItem())
					{
						lightning(gpi, player, plugin);
						return;
					}
				}
				if(z.getLightning())
				{
					lightning(gpi, player, plugin);
				}
				else if(z.getLift())
				{
					lift(gpi, player, plugin);
				}
				else if(player.getItemInHand().getType() == Material.GOLD_SWORD) stormsword(player);
			}
		}
	}

	public static void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isGod(player))
		{
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
			if(gpi.hasDeity(Divine.ZEUS))
			{
				if(!gpi.isAlive())
				{
					return;
				}
				Zeus z = (Zeus) gpi.getDeity(Divine.ZEUS);
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
						case LIGHTNING:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(player.getItemInHand().getType() == z.getLiftItem())
									{
										player.sendMessage(player.getItemInHand().getType().name() + ChatColor.YELLOW + " is already bound to Lifting.");
										break;
									}
									if(z.getLightningItem() != player.getItemInHand().getType())
									{
										z.setLightningItem(player.getItemInHand().getType());
										player.sendMessage("Lightning " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Lightning " + ChatColor.YELLOW + "is already bound to " + z.getLightningItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Lightning " + ChatColor.YELLOW + "is no longer bound to " + z.getLightningItem().name() + ".");
									z.setLightningItem(Material.AIR);
								}
								break;
							}
							if(z.getLift())
							{
								player.sendMessage("You are already in lifting mode.");
								break;
							}
							if(z.getLightning())
							{
								player.sendMessage("Lightning has been turned off.");
								z.setLightning(false);
							}
							else
							{
								player.sendMessage("Lightning has been turned on.");
								z.setLightning(true);
							}
							break;
						case LIFT:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(player.getItemInHand().getType() == z.getLightningItem())
									{
										player.sendMessage(player.getItemInHand().getType().name() + ChatColor.YELLOW + " is already bound to Lightning.");
										break;
									}
									if(z.getLiftItem() != player.getItemInHand().getType())
									{
										z.setLiftItem(player.getItemInHand().getType());
										player.sendMessage("Lift " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Lift " + ChatColor.YELLOW + "is already bound to " + z.getLiftItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Lift " + ChatColor.YELLOW + "is no longer bound to " + z.getLiftItem().name() + ".");
									z.setLiftItem(Material.AIR);
								}
								break;
							}
							if(z.getLightning())
							{
								player.sendMessage("You are already in lightning mode.");
								break;
							}
							if(z.getLift())
							{
								player.sendMessage("Lifting has been turned off.");
								z.setLift(false);
							}
							else
							{
								player.sendMessage("Lifting has been turned on.");
								z.setLift(true);
							}
							break;
						case STORM:
							lightningStorm(player, plugin);
							break;
						case SCATTER:
							if(player.getItemInHand().getType() == Material.GOLD_SWORD)
							{
								for(LivingEntity le : player.getWorld().getLivingEntities())
								{
									if(le.getLocation().toVector().isInSphere(player.getLocation().toVector(), 10))
									{
										Vector v = player.getLocation().toVector();
										Vector victor = le.getLocation().toVector().subtract(v);
										victor.multiply(0.8);
										le.setVelocity(victor);
									}
								}
								player.setItemInHand(null);
							}
							else player.sendMessage("You must be holding the " + ChatColor.GOLD + "Sword of Storms" + ChatColor.WHITE + " to do that.");
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
		LIGHTNING, LIFT, STORM, SCATTER
	}

	private static void lightningStorm(Player p, Demigods plugin)
	{
		GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
		if(1000 > gpi.getFavor())
		{
			p.sendMessage("A lightning storm costs 1000 Favor.");
			return;
		}
		Zeus z = (Zeus) gpi.getDeity(Divine.ZEUS);
		if(System.currentTimeMillis() < z.getStormTime())
		{
			p.sendMessage("You cannot use the lightning storm again for " + (((z.getStormTime() / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
			p.sendMessage("and " + (((z.getStormTime() / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
			return;
		}
		ArrayList<Entity> entitylist = new ArrayList<Entity>();
		Vector ploc = p.getLocation().toVector();
		for(Entity anEntity : p.getWorld().getEntities())
		{
			if(anEntity.getLocation().toVector().isInSphere(ploc, 50.0)) entitylist.add(anEntity);
		}
		int count = 0;
		for(Entity eee : entitylist)
		{
			try
			{
				LivingEntity e1 = (LivingEntity) eee;
				if(e1 instanceof Player)
				{
					Player ptemp = (Player) e1;
					if(!plugin.isGod(ptemp) && !ptemp.equals(p))
					{
						p.getWorld().strikeLightning(ptemp.getLocation());
						p.getWorld().strikeLightning(e1.getLocation());
						p.getWorld().strikeLightning(e1.getLocation());
						if(e1.getHealth() > 0)
						{ // JUST IN CASE
							Location targetat = e1.getLocation();
							Location newloc = new Location(e1.getWorld(), targetat.getX(), 126.0, targetat.getZ());
							e1.teleport(newloc);
						}
						count++;
					}
				}
				else
				{
					count++;
					p.getWorld().strikeLightning(e1.getLocation());
					p.getWorld().strikeLightning(e1.getLocation());
					p.getWorld().strikeLightning(e1.getLocation());
					if(e1.getHealth() > 0)
					{
						Location targetat = e1.getLocation();
						Location newloc = new Location(e1.getWorld(), targetat.getX(), 126.0, targetat.getZ());
						e1.teleport(newloc);
					}
				}
			}
			catch(Exception notAlive)
			{} // ignore stuff like minecarts
		}
		gpi.setFavor(gpi.getFavor() - 1000);
		z.setStormTime(System.currentTimeMillis() + (600000));
		p.sendMessage("In exchange for " + ChatColor.AQUA + "1000" + ChatColor.WHITE + " Favor, ");
		p.sendMessage(ChatColor.GOLD + "Zeus" + ChatColor.WHITE + " has unloaded his wrath on " + count + " non-allied entities.");
	}

	private static void lift(GodPlayerInfo gpi, Player player, Demigods plugin)
	{
		LivingEntity target = null;
		if(gpi.getFavor() >= 150)
		{
			Block b = player.getTargetBlock(null, 200);
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
							if(!plugin.isGod(ptemp) && !ptemp.equals(player))
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
			if(target != null)
			{
				try
				{
					gpi.setFavor(gpi.getFavor() - 150);
					target.setVelocity(new Vector(0, 6, 0));
					player.sendMessage("Swoosh! You now have " + gpi.getFavor() + " Favor.");
				}
				catch(Exception nullpointer)
				{} // ignore it if something went wrong
			}
			else player.sendMessage("Unable to find a target in that area. Allies cannot be lifted.");
		}
		else player.sendMessage("Lifting costs 150 Favor. You have " + gpi.getFavor() + ".");
	}

	private static void lightning(GodPlayerInfo gpi, Player player, Demigods plugin)
	{
		Location target = null;
		if(gpi.getFavor() >= 25)
		{
			Block b = player.getTargetBlock(null, 200);
			target = b.getLocation();
			for(Entity ee : b.getChunk().getEntities())
			{
				try
				{
					LivingEntity e1 = (LivingEntity) ee;
					if(e1.getLocation().toVector().distance(b.getLocation().toVector()) < 6) // jumps to the nearest entity
					{
						if(e1 instanceof Player)
						{
							Player ptemp = (Player) e1;
							if(!plugin.isGod(ptemp))
							{
								target = ptemp.getLocation();
								break;
							}
						}
						target = e1.getLocation();
						break;
					}
				}
				catch(Exception notAlive)
				{}
			}
			if(player.getLocation().toVector().distance(target.toVector()) > 3)
			{
				try
				{
					player.getWorld().strikeLightning(target);
					gpi.setFavor(gpi.getFavor() - 25);
					player.sendMessage("Kerpow! You now have " + gpi.getFavor() + " Favor.");
				}
				catch(Exception nullpointer)
				{} // ignore it if something went wrong
			}
			else player.sendMessage("Your target is too far away, or too close to you!");
		}
		else player.sendMessage("Lightning costs 25 Favor. You have " + gpi.getFavor() + ".");
	}

	private static void stormsword(Player player)
	{
		LivingEntity target = null;
		Block b = player.getTargetBlock(null, 16);
		for(Entity eee : b.getChunk().getEntities())
		{
			try
			{
				LivingEntity e1 = (LivingEntity) eee;
				if(e1.getLocation().toVector().distance(b.getLocation().toVector()) < 3)
				{
					if(e1 instanceof Player)
					{
						if(!((Player) target).equals(player)) target = e1;
					}
					else target = e1;
				}
			}
			catch(Exception notalive)
			{}
		}
		if(target != null)
		{
			Vector v = player.getLocation().toVector();
			Vector victor = target.getLocation().toVector().subtract(v);
			victor.multiply(0.4);
			target.setVelocity(victor); // super kb
		}
	}
}

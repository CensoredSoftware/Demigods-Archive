package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Hyperion;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class HyperionCommands
{
	public static void onPlayerInteractHyperion(PlayerInteractEvent e, Demigods plugin)
	{
		Player player = e.getPlayer();
		if(!plugin.isTitan(player)) return;
		TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
		if(!tpi.hasDeity(Divine.HYPERION)) return;
		if(!tpi.isAlive())
		{
			return;
		}
		Hyperion h = (Hyperion) tpi.getDeity(Divine.HYPERION);
		if(player.getItemInHand().getType() != Material.AIR)
		{
			if(player.getItemInHand().getType() == h.getCombustItem())
			{
				combust(player, tpi, plugin);
				return;
			}
		}
		if(h.getCombust()) combust(player, tpi, plugin);
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(e.getClickedBlock() != null)
			{
				if(e.getClickedBlock().getType() == Material.NETHERRACK || e.getClickedBlock().getType() == Material.SOUL_SAND)
				{
					if(plugin.isUnprotected(e.getClickedBlock().getLocation())) e.getClickedBlock().setType(Material.GLOWSTONE);
					else player.sendMessage("This is a protected area.");
				}
			}
		}
	}

	public static void onPlayerCommandPreprocessHyperion(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.HYPERION))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Hyperion h = (Hyperion) tpi.getDeity(Divine.HYPERION);
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
						case COMBUST:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(h.getCombustItem() != player.getItemInHand().getType())
									{
										h.setCombustItem(player.getItemInHand().getType());
										player.sendMessage("Combustion " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Combustion " + ChatColor.YELLOW + "is already bound to " + h.getCombustItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Combustion " + ChatColor.YELLOW + "is no longer bound to " + h.getCombustItem().name() + ".");
									h.setCombustItem(Material.AIR);
								}
								break;
							}
							if(!h.getCombust())
							{
								player.sendMessage("Combustion has been turned on.");
								h.setCombust(true);
							}
							else
							{
								player.sendMessage("Combustion has been turned off.");
								h.setCombust(false);
							}
							break;
						case SMITE:
							judgement(player, tpi, plugin);
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
		COMBUST, SMITE
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		try
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				Player p = (Player) ee.getDamager();
				if(plugin.isTitan(p))
				{
					if(!plugin.getInfo(p).isAlive())
					{
						return;
					}
					if(plugin.getInfo(p).hasDeity(Divine.HYPERION)) if(p.getLocation().getBlock().getLightLevel() > 8) e.setDamage((int) (e.getDamage() * 1.5));
				}
			}
		}
		catch(Exception notthatevent)
		{}
	}

	private static void combust(Player player, TitanPlayerInfo tpi, Demigods plugin)
	{
		LivingEntity target = null;
		if(tpi.getPower() >= 25)
		{
			for(Block b : player.getLineOfSight(null, 200))
			{
				for(Entity e1 : b.getChunk().getEntities())
				{
					try
					{
						if(e1.getLocation().toVector().distance(b.getLocation().toVector()) < 3) // jumps to the nearest entity
						{
							LivingEntity ee = (LivingEntity) e1;
							target = ee;
						}
					}
					catch(Exception notAlive)
					{}// ignore if not a livingentity
				}
			}
			try
			{
				if(!(target instanceof Player)) target.setHealth(0);
				if(plugin.isUnprotected(target.getLocation())) target.getWorld().getBlockAt(target.getLocation()).setType(Material.FIRE);
				tpi.setPower(tpi.getPower() - 25);
				player.sendMessage(ChatColor.RED + "Poof!" + ChatColor.WHITE + " You now have " + tpi.getPower() + " Power.");
			}
			catch(Exception nullpointer)
			{
				player.sendMessage("No valid targets found.");
			}
		}
		else
		{
			player.sendMessage("Combustion costs 25 Power. You have " + tpi.getPower() + ".");
			((Hyperion) tpi.getDeity(Divine.HYPERION)).setCombust(false);
		}
	}

	private static void judgement(final Player p, TitanPlayerInfo tpi, final Demigods plugin)
	{
		if(tpi.getPower() < 1100)
		{
			p.sendMessage("Smite costs 1100 Power.");
			return;
		}
		for(int ii = 0; ii < 600; ii += 60)
		{
			final int i = ii;
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					final ArrayList<Block> towipe = new ArrayList<Block>();
					for(LivingEntity e : p.getWorld().getLivingEntities())
					{
						if(e.getLocation().toVector().isInSphere(p.getLocation().toVector(), 50))
						{
							if(e instanceof Player)
							{
								if(!plugin.isTitan((Player) e))
								{
									Block b = e.getEyeLocation().getBlock().getRelative(BlockFace.UP);
									towipe.add(b);
									for(int x = -1; x <= 1; x++)
									{
										for(int z = -1; z <= 1; z++)
											towipe.add(b.getRelative(x, 0, z));
									}
									e.teleport(e.getLocation().getBlock().getLocation());
									e.damage(6, p);
								}
							}
							else
							{
								Block b = e.getEyeLocation().getBlock().getRelative(BlockFace.UP);
								towipe.add(b);
								for(int x = -1; x <= 1; x++)
								{
									for(int z = -1; z <= 1; z++)
										towipe.add(b.getRelative(x, 0, z));
								}
								e.teleport(e.getLocation().getBlock().getLocation());
								e.damage(6, p);
							}
						}
					}
					for(Block block : towipe)
					{
						block.setType(Material.GLOWSTONE);
					}
					plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
					{
						public void run()
						{
							for(Block b : towipe)
								b.setType(Material.AIR);
						}
					}, i + 15);
				}
			}, i);
		}
		tpi.setPower(tpi.getPower() - 1100);
		p.sendMessage(ChatColor.GOLD + "Hyperion" + ChatColor.WHITE + " strikes your enemies with the power of the sun.");
	}
}

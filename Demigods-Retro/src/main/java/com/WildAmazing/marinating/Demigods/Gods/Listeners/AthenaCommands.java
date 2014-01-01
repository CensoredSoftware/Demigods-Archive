package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Athena;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AthenaCommands
{
	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			try
			{
				EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
				if(ee.getDamager() instanceof Player)
				{
					Player pla = (Player) ee.getDamager();
					if(plugin.isGod(pla))
					{
						for(Player pl : p.getWorld().getPlayers())
						{
							if(pla.getLocation().toVector().isInSphere(pl.getLocation().toVector(), 30)) if(plugin.isGod(pl))
							{
								if(plugin.getInfo(pl).hasDeity(Divine.ATHENA))
								{
									if(((Athena) plugin.getInfo(pl).getDeity(Divine.ATHENA)).getBuff())
									{
										e.setDamage(e.getDamage() * 2);
										return;
									}
								}
							}
						}
					}
				}
			}
			catch(Exception notthatevent)
			{}
			if(e.getCause() == DamageCause.ENTITY_ATTACK)
			{
				for(Player pl : p.getWorld().getPlayers())
				{
					if(p.getLocation().toVector().isInSphere(pl.getLocation().toVector(), 30)) if(plugin.isGod(pl))
					{
						if(plugin.getInfo(pl).hasDeity(Divine.ATHENA))
						{
							if(((Athena) plugin.getInfo(pl).getDeity(Divine.ATHENA)).getCeasefire())
							{
								e.setDamage(0);
								return;
							}
							else if(((Athena) plugin.getInfo(pl).getDeity(Divine.ATHENA)).getBuff())
							{
								e.setDamage(e.getDamage() / 2);
								return;
							}
						}
					}
				}
			}
			if(!plugin.isGod(p)) return;
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
			if(!gpi.isAlive()) return;
			if(!gpi.hasDeity(Divine.ATHENA)) return;
			if(e.getCause() == DamageCause.ENTITY_ATTACK)
			{
				if((int) (Math.random() * 5) == 2)
				{
					p.sendMessage(ChatColor.GOLD + "Athena's grace" + ChatColor.WHITE + " shields you from " + (double) e.getDamage() / 2 + " damage.");
					e.setDamage(0);
				}
			}
		}
	}

	public static void onEntityTarget(EntityTargetEvent e, Demigods plugin)
	{
		for(Player pl : e.getEntity().getWorld().getPlayers())
		{
			if(e.getEntity().getLocation().toVector().isInSphere(pl.getLocation().toVector(), 30)) if(plugin.isGod(pl))
			{
				if(plugin.getInfo(pl).hasDeity(Divine.ATHENA))
				{
					if(((Athena) plugin.getInfo(pl).getDeity(Divine.ATHENA)).getCeasefire())
					{
						e.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	public static void onPlayerCommandPreprocessAthena(PlayerCommandPreprocessEvent event, final Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isGod(player))
		{
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
			if(gpi.hasDeity(Divine.ATHENA))
			{
				if(!gpi.isAlive())
				{
					return;
				}
				final Athena a = (Athena) gpi.getDeity(Divine.ATHENA);
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
						case SPY:
							if(args.length == 1)
							{
								try
								{
									Player pp = plugin.getServer().getPlayer(args[0]);
									if(plugin.isGod(pp))
									{
										GodPlayerInfo pi = (GodPlayerInfo) plugin.getInfo(pp);
										player.sendMessage(ChatColor.YELLOW + "--" + pp.getDisplayName() + "--" + pi.getRank() + "--");
										String send = pp.getDisplayName() + "'s deities are: ";
										for(int i = 0; i < pi.getAllegiance().size(); i++)
										{
											send += pi.getAllegiance().get(i).name() + " ";
										}
										player.sendMessage(send);
										player.sendMessage("Current favor: " + pi.getFavor());
										player.sendMessage("Current blessing: " + pi.getBlessing());
										player.sendMessage("Current health: " + ((double) pp.getHealth() / 2) + "/10");
									}
									else if(plugin.isTitan(pp))
									{
										TitanPlayerInfo pi = (TitanPlayerInfo) plugin.getInfo(pp);
										player.sendMessage(ChatColor.YELLOW + "--" + pp.getDisplayName() + "--" + pi.getRank() + "--");
										String send = pp.getDisplayName() + "'s deities are: ";
										for(int i = 0; i < pi.getAllegiance().size(); i++)
										{
											send += pi.getAllegiance().get(i).name() + " ";
										}
										player.sendMessage(send);
										player.sendMessage("Current power: " + pi.getPower());
										player.sendMessage("Current glory: " + pi.getGlory());
										player.sendMessage("Current health: " + ((double) pp.getHealth() / 2) + "/10");
									}
									else
									{
										player.sendMessage(ChatColor.YELLOW + "--" + pp.getDisplayName() + "--Mortal--");
										player.sendMessage(pp.getDisplayName() + " is not affiliated with any Gods or Titans.");
										player.sendMessage("Current health: " + ((double) pp.getHealth() / 2) + "/10");
									}
								}
								catch(Exception error)
								{
									player.sendMessage(ChatColor.RED + "Error. Was the player's name typed correctly?");
								}
							}
							else
							{
								player.sendMessage("Syntax: " + ChatColor.YELLOW + "/spy <playername>");
							}
							break;
						case CEASEFIRE:
							if(gpi.getFavor() < 80)
							{
								player.sendMessage("Ceasefire costs 80 Favor.");
								break;
							}
							if(a.getCeasefire())
							{
								player.sendMessage("Ceasefire is already in effect.");
								break;
							}
							else
							{
								gpi.setFavor(gpi.getFavor() - 80);
								for(Player pl : player.getWorld().getPlayers())
								{
									if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30)) pl.sendMessage(ChatColor.GOLD + "Athena" + ChatColor.GRAY + " has mandated a ceasefire for 30 seconds.");
								}
								a.setCeasefire(true);
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										for(Player pl : player.getWorld().getPlayers())
										{
											if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30)) pl.sendMessage(ChatColor.GRAY + "The ceasefire will be in effect for 10 more seconds.");
										}
									}
								}, 400);
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										a.setCeasefire(false);
										for(Player pl : player.getWorld().getPlayers())
										{
											if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30)) pl.sendMessage(ChatColor.GRAY + "The ceasefire has ended.");
										}
									}
								}, 600);
							}
							break;
						case SPECIALTACTICS:
							if(gpi.getFavor() < 600)
							{
								player.sendMessage("Special tactics costs 600 Favor.");
								break;
							}
							if(a.getBuff())
							{
								player.sendMessage("Special tactics are already in effect.");
								break;
							}
							else
							{
								gpi.setFavor(gpi.getFavor() - 600);
								for(Player pl : player.getWorld().getPlayers())
								{
									if(plugin.isGod(pl))
									{
										if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30))
										{
											pl.sendMessage(ChatColor.GOLD + "Athena" + ChatColor.GRAY + " has granted you the power of special tactics.");
											pl.sendMessage(ChatColor.GRAY + "Your damage will be doubled and damage taken will be halved.");
										}
									}
								}
								a.setBuff(true);
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										for(Player pl : player.getWorld().getPlayers())
										{
											if(plugin.isGod(pl))
											{
												if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30)) pl.sendMessage(ChatColor.GRAY + "Special tactics will be in effect for 10 more seconds.");
											}
										}
									}
								}, 1000);
								plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
								{
									public void run()
									{
										a.setBuff(false);
										for(Player pl : player.getWorld().getPlayers())
										{
											if(plugin.isGod(pl))
											{
												if(pl.getLocation().toVector().isInSphere(player.getLocation().toVector(), 30)) pl.sendMessage(ChatColor.GRAY + "Special tactics are finished.");
											}
										}
									}
								}, 1200);
							}
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
		SPY, CEASEFIRE, SPECIALTACTICS
	}
}

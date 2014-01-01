package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Hephaestus;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HephaestusCommands
{

	public static void onPlayerInteractHephaestus(PlayerInteractEvent e, Demigods plugin)
	{
		if(e.getClickedBlock() == null) return;
		if(e.getClickedBlock().getType() == Material.IRON_BLOCK || e.getClickedBlock().getType() == Material.GOLD_BLOCK || e.getClickedBlock().getType() == Material.DIAMOND_BLOCK)
		{
			Player p = e.getPlayer();
			if(!plugin.isGod(p)) return;
			if(!plugin.getInfo(p).hasDeity(Divine.HEPHAESTUS)) return;
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
			if(!gpi.isAlive())
			{
				return;
			}
			Hephaestus h = (Hephaestus) gpi.getDeity(Divine.HEPHAESTUS);
			if(!h.getOutfit()) return;
			if(e.getClickedBlock().getType() == Material.IRON_BLOCK)
			{
				e.getClickedBlock().setType(Material.AIR);
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.IRON_BOOTS, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.IRON_CHESTPLATE, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.IRON_HELMET, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.IRON_LEGGINGS, 1));
			}
			else if(e.getClickedBlock().getType() == Material.GOLD_BLOCK)
			{
				e.getClickedBlock().setType(Material.AIR);
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.GOLD_BOOTS, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.GOLD_CHESTPLATE, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.GOLD_HELMET, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.GOLD_LEGGINGS, 1));
			}
			else if(e.getClickedBlock().getType() == Material.DIAMOND_BLOCK)
			{
				e.getClickedBlock().setType(Material.AIR);
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.DIAMOND_BOOTS, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.DIAMOND_HELMET, 1));
				p.getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.DIAMOND_LEGGINGS, 1));
			}
			h.setOutfit(false);
			p.sendMessage("A full suit of armor was successfully forged.");
		}
	}

	public static void onFurnaceSmelt(FurnaceSmeltEvent e, Demigods plugin)
	{
		for(Player p : e.getFurnace().getWorld().getPlayers())
		{
			if(p.getLocation().toVector().distance(e.getFurnace().getLocation().toVector()) < 20) if(plugin.isGod(p))
			{
				if(plugin.getInfo(p).hasDeity(Divine.HEPHAESTUS))
				{
					e.setResult(new ItemStack(e.getResult().getType(), e.getResult().getAmount() * 2));
					break;
				}
			}
		}
	}

	public static void onPlayerCommandPreprocessHephaestus(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isGod(player))
		{
			GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
			if(gpi.hasDeity(Divine.HEPHAESTUS))
			{
				if(!gpi.isAlive())
				{
					return;
				}
				final Hephaestus h = (Hephaestus) gpi.getDeity(Divine.HEPHAESTUS);
				String[] sects = event.getMessage().split(" +", 2);
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
						case FIX:
							if(player.getItemInHand().getType() != Material.AIR)
							{
								int cost = (player.getItemInHand().getDurability() / 10);
								if(cost > 120) cost = 120;
								if(gpi.getFavor() < cost)
								{
									player.sendMessage("Repairing this " + player.getItemInHand().getType().name().toLowerCase() + " costs " + cost + " Favor.");
									player.sendMessage("You only have " + gpi.getFavor() + ".");
								}
								else
								{
									player.getItemInHand().setDurability((short) 0);
									player.sendMessage(player.getItemInHand().getType().name() + " was repaired for " + cost + " Favor.");
								}
							}
							else player.sendMessage("Nothing to repair.");
							break;
						case SHATTER:
							if(gpi.getFavor() < 600)
							{
								player.sendMessage("This skill costs 600 Favor.");
								break;
							}
							int count = 0;
							for(Player p : player.getWorld().getPlayers())
							{
								if(p.getLocation().toVector().isInSphere(player.getLocation().toVector(), 50))
								{
									if(!plugin.isGod(p))
									{
										if(p.getItemInHand().getType() != Material.AIR)
										{
											p.getItemInHand().setDurability((short) 2500);
											p.sendMessage(ChatColor.RED + "Hephaestus magically reduced the durability of your " + p.getItemInHand().getType().name().toLowerCase() + ".");
											p.sendMessage("(Durability only affects tools)");
											count++;
										}
									}
								}
							}
							gpi.setFavor(gpi.getFavor() - 600);
							player.sendMessage("Hephaestus has destroyed the durability of " + count + " players' items.");
							break;
						case FORGE:
							if(h.getOutfit())
							{
								player.sendMessage("You are no longer in Forge mode.");
								h.setOutfit(false);
							}
							else if(gpi.getFavor() >= 50)
							{
								h.setOutfit(true);
								player.sendMessage("Click an iron, gold, or diamond block to forge armor.");
							}
							else
							{
								player.sendMessage("This skill costs 50 Favor.");
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
		FIX, SHATTER, FORGE
	}

	public static void onEntityDamage(EntityDamageEvent e, Demigods plugin)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!plugin.isGod(p)) return;
			if(plugin.getInfo(p).hasDeity(Divine.HEPHAESTUS))
			{
				if(e.getCause() == DamageCause.LAVA || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.FIRE)
				{
					e.setCancelled(true);
				}
			}
		}
	}
}

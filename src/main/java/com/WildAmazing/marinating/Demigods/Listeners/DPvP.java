package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.WildAmazing.marinating.Demigods.DFixes;
import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Util.DSave;
import com.WildAmazing.marinating.Demigods.Util.DSettings;

public class DPvP implements Listener
{
	private static final double MULTIPLIER = DSettings.getSettingDouble("pvp_exp_bonus"); // bonus for dealing damage
	private static final int pvpkillreward = 1500; // Devotion
	private static final String genericReason = ChatColor.YELLOW + " has lost connection to the game for a generic reason.";
	private static final String endOfStream = ChatColor.YELLOW + " has lost connection to the game.";
	private static final String overflow = ChatColor.YELLOW + " has disconnected due to overload.";
	private static final String quitting = ChatColor.YELLOW + " has left the game.";
	private static final String timeout = ChatColor.YELLOW + " has disconnected due to timeout.";
	public static boolean filterCheckGeneric = false;
	public static boolean filterCheckStream = false;
	public static boolean filterCheckOverflow = false;
	public static boolean filterCheckQuitting = false;
	public static boolean filterCheckTimeout = false;

	@EventHandler(priority = EventPriority.HIGH)
	public void onArrowLaunch(ProjectileLaunchEvent e)
	{
		if(e.getEntity() instanceof Arrow)
		{
			Arrow arrow = (Arrow) e.getEntity();
			if(arrow.getShooter() instanceof Player)
			{
				Player shooter = (Player) arrow.getShooter();
				if(!DMiscUtil.canTarget(shooter, shooter.getLocation()))
				{
					shooter.sendMessage(ChatColor.YELLOW + "This is a no-PvP zone.");

					// Undo the arrow being removed from the inventory
					int slot = shooter.getInventory().first(Material.ARROW);
					ItemStack arrows = shooter.getInventory().getItem(slot);
					arrows.setAmount(arrows.getAmount() + 1);
					shooter.getInventory().setItem(slot, arrows);

					e.setCancelled(true);
				}
			}
		}
		else if(e.getEntityType().equals(EntityType.ENDER_PEARL))
		{
			if(e.getEntity().getShooter() instanceof Player)
			{
				Player player = (Player) e.getEntity().getShooter();
				if(!DMiscUtil.canWorldGuardBuild(player, player.getLocation())) e.getEntity().remove();
			}
		}
	}

	public static void pvpDamage(EntityDamageByEntityEvent e)
	{
		if(!(e.getEntity() instanceof Player)) return;
		Player target = (Player) e.getEntity();
		Player attacker;
		if(e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player) attacker = (Player) ((Arrow) e.getDamager()).getShooter();
		else if(e.getDamager() instanceof Player) attacker = (Player) e.getDamager();
		else return;
		if(!(DMiscUtil.isFullParticipant(attacker) && DMiscUtil.isFullParticipant(target)))
		{
			if(!DMiscUtil.canTarget(target, target.getLocation()))
			{
				attacker.sendMessage(ChatColor.YELLOW + "This is a no-PvP zone.");
				DFixes.checkAndCancel(e);
				return;
			}
		}
		if(!DSettings.getEnabledWorlds().contains(attacker.getWorld())) return;
		if(DMiscUtil.getAllegiance(attacker).equalsIgnoreCase(DMiscUtil.getAllegiance(target))) return; // Handled in DDamage...
		if(!DMiscUtil.canTarget(target, target.getLocation()))
		{
			attacker.sendMessage(ChatColor.YELLOW + "This is a no-PvP zone.");
			DFixes.checkAndCancel(e);
			return;
		}
		if(!DMiscUtil.canTarget(attacker, attacker.getLocation()))
		{
			attacker.sendMessage(ChatColor.YELLOW + "This is a no-PvP zone.");
			DFixes.checkAndCancel(e);
			return;
		}
		try
		{
			Deity d = DMiscUtil.getDeities(attacker).get((int) Math.floor(Math.random() * DMiscUtil.getDeities(attacker).size()));
			DMiscUtil.setDevotion(attacker, d, DMiscUtil.getDevotion(attacker, d) + (int) (e.getDamage() * MULTIPLIER));
			DLevels.levelProcedure(attacker);
		}
		catch(Exception ignored)
		{}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerDeath(final EntityDeathEvent e1)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(DMiscUtil.getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				if(e1.getEntity().getType().equals(EntityType.VILLAGER))
				{
					LivingEntity villager = e1.getEntity();
					if(villager.getLastDamageCause() instanceof EntityDamageByEntityEvent)
					{
						EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) villager.getLastDamageCause();

						if(!(e.getDamager() instanceof Player)) return;
						Player attacker = (Player) e.getDamager();

						// Define Mortal Soul
						ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET, 1);

						String mortalName = "Mortal Soul";
						List<String> mortalLore = new ArrayList<String>();
						mortalLore.add("Brings you back to life.");
						mortalLore.add("You regain 20 health.");

						ItemMeta mortalItem = mortalHealth.getItemMeta();
						mortalItem.setDisplayName(mortalName);
						mortalItem.setLore(mortalLore);

						mortalHealth.setItemMeta(mortalItem);

						villager.getLocation().getWorld().dropItemNaturally(villager.getLocation(), mortalHealth);
						attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
					}
				}

				if(!(e1.getEntity() instanceof Player)) return;
				Player attacked = (Player) e1.getEntity();
				if(!DSettings.getEnabledWorlds().contains(attacked.getWorld())) return;

				if((attacked.getLastDamageCause() != null) && (attacked.getLastDamageCause() instanceof EntityDamageByEntityEvent))
				{
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) attacked.getLastDamageCause();
					if(!(e.getDamager() instanceof Player)) return;
					Player attacker = (Player) e.getDamager();
					if(!(DMiscUtil.isFullParticipant(attacker))) return;
					if(DMiscUtil.isFullParticipant(attacked))
					{
						if(DMiscUtil.getAllegiance(attacker).equalsIgnoreCase(DMiscUtil.getAllegiance(attacked)))
						{ // betrayal
							DMiscUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " was betrayed by " + ChatColor.YELLOW + attacker.getName() + ChatColor.GRAY + " of the " + DMiscUtil.getAllegiance(attacker) + " alliance.");
							if(DMiscUtil.getKills(attacker) > 0)
							{
								DMiscUtil.setKills(attacker, DMiscUtil.getKills(attacker) - 1);
								attacker.sendMessage(ChatColor.RED + "Your number of kills has decreased to " + DMiscUtil.getKills(attacker) + ".");
							}
						}
						else
						{ // PVP kill
							DMiscUtil.setKills(attacker, DMiscUtil.getKills(attacker) + 1);
							DMiscUtil.setDeaths(attacked, DMiscUtil.getDeaths(attacked) + 1);
							DMiscUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " of the " + DMiscUtil.getAllegiance(attacked) + " alliance was slain by " + ChatColor.YELLOW + attacker.getName() + ChatColor.GRAY + " of the " + DMiscUtil.getAllegiance(attacker) + " alliance.");

							// Define Immortal Soul Fragment
							ItemStack health = new ItemStack(Material.GHAST_TEAR, 1);

							String name = "Immortal Soul Fragment";
							List<String> lore = new ArrayList<String>();
							lore.add("Brings you back to life.");
							lore.add("You regain full heath!");

							ItemMeta item = health.getItemMeta();
							item.setDisplayName(name);
							item.setLore(lore);

							health.setItemMeta(item);

							// Define Immortal Soul Dust
							ItemStack halfHealth = new ItemStack(Material.GLOWSTONE_DUST, 1);

							String halfName = "Immortal Soul Dust";
							List<String> halfLore = new ArrayList<String>();
							halfLore.add("Brings you back to life.");
							halfLore.add("You regain half heath!");

							ItemMeta halfItem = halfHealth.getItemMeta();
							halfItem.setDisplayName(halfName);
							halfItem.setLore(halfLore);

							halfHealth.setItemMeta(halfItem);

							if(DMiscUtil.getAscensions(attacked) > DMiscUtil.getAscensions(attacker))
							{
								attacked.getLocation().getWorld().dropItemNaturally(attacked.getLocation(), health);
								attacker.sendMessage(ChatColor.GRAY + "One stronger than you has been slain by your hand.");
							}

							if(DMiscUtil.getAscensions(attacker) >= DMiscUtil.getAscensions(attacked))
							{
								attacked.getLocation().getWorld().dropItemNaturally(attacked.getLocation(), halfHealth);
								attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
							}

							double adjusted = DMiscUtil.getKills(attacked) * 1.0 / DMiscUtil.getDeaths(attacked);
							if(adjusted > 5) adjusted = 5;
							if(adjusted < 0.2) adjusted = 0.2;
							for(Deity d : DMiscUtil.getDeities(attacker))
							{
								DMiscUtil.setDevotion(attacker, d, DMiscUtil.getDevotion(attacker, d) + (int) (pvpkillreward * MULTIPLIER * adjusted));
							}
						}
					}
					else
					{ // regular player
						DMiscUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " was slain by " + ChatColor.YELLOW + attacker.getName() + ChatColor.GRAY + " of the " + DMiscUtil.getAllegiance(attacker) + " alliance.");

						// Define Mortal Soul
						ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET, 1);

						String mortalName = "Mortal Soul";
						List<String> mortalLore = new ArrayList<String>();
						mortalLore.add("Brings you back to life.");
						mortalLore.add("You regain 20 health.");

						ItemMeta mortalItem = mortalHealth.getItemMeta();
						mortalItem.setDisplayName(mortalName);
						mortalItem.setLore(mortalLore);

						mortalHealth.setItemMeta(mortalItem);

						attacked.getLocation().getWorld().dropItemNaturally(attacked.getLocation(), mortalHealth);
						attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
					}
				}
			}
		}, 30);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// Define variables
		final Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		int delayTime = DSettings.getSettingInt("pvp_area_delay_time");
		onPlayerLineJump(player, to, from, delayTime);
		for(Location altar : DSave.getAllAltars())
		{
			double distance = altar.distance(event.getTo());
			GameMode gameMode = player.getGameMode();
			if(distance <= 16 && gameMode.equals(GameMode.SURVIVAL))
			{
				player.setGameMode(GameMode.ADVENTURE);
				break;
			}
			else if(distance > 16 && gameMode.equals(GameMode.ADVENTURE))
			{
				player.setGameMode(GameMode.SURVIVAL);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		// Define variables
		final Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		int delayTime = DSettings.getSettingInt("pvp_area_delay_time");

		if(DSave.hasData(player, "temp_flash") || event.getCause() == TeleportCause.ENDER_PEARL)
		{
			onPlayerLineJump(player, to, from, delayTime);
		}
		else if(!DMiscUtil.canLocationPVP(to) && DMiscUtil.canLocationPVP(from))
		{
			DSave.removeData(player, "temp_was_PVP");
			player.sendMessage(ChatColor.YELLOW + "You are now safe from all PVP!");
		}
		else if(!DMiscUtil.canLocationPVP(from) && DMiscUtil.canLocationPVP(to)) player.sendMessage(ChatColor.YELLOW + "You can now PVP!");
	}

	void onPlayerLineJump(final Player player, Location to, Location from, int delayTime)
	{
		// NullPointer Check
		if(to == null || from == null) return;

		if(DSave.hasData(player, "temp_was_PVP") || !DMiscUtil.isFullParticipant(player)) return;

		// No Spawn Line-Jumping
		if(!DMiscUtil.canLocationPVP(to) && DMiscUtil.canLocationPVP(from) && delayTime > 0 && !DMiscUtil.hasPermission(player, "demigods.bypasspvpdelay") && !DFixes.isNoob(player))
		{
			DSave.saveData(player, "temp_was_PVP", true);
			if(DSave.hasData(player, "temp_flash")) DSave.removeData(player, "temp_flash");

			DMiscUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DMiscUtil.getPlugin(), new Runnable()
			{
				@Override
				public void run()
				{
					DSave.removeData(player, "temp_was_PVP");
					if(!DMiscUtil.canLocationPVP(player.getLocation())) player.sendMessage(ChatColor.YELLOW + "You are now safe from all PVP!");
				}
			}, (delayTime * 20));
		}
		else if(!DSave.hasData(player, "temp_was_PVP") && !DMiscUtil.canLocationPVP(to) && DMiscUtil.canLocationPVP(from)) player.sendMessage(ChatColor.YELLOW + "You are now safe from all PVP!");

		// Let players know where they can PVP
		if(!DSave.hasData(player, "temp_was_PVP") && DMiscUtil.canLocationPVP(to) && !DMiscUtil.canLocationPVP(from))
		{
			if(!DMiscUtil.canLocationPVP(from) && DMiscUtil.canLocationPVP(to)) player.sendMessage(ChatColor.YELLOW + "You can now PVP!");
		}
	}
}

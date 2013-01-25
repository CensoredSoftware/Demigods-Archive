package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.WildAmazing.marinating.Demigods.DSave;
import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DPvP implements Listener
{
	static double MULTIPLIER = DSettings.getSettingDouble("pvp_exp_bonus"); //bonus for dealing damage
	static int pvpkillreward = 1500; //Devotion
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void pvpDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		Player attacker = (Player)e.getDamager();
		Player target = (Player)e.getEntity();
		if (!(DUtil.isFullParticipant(attacker) && DUtil.isFullParticipant(target)))
			return;
		if (!DSettings.getEnabledWorlds().contains(attacker.getWorld()))
			return;
		if (DUtil.getAllegiance(attacker).equalsIgnoreCase(DUtil.getAllegiance(target)))
			return;
		if (!DUtil.canTarget(target, target.getLocation())) {
			attacker.sendMessage(ChatColor.YELLOW+"This is a no-PvP zone.");
			e.setCancelled(true);
			return;
		}
		Deity d = DUtil.getDeities(attacker).get((int)Math.floor(Math.random()*DUtil.getDeities(attacker).size()));
		DUtil.setDevotion(attacker, d, DUtil.getDevotion(attacker, d)+(int)(e.getDamage()*MULTIPLIER));
		DLevels.levelProcedure(attacker);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerDeath(EntityDeathEvent e1)
	{
		if (e1.getEntity().getType().equals(EntityType.VILLAGER))
		{
			LivingEntity villager = (LivingEntity)e1.getEntity();
			if (villager.getLastDamageCause() instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)villager.getLastDamageCause();
				Player attacker = (Player)e.getDamager();
				
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
		
		if (!(e1.getEntity() instanceof Player))
			return;
		Player attacked = (Player)e1.getEntity();
		if (!DSettings.getEnabledWorlds().contains(attacked.getWorld()))
			return;
		
		if ((attacked.getLastDamageCause() != null) && (attacked.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)attacked.getLastDamageCause();
			if (!(e.getDamager() instanceof Player))
				return;
			Player attacker = (Player)e.getDamager();
			if (!(DUtil.isFullParticipant(attacker)))
				return;
			if (DUtil.isFullParticipant(attacked)) {
				if (DUtil.getAllegiance(attacker).equalsIgnoreCase(DUtil.getAllegiance(attacked))) { //betrayal
					DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" was betrayed by "+
							ChatColor.YELLOW+attacker.getName()+ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");
					if (DUtil.getKills(attacker) > 0) {
						DUtil.setKills(attacker, DUtil.getKills(attacker)-1);
						attacker.sendMessage(ChatColor.RED+"Your number of kills has decreased to "+DUtil.getKills(attacker)+".");
					}
				} else { //PVP kill
					DUtil.setKills(attacker, DUtil.getKills(attacker)+1);
					DUtil.setDeaths(attacked, DUtil.getDeaths(attacked)+1);
					DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" of the "+
							DUtil.getAllegiance(attacked)+ " alliance was slain by "+ChatColor.YELLOW+attacker.getName()+
							ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");

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
					
					if (DUtil.getAscensions(attacked) > DUtil.getAscensions(attacker))
					{
						attacked.getLocation().getWorld().dropItemNaturally(attacked.getLocation(), health);
						attacker.sendMessage(ChatColor.GRAY + "One stronger than you has been slain by your hand.");
					}
					
					if (DUtil.getAscensions(attacker) >= DUtil.getAscensions(attacked))
					{
						attacked.getLocation().getWorld().dropItemNaturally(attacked.getLocation(), halfHealth);
						attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
					}
					
					double adjusted = DUtil.getKills(attacked)*1.0/DUtil.getDeaths(attacked);
					if (adjusted > 5) adjusted = 5;
					if (adjusted < 0.2) adjusted = 0.2;
					for (Deity d : DUtil.getDeities(attacker)) {
						DUtil.setDevotion(attacker, d, DUtil.getDevotion(attacker, d)+(int)(pvpkillreward*MULTIPLIER*adjusted));
					}
				}
			} else { //regular player
				DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" was slain by "+
						ChatColor.YELLOW+attacker.getName()+ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");
				
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// Define variables
		final Player player = (Player) event.getPlayer();
		Location to = ((PlayerMoveEvent) event).getTo();
		Location from = ((PlayerMoveEvent) event).getFrom();
		int delayTime = DSettings.getSettingInt("pvp_area_delay_time");
		onPlayerLineJump(player, to, from, delayTime);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		// Define variables
		final Player player = (Player) event.getPlayer();
		Location to = ((PlayerMoveEvent) event).getTo();
		Location from = ((PlayerMoveEvent) event).getFrom();
		onPlayerLineJump(player, to, from, 0);
	}
	
	public void onPlayerLineJump(final Player player, Location to, Location from, int delayTime)
	{
		// NullPointer Check
		if(to == null || from == null) return;
			
		// No Spawn Line-Jumping
		if(!DUtil.canLocationPVP(to) && DUtil.canLocationPVP(from) && delayTime > 0)
		{
			DSave.saveData(player, "temp_was_PVP", true);
			
			DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable()
			{
				@Override
				public void run()
				{
					DSave.removeData(player, "temp_was_PVP");
					player.sendMessage(ChatColor.YELLOW + "You are now safe from all PVP!");
				}
			}, (delayTime * 20));
		}
		else if(!DUtil.canLocationPVP(to) && DUtil.canLocationPVP(from))
		{
			DSave.removeData(player, "temp_was_PVP");
			player.sendMessage(ChatColor.YELLOW + "You are now safe from all PVP!");
		}
		
		// Let players know where they can PVP
		if(!DUtil.canLocationPVP(from) && DUtil.canLocationPVP(to)) player.sendMessage(ChatColor.YELLOW + "You can now PVP!");
	}
}

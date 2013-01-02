package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		if (!DUtil.canPVP(target.getLocation())) {
			attacker.sendMessage(ChatColor.YELLOW+"This is a no-PvP zone.");
			return;
		}
		Deity d = DUtil.getDeities(attacker).get((int)Math.floor(Math.random()*DUtil.getDeities(attacker).size()));
		DUtil.setDevotion(attacker, d, DUtil.getDevotion(attacker, d)+(int)(e.getDamage()*MULTIPLIER));
		DLevels.levelProcedure(attacker);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerDeath(EntityDeathEvent e1) {
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
					ItemStack health = new ItemStack(Material.GOLD_NUGGET, 1);
					
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
						attacker.getInventory().addItem(health);
						attacker.sendMessage(ChatColor.GRAY + "One stronger than you has been slain by your hand.");
						attacker.sendMessage(ChatColor.GOLD + "You have captured an Immortal Soul Fragment.");
					}
					
					if (DUtil.getAscensions(attacker) >= DUtil.getAscensions(attacked))
					{
						attacker.getInventory().addItem(halfHealth);
						attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
						attacker.sendMessage(ChatColor.RED + "You have captured some Immortal Soul Dust.");
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
				
				attacker.getInventory().addItem(mortalHealth);
				attacker.sendMessage(ChatColor.GRAY + "One weaker than you has been slain by your hand.");
				attacker.sendMessage(ChatColor.DARK_RED + "You have captured a Mortal Soul.");
			}
		}
	}
}

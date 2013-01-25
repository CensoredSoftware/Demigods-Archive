package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DDamage implements Listener
{
	/*
	 * This handler deals with non-Demigods damage (all of that will go directly to DUtil's built in damage function) and converts it
	 * to Demigods HP, using individual multipliers for balance purposes.
	 * 
	 * The adjusted value should be around/less than 1 to adjust for the increased health, but not ridiculous
	 */
	public static boolean FRIENDLYFIRE = DSettings.getSettingBoolean("friendly_fire");

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e)
	{
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if (!DUtil.isFullParticipant(p))
		{
			return;
		}
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
		{
			return;
		}
		if (!DUtil.canWorldGuardDamage(p.getLocation()))
		{
			return;
		}
		
		if (!DUtil.canTarget(p, p.getLocation())) {
			e.setCancelled(true);
			return;
		}
		
		if (e instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent)e;
			if (ee.getDamager() instanceof Player)
			{
				if (!FRIENDLYFIRE) {
					if (DUtil.areAllied(p, (Player)ee.getDamager()))
					{
						e.setCancelled(true);
						return;
					}
				}
				DUtil.damageDemigods((LivingEntity)ee.getDamager(), p, e.getDamage(), e.getCause());
				e.setCancelled(true);
			}
		}
		
		if (e.getCause() == DamageCause.LAVA)
		{			
			e.setDamage(0); // Disable lava damage, fire damage does enough for Demigods.
			e.setCancelled(true);
		}
				
		if ((e.getCause() != DamageCause.ENTITY_ATTACK) && (e.getCause() != DamageCause.PROJECTILE))
		{
			DUtil.damageDemigodsNonCombat(p, e.getDamage(), e.getCause());
			e.setCancelled(true);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e)
	{
		if (DUtil.isFullParticipant(e.getPlayer()))
		{
			DUtil.setHP(e.getPlayer(), DUtil.getMaxHP(e.getPlayer()));
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onHeal(EntityRegainHealthEvent e)
	{
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if (!DUtil.isFullParticipant(p))
			return;
		DUtil.setHP(p, DUtil.getHP(p)+e.getAmount());
	}
	public static void syncHealth(Player p)
	{
		int current = DUtil.getHP(p);
		if (current < 1) { //if player should be dead
			p.setHealth(0);
			return;
		}
		double ratio = ((double)current)/DUtil.getMaxHP(p);
		int disp = (int)Math.ceil(ratio*20);
		if (disp < 1) disp = 1;
		p.setHealth(disp);
	}
	@SuppressWarnings("incomplete-switch")
	public static int armorReduction(Player p)
	{
		if (p.getLastDamageCause() != null)
			if ((p.getLastDamageCause().getCause() == DamageCause.FIRE) || (p.getLastDamageCause().getCause() == DamageCause.FIRE_TICK) ||(p.getLastDamageCause().getCause() == DamageCause.SUFFOCATION) ||
					(p.getLastDamageCause().getCause() == DamageCause.LAVA) || (p.getLastDamageCause().getCause() == DamageCause.DROWNING) || (p.getLastDamageCause().getCause() == DamageCause.STARVATION)
					|| (p.getLastDamageCause().getCause() == DamageCause.FALL) || (p.getLastDamageCause().getCause() == DamageCause.VOID) || (p.getLastDamageCause().getCause() == DamageCause.POISON) ||
					(p.getLastDamageCause().getCause() == DamageCause.MAGIC) || (p.getLastDamageCause().getCause() == DamageCause.SUICIDE)) {
				return 0;
			}
		double reduction = 0.0;
		if ((p.getInventory().getBoots() != null) && (p.getInventory().getBoots().getType() != Material.AIR))
		{
			switch (p.getInventory().getBoots().getType())
			{
			case LEATHER_BOOTS: reduction += 0.3; break;
			case IRON_BOOTS: reduction += 0.6; break;
			case GOLD_BOOTS: reduction += 0.5; break;
			case DIAMOND_BOOTS: reduction += 0.8; break;
			case CHAINMAIL_BOOTS: reduction += 0.7; break;
			}
			p.getInventory().getBoots().setDurability((short) (p.getInventory().getBoots().getDurability()+1));
			if (p.getInventory().getBoots().getDurability() > p.getInventory().getBoots().getType().getMaxDurability())
				p.getInventory().setBoots(null);
		}
		if ((p.getInventory().getLeggings() != null) && (p.getInventory().getLeggings().getType() != Material.AIR))
		{
			switch (p.getInventory().getLeggings().getType())
			{
			case LEATHER_LEGGINGS: reduction += 0.5; break;
			case IRON_LEGGINGS: reduction += 1; break;
			case GOLD_LEGGINGS: reduction += 0.8; break;
			case DIAMOND_LEGGINGS: reduction += 1.4; break;
			case CHAINMAIL_LEGGINGS: reduction += 1.1; break;
			}
			p.getInventory().getLeggings().setDurability((short) (p.getInventory().getLeggings().getDurability()+1));
			if (p.getInventory().getLeggings().getDurability() > p.getInventory().getLeggings().getType().getMaxDurability())
				p.getInventory().setLeggings(null);
		}
		if ((p.getInventory().getChestplate() != null) && (p.getInventory().getChestplate().getType() != Material.AIR)) {
			switch (p.getInventory().getChestplate().getType())
			{
			case LEATHER_CHESTPLATE: reduction += 0.8; break;
			case IRON_CHESTPLATE: reduction += 1.6; break;
			case GOLD_CHESTPLATE: reduction += 1.4; break;
			case DIAMOND_CHESTPLATE: reduction += 2; break;
			case CHAINMAIL_CHESTPLATE: reduction += 1.8; break;
			}
			p.getInventory().getChestplate().setDurability((short) (p.getInventory().getChestplate().getDurability()+1));
			if (p.getInventory().getChestplate().getDurability() > p.getInventory().getChestplate().getType().getMaxDurability())
				p.getInventory().setChestplate(null);
		}
		if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() != Material.AIR))
		{
			switch (p.getInventory().getHelmet().getType()) {
			case LEATHER_HELMET: reduction += 0.4; break;
			case IRON_HELMET: reduction += 0.8; break;
			case GOLD_HELMET: reduction += 0.7; break;
			case DIAMOND_HELMET: reduction += 1.3; break;
			case CHAINMAIL_HELMET: reduction += 1; break;
			}
			p.getInventory().getHelmet().setDurability((short) (p.getInventory().getHelmet().getDurability()+1));
			if (p.getInventory().getHelmet().getDurability() > p.getInventory().getHelmet().getType().getMaxDurability())
				p.getInventory().setHelmet(null);
		}
		return (int)(Math.round(reduction));
	}
	public static int specialReduction(Player p, int amount)
	{
		if (DUtil.getActiveEffectsList(p.getName()) == null)
			return amount;
		if (DUtil.getActiveEffectsList(p.getName()).contains("Invincible"))
		{
			amount *= 0.5;
		}
		if (DUtil.getActiveEffectsList(p.getName()).contains("Ceasefire"))
		{
			amount *= 0;
		}
		return amount;
	}
	
	public static ItemStack getBestSoul(Player p)
	{
		// Define Immortal Soul Fragment
		ItemStack health = new ItemStack(Material.GHAST_TEAR);
		
		String name = "Immortal Soul Fragment";
		List<String> lore = new ArrayList<String>();
		lore.add("Brings you back to life.");
		lore.add("You regain full heath!");
		
		ItemMeta item = health.getItemMeta();
		item.setDisplayName(name);
		item.setLore(lore);
		
		health.setItemMeta(item);
		
		// Define Immortal Soul Dust
		ItemStack halfHealth = new ItemStack(Material.GLOWSTONE_DUST);
		
		String halfName = "Immortal Soul Dust";
		List<String> halfLore = new ArrayList<String>();
		halfLore.add("Brings you back to life.");
		halfLore.add("You regain half heath!");
		
		ItemMeta halfItem = halfHealth.getItemMeta();
		halfItem.setDisplayName(halfName);
		halfItem.setLore(halfLore);
		
		halfHealth.setItemMeta(halfItem);
		
		// Define Mortal Soul
		ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET);
		
		String mortalName = "Mortal Soul";
		List<String> mortalLore = new ArrayList<String>();
		mortalLore.add("Brings you back to life.");
		mortalLore.add("You regain 20 health.");
		
		ItemMeta mortalItem = mortalHealth.getItemMeta();
		mortalItem.setDisplayName(mortalName);
		mortalItem.setLore(mortalLore);
		
		mortalHealth.setItemMeta(mortalItem);
		
		// Player inventory
		ItemStack[] invItems = p.getInventory().getContents();
		
		// Has soul?
		boolean hasFull = false;
		boolean hasHalf = false;
		boolean hasMortal = false;
		
		for (ItemStack invItem : invItems)
		{
			if (invItem == null) continue;
			if (!invItem.hasItemMeta()) continue;
			
			if (invItem.isSimilar(health))
			{
				hasFull = true;
			}
			else if (invItem.isSimilar(halfHealth))
			{
				hasHalf = true;
			}
			else if (invItem.isSimilar(mortalHealth))
			{
				hasMortal = true;
			}
		}
		
		if (hasFull) return health;
		else if (hasHalf) return halfHealth;
		else if (hasMortal) return mortalHealth;
		else return null;
	}
	
	public static Boolean cancelSoulDamage(Player p, int damage)
	{
		if (damage >= DUtil.getHP(p))
		{
			// Define Immortal Soul Fragment
			ItemStack health = new ItemStack(Material.GHAST_TEAR);
			
			String name = "Immortal Soul Fragment";
			List<String> lore = new ArrayList<String>();
			lore.add("Brings you back to life.");
			lore.add("You regain full heath!");
			
			ItemMeta item = health.getItemMeta();
			item.setDisplayName(name);
			item.setLore(lore);
			
			health.setItemMeta(item);
			
			// Define Immortal Soul Dust
			ItemStack halfHealth = new ItemStack(Material.GLOWSTONE_DUST);
			
			String halfName = "Immortal Soul Dust";
			List<String> halfLore = new ArrayList<String>();
			halfLore.add("Brings you back to life.");
			halfLore.add("You regain half heath!");
			
			ItemMeta halfItem = halfHealth.getItemMeta();
			halfItem.setDisplayName(halfName);
			halfItem.setLore(halfLore);
			
			halfHealth.setItemMeta(halfItem);
			
			// Define Mortal Soul
			ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET);
			
			String mortalName = "Mortal Soul";
			List<String> mortalLore = new ArrayList<String>();
			mortalLore.add("Brings you back to life.");
			mortalLore.add("You regain 20 health.");
			
			ItemMeta mortalItem = mortalHealth.getItemMeta();
			mortalItem.setDisplayName(mortalName);
			mortalItem.setLore(mortalLore);
			
			mortalHealth.setItemMeta(mortalItem);
			
			ItemStack[] invItems = p.getInventory().getContents();
			
			if (getBestSoul(p) == null) return false;
			
			for (ItemStack invItem : invItems)
			{
				if (invItem == null) continue;
				if (!invItem.hasItemMeta()) continue;
				
				if (invItem.isSimilar(getBestSoul(p)))
				{
					int amount = invItem.getAmount();
					p.getInventory().removeItem(invItem);
					invItem.setAmount(amount - 1);
					p.getInventory().addItem(invItem);
					
					if (getBestSoul(p) == health)hasFull(p);
					else if (getBestSoul(p) == halfHealth)
					{
						hasHalf(p);
						lessFakeDeath(p);
					}
					else if (getBestSoul(p) == mortalHealth)
					{
						hasMortal(p);
						fakeDeath(p);
					}
					
					return true;
				}
			}
		}
		return false;
	}
	
	public static void hasFull(Player p)
	{
		DUtil.setHP(p, DUtil.getMaxHP(p));
	}
	
	public static void hasHalf(Player p)
	{
		DUtil.setHP(p, (DUtil.getMaxHP(p) / 2));
	}
	
	public static void hasMortal(Player p)
	{
		DUtil.setHP(p, 20);
	}
	
	public static void fakeDeath(Player p)
	{
		double reduced = 0.1; //TODO
		long before = DUtil.getDevotion(p);
		for (Deity d : DUtil.getDeities(p)) {
			int reduceamt = (int)Math.round(DUtil.getDevotion(p, d)*reduced*DLevels.MULTIPLIER);
			if (reduceamt > DLevels.LOSSLIMIT)
				reduceamt = DLevels.LOSSLIMIT;
			DUtil.setDevotion(p, d, DUtil.getDevotion(p, d)-reduceamt);
		}
		if (DUtil.getDeities(p).size() < 2)
			p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to "+DUtil.getDeities(p).get(0).getName()+".");
		else p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to your deities.");
		p.sendMessage(ChatColor.DARK_RED+"Your Devotion has been reduced by "+(before-DUtil.getDevotion(p))+".");
	}
	
	public static void lessFakeDeath(Player p)
	{
		double reduced = 0.025; //TODO
		long before = DUtil.getDevotion(p);
		for (Deity d : DUtil.getDeities(p)) {
			int reduceamt = (int)Math.round(DUtil.getDevotion(p, d)*reduced*DLevels.MULTIPLIER);
			if (reduceamt > DLevels.LOSSLIMIT)
				reduceamt = DLevels.LOSSLIMIT;
			DUtil.setDevotion(p, d, DUtil.getDevotion(p, d)-reduceamt);
		}
		if (DUtil.getDeities(p).size() < 2)
			p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to "+DUtil.getDeities(p).get(0).getName()+".");
		else p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to your deities.");
		p.sendMessage(ChatColor.DARK_RED+"Your Devotion has been reduced by "+(before-DUtil.getDevotion(p))+".");
	}
}

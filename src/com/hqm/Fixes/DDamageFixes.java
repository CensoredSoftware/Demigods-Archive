package com.hqm.Fixes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.WildAmazing.marinating.Demigods.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Listeners.DDamage;
import com.WildAmazing.marinating.Demigods.Listeners.DDeities;
import com.WildAmazing.marinating.Demigods.Listeners.DPvP;

public class DDamageFixes implements Listener
{
	private static Set<EntityDamageEvent> important = Collections.synchronizedSet(new HashSet<EntityDamageEvent>());
	private static Set<EntityDamageEvent> processed = Collections.synchronizedSet(new HashSet<EntityDamageEvent>());

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onImportantDamage(EntityDamageEvent event)
	{
		if(isProcessed(event)) return;
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player && DMiscUtil.isFullParticipant((Player) ((EntityDamageByEntityEvent) event).getDamager())) important.add(event);
		else if(event instanceof EntityDamageByEntityEvent && (((EntityDamageByEntityEvent) event).getDamager() instanceof Fireball || ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow)) important.add(event);
		else if(event.getEntity() instanceof Player && DMiscUtil.isFullParticipant((Player) event.getEntity())) important.add(event);

		triggerDownstream(event);
	}

	public static void checkAndCancel(EntityDamageEvent event, boolean cancel)
	{
		if(important.contains(event))
		{
			event.setCancelled(cancel);
			important.remove(event);
		}
	}

	public static boolean isProcessed(EntityDamageEvent event)
	{
		return processed.contains(event);
	}

	public static void setLastDamage(LivingEntity target, EntityDamageEvent.DamageCause cause, int amount)
	{
		EntityDamageEvent damage = new EntityDamageEvent(target, cause, amount);
		processed.add(damage);
		target.setLastDamageCause(damage);
	}

	public static void setLastDamageBy(LivingEntity source, LivingEntity target, EntityDamageByEntityEvent.DamageCause cause, int amount)
	{
		EntityDamageByEntityEvent damageBy = new EntityDamageByEntityEvent(source, target, cause, amount);
		processed.add(damageBy);
		target.setLastDamageCause(damageBy);
	}

	private static void triggerDownstream(EntityDamageEvent event)
	{
		if(event instanceof EntityDamageByEntityEvent) DPvP.pvpDamage((EntityDamageByEntityEvent) event);
		DDamage.onDamage(event);
		DDeities.onEntityDamage(event);
	}
}

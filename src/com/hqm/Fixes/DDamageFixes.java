package com.hqm.Fixes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.*;
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

	@EventHandler(priority = EventPriority.HIGH)
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

	public static void setLastDamage(LivingEntity target, EntityDamageEvent.DamageCause cause, double amount)
	{
		EntityDamageEvent damage = new EntityDamageEvent(target, cause, amount);
		processed.add(damage);
		target.setLastDamageCause(damage);
	}

	public static void setLastDamageBy(LivingEntity source, LivingEntity target, EntityDamageByEntityEvent.DamageCause cause, double amount)
	{
		EntityDamageByEntityEvent damageBy = new EntityDamageByEntityEvent(source, target, cause, amount);
		processed.add(damageBy);
		target.setLastDamageCause(damageBy);
	}

	private static void deityDamageImmunity(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player p = (Player) event.getEntity();
			if((DMiscUtil.hasDeity(p, "Prometheus") || DMiscUtil.hasDeity(p, "Hephaestus")) && (event.getCause() == EntityDamageEvent.DamageCause.FIRE) || (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA))
			{
				p.setFireTicks(0);
				DDamageFixes.checkAndCancel(event, true);
				return;
			}
			else if(DMiscUtil.hasDeity(p, "Zeus") && (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING))
			{
				DDamageFixes.checkAndCancel(event, true);
				return;
			}
			else if(DMiscUtil.hasDeity(p, "Zeus") && event.getCause() == EntityDamageEvent.DamageCause.DROWNING)
			{
				DDamageFixes.checkAndCancel(event, true);
				return;
			}
			else if(DMiscUtil.hasDeity(p, "Atlas"))
			{
				if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
				{
					double reduction = (double) Math.round(Math.pow(DMiscUtil.getDevotion(p, "Atlas"), 0.115));
					if(reduction > event.getDamage()) reduction = event.getDamage();
					event.setDamage(event.getDamage() - reduction);
				}
				else if(event.getCause() == EntityDamageEvent.DamageCause.FALL)
				{
					if(DMiscUtil.getActiveEffectsList(p.getName()).contains("Unburden")) event.setDamage(event.getDamage() / 3.0);
				}
			}
			if(event instanceof EntityDamageByEntityEvent)
			{
				try
				{
					if(DMiscUtil.getActiveEffectsList(p.getName()).contains("Ceasefire"))
					{
						DDamageFixes.checkAndCancel(event, true);
						return;
					}
				}
				catch(Exception ignored)
				{}
				EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
				if(damageByEntityEvent.getDamager() instanceof Player)
				{
					Player damager = (Player) damageByEntityEvent.getDamager();
					if(DMiscUtil.isFullParticipant(p))
					{
						try
						{
							if(DMiscUtil.getActiveEffectsList(damager.getName()).contains("Ceasefire"))
							{
								DDamageFixes.checkAndCancel(event, true);
								return;
							}
						}
						catch(Exception ignored)
						{}
					}
				}
				if(DMiscUtil.hasDeity(p, "Hades") && (damageByEntityEvent.getDamager() instanceof Zombie) || (damageByEntityEvent.getDamager() instanceof Skeleton))
				{
					DDamageFixes.checkAndCancel(event, true);
					return;
				}
			}
		}
	}

	private static void triggerDownstream(EntityDamageEvent event)
	{
		DDamageFixes.deityDamageImmunity(event);
		if(event.isCancelled()) return;
		if(event instanceof EntityDamageByEntityEvent) DPvP.pvpDamage((EntityDamageByEntityEvent) event);
		DDamage.onDamage(event);
		DDeities.onEntityDamage(event);
	}
}

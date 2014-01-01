package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.Gods.Listeners.*;
import com.WildAmazing.marinating.Demigods.OtherCommands.PhantomCommands;
import com.WildAmazing.marinating.Demigods.Titans.Listeners.*;
import com.WildAmazing.marinating.Demigods.Utilities.Cuboid;
import com.WildAmazing.marinating.Demigods.Utilities.DeityLocale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class DemigodsEntityListener implements Listener
{
	private Demigods plugin;

	public DemigodsEntityListener(Demigods instance)
	{
		plugin = instance;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(!plugin.getConfigHandler().isParticipating(e.getEntity().getWorld())) return;
		if(e.isCancelled()) return;
		// overall
		GodCommands.onEntityDamage(e, plugin);
		TitanCommands.onEntityDamage(e, plugin);
		PhantomCommands.onPhantomDamage(e, plugin);
		// gods
		ZeusCommands.onEntityDamage(e, plugin);
		PoseidonCommands.onEntityDamage(e, plugin);
		HadesCommands.onEntityDamage(e, plugin);
		AresCommands.onEntityDamage(e, plugin);
		AthenaCommands.onEntityDamage(e, plugin);
		HephaestusCommands.onEntityDamage(e, plugin);
		ApolloCommands.onEntityDamage(e, plugin);
		// titans
		CronusCommands.onEntityDamage(e, plugin);
		PrometheusCommands.onEntityDamage(e, plugin);
		HyperionCommands.onEntityDamage(e, plugin);
		TyphonCommands.onEntityDamage(e, plugin);
		OceanusCommands.onEntityDamage(e, plugin);
		StyxCommands.onEntityDamage(e, plugin);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		if(!plugin.getConfigHandler().isParticipating(e.getEntity().getWorld())) return;
		// overall
		GodCommands.onEntityDeath(e, plugin);
		TitanCommands.onEntityDeath(e, plugin);
		// gods
		// titans
		CronusCommands.onEntityDeath(e, plugin);
		TyphonCommands.onEntityDeath(e, plugin);
		// other
		PhantomCommands.onPhantomDeath(e, plugin);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent e)
	{
		if(!plugin.getConfigHandler().isParticipating(e.getEntity().getWorld())) return;
		if(e.isCancelled()) return;
		PhantomCommands.onEntityTarget(e, plugin);
		// gods
		HadesCommands.onEntityTarget(e, plugin);
		AthenaCommands.onEntityTarget(e, plugin);
		// titans
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent e)
	{
		if(!plugin.getConfigHandler().isParticipating(e.getEntity().getWorld())) return;
		if(e.isCancelled()) return;
		// gods
		HadesCommands.onEntityCombust(e, plugin);
		// titans
	}

	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent e)
	{
		if(!plugin.getConfigHandler().isParticipating(e.getEntity().getWorld())) return;
		if(e.isCancelled()) return;
		for(DeityLocale dl : plugin.getAllLocs())
		{
			for(Cuboid c : dl.getLocale())
			{
				if(c.isInCuboid(e.getEntity().getLocation())) e.setRadius(0);
			}
		}
	}
}
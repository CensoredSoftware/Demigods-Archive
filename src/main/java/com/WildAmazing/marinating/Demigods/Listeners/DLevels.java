package com.WildAmazing.marinating.Demigods.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Util.DSettings;

public class DLevels implements Listener
{
	static final double MULTIPLIER = DSettings.getSettingDouble("globalexpmultiplier"); // can be modified
	static final int LOSSLIMIT = DSettings.getSettingInt("max_devotion_lost_on_death"); // max devotion lost on death per deity

	@SuppressWarnings({ "incomplete-switch" })
	@EventHandler(priority = EventPriority.HIGHEST)
	public void gainEXP(BlockBreakEvent e)
	{
		if(e.getPlayer() != null)
		{
			Player p = e.getPlayer();
			try
			{
				if(!DMiscUtil.canWorldGuardBuild(p, e.getBlock().getLocation())) return;
			}
			catch(Exception ex)
			{
				// Do nothing
			}
			if(!DMiscUtil.isFullParticipant(p)) return;
			if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
			int value = 0;
			switch(e.getBlock().getType())
			{
				case STONE:
					value = 1;
					break;
				case GOLD_ORE:
					value = 40;
					break;
				case IRON_ORE:
					value = 15;
					break;
				case DIAMOND_ORE:
					value = 100;
					break;
				case COAL_ORE:
					value = 3;
					break;
				case LAPIS_ORE:
					value = 30;
					break;
				case OBSIDIAN:
					value = 15;
					break;
				case SMOOTH_BRICK:
					value = 5;
					break;
				case MOSSY_COBBLESTONE:
					value = 6;
					break;
				case MOB_SPAWNER:
					value = 250;
					break;
				case REDSTONE_ORE:
					value = 5;
					break;
				case CLAY:
					value = 5;
					break;
				case GLOWSTONE:
					value = 5;
					break;
				case NETHERRACK:
					value = 2;
					break;
				case SOUL_SAND:
					value = 2;
					break;
				case MYCEL:
					value = 2;
					break;
				case NETHER_BRICK:
					value = 2;
					break;
				case ENDER_PORTAL_FRAME:
					value = 100;
					break;
				case ENDER_STONE:
					value = 5;
					break;
			}
			value *= MULTIPLIER;
			/*
			 * for (Deity d : DMiscUtil.getDeities(p)) {
			 * DMiscUtil.setDevotion(p, d, DMiscUtil.getDevotion(p, d)+value);
			 * }
			 */
			Deity d = DMiscUtil.getDeities(p).get((int) Math.floor(Math.random() * DMiscUtil.getDeities(p).size()));
			DMiscUtil.setDevotion(p, d, DMiscUtil.getDevotion(p, d) + value);
			levelProcedure(p);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void gainEXP(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			try
			{
				if(!DMiscUtil.canWorldGuardBuild(p, e.getEntity().getLocation())) return;
			}
			catch(Exception ex)
			{
				// Do nothing
			}
			if(!DMiscUtil.isFullParticipant(p)) return;
			if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
			if(!DMiscUtil.canTarget(e.getEntity(), e.getEntity().getLocation()))
			{
				return;
			}
			/*
			 * for (Deity d : DMiscUtil.getDeities(p)) {
			 * DMiscUtil.setDevotion(p, d, (int)(DMiscUtil.getDevotion(p, d)+e.getDamage()*MULTIPLIER));
			 * }
			 */
			// random deity
			Deity d = DMiscUtil.getDeities(p).get((int) Math.floor(Math.random() * DMiscUtil.getDeities(p).size()));
			DMiscUtil.setDevotion(p, d, (int) (DMiscUtil.getDevotion(p, d) + e.getDamage() * MULTIPLIER));
			levelProcedure(p);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void deathPenalty(EntityDeathEvent e)
	{
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if(!DMiscUtil.isFullParticipant(p)) return;
		if(!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
		double reduced = 0.1; // TODO
		long before = DMiscUtil.getDevotion(p);
		for(Deity d : DMiscUtil.getDeities(p))
		{
			int reduceamt = (int) Math.round(DMiscUtil.getDevotion(p, d) * reduced * MULTIPLIER);
			if(reduceamt > LOSSLIMIT) reduceamt = LOSSLIMIT;
			DMiscUtil.setDevotion(p, d, DMiscUtil.getDevotion(p, d) - reduceamt);
		}
		if(DMiscUtil.getDeities(p).size() < 2) p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to " + DMiscUtil.getDeities(p).get(0).getName() + ".");
		else p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to your deities.");
		p.sendMessage(ChatColor.DARK_RED + "Your Devotion has been reduced by " + (before - DMiscUtil.getDevotion(p)) + ".");
		DMiscUtil.setHP(p, 0);
	}

	public static void levelProcedure(Player p)
	{
		levelProcedure(p.getName());
	}

	public static void levelProcedure(String p)
	{
		if(DMiscUtil.isFullParticipant(p)) if(DMiscUtil.getAscensions(p) >= DMiscUtil.ASCENSIONCAP) return;
		while((DMiscUtil.getDevotion(p) >= DMiscUtil.costForNextAscension(p)) && (DMiscUtil.getAscensions(p) < DMiscUtil.ASCENSIONCAP))
		{
			DMiscUtil.setMaxHP(p, DMiscUtil.getMaxHP(p) + 10);
			DMiscUtil.setHP(p, DMiscUtil.getMaxHP(p));
			DMiscUtil.setAscensions(p, DMiscUtil.getAscensions(p) + 1);

			if(DMiscUtil.getOnlinePlayer(p) != null)
			{
				DMiscUtil.getOnlinePlayer(p).sendMessage(ChatColor.AQUA + "Congratulations! Your Ascensions increased to " + DMiscUtil.getAscensions(p) + ".");
				DMiscUtil.getOnlinePlayer(p).sendMessage(ChatColor.YELLOW + "Your maximum HP has increased to " + DMiscUtil.getMaxHP(p) + ".");
			}
		}
	}
}

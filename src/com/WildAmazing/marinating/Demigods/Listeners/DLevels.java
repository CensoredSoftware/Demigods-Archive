package com.WildAmazing.marinating.Demigods.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DLevels
{
	static double MULTIPLIER = DSettings.getSettingDouble("globalexpmultiplier"); //can be modified
	static int LOSSLIMIT = 15000; //max devotion lost on death per deity

	@SuppressWarnings({ "incomplete-switch" })
	public static void gainEXP(BlockBreakEvent e)
	{
		if (e.getPlayer() != null) {
			Player p = e.getPlayer();
			try
			{
				if (!DUtil.getWorldGuard().canBuild(p, e.getBlock().getLocation()))
					return;
			}
			catch (Exception ex)
			{
				// Do nothing
			}
			if (!DUtil.isFullParticipant(p))
				return;
			if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
				return;
			int value = 0;
			switch (e.getBlock().getType()) {
			case STONE: value = 1; break;
			case GOLD_ORE: value = 40; break;
			case IRON_ORE: value = 15; break;
			case DIAMOND_ORE: value = 100; break;
			case COAL_ORE: value = 3; break;
			case LAPIS_ORE: value = 30; break;
			case OBSIDIAN: value = 15; break;
			case SMOOTH_BRICK: value = 5; break;
			case MOSSY_COBBLESTONE: value = 6; break;
			case MOB_SPAWNER: value = 250; break;
			case REDSTONE_ORE: value = 5; break;
			case CLAY: value = 5; break;
			case GLOWSTONE: value = 5; break;
			case NETHERRACK: value = 2; break;
			case SOUL_SAND: value = 2; break;
			case MYCEL: value = 2; break;
			case NETHER_BRICK: value = 2; break;
			case ENDER_PORTAL_FRAME: value = 100; break;
			case ENDER_STONE: value = 5; break;
			}
			value *= MULTIPLIER;
			/*
			for (Deity d : DUtil.getDeities(p)) {
				DUtil.setDevotion(p, d, DUtil.getDevotion(p, d)+value);
			} */
			Deity d = DUtil.getDeities(p).get((int)Math.floor(Math.random()*DUtil.getDeities(p).size()));
			DUtil.setDevotion(p, d, DUtil.getDevotion(p, d)+value);
			levelProcedure(p);
		}
	}

	public static void gainEXP(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player){
			Player p = (Player)e.getDamager();
			try
			{
				if (!DUtil.getWorldGuard().canBuild(p, e.getEntity().getLocation()))
					return;
			}
			catch (Exception ex)
			{
				// Do nothing
			}
			if (!DUtil.isFullParticipant(p))
				return;
			if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
				return;
			if (!DUtil.canPVP(e.getEntity().getLocation())) {
				return;
			}
			/*
			for (Deity d : DUtil.getDeities(p)) {
				DUtil.setDevotion(p, d, (int)(DUtil.getDevotion(p, d)+e.getDamage()*MULTIPLIER));
			}*/
			//random deity
			Deity d = DUtil.getDeities(p).get((int)Math.floor(Math.random()*DUtil.getDeities(p).size()));
			DUtil.setDevotion(p, d, (int)(DUtil.getDevotion(p, d)+e.getDamage()*MULTIPLIER));
			levelProcedure(p);
		}
	}
	
	public static void deathPenalty(EntityDeathEvent e)
	{
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if (!DUtil.isFullParticipant(p))
			return;
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		double reduced = 0.1; //TODO
		long before = DUtil.getDevotion(p);
		for (Deity d : DUtil.getDeities(p)) {
			int reduceamt = (int)Math.round(DUtil.getDevotion(p, d)*reduced*MULTIPLIER);
			if (reduceamt > LOSSLIMIT)
				reduceamt = LOSSLIMIT;
			DUtil.setDevotion(p, d, DUtil.getDevotion(p, d)-reduceamt);
		}
		if (DUtil.getDeities(p).size() < 2)
			p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to "+DUtil.getDeities(p).get(0).getName()+".");
		else p.sendMessage(ChatColor.DARK_RED+"You have failed in your service to your deities.");
		p.sendMessage(ChatColor.DARK_RED+"Your Devotion has been reduced by "+(before-DUtil.getDevotion(p))+".");
		DUtil.setHP(p, 0);
	}
	
	public static void levelProcedure(Player p)
	{
		levelProcedure(p.getName());
	}
	
	public static void levelProcedure(String p) 
	{
		if (DUtil.isFullParticipant(p))
			if (DUtil.getAscensions(p) >= DUtil.ASCENSIONCAP)
				return;
		while ((DUtil.getDevotion(p) >= DUtil.costForNextAscension(p)) && (DUtil.getAscensions(p) < DUtil.ASCENSIONCAP)) {
			DUtil.setMaxHP(p, DUtil.getMaxHP(p)+10);
			DUtil.setHP(p, DUtil.getMaxHP(p));
			DUtil.setAscensions(p, DUtil.getAscensions(p)+1);
			if (DUtil.getOnlinePlayer(p) != null) {
				DUtil.getOnlinePlayer(p).sendMessage(ChatColor.AQUA+"Congratulations! Your Ascensions increased to "+DUtil.getAscensions(p)+".");
				DUtil.getOnlinePlayer(p).sendMessage(ChatColor.YELLOW+"Your maximum HP has increased to "+DUtil.getMaxHP(p)+".");
			}
		}
	}
}

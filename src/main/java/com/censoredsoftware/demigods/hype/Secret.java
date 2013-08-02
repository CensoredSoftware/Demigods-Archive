package com.censoredsoftware.demigods.hype;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;

public class Secret implements Deity
{
	private static final long serialVersionUID = -7376781567872708495L;

	private final String PLAYER;

	public Secret(String name)
	{
		PLAYER = name;
	}

	@Override
	public String getName()
	{
		return "?????";
	}

	@Override
	public String getPlayerName()
	{
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance()
	{
		return "?????";
	}

	@Override
	public void printInfo(Player p)
	{
		if(DMiscUtil.isFullParticipant(p) && DMiscUtil.hasDeity(p, getName()))
		{
			int devotion = DMiscUtil.getDevotion(p, getName());
			p.sendMessage("--" + ChatColor.GOLD + getName() + ChatColor.GRAY + "[" + devotion + "]");
			return;
		}
	}

	@Override
	public void onEvent(Event ee)
	{}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind)
	{}

	@Override
	public void onTick(long timeSent)
	{}
}

package com.WildAmazing.marinating.Demigods.Utilities;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class TitanPlayerInfo extends PlayerInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2412312732957076739L;
	private int POWER, GLORY;
	boolean CHAT;

	public TitanPlayerInfo(Player player, Divine firstDeity)
	{
		super(player);
		super.addToAllegiance(firstDeity);
		POWER = 0;
		GLORY = 0;
		CHAT = false;
	}

	public TitanPlayerInfo(Player player, ArrayList<Divine> deities, int power, int glory)
	{
		super(player);
		for(Divine d : deities)
			super.addToAllegiance(d);
		POWER = power;
		GLORY = glory;
		CHAT = false;
	}

	public boolean hasDeity(Divine deity)
	{
		if(deity == Divine.TITAN) return true;
		if(getAllegiance().contains(deity)) return true;
		return false;
	}

	public int getPower()
	{
		return POWER;
	}

	public void setPower(int amount)
	{
		POWER = amount;
	}

	public int getGlory()
	{
		return GLORY;
	}

	public void setGlory(int amount)
	{
		GLORY = amount;
	}

	public boolean isChat()
	{
		return CHAT;
	}

	public void setChat(boolean b)
	{
		CHAT = b;
	}

	public boolean isTierOne(Divine d)
	{
		switch(d)
		{
			case CRONUS:
			case RHEA:
			case PROMETHEUS:
				return true;
			default:
				return false;
		}
	}

	public int costForNext()
	{
		switch(super.getAllegiance().size())
		{
			case 1:
				return 6000;
			case 2:
				return 9000;
			case 3:
				return 15000;
			case 4:
				return 30000;
			case 5:
				return 50000;
			default:
				return 80000;
		}
	}

	public String getRank()
	{
		if(super.getAllegiance().size() == 1) return "Fallen";
		else if(super.getAllegiance().size() == 2) return "Lord";
		else if(super.getAllegiance().size() == 3) return "King";
		else if(super.getAllegiance().size() == 4) return "Reborn";
		else if(super.getAllegiance().size() == 5) return "Legion";
		else if(super.getAllegiance().size() >= 6) return "Immortal";
		return "Spawn";
	}

	public int getRanking()
	{
		return (POWER * 2) + (GLORY * 4) + (super.getAllegiance().size() * 20000);
	}
}

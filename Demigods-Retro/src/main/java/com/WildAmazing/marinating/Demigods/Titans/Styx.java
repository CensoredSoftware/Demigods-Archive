package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.entity.Player;

public class Styx extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5452083554070420482L;
	private long timeInvincible;
	private long reviveTime;
	private String invinciblename = null;

	public Styx(String p)
	{
		super(Divine.STYX, p);
		timeInvincible = System.currentTimeMillis();
		reviveTime = System.currentTimeMillis();
	}

	public void setPlayer(Player p)
	{
		invinciblename = p.getName();
	}

	public String getPlayer()
	{
		return invinciblename;
	}

	public void setTime(long l)
	{
		timeInvincible = l;
	}

	public long getTime()
	{
		return timeInvincible;
	}

	public void setReviveTime(long l)
	{
		reviveTime = l;
	}

	public long getReviveTime()
	{
		return reviveTime;
	}
}
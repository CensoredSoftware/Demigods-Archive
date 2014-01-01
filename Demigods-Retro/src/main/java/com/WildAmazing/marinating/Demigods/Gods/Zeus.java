package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Zeus extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4054921217543910156L;
	private boolean LIGHTNING = false;
	private boolean LIFT = false;
	private long stormTime;
	private Material LIFTITEM = null;
	private Material LIGHTNINGITEM = null;

	public Zeus(String p)
	{
		super(Divine.ZEUS, p);
		stormTime = System.currentTimeMillis();
	}

	public Material getLiftItem()
	{
		return LIFTITEM;
	}

	public void setLiftItem(Material m)
	{
		LIFTITEM = m;
	}

	public Material getLightningItem()
	{
		return LIGHTNINGITEM;
	}

	public void setLightningItem(Material m)
	{
		LIGHTNINGITEM = m;
	}

	public boolean getLightning()
	{
		return LIGHTNING;
	}

	public void setLightning(boolean b)
	{
		LIGHTNING = b;
	}

	public boolean getLift()
	{
		return LIFT;
	}

	public void setLift(boolean b)
	{
		LIFT = b;
	}

	public long getStormTime()
	{
		return stormTime;
	}

	public void setStormTime(long l)
	{
		stormTime = l;
	}
}
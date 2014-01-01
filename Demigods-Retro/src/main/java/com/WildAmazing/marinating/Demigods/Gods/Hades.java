package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Hades extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7576530048011441666L;
	boolean TARGET = false;
	boolean ENTOMB = false;
	long TATIME;
	Material ENTOMBITEM;
	Material TARGETITEM;

	public Hades(String p)
	{
		super(Divine.HADES, p);
		TATIME = System.currentTimeMillis();
	}

	public boolean getTarget()
	{
		return TARGET;
	}

	public void setTarget(boolean b)
	{
		TARGET = b;
	}

	public boolean getEntomb()
	{
		return ENTOMB;
	}

	public void setEntomb(boolean b)
	{
		ENTOMB = b;
	}

	public void setTime(long time)
	{
		TATIME = time;
	}

	public long getTime()
	{
		return TATIME;
	}

	public void setEntombItem(Material m)
	{
		ENTOMBITEM = m;
	}

	public Material getEntombItem()
	{
		return ENTOMBITEM;
	}

	public void setTargetItem(Material m)
	{
		TARGETITEM = m;
	}

	public Material getTargetItem()
	{
		return TARGETITEM;
	}
}
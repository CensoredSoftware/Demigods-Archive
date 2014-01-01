package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Poseidon extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7073035404815001514L;
	boolean SPEEDBREAK = false;
	boolean LIQUEFY = false;
	boolean TRIDENT = false;
	int DELAY = 3;
	long tridentTime;
	public long breakTime;
	Material LIQUEFYITEM;

	public Poseidon(String p)
	{
		super(Divine.POSEIDON, p);
		tridentTime = System.currentTimeMillis();
	}

	public boolean getTrident()
	{
		return TRIDENT;
	}

	public void setTrident(boolean b)
	{
		TRIDENT = b;
	}

	public long getTridentTime()
	{
		return tridentTime;
	}

	public void setTridentTime(long l)
	{
		tridentTime = l;
	}

	public boolean getLiquefy()
	{
		return LIQUEFY;
	}

	public void setLiquefy(boolean b)
	{
		LIQUEFY = b;
	}

	public boolean getBreak()
	{
		return SPEEDBREAK;
	}

	public void setBreak(boolean b)
	{
		SPEEDBREAK = b;
	}

	public void setDelay(int in)
	{
		DELAY = in;
	}

	public int getDelay()
	{
		return DELAY;
	}

	public Material getLiquefyItem()
	{
		return LIQUEFYITEM;
	}

	public void setLiquefyItem(Material m)
	{
		LIQUEFYITEM = m;
	}
}
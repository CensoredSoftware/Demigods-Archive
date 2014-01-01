package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Ares extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4766128921330356346L;
	private boolean BLOODTHIRST = false;
	private boolean FLING = false;
	private Material FLINGITEM;

	public Ares(String p)
	{
		super(Divine.ARES, p);
	}

	public void setBloodthirst(boolean b)
	{
		BLOODTHIRST = b;
	}

	public boolean getBloodthirst()
	{
		return BLOODTHIRST;
	}

	public void setFling(boolean b)
	{
		FLING = b;
	}

	public boolean getFling()
	{
		return FLING;
	}

	public void setFlingItem(Material m)
	{
		FLINGITEM = m;
	}

	public Material getFlingItem()
	{
		return FLINGITEM;
	}
}

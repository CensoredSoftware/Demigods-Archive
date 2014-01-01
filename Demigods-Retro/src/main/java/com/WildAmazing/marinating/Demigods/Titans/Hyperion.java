package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Hyperion extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3423750475269887528L;
	boolean FIREBALL = false;
	Material FIREBALLITEM;

	public Hyperion(String p)
	{
		super(Divine.HYPERION, p);
	}

	public void setCombust(boolean b)
	{
		FIREBALL = b;
	}

	public boolean getCombust()
	{
		return FIREBALL;
	}

	public void setCombustItem(Material m)
	{
		FIREBALLITEM = m;
	}

	public Material getCombustItem()
	{
		return FIREBALLITEM;
	}
}

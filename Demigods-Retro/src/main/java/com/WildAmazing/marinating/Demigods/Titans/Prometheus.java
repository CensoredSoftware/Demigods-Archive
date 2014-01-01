package com.WildAmazing.marinating.Demigods.Titans;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import org.bukkit.Material;

public class Prometheus extends Deity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4207696174916502585L;
	boolean FIREBALL = false;
	Material FIREBALLITEM;

	public Prometheus(String p)
	{
		super(Divine.PROMETHEUS, p);
	}

	public void setFireball(boolean b)
	{
		FIREBALL = b;
	}

	public boolean getFireball()
	{
		return FIREBALL;
	}

	public void setFireballItem(Material m)
	{
		FIREBALLITEM = m;
	}

	public Material getFireballItem()
	{
		return FIREBALLITEM;
	}
}

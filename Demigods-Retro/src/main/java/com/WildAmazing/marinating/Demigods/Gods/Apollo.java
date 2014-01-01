package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;

public class Apollo extends Deity
{
	private long infiniteTime;
	private boolean fireArrow;
	private boolean healArrow;
	private static final long serialVersionUID = -5442142595548004514L;

	public Apollo(String p)
	{
		super(Divine.APOLLO, p);
		infiniteTime = System.currentTimeMillis();
		fireArrow = false;
		healArrow = false;
	}

	public boolean getHealArrow()
	{
		return healArrow;
	}

	public void setHealArrow(boolean b)
	{
		healArrow = b;
	}

	public boolean getFireArrow()
	{
		return fireArrow;
	}

	public void setFireArrow(boolean b)
	{
		fireArrow = b;
	}

	public long getInfinite()
	{
		return infiniteTime;
	}

	public void setInfinite(long l)
	{
		infiniteTime = l;
	}
}
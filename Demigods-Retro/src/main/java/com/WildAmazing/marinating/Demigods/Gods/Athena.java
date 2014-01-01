package com.WildAmazing.marinating.Demigods.Gods;

import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;

public class Athena extends Deity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6613139535440000477L;
	boolean CEASEFIRE = false;
	boolean BUFF = false;

	public Athena(String p)
	{
		super(Divine.ATHENA, p);
	}

	public boolean getCeasefire()
	{
		return CEASEFIRE;
	}

	public void setCeasefire(boolean b)
	{
		CEASEFIRE = b;
	}

	public boolean getBuff()
	{
		return BUFF;
	}

	public void setBuff(boolean b)
	{
		BUFF = b;
	}
}

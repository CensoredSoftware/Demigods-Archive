package com.WildAmazing.marinating.Demigods.Utilities;

import java.io.Serializable;

public abstract class Deity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3835404655799787382L;
	private Divine NAME;
	private String PLAYER;

	/**
	 * Control class. Extended by other Gods.
	 */
	public Deity()
	{
		NAME = Divine.OMNI;
		PLAYER = null;
	}

	public Deity(Divine deityName, String playername)
	{
		NAME = deityName;
		PLAYER = playername;
	}

	public String getPlayer()
	{
		return PLAYER;
	}

	public Divine getName()
	{
		return NAME;
	}
}

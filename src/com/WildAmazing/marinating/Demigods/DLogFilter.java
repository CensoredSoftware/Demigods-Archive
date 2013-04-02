package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.Listeners.DPvP;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class DLogFilter implements Filter
{
	public DLogFilter()
	{}

	public boolean isLoggable(LogRecord arg0)
	{
		if(arg0.getMessage().toLowerCase().contains("disconnect"))
		{
			DPvP.filterCheckGeneric = false;
			DPvP.filterCheckStream = false;
			DPvP.filterCheckOverflow = false;
			DPvP.filterCheckTimeout = false;

			if(arg0.getMessage().toLowerCase().contains("genericreason"))
			{
				DPvP.filterCheckGeneric = true;
				return true;
			}
			if(arg0.getMessage().toLowerCase().contains("endofstream"))
			{
				DPvP.filterCheckStream = true;
				return true;
			}
			if(arg0.getMessage().toLowerCase().contains("overflow"))
			{
				DPvP.filterCheckOverflow = true;
				return true;
			}
			if(arg0.getMessage().toLowerCase().contains("timeout"))
			{
				DPvP.filterCheckTimeout = true;
				return true;
			}
			if(arg0.getMessage().toLowerCase().contains("quitting"))
			{
				DPvP.filterCheckQuitting = true;
				return true;
			}
			return true;
		}
		return true;
	}
}

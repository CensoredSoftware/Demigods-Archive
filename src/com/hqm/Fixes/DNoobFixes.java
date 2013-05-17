package com.hqm.Fixes;

import org.bukkit.entity.Player;

import com.WildAmazing.marinating.Demigods.DMiscUtil;
import com.WildAmazing.marinating.Demigods.DSettings;

public class DNoobFixes
{
	public static boolean isNoob(Player player)
	{
		return DMiscUtil.getDevotion(player) <= DSettings.getSettingInt("noob_level");
	}
}

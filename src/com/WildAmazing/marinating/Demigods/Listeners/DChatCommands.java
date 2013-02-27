package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DChatCommands implements Listener
{
	@EventHandler
	public void onChatCommand(AsyncPlayerChatEvent e)
	{
		// Define variables
		Player p = e.getPlayer();

		if(!DUtil.isFullParticipant(p)) return;
		if(e.getMessage().contains("qd")) qd(p, e);
		else if(e.getMessage().equals("dg")) dg(p, e);
	}

	private void qd(Player p, AsyncPlayerChatEvent e)
	{
		if((e.getMessage().charAt(0) == 'q') && (e.getMessage().charAt(1) == 'd'))
		{
			String str;
			if(p.getHealth() > 0)
			{
				ChatColor color = ChatColor.GREEN;
				if((DUtil.getHP(p) / (double) DUtil.getMaxHP(p)) < 0.25) color = ChatColor.RED;
				else if((DUtil.getHP(p) / (double) DUtil.getMaxHP(p)) < 0.5) color = ChatColor.YELLOW;
				str = "-- Your HP " + color + "" + DUtil.getHP(p) + "/" + DUtil.getMaxHP(p) + ChatColor.YELLOW + " Favor " + DUtil.getFavor(p) + "/" + DUtil.getFavorCap(p);
				if(DUtil.getActiveEffects(p.getName()).size() > 0)
				{
					HashMap<String, Long> effects = DUtil.getActiveEffects(p.getName());
					str += ChatColor.WHITE + " Active effects:";
					for(String stt : effects.keySet())
						str += " " + stt + "[" + ((effects.get(stt) - System.currentTimeMillis()) / 1000) + "s]";
				}
				try
				{
					String other = e.getMessage().split(" ")[1];
					if(other != null) other = DUtil.getDemigodsPlayer(other);
					if((other != null) && DUtil.isFullParticipant(other))
					{
						p.sendMessage(other + " -- " + DUtil.getAllegiance(other));
						if(DUtil.hasDeity(p, "Athena") || DUtil.hasDeity(p, "Themis"))
						{
							String st = ChatColor.GRAY + "Deities:";
							for(Deity d : DUtil.getDeities(other))
								st += " " + d.getName();
							p.sendMessage(st);
							p.sendMessage(ChatColor.GRAY + "HP " + DUtil.getHP(other) + "/" + DUtil.getMaxHP(other) + " Favor " + DUtil.getFavor(other) + "/" + DUtil.getFavorCap(other));
							if(DUtil.getActiveEffects(other).size() > 0)
							{
								HashMap<String, Long> fx = DUtil.getActiveEffects(other);
								str += ChatColor.GRAY + " Active effects:";
								for(String stt : fx.keySet())
									str += " " + stt + "[" + ((fx.get(stt) - System.currentTimeMillis()) / 1000) + "s]";
							}
						}
					}
				}
				catch(Exception invalid)
				{}
				p.sendMessage(str);
				e.getRecipients().clear();
				e.setCancelled(true);
			}
		}
	}

	private void dg(Player p, AsyncPlayerChatEvent e)
	{
		HashMap<String, ArrayList<String>> alliances = new HashMap<String, ArrayList<String>>();
		for(Player pl : DUtil.getPlugin().getServer().getOnlinePlayers())
		{
			if(DSettings.getEnabledWorlds().contains(pl.getWorld()))
			{
				if(DUtil.isFullParticipant(pl))
				{
					if(!alliances.containsKey(DUtil.getAllegiance(pl).toUpperCase()))
					{
						alliances.put(DUtil.getAllegiance(pl).toUpperCase(), new ArrayList<String>());
					}
					alliances.get(DUtil.getAllegiance(pl).toUpperCase()).add(pl.getName());
				}
			}
		}
		for(String alliance : alliances.keySet())
		{
			String names = "";
			for(String name : alliances.get(alliance))
				names += " " + name;
			p.sendMessage(ChatColor.YELLOW + alliance + ": " + ChatColor.WHITE + names);
		}
		e.getRecipients().clear();
		e.setCancelled(true);
	}
}

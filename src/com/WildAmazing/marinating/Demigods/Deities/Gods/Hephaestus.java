package com.WildAmazing.marinating.Demigods.Deities.Gods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import com.WildAmazing.marinating.Demigods.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Hephaestus implements Deity, Listener
{
	private static final long serialVersionUID = -2472769863144336856L;
	private String PLAYER;

	private static final int SKILLCOST = 200;
	private static final int ULTIMATECOST = 9000;
	private static final int ULTIMATECOOLDOWNMAX = 1800; // seconds
	private static final int ULTIMATECOOLDOWNMIN = 900;

	private static final String skillname = "Reforge";
	private static final String ult = "Shatter";

	private long ULTIMATETIME;

	public Hephaestus(String player)
	{
		PLAYER = player;
		ULTIMATETIME = System.currentTimeMillis();
	}

	@Override
	public String getName()
	{
		return "Hephaestus";
	}

	@Override
	public String getPlayerName()
	{
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance()
	{
		return "God";
	}

	@Override
	public void printInfo(Player p)
	{
		if(DMiscUtil.isFullParticipant(p) && DMiscUtil.hasDeity(p, getName()))
		{
			int devotion = DMiscUtil.getDevotion(p, getName());
			//
			int passiverange = (int) Math.round(20 * Math.pow(devotion, 0.15));
			int repairamt = (int) Math.ceil(10 * Math.pow(devotion, 0.09)); // percent
			int ultrange = (int) Math.ceil(15 * Math.pow(devotion, 0.09));
			int ultdamage = (int) Math.ceil(200 * Math.pow(devotion, 0.17));
			int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / DMiscUtil.ASCENSIONCAP)));
			//
			p.sendMessage("--" + ChatColor.GOLD + getName() + ChatColor.GRAY + "[" + devotion + "]");
			p.sendMessage(":Furnaces up to " + passiverange + " blocks away produce double yields.");
			p.sendMessage(":Immune to fire damage.");
			p.sendMessage(":Repair the item in hand by up to " + repairamt + "% of its durability.");
			p.sendMessage(ChatColor.GREEN + " /reforge " + ChatColor.YELLOW + "Costs " + SKILLCOST + " Favor.");
			p.sendMessage(":Hephaestus cripples the durability of enemy weapons and armor.");
			p.sendMessage("Range: " + ultrange + " Damage: " + ultdamage + "" + ChatColor.GREEN + " /shatter");
			p.sendMessage(ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
			return;
		}
		p.sendMessage("--" + getName());
		p.sendMessage("Passive: Doubles the output of nearby furnaces.");
		p.sendMessage("Passive: Immune to fire damage.");
		p.sendMessage("Active: Repair the durability of an item in hand. " + ChatColor.GREEN + "/reforge");
		p.sendMessage(ChatColor.YELLOW + "Costs " + SKILLCOST + " Favor.");
		p.sendMessage("Ultimate: Hephaestus unforges the weapons and armor of your");
		p.sendMessage("opponents. " + ChatColor.GREEN + "/shatter");
		p.sendMessage(ChatColor.YELLOW + "Select item: furnace");
	}

	@Override
	public void onEvent(Event ee)
	{
		// Nothing.
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind)
	{
		final Player p = P;
		if(DMiscUtil.hasDeity(p, getName()))
		{
			if(str.equalsIgnoreCase(skillname))
			{
				if(DMiscUtil.getFavor(p) < SKILLCOST)
				{
					p.sendMessage(ChatColor.YELLOW + "" + skillname + " requires " + SKILLCOST + " Favor to use.");
					return;
				}
				int durability = p.getItemInHand().getDurability();
				if(durability == 0)
				{
					p.sendMessage(ChatColor.YELLOW + "This item cannot be repaired.");
					return;
				}
				double repairamt = Math.ceil(10 * Math.pow(DMiscUtil.getDevotion(p, getName()), 0.09)) / 100;
				short num = (short) (p.getItemInHand().getDurability() * (1 - repairamt));
				p.sendMessage(ChatColor.RED + "Hephaestus" + ChatColor.WHITE + " has increased the item's durability by " + (p.getItemInHand().getDurability() - num) + ".");
				p.getItemInHand().setDurability(num);
				DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - SKILLCOST);
			}
			else if(str.equalsIgnoreCase(ult))
			{
				long TIME = ULTIMATETIME;
				if(System.currentTimeMillis() < TIME)
				{
					p.sendMessage(ChatColor.YELLOW + "You cannot use " + ult + " again for " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
					p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
					return;
				}
				if(DMiscUtil.getFavor(p) >= ULTIMATECOST)
				{
					if(!DMiscUtil.canTarget(p, p.getLocation()))
					{
						p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
						return;
					}
					int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / DMiscUtil.ASCENSIONCAP)));
					ULTIMATETIME = System.currentTimeMillis() + (t * 1000);
					int num = shatter(p);
					if(num > 0)
					{
						p.sendMessage(ChatColor.RED + "Hephaestus" + ChatColor.WHITE + " has unforged the equipment of " + num + " enemy players.");
						DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - ULTIMATECOST);
					}
					else p.sendMessage(ChatColor.YELLOW + "No targets found.");
				}
				else p.sendMessage(ChatColor.YELLOW + "" + ult + " requires " + ULTIMATECOST + " Favor.");
			}
		}
	}

	@Override
	public void onTick(long timeSent)
	{}

	@EventHandler
	public static void onSmelt(FurnaceSmeltEvent e)
	{
		if(e.getBlock() == null) return;
		for(String s : DMiscUtil.getFullParticipants())
		{
			Player p = DMiscUtil.getOnlinePlayer(s);
			if((p == null) || p.isDead()) continue;
			if(DMiscUtil.hasDeity(p, "Hephaestus"))
			{
				if(p.getLocation().getWorld().equals(e.getBlock().getLocation().getWorld())) if(p.getLocation().distance(e.getBlock().getLocation()) < (int) Math.round(20 * Math.pow(DMiscUtil.getDevotion(p, "Hephaestus"), 0.15)))
				{
					int amount = e.getResult().getAmount() * 2;
					ItemStack out = e.getResult();
					out.setAmount(amount);
					e.setResult(out);
					return;
				}
			}
		}
	}

	private int shatter(Player p)
	{
		int ultrange = (int) Math.ceil(15 * Math.pow(DMiscUtil.getDevotion(p, getName()), 0.09));
		int ultdamage = (int) Math.ceil(200 * Math.pow(DMiscUtil.getDevotion(p, getName()), 0.17));
		if(ultdamage > 2000) ultdamage = 2000;
		int i = 0;
		for(Player pl : p.getWorld().getPlayers())
		{
			if(pl.getLocation().distance(p.getLocation()) <= ultrange)
			{
				if(!DMiscUtil.canTarget(pl, pl.getLocation())) continue;
				if(DMiscUtil.isFullParticipant(pl))
				{
					if(DMiscUtil.getAllegiance(pl).equalsIgnoreCase(getDefaultAlliance()))
					{
						i++;
						pl.sendMessage(ChatColor.RED + "Hephaestus" + ChatColor.WHITE + " has unforged your equipment.");
						if(p.getItemInHand() != null) p.getItemInHand().setDurability((short) ultdamage);
						for(ItemStack ii : pl.getInventory().getArmorContents())
							if(ii != null) ii.setDurability((short) ultdamage);
					}
				}
			}
		}
		return i;
	}
}

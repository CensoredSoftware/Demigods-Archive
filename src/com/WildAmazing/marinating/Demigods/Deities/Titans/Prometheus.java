package com.WildAmazing.marinating.Demigods.Deities.Titans;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

//TODO better replacement for BLAZE
public class Prometheus implements Deity
{
	private static final long serialVersionUID = -6437607905225500420L;
	private String PLAYER;
	private final int FIREBALLCOST = 100;
	private final int PROMETHEUSULTIMATECOST = 5500;
	private final int PROMETHEUSULTIMATECOOLDOWNMAX = 600; // seconds
	private final int PROMETHEUSULTIMATECOOLDOWNMIN = 60;
	private final int BLAZECOST = 400;
	private final double FIREBALLDELAY = 0.5; // seconds
	private final double BLAZEDELAY = 15;

	public Material FIREBALLITEM = null;
	public Material BLAZEITEM = null;
	private boolean FIREBALL = false;
	private boolean BLAZE = false;
	private long FIRESTORMTIME;
	private long BLAZETIME;
	private long FIREBALLTIME;
	public String DISPLAYNAME;

	public Prometheus(String name)
	{
		PLAYER = name;
		FIRESTORMTIME = System.currentTimeMillis();
		BLAZETIME = System.currentTimeMillis();
		DISPLAYNAME = name;
		FIREBALLTIME = System.currentTimeMillis();
	}

	@Override
	public String getName()
	{
		return "Prometheus";
	}

	public boolean getBLAZE()
	{
		return BLAZE;
	}

	@Override
	public String getPlayerName()
	{
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance()
	{
		return "Titan";
	}

	@Override
	public void printInfo(Player p)
	{
		if(DUtil.hasDeity(p, "Prometheus") && DUtil.isFullParticipant(p))
		{
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			int t = (int) (PROMETHEUSULTIMATECOOLDOWNMAX - ((PROMETHEUSULTIMATECOOLDOWNMAX - PROMETHEUSULTIMATECOOLDOWNMIN) * ((double) DUtil.getAscensions(p) / 100)));
			int diameter = (int) Math.ceil(1.43 * Math.pow(devotion, 0.1527));
			if(diameter > 12) diameter = 12;
			int firestormshots = (int) Math.round(2 * Math.pow(DUtil.getDevotion(p, getName()), 0.15));
			/*
			 * The printed text
			 */
			p.sendMessage("--" + ChatColor.GOLD + "Prometheus" + ChatColor.GRAY + "[" + devotion + "]");
			p.sendMessage(":Immune to fire damage.");
			p.sendMessage(":Shoot a fireball at the cursor's location. " + ChatColor.GREEN + "/fireball");
			p.sendMessage(ChatColor.YELLOW + "Costs " + FIREBALLCOST + " Favor.");
			if(((Prometheus) (DUtil.getDeity(p, "Prometheus"))).FIREBALLITEM != null) p.sendMessage(ChatColor.AQUA + "    Bound to " + ((Prometheus) (DUtil.getDeity(p, "Prometheus"))).FIREBALLITEM.name());
			else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
			p.sendMessage(":Ignite the ground at the target location with diameter " + diameter + ". " + ChatColor.GREEN + "/blaze");
			p.sendMessage(ChatColor.YELLOW + "Costs " + BLAZECOST + " Favor. Cooldown time: " + BLAZEDELAY + " seconds.");
			if(((Prometheus) (DUtil.getDeity(p, "Prometheus"))).BLAZEITEM != null) p.sendMessage(ChatColor.AQUA + "    Bound to " + ((Prometheus) (DUtil.getDeity(p, "Prometheus"))).BLAZEITEM.name());
			else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
			p.sendMessage(":Prometheus rains fire on nearby enemies.");
			p.sendMessage("Shoots " + firestormshots + " fireballs. " + ChatColor.GREEN + "/firestorm");
			p.sendMessage(ChatColor.YELLOW + "Costs " + PROMETHEUSULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
			return;
		}
		p.sendMessage("--" + ChatColor.GOLD + "Prometheus");
		p.sendMessage("Passive: Immune to fire damage.");
		p.sendMessage("Active: Shoot a fireball. " + ChatColor.GREEN + "/fireball");
		p.sendMessage(ChatColor.YELLOW + "Costs " + FIREBALLCOST + " Favor. Can bind.");
		p.sendMessage("Active: Ignite the ground around the target." + ChatColor.GREEN + " /blaze ");
		p.sendMessage(ChatColor.YELLOW + "Costs " + BLAZECOST + " Favor. Can bind. Has cooldown.");
		p.sendMessage("Ultimate: Prometheus rains fireballs on your enemies.");
		p.sendMessage(ChatColor.GREEN + "/firestorm" + ChatColor.YELLOW + " Costs " + PROMETHEUSULTIMATECOST + " Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW + "Select item: clay ball");
	}

	@Override
	public void onEvent(Event ee)
	{
		if(ee instanceof PlayerInteractEvent)
		{
			PlayerInteractEvent e = (PlayerInteractEvent) ee;
			Player p = e.getPlayer();
			if(!DUtil.isFullParticipant(p)) return;
			if(!DUtil.hasDeity(p, "Prometheus")) return;
			if(FIREBALL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == FIREBALLITEM)))
			{
				if(System.currentTimeMillis() < FIREBALLTIME) return;
				if(DUtil.getFavor(p) >= FIREBALLCOST)
				{
					if(!DUtil.canTarget(p, p.getLocation()))
					{
						p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
						return;
					}
					DUtil.setFavor(p, DUtil.getFavor(p) - FIREBALLCOST);
					shootFireball(p.getEyeLocation(), DUtil.getTargetLocation(p), p);
					FIREBALLTIME = System.currentTimeMillis() + (long) (FIREBALLDELAY * 1000);
				}
				else
				{
					FIREBALL = false;
					p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor to do that.");
				}
			}
			if(BLAZE || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == BLAZEITEM)))
			{
				if(System.currentTimeMillis() < BLAZETIME)
				{
					p.sendMessage(ChatColor.YELLOW + "Blaze is on cooldown.");
					return;
				}
				if(DUtil.getFavor(p) >= BLAZECOST)
				{
					if(!DUtil.canTarget(p, p.getLocation()))
					{
						p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
						return;
					}
					int diameter = (int) Math.ceil(1.43 * Math.pow(DUtil.getDevotion(p, getName()), 0.1527));
					if(diameter > 12) diameter = 12;
					if(DUtil.canLocationPVP(DUtil.getTargetLocation(p)))
					{
						blaze(DUtil.getTargetLocation(p), diameter);
						DUtil.setFavor(p, DUtil.getFavor(p) - BLAZECOST);
						BLAZETIME = System.currentTimeMillis() + (long) (BLAZEDELAY * 1000);
					}
					else p.sendMessage(ChatColor.YELLOW + "That is a protected area.");
				}
				else
				{
					BLAZE = false;
					p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor to do that.");
				}
			}
		}
		else if(ee instanceof EntityDamageEvent)
		{
			EntityDamageEvent e1 = (EntityDamageEvent) ee;
			if((e1.getCause() == DamageCause.FIRE) || (e1.getCause() == DamageCause.FIRE_TICK))
			{
				if(!(e1.getEntity() instanceof Player)) return;
				Player p = (Player) e1.getEntity();
				if(!DUtil.isFullParticipant(p)) return;
				if(!DUtil.hasDeity(p, "Prometheus")) return;
				e1.setDamage(0);
				e1.setCancelled(true);
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind)
	{
		final Player p = P;
		if(!DUtil.isFullParticipant(p)) return;
		if(!DUtil.hasDeity(p, "Prometheus")) return;
		if(str.equalsIgnoreCase("fireball"))
		{
			if(bind)
			{
				if(FIREBALLITEM == null)
				{
					if(DUtil.isBound(p, p.getItemInHand().getType())) p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
					if(p.getItemInHand() == null) p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
					else
					{
						DUtil.registerBind(p, p.getItemInHand().getType());
						FIREBALLITEM = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW + "Fireball is now bound to " + p.getItemInHand().getType().name() + ".");
					}
				}
				else
				{
					DUtil.removeBind(p, FIREBALLITEM);
					p.sendMessage(ChatColor.YELLOW + "Fireball is no longer bound to " + FIREBALLITEM.name() + ".");
					FIREBALLITEM = null;
				}
				return;
			}
			if(FIREBALL)
			{
				FIREBALL = false;
				p.sendMessage(ChatColor.YELLOW + "Fireball is no longer active.");
			}
			else
			{
				FIREBALL = true;
				p.sendMessage(ChatColor.YELLOW + "Fireball is now active.");
			}
		}
		else if(str.equalsIgnoreCase("blaze"))
		{
			if(bind)
			{
				if(BLAZEITEM == null)
				{
					if(DUtil.isBound(p, p.getItemInHand().getType())) p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
					if(p.getItemInHand() == null) p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
					else
					{
						DUtil.registerBind(p, p.getItemInHand().getType());
						BLAZEITEM = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW + "Blaze is now bound to " + p.getItemInHand().getType().name() + ".");
					}
				}
				else
				{
					DUtil.removeBind(p, BLAZEITEM);
					p.sendMessage(ChatColor.YELLOW + "Blaze is no longer bound to " + BLAZEITEM.name() + ".");
					BLAZEITEM = null;
				}
				return;
			}
			if(BLAZE)
			{
				BLAZE = false;
				p.sendMessage(ChatColor.YELLOW + "Blaze is no longer active.");
			}
			else
			{
				BLAZE = true;
				p.sendMessage(ChatColor.YELLOW + "Blaze is now active.");
			}
		}
		else if(str.equalsIgnoreCase("firestorm"))
		{
			if(System.currentTimeMillis() < FIRESTORMTIME)
			{
				p.sendMessage(ChatColor.YELLOW + "You cannot use the firestorm again for " + ((((FIRESTORMTIME) / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
				p.sendMessage(ChatColor.YELLOW + "and " + ((((FIRESTORMTIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
				return;
			}
			if(DUtil.getFavor(p) >= PROMETHEUSULTIMATECOST)
			{
				if(!DUtil.canTarget(p, p.getLocation()))
				{
					p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
					return;
				}
				int t = (int) (PROMETHEUSULTIMATECOOLDOWNMAX - ((PROMETHEUSULTIMATECOOLDOWNMAX - PROMETHEUSULTIMATECOOLDOWNMIN) * ((double) DUtil.getAscensions(p) / 100)));
				FIRESTORMTIME = System.currentTimeMillis() + (t * 1000);
				p.sendMessage("In exchange for " + ChatColor.AQUA + PROMETHEUSULTIMATECOST + ChatColor.WHITE + " Favor, ");
				p.sendMessage(ChatColor.GOLD + "Prometheus " + ChatColor.WHITE + " has unleashed his wrath on " + firestorm(p) + " non-allied entities.");
				DUtil.setFavor(p, DUtil.getFavor(p) - PROMETHEUSULTIMATECOST);
			}
			else p.sendMessage("Firestorm requires " + PROMETHEUSULTIMATECOST + " Favor.");
        }
	}

	@Override
	public void onTick(long timeSent)
	{

	}

	private void shootFireball(Location from, Location to, Player player)
	{
		if(!DUtil.canLocationPVP(to) || !DUtil.canLocationPVP(from)) return;

		Vector path = to.toVector().subtract(from.toVector());
		Location spawnLocation = new Location(player.getWorld(), path.getX(), path.getY(), path.getZ());

		// Fire the fireball
		Entity fireball = player.getWorld().spawn(spawnLocation, Fireball.class);
		fireball.teleport(to);
	}

	private void blaze(Location target, int diameter)
	{
		for(int x = -diameter / 2; x <= diameter / 2; x++)
		{
			for(int y = -diameter / 2; y <= diameter / 2; y++)
			{
				for(int z = -diameter / 2; z <= diameter / 2; z++)
				{
					Block b = target.getWorld().getBlockAt(target.getBlockX() + x, target.getBlockY() + y, target.getBlockZ() + z);
					if((b.getType() == Material.AIR) || (((b.getType() == Material.SNOW)) && DUtil.canLocationPVP(b.getLocation()))) b.setType(Material.FIRE);
				}
			}
		}
	}

	private int firestorm(Player p)
	{
		int total = 20 * (int) Math.round(2 * Math.pow(DUtil.getDevotion(p, getName()), 0.15));
		Vector ploc = p.getLocation().toVector();
		ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
		for(LivingEntity anEntity : p.getWorld().getLivingEntities())
		{
			if(anEntity instanceof Player) if(DUtil.isFullParticipant((Player) anEntity)) if(DUtil.areAllied(p, (Player) anEntity)) continue;
			if(!DUtil.canTarget(anEntity, anEntity.getLocation())) continue;
			if(anEntity.getLocation().toVector().isInSphere(ploc, 50)) entitylist.add(anEntity);
		}
		final Player pl = p;
		final ArrayList<LivingEntity> enList = entitylist;
		for(int i = 0; i <= total; i += 20)
		{
			DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable()
			{
				@Override
				public void run()
				{
					for(LivingEntity e1 : enList)
					{
						Location up = new Location(e1.getWorld(), e1.getLocation().getX() + Math.random() * 5, 256, e1.getLocation().getZ() + Math.random() * 5);
						up.setPitch(90);
						shootFireball(up, new Location(e1.getWorld(), e1.getLocation().getX() + Math.random() * 5, e1.getLocation().getY(), e1.getLocation().getZ() + Math.random() * 5), pl);
					}
				}
			}, i);
		}
		return enList.size();
	}
}

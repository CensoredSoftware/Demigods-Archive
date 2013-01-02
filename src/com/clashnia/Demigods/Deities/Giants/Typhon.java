package com.clashnia.Demigods.Deities.Giants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Typhon implements Deity {
	private static final long serialVersionUID = -7376781567872708495L;

	private String PLAYER;

	private static final int SKILLCOST = 120;
	private static final int SKILLDELAY = 1250; //milliseconds
	private static final int ULTIMATECOST = 10000;
	private static final int ULTIMATECOOLDOWNMAX = 180; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 60;
	private static final int EXPLOSIONSIZE = 4;
	private static final double PLAYERPUSH = 0.8;

	private static final String skillname = "";
	private static final String ult = "";

	private boolean SKILL = false;
	private Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;
	private long LASTCHECK;

	public Typhon(String name) {
		PLAYER = name;
		SKILLTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Typhon";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance() {
		return "Giant";
	}

	@Override
	public void printInfo(Player p) {
		if (DUtil.isFullParticipant(p) && DUtil.hasDeity(p, getName())) {
			int devotion = DUtil.getDevotion(p, getName());
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			return;
		}
		p.sendMessage("--"+getName());
		p.sendMessage("Passive: Explodes on death caused by PvP.");
		p.sendMessage("Passive: Knockback largely increased.");
		p.sendMessage("Active: "); //TODO
		//p.sendMessage("Ultimate: ");
		p.sendMessage(ChatColor.YELLOW+"Select item: gunpowder");
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.isFullParticipant(p) || !DUtil.hasDeity(p, getName()))
				return;
			if (SKILL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SKILLBIND))) {
				if (SKILLTIME > System.currentTimeMillis())
					return;
				SKILLTIME = System.currentTimeMillis()+SKILLDELAY;
				if (DUtil.getFavor(p) >= SKILLCOST) {
					/*
					 * Skill
					 */
					DUtil.setFavor(p, DUtil.getFavor(p)-SKILLCOST);
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					SKILL = false;
				}
			}
		}
		else if (ee instanceof EntityDeathEvent)
		{
			EntityDeathEvent e = (EntityDeathEvent)ee;
			if (e.getEntity() instanceof Player)
			{
				EntityDamageEvent ede = e.getEntity().getLastDamageCause();
			    DamageCause dc = ede.getCause();
				if (dc == DamageCause.ENTITY_ATTACK || dc == DamageCause.SUICIDE)
				{
					Player p = (Player)e.getEntity();
						if (!DUtil.hasDeity(p, "Typhon"))
							return;
						if ((DUtil.canWorldGuardPVP(p.getLocation()) || DUtil.canFactionsPVP(p.getLocation())) && (DUtil.getNearbyShrine(e.getEntity().getLocation()) == null || DUtil.getDeityAtShrine(DUtil.getNearbyShrine(e.getEntity().getLocation())) != "Typhon"))
							p.getWorld().createExplosion(p.getLocation(), EXPLOSIONSIZE);
				}
			}
		}
		else if (ee instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)ee;
			if (e.getDamager() instanceof Player)
			{
				Player p = (Player)e.getDamager();
				if (!DUtil.canWorldGuardPVP(p.getLocation()) || !DUtil.canFactionsPVP(p.getLocation())) return;
				if (!DUtil.hasDeity(p, "Typhon")) return;
				LivingEntity le = (LivingEntity)e.getEntity();
				Vector v = p.getLocation().toVector();
				Vector victor = le.getLocation().toVector().subtract(v);
				victor.multiply(PLAYERPUSH);
				if (le instanceof Player)
				{
					Player pl = (Player)le;
					if (DUtil.isFullParticipant(pl))
					{
						if (e.getEntity().isDead()) return;
					}
				}
				le.setVelocity(victor); //super kb
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (DUtil.hasDeity(p, getName())) {
			if (str.equalsIgnoreCase(skillname)) {
				if (bind) {
					if (SKILLBIND == null) {
						if (DUtil.isBound(p, p.getItemInHand().getType()))
							p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
						if (p.getItemInHand().getType() == Material.AIR)
							p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
						else {
							DUtil.registerBind(p, p.getItemInHand().getType());
							SKILLBIND = p.getItemInHand().getType();
							p.sendMessage(ChatColor.YELLOW+""+skillname+" is now bound to "+p.getItemInHand().getType().name()+".");
						}
					} else {
						DUtil.removeBind(p, SKILLBIND);
						p.sendMessage(ChatColor.YELLOW+""+skillname+" is no longer bound to "+SKILLBIND.name()+".");
						SKILLBIND = null;
					}
					return;
				}
				if (SKILL) {
					SKILL = false;
					p.sendMessage(ChatColor.YELLOW+""+skillname+" is no longer active.");
				} else {
					SKILL = true;
					p.sendMessage(ChatColor.YELLOW+""+skillname+" is now active.");
				}
			} else if (str.equalsIgnoreCase(ult)) {
				long TIME = ULTIMATETIME;
				if (System.currentTimeMillis() < TIME){
					p.sendMessage(ChatColor.YELLOW+"You cannot use "+ult+" again for "+((((TIME)/1000)-
							(System.currentTimeMillis()/1000)))/60+" minutes");
					p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
					return;
				}
				if (DUtil.getFavor(p)>=ULTIMATECOST) {
					int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/100)));
					ULTIMATETIME = System.currentTimeMillis()+(t*1000);
					/*
					 * Ultimate code
					 */
					DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
				} else p.sendMessage(ChatColor.YELLOW+""+ult+" requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}

	@Override
	public void onTick(long timeSent) {
		if (timeSent > LASTCHECK+1000) {
			LASTCHECK = timeSent;
		}
	}
}

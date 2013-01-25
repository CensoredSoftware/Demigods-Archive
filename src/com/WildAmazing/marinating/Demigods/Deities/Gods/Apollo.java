package com.WildAmazing.marinating.Demigods.Deities.Gods;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Apollo implements Deity {
	private static final long serialVersionUID = -5219841682574911103L;

	private String PLAYER;

	private static final int SKILLCOST = 150;
	private static final int SKILLDELAY = 3000; //milliseconds
	private static final int ULTIMATECOST = 6300;
	private static final int ULTIMATECOOLDOWNMAX = 600; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 120;

	private static final String skillname = "Cure";
	private static final String ult = "Finale";

	private boolean SKILL = false;
	private Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;
	private long LASTCHECK;

	public Apollo(String player) {
		PLAYER = player;
		SKILLTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Apollo";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance() {
		return "God";
	}

	@Override
	public void printInfo(Player p) {
		if (DUtil.isFullParticipant(p) && DUtil.hasDeity(p, getName())) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Special values
			 */
			//passive
			int duration = (int)Math.round(60*Math.pow(devotion, 0.09)); //seconds
			//active
			int healamt = (int)Math.round(5*Math.pow(devotion, 0.09));
			//ult
			int ultrange = (int)Math.round(20*Math.pow(devotion, 0.15));
			int ultslowduration = (int)Math.round(10*Math.pow(devotion, 0.05)); //seconds
			int ultattacks = (int)Math.round(4*Math.pow(devotion, 0.08)); //number of arrow "waves"
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/DUtil.ASCENSIONCAP)));
			//print
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Play a music disc to receive a buff lasting "+duration+" seconds.");
			p.sendMessage(":Left-click to heal yourself for "+(healamt/2)+" and a target");
			p.sendMessage("ally for "+healamt+" health."+ChatColor.GREEN+" /cure "+ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor.");
			if (((Apollo)DUtil.getDeity(p, getName())).SKILLBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Apollo)DUtil.getDeity(p, getName())).SKILLBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage("Slow enemies in range "+ultrange+" for "+ultslowduration+" seconds and strike");
			p.sendMessage("them with "+ultattacks+" waves of arrows."+ChatColor.GREEN+" /finale");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+getName());
		p.sendMessage("Passive: Play a music disc to receive special buffs from Apollo.");
		p.sendMessage("Active: Heal yourself and a target ally."+ChatColor.GREEN+" /cure");
		p.sendMessage(ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Slow enemies and rain arrows on them."+ChatColor.GREEN+" /finale ");
		p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: jukebox");
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onEvent(Event ee) {
		if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.isFullParticipant(p) || !DUtil.hasDeity(p, getName()))
				return;
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (e.getClickedBlock().getType() != Material.JUKEBOX)
					return;
				if (p.getItemInHand() != null)
					switch (p.getItemInHand().getType()) {
					case GOLD_RECORD:
						applyEffect(PotionEffectType.SPEED, "speed");
						break;
					case GREEN_RECORD:
						applyEffect(PotionEffectType.FAST_DIGGING, "mining speed");
						break;
					case RECORD_3:
						applyEffect(null, "health regeneration");
						break;
					case RECORD_4:
						applyEffect(null, "Favor regeneration");
						break;
					case RECORD_5:
						applyEffect(PotionEffectType.INCREASE_DAMAGE, "strength");
						break;
					case RECORD_6:
						applyEffect(PotionEffectType.JUMP, "jump");
						break;
					case RECORD_7:
						applyEffect(PotionEffectType.DAMAGE_RESISTANCE, "damage resistance");
						break;
					case RECORD_8:
						applyEffect(PotionEffectType.FIRE_RESISTANCE, "fire resistance");
						break;
					case RECORD_9:
						applyEffect(PotionEffectType.WATER_BREATHING, "water breathing");
						break;
					case RECORD_10:
						applyEffect(PotionEffectType.NIGHT_VISION, "night vision");
						break;
					case RECORD_11:
						applyEffect(PotionEffectType.INVISIBILITY, "invisibility");
						break;
					}
			}
			if (SKILL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SKILLBIND))) {
				if (SKILLTIME > System.currentTimeMillis())
					return;
				SKILLTIME = System.currentTimeMillis()+SKILLDELAY;
				if (DUtil.getFavor(p) >= SKILLCOST) {
					cure();
					DUtil.setFavor(p, DUtil.getFavor(p)-SKILLCOST);
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					SKILL = false;
				}
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
					if (!DUtil.canTarget(p, p.getLocation())) {
						p.sendMessage(ChatColor.YELLOW+"You can't do that from a no-PVP zone.");
						return;
					}
					int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/DUtil.ASCENSIONCAP)));
					int hit = finale(p);
					if (hit > 0) {
						ULTIMATETIME = System.currentTimeMillis()+(t*1000);
						p.sendMessage(ChatColor.GOLD+"Apollo "+ChatColor.WHITE+" rains arrows on "+hit+" of your foes.");
						DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
					} else p.sendMessage(ChatColor.YELLOW+"No targets for Finale were found.");
				} else p.sendMessage(ChatColor.YELLOW+""+ult+" requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}

	@Override
	public void onTick(long timeSent) {
		if (timeSent > LASTCHECK+10000) {
			LASTCHECK = timeSent;
			if ((DUtil.getOnlinePlayer(getPlayerName()) != null) && !DUtil.getOnlinePlayer(getPlayerName()).isDead()) {
				Player p = DUtil.getOnlinePlayer(getPlayerName());
				if (DUtil.getActiveEffectsList(getPlayerName()).contains("Apollo health regeneration")) {
					DUtil.setHP(p, DUtil.getHP(p)+1);
					if (DUtil.getHP(p) > DUtil.getMaxHP(p))
						DUtil.setHP(p, DUtil.getMaxHP(p));
				} else if (DUtil.getActiveEffectsList(getPlayerName()).contains("Apollo Favor regeneration")) {
					DUtil.setFavor(p, DUtil.getFavor(p)+5);
					if (DUtil.getFavor(p) > DUtil.getFavorCap(p))
						DUtil.setFavor(p, DUtil.getFavorCap(p));
				}
			}
		}
	}
	private void applyEffect(PotionEffectType e, String description) {
		int duration = (int)Math.round(60*Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.09));
		Player p = DUtil.getOnlinePlayer(getPlayerName());
		if (DUtil.getActiveEffectsList(p.getName()).contains("Music Buff")) {
			p.sendMessage(ChatColor.YELLOW+"You have already received a Music Buff from Apollo.");
			return;
		}
		if (e == null) {
			p.sendMessage(ChatColor.GOLD+"Apollo"+ChatColor.WHITE+" has granted you a "+description+
					" bonus for "+duration+" seconds.");
			p.sendMessage(ChatColor.YELLOW+"NOTE: This bonus cannot be applied to your allies.");
			DUtil.addActiveEffect(p.getName(), "Apollo "+description, duration);
			DUtil.addActiveEffect(p.getName(), "Music Buff", duration);
		} else
			for (Player pl : p.getWorld().getPlayers()) {
				if (pl.getLocation().toVector().isInSphere(p.getLocation().toVector(), 15)) {
					if (DUtil.isFullParticipant(pl)) {
						if (DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p))) {
							pl.sendMessage(ChatColor.GOLD+"Apollo"+ChatColor.WHITE+" has granted you a "+description+
									" bonus for "+duration+" seconds.");
							pl.addPotionEffect(new PotionEffect(e, duration*20, 0));
							DUtil.addActiveEffect(pl.getName(), "Music Buff", duration);
						}
					}
				}
			}
	}
	private void cure() {
		Player p = DUtil.getOnlinePlayer(getPlayerName());
		int healamt = DUtil.getMaxHP(p);
		int selfheal = healamt/9;
		if (DUtil.getHP(p)+selfheal > DUtil.getMaxHP(p))
		{
			selfheal = DUtil.getMaxHP(p)-DUtil.getHP(p);
		}
		DUtil.setHP(p, DUtil.getHP(p)+selfheal);
		p.sendMessage(ChatColor.GREEN+"Apollo has cured you for "+selfheal+" health.");
		LivingEntity le = DUtil.getTargetLivingEntity(p, 3);
		if (le instanceof Player) {
			Player pl = (Player)le;
			if (DUtil.isFullParticipant(pl) && DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(pl))) {
				if (DUtil.getHP(pl)+healamt > DUtil.getMaxHP(pl)) {
					healamt = DUtil.getMaxHP(pl)-DUtil.getHP(pl);
				}
				DUtil.setHP(pl, DUtil.getHP(pl)+healamt);
				pl.sendMessage(ChatColor.GREEN+"Apollo has cured you for "+healamt+" health.");
				p.sendMessage(ChatColor.YELLOW+pl.getName()+" has been cured for "+healamt+" health.");
			}
		}
	}
	private int finale(final Player p) {
		int devotion = DUtil.getDevotion(p, getName());
		int ultrange = (int)Math.round(20*Math.pow(devotion, 0.15));
		int ultslowduration = (int)Math.round(10*Math.pow(devotion, 0.05)); //seconds
		int ultattacks = (int)Math.round(4*Math.pow(devotion, 0.08)); //number of arrow "waves"
		ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
		Vector ploc = p.getLocation().toVector();
		for (LivingEntity anEntity : p.getWorld().getLivingEntities()){
			if (anEntity instanceof Player)
				if (DUtil.isFullParticipant((Player)anEntity))
					if (DUtil.getAllegiance((Player)anEntity).equalsIgnoreCase(DUtil.getAllegiance(p)))
						continue;
			if (!DUtil.canTarget(anEntity, anEntity.getLocation()))
				continue;
			if (anEntity.getLocation().toVector().isInSphere(ploc, ultrange))
				entitylist.add(anEntity);
		}
		for (final LivingEntity target : entitylist) {
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ultslowduration*20, 1));
			for (int i=0;i<=ultattacks*20;i+=20) {
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (target.isDead())
							return;
						Location targ = target.getLocation();
						Location start = targ;
						start.setY(start.getBlockY()+50);
						Arrow ar = target.getWorld().spawnArrow(start, new Vector(0, -5, 0), 5, (float)0.2);
						ar.setVelocity(new Vector(0, -5, 0));
						if (Math.random() > 0.7)
							ar.setFireTicks(500);
					}
				}, i);
			}
		}
		return entitylist.size();
	}
}

package com.WildAmazing.marinating.Demigods.Deities.Titans;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Set;

public class Cronus implements Deity {

    private static final long serialVersionUID = -6160291350540472542L;

    // global vars
    private static final int CLEAVECOST = 100;
    private static final int SLOWCOST = 180;
    private static final int CRONUSULTIMATECOST = 5000;
    private static final int CRONUSULTIMATECOOLDOWNMAX = 500;
    private static final int CRONUSULTIMATECOOLDOWNMIN = 120;

    // per player
    private final String PLAYER;
    private boolean CLEAVE = false;
    private Material CLEAVEITEM = null;
    private boolean SLOW = false;
    private Material SLOWITEM = null;
    private long CRONUSULTIMATETIME;
    private long CLEAVETIME;

    public Cronus(String player) {
        PLAYER = player;
        CRONUSULTIMATETIME = System.currentTimeMillis();
        CLEAVETIME = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Cronus";
    }

    @Override
    public String getDefaultAlliance() {
        return "Titan";
    }

    @Override
    public void printInfo(Player p) {
        if (DMiscUtil.hasDeity(p, "Cronus") && DMiscUtil.isFullParticipant(p)) {
            int devotion = DMiscUtil.getDevotion(p, getName());
            /*
			 * Calculate special values first
			 */
            // cleave
            int damage = (int) Math.ceil(Math.pow(devotion, 0.35));
            int hungerdamage = (int) Math.ceil(Math.pow(devotion, 0.1776));
            // slow
            int duration = (int) Math.ceil(3.635 * Math.pow(devotion, 0.2576)); // seconds
            int strength = (int) Math.ceil(2.757 * Math.pow(devotion, 0.097));
            // ultimate
            int slowamount = (int) Math.round(.77179 * Math.pow(DMiscUtil.getAscensions(p), 0.17654391));
            int stopduration = (int) Math.round(9.9155621 * Math.pow(DMiscUtil.getAscensions(p), 0.459019));
            int t = (int) (CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / 100)));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Cronus" + ChatColor.GRAY + "[" + devotion + "]");
            p.sendMessage(":Slow your enemy when attacking with a scythe (hoe).");
            p.sendMessage(":Attack with a scythe to deal " + damage + " damage and " + hungerdamage + " hunger. " + ChatColor.GREEN + "/cleave");
            p.sendMessage(ChatColor.YELLOW + "Costs " + CLEAVECOST + " Favor.");
            if (((Cronus) (DMiscUtil.getDeity(p, "Cronus"))).CLEAVEITEM != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + (((Cronus) (DMiscUtil.getDeity(p, "Cronus"))).CLEAVEITEM).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Slow time to reduce movement speed of an enemy player. " + ChatColor.GREEN + "/slow");
            p.sendMessage(ChatColor.YELLOW + "Costs " + SLOWCOST + " Favor.");
            p.sendMessage("Slow power: " + strength + " for " + duration + " seconds.");
            if (((Cronus) (DMiscUtil.getDeity(p, "Cronus"))).SLOWITEM != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + (((Cronus) (DMiscUtil.getDeity(p, "Cronus"))).SLOWITEM).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Cronus slows enemies' perception of time, slowing their");
            p.sendMessage("movement by " + slowamount + " for " + stopduration + " seconds. " + ChatColor.GREEN + "/timestop");
            p.sendMessage(ChatColor.YELLOW + "Costs " + CRONUSULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Cronus");
        p.sendMessage("Passive: Slow your enemy when attacking with a scythe (hoe).");
        p.sendMessage("Active: Cause extra damage and hunger with a scythe (hoe). " + ChatColor.GREEN + "/cleave");
        p.sendMessage(ChatColor.YELLOW + "Costs " + CLEAVECOST + " Favor. Can bind.");
        p.sendMessage("Active: Slow time to reduce movement speed of an enemy player.");
        p.sendMessage(ChatColor.GREEN + "/slow " + ChatColor.YELLOW + "Costs " + SLOWCOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Cronus slows enemies' perception of time,");
        p.sendMessage("slowing their movement drastically. " + ChatColor.GREEN + "/timestop");
        p.sendMessage(ChatColor.YELLOW + "Costs " + CRONUSULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: soul sand");
    }

    @Override
    public String getPlayerName() {
        return PLAYER;
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof EntityDamageEvent) {
            if ((EntityDamageEvent) ee instanceof EntityDamageByEntityEvent && !((EntityDamageByEntityEvent) ee).isCancelled()) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
                if (e.getDamager() instanceof Player) {
                    Player p = (Player) e.getDamager();
                    if (DMiscUtil.isFullParticipant(p)) {
                        if (!DMiscUtil.hasDeity(p, "Cronus")) return;
                        if (!p.getItemInHand().getType().name().contains("_HOE")) return;
                        if (!DMiscUtil.canTarget(p, p.getLocation())) return;

						/*
						 * Passive ability (stop movement)
						 */
                        if (e.getEntity() instanceof Player) {
                            Player attacked = (Player) e.getEntity();
                            if (!DMiscUtil.canTarget(attacked, attacked.getLocation())) return;
                            if (!DMiscUtil.isFullParticipant(attacked) || (DMiscUtil.isFullParticipant(attacked) && !(DMiscUtil.getAllegiance(p).equalsIgnoreCase(DMiscUtil.getAllegiance(attacked))))) {
                                attacked.setVelocity(new Vector(0, 0, 0));
                            }
                        }
						/*
						 * Cleave
						 */
                        if (CLEAVE || ((CLEAVEITEM != null) && (p.getItemInHand().getType() == CLEAVEITEM))) {
                            if (DMiscUtil.getFavor(p) >= CLEAVECOST) {
                                if (!(e.getEntity() instanceof LivingEntity)) return;
                                if (System.currentTimeMillis() < CLEAVETIME + 100) return;
                                if (!DMiscUtil.canTarget(e.getEntity(), e.getEntity().getLocation())) return;
                                DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - CLEAVECOST);
                                for (int i = 1; i <= 31; i += 4)
                                    e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.SMOKE, i);
                                DMiscUtil.damageDemigods(p, (LivingEntity) e.getEntity(), (int) Math.ceil(Math.pow(DMiscUtil.getDevotion(p, getName()), 0.35)), EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                                CLEAVETIME = System.currentTimeMillis();
                                if ((LivingEntity) e.getEntity() instanceof Player) {
                                    Player otherP = (Player) e.getEntity();
                                    otherP.setFoodLevel(otherP.getFoodLevel() - (int) (e.getDamage() / 2));
                                    if (otherP.getFoodLevel() < 0) otherP.setFoodLevel(0);
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "You don't have enough Favor to do that.");
                                CLEAVE = false;
                            }
                        }
                    }
                }
            }
        } else if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DMiscUtil.hasDeity(p, "Cronus")) return;
            if (SLOW || ((SLOWITEM != null) && (p.getItemInHand().getType() == SLOWITEM))) {
                if (DMiscUtil.getFavor(p) >= SLOWCOST) {
                    if (slow(p)) DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - SLOWCOST);
                } else {
                    SLOW = false;
                    p.sendMessage(ChatColor.YELLOW + "You don't have enough Favor to do that.");
                }
            }
        }
    }

    @Override
    public void onCommand(final Player p, String str, String[] args, boolean bind) {
        if (!DMiscUtil.hasDeity(p, "Cronus")) return;
        if (str.equalsIgnoreCase("cleave")) {
            if (bind) {
                if (CLEAVEITEM == null) {
                    if (DMiscUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    if (!p.getItemInHand().getType().name().contains("_HOE"))
                        p.sendMessage(ChatColor.YELLOW + "Cleave can only be bound to a scythe (hoe).");
                    else {
                        DMiscUtil.registerBind(p, p.getItemInHand().getType());
                        CLEAVEITEM = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Cleave is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DMiscUtil.removeBind(p, CLEAVEITEM);
                    p.sendMessage(ChatColor.YELLOW + "Cleave is no longer bound to " + CLEAVEITEM.name() + ".");
                    CLEAVEITEM = null;
                }
                return;
            }
            if (CLEAVE) {
                CLEAVE = false;
                p.sendMessage(ChatColor.YELLOW + "Cleave is no longer active.");
            } else {
                CLEAVE = true;
                p.sendMessage(ChatColor.YELLOW + "Cleave is now active.");
            }
        } else if (str.equalsIgnoreCase("slow")) {
            if (bind) {
                if (SLOWITEM == null) {
                    if (DMiscUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DMiscUtil.registerBind(p, p.getItemInHand().getType());
                        SLOWITEM = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Slow is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DMiscUtil.removeBind(p, SLOWITEM);
                    p.sendMessage(ChatColor.YELLOW + "Slow is no longer bound to " + SLOWITEM.name() + ".");
                    SLOWITEM = null;
                }
                return;
            }
            if (SLOW) {
                SLOW = false;
                p.sendMessage(ChatColor.YELLOW + "Slow is no longer active.");
            } else {
                SLOW = true;
                p.sendMessage(ChatColor.YELLOW + "Slow is now active.");
            }
        } else if (str.equalsIgnoreCase("timestop")) {
            if (!DMiscUtil.hasDeity(p, "Cronus")) return;
            if (System.currentTimeMillis() < CRONUSULTIMATETIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot stop time again for " + ((((CRONUSULTIMATETIME) / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((CRONUSULTIMATETIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DMiscUtil.getFavor(p) >= CRONUSULTIMATECOST) {
                if (!DMiscUtil.canTarget(p, p.getLocation())) {
                    p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
                    return;
                }
                int t = (int) (CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / 100)));
                CRONUSULTIMATETIME = System.currentTimeMillis() + (t * 1000);
                timeStop(p);
                DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - CRONUSULTIMATECOST);
            } else p.sendMessage(ChatColor.YELLOW + "Stopping time requires " + CRONUSULTIMATECOST + " Favor.");
        }
    }

    @Override
    public void onTick(long timeSent) {

    }

    private boolean slow(Player p) {
        int devotion = DMiscUtil.getDevotion(p, getName());
        int duration = (int) Math.ceil(3.635 * Math.pow(devotion, 0.2576)); // seconds
        int strength = (int) Math.ceil(2.757 * Math.pow(devotion, 0.097));
        Player target = null;
        Block b = p.getTargetBlock((Set) null, 200);
        for (Player pl : b.getWorld().getPlayers()) {
            if (pl.getLocation().distance(b.getLocation()) < 4) {
                if (!DMiscUtil.areAllied(pl, p) && DMiscUtil.canTarget(pl, pl.getLocation())) {
                    target = pl;
                    break;
                }
            }
        }
        if ((target != null) && (target.getEntityId() != p.getEntityId())) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, strength));
            p.sendMessage(ChatColor.YELLOW + target.getName() + " has been slowed.");
            target.sendMessage(ChatColor.RED + "You have been slowed for " + duration + " seconds.");
            DMiscUtil.addActiveEffect(target.getName(), "Slow", duration);
            return true;
        } else {
            p.sendMessage(ChatColor.YELLOW + "No target found.");
            return false;
        }
    }

    private void timeStop(Player p) {
        int slowamount = (int) Math.round(4.77179 * Math.pow(DMiscUtil.getAscensions(p), 0.17654391));
        int duration = (int) Math.round(9.9155621 * Math.pow(DMiscUtil.getAscensions(p), 0.459019));
        int count = 0;
        for (Player pl : p.getWorld().getPlayers()) {
            if (!(pl.getLocation().toVector().isInSphere(p.getLocation().toVector(), 70))) continue;
            if (DMiscUtil.isFullParticipant(pl)) {
                if (DMiscUtil.getAllegiance(pl).equalsIgnoreCase(DMiscUtil.getAllegiance(p))) continue;
                if (!DMiscUtil.canTarget(pl, pl.getLocation())) continue;
            }
            pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, slowamount));
            p.sendMessage(ChatColor.DARK_RED + "Cronus has slowed time around you.");
            DMiscUtil.addActiveEffect(pl.getName(), "Time Stop", duration);
            count++;
        }
        p.sendMessage(ChatColor.RED + "Cronus has slowed time for " + count + " players nearby.");
    }
}

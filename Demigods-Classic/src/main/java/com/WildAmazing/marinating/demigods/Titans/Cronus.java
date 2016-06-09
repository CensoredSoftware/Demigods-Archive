package com.WildAmazing.marinating.demigods.Titans;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

    //global vars
    private static final int CLEAVECOST = 20;
    private static final int SLOWCOST = 40;
    private static final int CRONUSULTIMATECOST = 600;
    private static final int CRONUSULTIMATECOOLDOWNMAX = 360;
    private static final int CRONUSULTIMATECOOLDOWNMIN = 80;

    //per player
    String PLAYER;
    boolean CLEAVE = false;
    Material CLEAVEITEM = null;
    boolean SLOW = false;
    Material SLOWITEM = null;
    long CRONUSULTIMATETIME;

    public Cronus(String player) {
        PLAYER = player;
        CRONUSULTIMATETIME = System.currentTimeMillis();
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
        if (DUtil.hasDeity(p, "Cronus") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            int duration = (int) Math.floor(1.983 * Math.pow(DUtil.getLevel(p), 0.45901)); //seconds
            int strength = (int) (Math.floor(1.0597 * Math.pow(1.038936, DUtil.getLevel(p)))); //1-6
            int slowamount = (int) Math.round(4.77179 * Math.pow(DUtil.getLevel(p), 0.17654391));
            int stopduration = (int) Math.round(9.9155621 * Math.pow(DUtil.getLevel(p), 0.459019));
            int t = (int) (CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Cronus" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Stop your enemy when attacking with a scythe (hoe).");
            p.sendMessage(":Attack with a scythe to cause extra damage and hunger. " + ChatColor.GREEN + "/cleave");
            p.sendMessage(ChatColor.YELLOW + "Costs " + CLEAVECOST + " Favor.");
            if (((Cronus) (DUtil.getDeity(p, "Cronus"))).CLEAVEITEM != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + (((Cronus) (DUtil.getDeity(p, "Cronus"))).CLEAVEITEM).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Slow time to reduce movement speed of an enemy player. " + ChatColor.GREEN + "/slow");
            p.sendMessage(ChatColor.YELLOW + "Costs " + SLOWCOST + " Favor.");
            p.sendMessage("Slow power: " + strength + " for " + duration + " seconds.");
            if (((Cronus) (DUtil.getDeity(p, "Cronus"))).SLOWITEM != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + (((Cronus) (DUtil.getDeity(p, "Cronus"))).SLOWITEM).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Cronus slows enemies' perception of time, slowing their");
            p.sendMessage("movement by " + slowamount + " for " + stopduration + " seconds. " + ChatColor.GREEN + "/timestop");
            p.sendMessage(ChatColor.YELLOW + "Costs " + CRONUSULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Cronus");
        p.sendMessage("Passive: Stop your enemy when attacking with a scythe (hoe).");
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
            if ((EntityDamageEvent) ee instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
                if (e.getDamager() instanceof Player) {
                    Player p = (Player) e.getDamager();
                    if (DUtil.isFullParticipant(p)) {
                        if (!DUtil.hasDeity(p, "Cronus"))
                            return;
                        if (!p.getItemInHand().getType().name().contains("_HOE"))
                            return;
						/*
						 * Passive ability (damage increase)
						 */
                        if (e.getEntity() instanceof Player) {
                            Player attacked = (Player) e.getEntity();
                            if (!DUtil.isFullParticipant(attacked) ||
                                    (DUtil.isFullParticipant(attacked) && !(DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getAllegiance(attacked))))) {
                                attacked.setVelocity(new Vector(0, 0, 0));
                            }
                        }
						/*
						 * Cleave
						 */
                        if (CLEAVE || ((CLEAVEITEM != null) && (p.getItemInHand().getType() == CLEAVEITEM))) {
                            if (DUtil.getFavor(p) >= CLEAVECOST) {
                                DUtil.setFavor(p, DUtil.getFavor(p) - CLEAVECOST);
                                for (int i = 1; i <= 31; i += 4)
                                    e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.SMOKE, i);
                                int additional = 0;
                                switch (p.getItemInHand().getType()) {
                                    case WOOD_HOE:
                                        additional = 3;
                                    case IRON_HOE:
                                        additional = 6;
                                    case GOLD_HOE:
                                        additional = 7;
                                    case DIAMOND_HOE:
                                        additional = 8;
                                }
                                e.setDamage(e.getDamage() + additional);
                                if (e.getEntity() instanceof Player) {
                                    Player otherP = (Player) e.getEntity();
                                    otherP.setFoodLevel((int) (otherP.getFoodLevel() - (e.getDamage() / 2)));
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
            if (!DUtil.hasDeity(p, "Cronus"))
                return;
            if (SLOW || ((SLOWITEM != null) && (p.getItemInHand().getType() == SLOWITEM))) {
                if (DUtil.getFavor(p) >= SLOWCOST) {
                    if (slow(p))
                        DUtil.setFavor(p, DUtil.getFavor(p) - SLOWCOST);
                } else {
                    SLOW = false;
                    p.sendMessage(ChatColor.YELLOW + "You don't have enough Favor to do that.");
                }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (!DUtil.hasDeity(p, "Cronus"))
            return;
        if (str.equalsIgnoreCase("cleave")) {
            if (bind) {
                if (CLEAVEITEM == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    if (!p.getItemInHand().getType().name().contains("_HOE"))
                        p.sendMessage(ChatColor.YELLOW + "Cleave can only be bound to a scythe (hoe).");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        CLEAVEITEM = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Cleave is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, CLEAVEITEM);
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
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        SLOWITEM = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Slow is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, SLOWITEM);
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
            if (!DUtil.hasDeity(p, "Cronus"))
                return;
            if (System.currentTimeMillis() < CRONUSULTIMATETIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot stop time again for " + ((((CRONUSULTIMATETIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((CRONUSULTIMATETIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= CRONUSULTIMATECOST) {
                int t = (int) (CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                CRONUSULTIMATETIME = System.currentTimeMillis() + (t * 1000);
                timeStop(p);
                DUtil.setFavor(p, DUtil.getFavor(p) - CRONUSULTIMATECOST);
            } else p.sendMessage(ChatColor.YELLOW + "Stopping time requires " + CRONUSULTIMATECOST + " Favor.");
            return;
        }
    }

    private boolean slow(Player p) {
        int duration = (int) Math.floor(1.983 * Math.pow(DUtil.getLevel(p), 0.45901)); //seconds
        if (duration < 1) duration = 1;
        int strength = (int) (Math.floor(1.0597 * Math.pow(1.038936, DUtil.getLevel(p)))); //1-6
        Player target = null;
        Block b = p.getTargetBlock((Set) null, 200);
        for (Player pl : b.getWorld().getPlayers()) {
            if (pl.getLocation().distance(b.getLocation()) < 4) {
                if (!DUtil.isTitan(pl)) {
                    target = pl;
                    break;
                }
            }
        }
        if ((target != null) && (target.getEntityId() != p.getEntityId())) {
            final Player pt = target;
            final PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, duration, strength);
            pt.addPotionEffect(slow);
            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    pt.removePotionEffect(PotionEffectType.SLOW);
                }
            }, duration * 20);
            p.sendMessage(ChatColor.YELLOW + pt.getName() + " has been slowed.");
            pt.sendMessage(ChatColor.RED + "You have been slowed for " + duration + " seconds.");
            return true;
        } else {
            p.sendMessage(ChatColor.YELLOW + "No target found.");
            return false;
        }
    }

    private void timeStop(Player p) {
        int slowamount = (int) Math.round(4.77179 * Math.pow(DUtil.getLevel(p), 0.17654391));
        int duration = (int) Math.round(9.9155621 * Math.pow(DUtil.getLevel(p), 0.459019));
        int count = 0;
        for (Player pl : p.getWorld().getPlayers()) {
            if (DUtil.isFullParticipant(pl)) {
                if (DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
                    continue;
            }
            final Player pt = pl;
            final PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, duration, slowamount);
            pt.addPotionEffect(slow);
            pt.sendMessage(ChatColor.DARK_RED + "Your perception of time has been slowed by Cronus.");
            count++;
            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    pt.removePotionEffect(PotionEffectType.SLOW);
                }
            }, duration * 20);
        }
        p.sendMessage(ChatColor.RED + "Cronus has slowed time for " + count + " players in your world.");
    }
}

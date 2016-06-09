package com.WildAmazing.marinating.demigods.Gods;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * Affected by level:
 * Fall damage reduction
 * Shove distance/range
 * Ultimate cooldown
 */

public class Zeus implements Deity {
    /**
     *
     */
    private static final long serialVersionUID = 2242753324910371936L;

    private String PLAYER;
    private static final int SHOVECOST = 80;
    private static final int SHOVEDELAY = 1500; //milliseconds
    private static final int LIGHTNINGCOST = 60;
    private static final int LIGHTNINGDELAY = 1000; //milliseconds
    private static final int ZEUSULTIMATECOST = 1000;
    private static final int ZEUSULTIMATECOOLDOWNMAX = 600; //seconds
    private static final int ZEUSULTIMATECOOLDOWNMIN = 60;

    private long ZEUSULTIMATETIME;
    private long ZEUSSHOVETIME;
    private long ZEUSLIGHTNINGTIME;
    private boolean SHOVE = false;
    private boolean LIGHTNING = false;
    private Material SHOVEBIND = null;
    private Material LIGHTNINGBIND = null;

    public Zeus(String player) {
        PLAYER = player;
        ZEUSULTIMATETIME = System.currentTimeMillis();
        ZEUSSHOVETIME = System.currentTimeMillis();
        ZEUSLIGHTNINGTIME = System.currentTimeMillis();
    }

    @Override
    public String getDefaultAlliance() {
        return "God";
    }

    @Override
    public void printInfo(Player p) {
        if (DUtil.hasDeity(p, "Zeus") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            int targets = (int) (3.08 * (Math.pow(1.05, DUtil.getLevel(p))));
            double multiply = 0.474 * Math.pow(1.03, DUtil.getLevel(p));
            int amt = 50 + 5 * DUtil.getLevel(p);
            int t = (int) (ZEUSULTIMATECOOLDOWNMAX - ((ZEUSULTIMATECOOLDOWNMAX - ZEUSULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Zeus" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            if (amt > 100) amt = 100;
            p.sendMessage(":Fall damage is reduced by " + Math.abs(amt) + "%");
            p.sendMessage(":Strike lightning at a target location. " + ChatColor.GREEN + "/lightning");
            p.sendMessage(ChatColor.YELLOW + "Costs " + LIGHTNINGCOST + " Favor.");
            if (((Zeus) (DUtil.getDeity(p, "Zeus"))).LIGHTNINGBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + ((Zeus) (DUtil.getDeity(p, "Zeus"))).LIGHTNINGBIND.name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Use the force of wind to knock back enemies. " + ChatColor.GREEN + "/shove");
            p.sendMessage(ChatColor.YELLOW + "Costs " + SHOVECOST + " Favor.");
            p.sendMessage("Affects up to " + targets + " targets with power " + (int) (Math.round(multiply * 10)) + ".");
            if (((Zeus) (DUtil.getDeity(p, "Zeus"))).SHOVEBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + ((Zeus) (DUtil.getDeity(p, "Zeus"))).SHOVEBIND.name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Zeus strikes lightning on nearby enemies as they are");
            p.sendMessage("raised in the air and dropped. " + ChatColor.GREEN + "/storm");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ZEUSULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Zeus");
        p.sendMessage("Passive: Take reduced fall damage.");
        p.sendMessage("Active: Strike lightning at a target location. " + ChatColor.GREEN + "/lightning");
        p.sendMessage(ChatColor.YELLOW + "Costs " + LIGHTNINGCOST + " Favor. Can bind.");
        p.sendMessage("Active: Use the force of wind to knock back enemies. " + ChatColor.GREEN + "/shove");
        p.sendMessage(ChatColor.YELLOW + "Costs " + SHOVECOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Zeus strikes lightning on nearby enemies as they are");
        p.sendMessage("raised in the air and dropped. " + ChatColor.GREEN + "/storm");
        p.sendMessage(ChatColor.YELLOW + "Costs " + ZEUSULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: iron ingot");
    }

    @Override
    public String getName() {
        return "Zeus";
    }

    @Override
    public String getPlayerName() {
        return PLAYER;
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof EntityDamageEvent) {
            EntityDamageEvent e = (EntityDamageEvent) ee;
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (!DUtil.hasDeity(p, "Zeus") || !DUtil.isFullParticipant(p))
                    return;
                if (e.getCause() == DamageCause.FALL) {
                    int damagereduction = (int) Math.round(e.getDamage() * (.5 + 0.05 * DUtil.getLevel(p)));
                    if (damagereduction < 1) damagereduction = 1;
                    //			p.sendMessage(ChatColor.GOLD+"Zeus"+ChatColor.WHITE+" has protected you from "+damagereduction+" falling damage.");
                    e.setDamage(e.getDamage() - damagereduction);
                } else if (e.getCause() == DamageCause.LIGHTNING) {
                    e.setCancelled(true);
                }
            }
        } else if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.hasDeity(p, "Zeus") || !DUtil.isFullParticipant(p))
                return;
            if (SHOVE || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SHOVEBIND))) {
                if (ZEUSSHOVETIME > System.currentTimeMillis())
                    return;
                ZEUSSHOVETIME = System.currentTimeMillis() + SHOVEDELAY;
                if (DUtil.getFavor(p) >= SHOVECOST) {
                    shove(p);
                    DUtil.setFavor(p, DUtil.getFavor(p) - SHOVECOST);
                    return;
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                    SHOVE = false;
                }
            }
            if (LIGHTNING || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == LIGHTNINGBIND))) {
                if (ZEUSLIGHTNINGTIME > System.currentTimeMillis())
                    return;
                ZEUSLIGHTNINGTIME = System.currentTimeMillis() + LIGHTNINGDELAY;
                if (DUtil.getFavor(p) >= LIGHTNINGCOST) {
                    lightning(p);
                    DUtil.setFavor(p, DUtil.getFavor(p) - LIGHTNINGCOST);
                    return;
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                    LIGHTNING = false;
                }
            }
        }
    }

    /*
     * ---------------
     * Commands
     * ---------------
     */
    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (!DUtil.hasDeity(p, "Zeus"))
            return;
        if (str.equalsIgnoreCase("lightning")) {
            if (bind) {
                if (LIGHTNINGBIND == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        LIGHTNINGBIND = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Lightning is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, LIGHTNINGBIND);
                    p.sendMessage(ChatColor.YELLOW + "Lightning is no longer bound to " + LIGHTNINGBIND.name() + ".");
                    LIGHTNINGBIND = null;
                }
                return;
            }
            if (LIGHTNING) {
                LIGHTNING = false;
                p.sendMessage(ChatColor.YELLOW + "Lightning is no longer active.");
            } else {
                LIGHTNING = true;
                p.sendMessage(ChatColor.YELLOW + "Lightning is now active.");
            }
        } else if (str.equalsIgnoreCase("shove")) {
            if (bind) {
                if (SHOVEBIND == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        SHOVEBIND = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Shove is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, SHOVEBIND);
                    p.sendMessage(ChatColor.YELLOW + "Shove is no longer bound to " + SHOVEBIND.name() + ".");
                    SHOVEBIND = null;
                }
                return;
            }
            if (SHOVE) {
                SHOVE = false;
                p.sendMessage(ChatColor.YELLOW + "Shove is no longer active.");
            } else {
                SHOVE = true;
                p.sendMessage(ChatColor.YELLOW + "Shove is now active.");
            }
        } else if (str.equalsIgnoreCase("storm")) {
            if (!DUtil.hasDeity(p, "Zeus"))
                return;
            long TIME = ZEUSULTIMATETIME;
            if (System.currentTimeMillis() < TIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot use the lightning storm again for " + ((((TIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= ZEUSULTIMATECOST) {
                int t = (int) (ZEUSULTIMATECOOLDOWNMAX - ((ZEUSULTIMATECOOLDOWNMAX - ZEUSULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                ZEUSULTIMATETIME = System.currentTimeMillis() + t * 1000;
                p.sendMessage("In exchange for " + ChatColor.AQUA + ZEUSULTIMATECOST + ChatColor.WHITE + " Favor, ");
                p.sendMessage(ChatColor.GOLD + "Zeus" + ChatColor.WHITE + " has unloaded his wrath on " + storm(p) + " non-allied entities.");
                DUtil.setFavor(p, DUtil.getFavor(p) - ZEUSULTIMATECOST);
            } else p.sendMessage(ChatColor.YELLOW + "Lightning storm requires " + ZEUSULTIMATECOST + " Favor.");
            return;
        }
    }

    /*
     * ---------------
     * Helper methods
     * ---------------
     */
    private void shove(Player p) {
        ArrayList<LivingEntity> hit = new ArrayList<LivingEntity>();
        int targets = (int) (3.08 * (Math.pow(1.05, DUtil.getLevel(p))));
        double multiply = 0.474 * Math.pow(1.03, DUtil.getLevel(p));
        for (Block b : (List<Block>) p.getLineOfSight((Set) null, 10)) {
            for (LivingEntity le : p.getWorld().getLivingEntities()) {
                if (targets == hit.size())
                    break;
                if (le instanceof Player) {
                    if (DUtil.isGod((Player) le)) {
                        if (le.getEntityId() == p.getEntityId()) {
                            le.setVelocity((new Vector(0, 2.75, 0)).multiply(multiply));
                            return;
                        }
                        continue;
                    }
                }
                if ((le.getLocation().distance(b.getLocation()) <= 5) && !hit.contains(le))
                    hit.add(le);
            }
        }
        if (hit.size() > 0) {
            for (LivingEntity le : hit) {
                Vector v = p.getLocation().toVector();
                Vector victor = le.getLocation().toVector().subtract(v);
                victor.multiply(multiply);
                le.setVelocity(victor);
            }
        }
    }

    private void lightning(Player p) {
        Location target = null;
        Block b = p.getTargetBlock((Set) null, 200);
        target = b.getLocation();
        if (p.getLocation().distance(target) > 2) {
            try {
                strikeLightning(p, target);
                if (!DUtil.isProtected(target))
                    p.getWorld().createExplosion(target, 1);
            } catch (Exception nullpointer) {
            } //ignore it if something went wrong
        } else
            p.sendMessage(ChatColor.YELLOW + "Your target is too far away, or too close to you.");
    }

    private int storm(Player p) {
        ArrayList<Entity> entitylist = new ArrayList<Entity>();
        Vector ploc = p.getLocation().toVector();
        for (Entity anEntity : p.getWorld().getEntities()) {
            if (anEntity.getLocation().toVector().isInSphere(ploc, 50.0))
                entitylist.add(anEntity);
        }
        int count = 0;
        for (Entity eee : entitylist) {
            try {
                LivingEntity e1 = (LivingEntity) eee;
                if (e1 instanceof Player) {
                    Player ptemp = (Player) e1;
                    if (!DUtil.isGod(ptemp) && !ptemp.equals(p)) {
                        strikeLightning(p, ptemp.getLocation());
                        strikeLightning(p, ptemp.getLocation());
                        strikeLightning(p, ptemp.getLocation());
                        count++;
                    }
                } else {
                    count++;
                    strikeLightning(p, e1.getLocation());
                    strikeLightning(p, e1.getLocation());
                    strikeLightning(p, e1.getLocation());
                }
            } catch (Exception notAlive) {
            } //ignore stuff like minecarts
        }
        return count;
    }

    private void strikeLightning(Player p, Location target) {
        if (!p.getWorld().equals(target.getWorld()))
            return;
        p.getWorld().strikeLightning(target);
        for (Entity e : target.getBlock().getChunk().getEntities()) {
            if (e instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e;
                if (le.getLocation().distance(target) < 1.5)
                    le.damage(2, p);
            }
        }
    }
}
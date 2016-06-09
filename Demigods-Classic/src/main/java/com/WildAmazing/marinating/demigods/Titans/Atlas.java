package com.WildAmazing.marinating.demigods.Titans;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Atlas implements Deity {

    /* General */
    private static final long serialVersionUID = 1898032566168889851L;
    private final int SKILLCOST = 70;
    private final int SKILLDELAY = 1500;
    private final int ULTIMATECOST = 500;
    private final int ULTIMATECOOLDOWNMAX = 400;
    private final int ULTIMATECOOLDOWNMIN = 200;

    /* Specific to player */
    private String PLAYER;
    private boolean SKILL = false;
    private long SKILLTIME, ULTIMATETIME;
    //just for ult
    private long INVINCIBLETIME = 0;
    private double INVINCIBLEREDUCTION = 0.9;
    private int INVINCIBLERANGE = 0;

    public Atlas(String name) {
        PLAYER = name;
        SKILLTIME = System.currentTimeMillis();
        ULTIMATETIME = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Atlas";
    }

    @Override
    public String getPlayerName() {
        return PLAYER;
    }

    @Override
    public String getDefaultAlliance() {
        return "Titan";
    }

    @Override
    public void printInfo(Player p) {
        if (DUtil.hasDeity(p, "Atlas") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            double reduction = (25 + DUtil.getLevel(p) * 0.5) / 100;
            //
            int maxrange = (int) Math.round(2.435943 * Math.pow(1.0296869, DUtil.getLevel(p)));
            float explosion = (float) Math.ceil(0.81253088 * Math.pow(DUtil.getLevel(p), 0.3542486)); //radius
            //
            int duration = (int) (Math.ceil(55.819821 * Math.pow(DUtil.getLevel(p), 0.26798863))); //seconds
            int percentagereduction = (int) (Math.ceil(23.794986 * Math.pow(DUtil.getLevel(p), 0.28130795)));
            int radius = (int) (Math.ceil(4.957781 * Math.pow(DUtil.getLevel(p), 0.45901927)));
            int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Atlas" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Reduce all incoming damage by " + reduction + "%.");
            p.sendMessage(":Punch with incredible force, causing an explosion.");
            p.sendMessage("Range: " + maxrange + " Explosion size: " + explosion + ChatColor.GREEN + " /blast " + ChatColor.YELLOW + "Costs " + SKILLCOST + " Favor.");
            if (((Atlas) DUtil.getDeity(p, "Atlas")).SKILL)
                p.sendMessage(ChatColor.AQUA + "    Skill is active.");
            p.sendMessage(":Atlas shields you and nearby allies from harm.");
            p.sendMessage(percentagereduction + "% damage reduction with range " + radius + " for " + duration + " seconds.");
            p.sendMessage(ChatColor.GREEN + " /invincible" + ChatColor.YELLOW + " Costs " + ULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            long itime = ((Atlas) DUtil.getDeity(p, "Atlas")).getInvincibleTime();
            if (itime > System.currentTimeMillis())
                p.sendMessage(ChatColor.YELLOW + "Invincible will be active for " + ((((itime) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes and " + ((((itime) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Atlas");
        p.sendMessage("Passive: Reduce incoming damage.");
        p.sendMessage("Active: Punch the air with explosive force.");
        p.sendMessage(ChatColor.GREEN + "/blast" + ChatColor.YELLOW + " Costs " + SKILLCOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Atlas shields you and nearby allies from harm.");
        p.sendMessage(ChatColor.GREEN + "/invincible" + ChatColor.YELLOW + " Costs " + ULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: obsidian");
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof EntityDamageEvent) {
            EntityDamageEvent e = (EntityDamageEvent) ee;
            if (!(e.getEntity() instanceof Player))
                return;
            Player p = (Player) e.getEntity();
            if (!DUtil.isFullParticipant(p))
                return;
            if (DUtil.hasDeity(p, "Atlas")) {
                double reduction = (25 + DUtil.getLevel(p) * 0.5) / 100;
                e.setDamage((int) Math.ceil(reduction * e.getDamage()));
            }
            for (Player pl : DUtil.getPlugin().getServer().getOnlinePlayers()) {
                if (!DUtil.isFullParticipant(pl))
                    continue;
                if (!DUtil.hasDeity(pl, "Atlas"))
                    continue;
                Atlas a = (Atlas) DUtil.getDeity(pl, "Atlas");
                if (a.getInvincibleTime() > System.currentTimeMillis()) {
                    if (p.getLocation().distance(pl.getLocation()) < a.getInvincibleRadius())
                        e.setDamage((int) Math.ceil(e.getDamage() * (1 - a.getInvincibleReduction())));
                }
            }
        } else if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (SKILL) {
                if (p.getItemInHand().getType() == Material.AIR) {
                    if (SKILLTIME > System.currentTimeMillis())
                        return;
                    if (DUtil.getFavor(p) > SKILLCOST) {
                        if (blast(p)) {
                            DUtil.setFavor(p, DUtil.getFavor(p) - SKILLCOST);
                            SKILLTIME = System.currentTimeMillis() + SKILLDELAY;
                        }
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                        SKILL = false;
                    }
                }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (!DUtil.isFullParticipant(p))
            return;
        if (!DUtil.hasDeity(p, "Atlas"))
            return;
        if (str.equalsIgnoreCase("blast")) {
            if (SKILL) {
                SKILL = false;
                p.sendMessage(ChatColor.YELLOW + "Blast is no longer active.");
            } else {
                SKILL = true;
                p.sendMessage(ChatColor.YELLOW + "Blast is now active.");
                p.sendMessage(ChatColor.YELLOW + "It can only be used with fists.");
            }
        } else if (str.equalsIgnoreCase("invincible")) {
            long TIME = ULTIMATETIME;
            if (System.currentTimeMillis() < TIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot use Invincible again for " + ((((TIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= ULTIMATECOST) {
                int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                //
                final int seconds = (int) (Math.ceil(55.819821 * Math.pow(DUtil.getLevel(p), 0.26798863)));
                INVINCIBLETIME = seconds * 1000 + System.currentTimeMillis();
                INVINCIBLEREDUCTION = (Math.ceil(23.794986 * Math.pow(DUtil.getLevel(p), 0.28130795))) / 100;
                INVINCIBLERANGE = (int) (Math.ceil(4.957781 * Math.pow(DUtil.getLevel(p), 0.45901927)));
                p.sendMessage(ChatColor.YELLOW + "Invincible will be in effect for " + seconds + " seconds.");
                DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatColor.YELLOW + "Invincible will be in effect for " + seconds / 2 + " more seconds.");
                    }
                }, seconds * 10);
                DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatColor.YELLOW + "Invincible is no longer in effect.");
                    }
                }, seconds * 20);
                //
                p.sendMessage(ChatColor.DARK_AQUA + "Atlas" + ChatColor.GRAY + " shields you and your allies from harm.");
                DUtil.setFavor(p, DUtil.getFavor(p) - ULTIMATECOST);
                ULTIMATETIME = System.currentTimeMillis() + t * 1000;
            } else p.sendMessage(ChatColor.YELLOW + "Invincible requires " + ULTIMATECOST + " Favor.");
            return;
        }
    }

    private boolean blast(Player p) {
        //
        int maxrange = (int) Math.round(2.435943 * Math.pow(1.0296869, DUtil.getLevel(p)));
        float explosion = (float) Math.ceil(0.81253088 * Math.pow(DUtil.getLevel(p), 0.3542486)); //radius
        //
        LivingEntity le = DUtil.getTargetLivingEntity(p, 2, maxrange);
        if (le != null) {
            if (DUtil.isProtectedDemigodsOnly(le.getLocation())) {
                p.sendMessage(ChatColor.YELLOW + "That area is protected.");
                return false;
            }
            if (le instanceof Player) {
                Player P = (Player) le;
                if (DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getAllegiance(P))) {
                    p.sendMessage(ChatColor.YELLOW + "You may not target your alliance members.");
                    return false;
                }
            }
            le.getWorld().createExplosion(le.getLocation(), explosion);
        } else {
            Location target = DUtil.getTargetLocation(p);
            if (DUtil.isProtectedDemigodsOnly(target)) {
                p.sendMessage(ChatColor.YELLOW + "That area is protected.");
                return false;
            }
            if ((target == null) || (target.distance(p.getLocation()) > maxrange)) {
                p.sendMessage(ChatColor.YELLOW + "That is outside your range.");
                return false;
            }
            target.getWorld().createExplosion(target, explosion);
        }
        return true;
    }

    private long getInvincibleTime() {
        return INVINCIBLETIME;
    }

    private int getInvincibleRadius() {
        return INVINCIBLERANGE;
    }

    private double getInvincibleReduction() {
        return INVINCIBLEREDUCTION;
    }
}

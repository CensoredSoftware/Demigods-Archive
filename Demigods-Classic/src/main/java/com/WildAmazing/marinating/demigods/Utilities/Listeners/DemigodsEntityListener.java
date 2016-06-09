package com.WildAmazing.marinating.demigods.Utilities.Listeners;

import com.WildAmazing.marinating.demigods.Demigods;
import com.WildAmazing.marinating.demigods.Utilities.DSave;
import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import com.WildAmazing.marinating.demigods.Utilities.Shrine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.Random;

public class DemigodsEntityListener implements Listener {
    //	private Demigods plugin;
    private static final int MAXFAVORGIVEN = 2500;

    public DemigodsEntityListener(Demigods instance) {
        //		plugin = instance;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        for (Player pl : e.getEntity().getWorld().getPlayers()) {
            if (DUtil.isFullParticipant(pl)) {
                if ((DUtil.getDeities(pl) != null) && (DUtil.getDeities(pl).size() > 0)) {
                    for (Deity d : DUtil.getDeities(pl))
                        d.onEvent(e);
                }
            }
        }
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e instanceof EntityDamageByEntityEvent) {
                if (DUtil.getShrine(p.getLocation()) != null) {
                    Shrine s = DUtil.getShrine(p.getLocation());
                    if (s.getAlliance().equalsIgnoreCase("ADMIN")) {
                        e.setDamage(0);
                        e.setCancelled(true);
                    }
                }
            }
            if (DUtil.isFullParticipant(p)) {
                if (DUtil.getShrineInvincible(p.getLocation()) != null) {
                    if (DUtil.getShrineInvincible(p.getLocation()).getAlliance() != null)
                        if (DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getShrineInvincible(p.getLocation()).getAlliance())) {
                            e.setDamage(0);
                            e.setCancelled(true);
                        }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent)
                killsCode((EntityDamageByEntityEvent) p.getLastDamageCause());
            if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
                for (Deity d : DUtil.getDeities(p))
                    d.onEvent(e);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
                for (Deity d : DUtil.getDeities(p))
                    d.onEvent(e);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
                for (Deity d : DUtil.getDeities(p))
                    d.onEvent(e);
            }
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        for (Shrine s : DSave.getShrines()) {
            if (e.getEntity().getWorld().getName().equals(s.getCenter().getWorld()))
                if ((e.getEntity().getLocation().distance(DUtil.toLocation(s.getCenter())) <= (s.getRadius() + 7)) ||
                        DUtil.isProtectedDemigodsOnly(e.getEntity().getLocation())) {
                    e.setRadius(0);
                    e.setFire(false);
                    e.setCancelled(true);
                }
        }
    }

    private void killsCode(EntityDamageByEntityEvent e) {
        Player attacker = null;
        Player attacked = null;
        if (e.getEntity() instanceof Player)
            attacked = (Player) e.getEntity();
        if (e.getDamager() instanceof Player)
            attacker = (Player) e.getDamager();
        if ((attacker == null) || (attacked == null))
            return;
        if (attacked.getHealth() - e.getDamage() > 0)
            return;
        if (DUtil.isFullParticipant(attacker)) {
            if (DUtil.isFullParticipant(attacked)) {
                if (!DUtil.getAllegiance(attacker).equals(DUtil.getAllegiance(attacked))) {
                    DUtil.setKills(attacker, DUtil.getKills(attacker) + 1);
                    int amt = DUtil.getLevel(attacked) * 5 + DUtil.getDeities(attacked).size() * 50;
                    amt += DUtil.getFavor(attacked) / 25 + (new Random()).nextInt(DUtil.getLevel(attacker) * 30 + 1);
                    if (amt > MAXFAVORGIVEN)
                        amt = MAXFAVORGIVEN;
                    DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " of the " +
                            DUtil.getAllegiance(attacked) + " alliance was slain by " + ChatColor.YELLOW + attacker.getName() +
                            ChatColor.GRAY + " of the " + DUtil.getAllegiance(attacker) + " alliance.");
                    DUtil.setDeaths(attacked, DUtil.getDeaths(attacked) + 1);
                    attacker.sendMessage(ChatColor.YELLOW + "You have received " + amt + " Favor as a reward.");
                    DUtil.setFavor(attacker, DUtil.getFavor(attacker) + amt);
                    attacked.sendMessage(ChatColor.RED + "You have failed in your service to the " + DUtil.getAllegiance(attacked) + " alliance.");
                    attacked.sendMessage(ChatColor.RED + "Your Favor has been reduced to " + (int) (DUtil.getFavor(attacked) * 0.25) + ".");
                    DUtil.setFavor(attacked, (int) (DUtil.getFavor(attacked) * 0.25));
                } else {
                    if (DUtil.getKills(attacker) > 0)
                        DUtil.setKills(attacker, DUtil.getKills(attacker) - 1);
                    DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " was betrayed by " +
                            ChatColor.YELLOW + attacker.getName() + ChatColor.GRAY + " of the " + DUtil.getAllegiance(attacker) + " alliance.");
                }
            } else {
                DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + attacked.getName() + ChatColor.GRAY + " was slain by " +
                        ChatColor.YELLOW + attacker.getName() + ChatColor.GRAY + " of the " + DUtil.getAllegiance(attacker) + " alliance.");
            }
        }
    }
}
package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Apollo;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class ApolloCommands {
    public static void onPlayerInteractApollo(PlayerInteractEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() != Material.BOW) return;
        if (!plugin.isGod(p)) return;
        if (!plugin.getInfo(p).hasDeity(Divine.APOLLO)) return;
        if (!plugin.getInfo(p).isAlive()) return;
        Apollo a = (Apollo) plugin.getInfo(p).getDeity(Divine.APOLLO);
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
            if (a.getFireArrow()) {
                if (gpi.getFavor() > 45) {
                    if (!p.getInventory().contains(Material.ARROW)) p.launchProjectile(Arrow.class);
                    for (Entity en : p.getLocation().getBlock().getChunk().getEntities()) {
                        if (en instanceof Arrow) {
                            if (en.getLocation().distance(p.getLocation()) < 3) fireArrow((Arrow) en, plugin);
                            gpi.setFavor(gpi.getFavor() - 45);
                            p.sendMessage("You now have " + gpi.getFavor() + " Favor.");
                            break;
                        }
                    }
                } else {
                    a.setFireArrow(false);
                    p.sendMessage("You don't have enough Favor to use fire arrows.");
                }
            }
            if (System.currentTimeMillis() < a.getInfinite()) {
                p.launchProjectile(Arrow.class);
                e.setCancelled(true);
            }
        }
    }

    public static void onEntityDamage(EntityDamageEvent e, Demigods plugin) {
        if (e instanceof EntityDamageByEntityEvent)
            if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
                EntityDamageByEntityEvent d = (EntityDamageByEntityEvent) e;
                if (d.getEntity() instanceof Player) {
                    Player p = (Player) d.getEntity();
                    if (p.getItemInHand().getType() != Material.BOW) return;
                    if (!plugin.isGod(p)) return;
                    if (!plugin.getInfo(p).hasDeity(Divine.APOLLO)) return;
                    if (!plugin.getInfo(p).isAlive()) return;
                    d.setDamage(0);
                }
                if (((Projectile) d.getDamager()).getShooter() instanceof Player) {
                    Player p = (Player) ((Projectile) d.getDamager()).getShooter();
                    if (p.getItemInHand().getType() != Material.BOW) return;
                    if (!plugin.isGod(p)) return;
                    if (!plugin.getInfo(p).hasDeity(Divine.APOLLO)) return;
                    if (!plugin.getInfo(p).isAlive()) return;
                    GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                    Apollo a = (Apollo) plugin.getInfo(p).getDeity(Divine.APOLLO);
                    if (a.getHealArrow()) {
                        p.sendMessage("Heal arrows are on.");
                        if (gpi.getFavor() >= 20) {
                            e.setDamage(0);
                            if (e.getEntity() instanceof LivingEntity) {
                                LivingEntity le = (LivingEntity) e.getEntity();
                                if (le.getHealth() + 2 > 20) le.setHealth(20);
                                else le.setHealth(le.getHealth() + 2);
                                gpi.setFavor(gpi.getFavor() - 20);
                                if (le instanceof Player) {
                                    p.sendMessage(((Player) le).getName() + " was healed. You now have " + gpi.getFavor() + " Favor.");
                                } else
                                    p.sendMessage("Your target was healed. You now have " + gpi.getFavor() + " Favor.");
                            }
                        } else {
                            a.setHealArrow(false);
                            p.sendMessage("You don't have enough Favor to use heal arrows.");
                        }
                    }
                }
            }
    }

    public static void onPlayerCommandPreprocessApollo(PlayerCommandPreprocessEvent event, final Demigods plugin) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        if (plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
        if (plugin.isGod(player)) {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
            if (gpi.hasDeity(Divine.APOLLO)) {
                if (!gpi.isAlive()) {
                    return;
                }
                final Apollo a = (Apollo) gpi.getDeity(Divine.APOLLO);
                String[] sects = event.getMessage().split(" +", 2);
                Commands cmd;
                try {
                    cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
                } catch (Exception ex) {
                    return;
                }
                try {
                    switch (cmd) {
                        case HEALARROW:
                            if (a.getHealArrow()) {
                                player.sendMessage("Healing arrows have been disabled.");
                                a.setHealArrow(false);
                            } else {
                                if (a.getFireArrow()) {
                                    a.setFireArrow(false);
                                    player.sendMessage("Fire arrows are already enabled.");
                                    break;
                                }
                                player.sendMessage("Healing arrows have been enabled.");
                                a.setHealArrow(true);
                            }
                            break;
                        case FIREARROW:
                            if (a.getFireArrow()) {
                                player.sendMessage("Fire arrows have been disabled.");
                                a.setFireArrow(false);
                            } else {
                                if (a.getHealArrow()) {
                                    a.setHealArrow(false);
                                    player.sendMessage("Heal arrows are already enabled.");
                                    break;
                                }
                                player.sendMessage("Fire arrows have been enabled.");
                                a.setFireArrow(true);
                            }
                            break;
                        case SOULARROW:
                            if (a.getInfinite() >= System.currentTimeMillis()) {
                                player.sendMessage("Soul arrow is still in effect.");
                            } else {
                                if (gpi.getFavor() < 400) {
                                    player.sendMessage("Soul arrow costs 400 Favor.");
                                } else {
                                    gpi.setFavor(gpi.getFavor() - 400);
                                    player.sendMessage("Soul arrow is in effect. You now have " + gpi.getFavor() + " Favor.");
                                    player.sendMessage(ChatColor.GRAY + "You will have infinite arrows for 30 seconds.");
                                    a.setInfinite(System.currentTimeMillis() + 30000);
                                    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                                        public void run() {
                                            player.sendMessage("Soul arrow is no longer in effect.");
                                        }
                                    }, 600);
                                }
                            }
                            break;
                    }
                } catch (NoSuchMethodError ex) {
                    player.sendMessage("The plugin for " + sects[0].toLowerCase() + " is broken or out of date.");
                } catch (Exception ex) {
                    player.sendMessage("�cError: " + ex.getMessage());
                }
                event.setCancelled(true);
            }
        }
    }

    private enum Commands {
        HEALARROW, FIREARROW, SOULARROW
    }

    private static void fireArrow(final Arrow a, Demigods plugin) {
        a.setFireTicks(a.getMaxFireTicks());
        for (int i = 0; i <= 80; i += 4) {
            final List<LivingEntity> potentialTargets = a.getLocation().getWorld().getLivingEntities();
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    if (a != null) {
                        for (LivingEntity le : potentialTargets) {
                            if (a.getLocation().distance(le.getLocation()) < 10 && !le.equals(a.getShooter())) {
                                a.setVelocity(le.getLocation().toVector().subtract(a.getLocation().toVector()));
                                // a.setVelocity(new Vector(0,0,0));
                            }
                        }
                    }
                }
            }, i);
        }
    }
}

package com.WildAmazing.marinating.demigods.Titans;

import com.WildAmazing.marinating.demigods.Utilities.DSave;
import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Prometheus implements Deity {
    private static final long serialVersionUID = -6437607905225500420L;
    private String PLAYER;
    private final int FIREBALLCOST = 50;
    private final int PROMETHEUSULTIMATECOST = 700;
    private final int PROMETHEUSULTIMATECOOLDOWNMAX = 600; //seconds
    private final int PROMETHEUSULTIMATECOOLDOWNMIN = 60;
    private final int DEFECTCOST = 350;
    private final int FIREBALLDELAY = 1; //seconds

    public Material FIREBALLITEM = null;
    private boolean FIREBALL = false;
    private long FIRESTORMTIME;
    private long DEFECTTIME;
    private long FIREBALLTIME;
    public String DISPLAYNAME;

    public Prometheus(String name) {
        PLAYER = name;
        FIRESTORMTIME = System.currentTimeMillis();
        DEFECTTIME = System.currentTimeMillis();
        DISPLAYNAME = name;
        FIREBALLTIME = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Prometheus";
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
        if (DUtil.hasDeity(p, "Prometheus") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            int t = (int) (PROMETHEUSULTIMATECOOLDOWNMAX - ((PROMETHEUSULTIMATECOOLDOWNMAX - PROMETHEUSULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
            double percentreduce = 10.921294 * Math.pow(DUtil.getLevel(p), 0.5971283);
            if (percentreduce > 100) percentreduce = 100;
            if (percentreduce < 0) percentreduce = 0;
            int time = (int) Math.round(74.1755 * Math.pow(1.0307, DUtil.getLevel(p))); //seconds
            int cooldowntime = (int) Math.round(791.2634 * Math.pow(0.99097, DUtil.getLevel(p))); //seconds
            int firestormshots = (DUtil.getLevel(p) / 10 + 3);
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Prometheus" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Reduce fire damage by " + percentreduce + "%.");
            p.sendMessage(":Shoot a fireball at the cursor's location. " + ChatColor.GREEN + "/fireball");
            p.sendMessage(ChatColor.YELLOW + "Costs " + FIREBALLCOST + " Favor.");
            if (((Prometheus) (DUtil.getDeity(p, "Prometheus"))).FIREBALLITEM != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + ((Prometheus) (DUtil.getDeity(p, "Prometheus"))).FIREBALLITEM.name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Disguise yourself as a God for an amount of time. " + ChatColor.GREEN + "/defect");
            p.sendMessage(ChatColor.YELLOW + "Costs " + DEFECTCOST + " Favor.");
            p.sendMessage("Change for " + time + " seconds, with cooldown of " + cooldowntime + " seconds.");
            p.sendMessage(":Prometheus rains fire on nearby enemies.");
            p.sendMessage("Shoots " + firestormshots + " fireballs. " + ChatColor.GREEN + "/firestorm");
            p.sendMessage(ChatColor.YELLOW + "Costs " + PROMETHEUSULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Prometheus");
        p.sendMessage("Passive: Reduce damage from fire..");
        p.sendMessage("Active: Shoot a fireball. " + ChatColor.GREEN + "/fireball");
        p.sendMessage(ChatColor.YELLOW + "Costs " + FIREBALLCOST + " Favor. Can bind.");
        p.sendMessage("Active: Disguise yourself as a God as a short amount of time.");
        p.sendMessage("While disguised, blocks cannot be placed or broken. " + ChatColor.GREEN + "/defect " +
                ChatColor.YELLOW + "Costs " + DEFECTCOST + " Favor.");
        p.sendMessage("Ultimate: Prometheus rains fireballs on your enemies.");
        p.sendMessage(ChatColor.GREEN + "/firestorm" + ChatColor.YELLOW + " Costs " + PROMETHEUSULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: clay ball");
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Prometheus"))
                return;
            if (FIREBALL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == FIREBALLITEM))) {
                if (System.currentTimeMillis() < FIREBALLTIME)
                    return;
                if (DUtil.getFavor(p) >= FIREBALLCOST) {
                    DUtil.setFavor(p, DUtil.getFavor(p) - FIREBALLCOST);
                    shootFireball(p.getEyeLocation(), DUtil.getTargetLocation(p), p);
                    FIREBALLTIME = System.currentTimeMillis() + FIREBALLDELAY * 1000;
                } else {
                    FIREBALL = false;
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor to do that.");
                }
            }
        } else if (ee instanceof BlockBreakEvent) {
            BlockBreakEvent e = (BlockBreakEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Prometheus"))
                return;
            if (!DUtil.isGod(p))
                return;
            p.sendMessage(ChatColor.YELLOW + "You cannot break blocks while you are a God.");
            e.setCancelled(true);
        } else if (ee instanceof BlockPlaceEvent) {
            BlockPlaceEvent e = (BlockPlaceEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Prometheus"))
                return;
            if (!DUtil.isGod(p))
                return;
            p.sendMessage(ChatColor.YELLOW + "You cannot place blocks while you are a God.");
            e.setCancelled(true);
        } else if (ee instanceof EntityDamageEvent) {
            EntityDamageEvent e1 = (EntityDamageEvent) ee;
            if ((e1.getCause() == DamageCause.FIRE) || (e1.getCause() == DamageCause.FIRE_TICK)) {
                if (!(e1.getEntity() instanceof Player))
                    return;
                Player p = (Player) e1.getEntity();
                if (!DUtil.isFullParticipant(p))
                    return;
                if (!DUtil.hasDeity(p, "Prometheus"))
                    return;
                double percentreduce = 10.921294 * Math.pow(DUtil.getLevel(p), 0.5971283);
                if (percentreduce > 100) percentreduce = 100;
                if (percentreduce < 0) percentreduce = 0;
                percentreduce /= 100;
                e1.setDamage((int) Math.floor(e1.getDamage() * (1 - percentreduce)));
            }
            if (ee instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
                if (e.getEntity() instanceof Player) {
                    if (e.getDamager() instanceof Player) {
                        Player attacker = (Player) e.getDamager();
                        Player attacked = (Player) e.getEntity();
                        if (!(DUtil.isFullParticipant(attacked) && DUtil.isFullParticipant(attacker)))
                            return;
                        if (!DUtil.hasDeity(attacked, "Prometheus"))
                            return;
                        if (!(DUtil.isGod(attacker) && DUtil.isGod(attacked)))
                            return;
                        attacker.sendMessage(ChatColor.YELLOW + "You have revealed " + attacked.getName() + " as a Titan!");
                        attacked.sendMessage(ChatColor.YELLOW + "You have been revealed as a Titan by " + attacker.getName() + "!");
                        DUtil.setTitan(attacked);
                        attacked.setDisplayName(((Prometheus) DUtil.getDeity(attacked, "Prometheus")).DISPLAYNAME);
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
        if (!DUtil.hasDeity(p, "Prometheus"))
            return;
        if (str.equalsIgnoreCase("fireball")) {
            if (bind) {
                if (FIREBALLITEM == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand() == null)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        FIREBALLITEM = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Fireball is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, FIREBALLITEM);
                    p.sendMessage(ChatColor.YELLOW + "Fireball is no longer bound to " + FIREBALLITEM.name() + ".");
                    FIREBALLITEM = null;
                }
                return;
            }
            if (FIREBALL) {
                FIREBALL = false;
                p.sendMessage(ChatColor.YELLOW + "Fireball is no longer active.");
            } else {
                FIREBALL = true;
                p.sendMessage(ChatColor.YELLOW + "Fireball is now active.");
            }
        } else if (str.equalsIgnoreCase("defect")) {
            if (DUtil.getFavor(p) < DEFECTCOST) {
                p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                return;
            }
            if (DEFECTTIME > System.currentTimeMillis()) {
                p.sendMessage(ChatColor.YELLOW + "You cannot change alliances again for " + ((((DEFECTTIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((DEFECTTIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            DUtil.setFavor(p, DUtil.getFavor(p) - DEFECTCOST);
            defect(p);
        } else if (str.equalsIgnoreCase("firestorm")) {
            if (System.currentTimeMillis() < FIRESTORMTIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot use the firestorm again for " + ((((FIRESTORMTIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage("and " + ((((FIRESTORMTIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= PROMETHEUSULTIMATECOST) {
                int t = (int) (PROMETHEUSULTIMATECOOLDOWNMAX - ((PROMETHEUSULTIMATECOOLDOWNMAX - PROMETHEUSULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                DSave.saveData(p, "PROMETHEUSULTIMATETIME", (System.currentTimeMillis() + (t * 1000)));
                p.sendMessage("In exchange for " + ChatColor.AQUA + PROMETHEUSULTIMATECOST + ChatColor.WHITE + " Favor, ");
                p.sendMessage(ChatColor.GOLD + "Prometheus " + ChatColor.WHITE + " has unleashed his wrath on " + firestorm(p) + " non-allied entities.");
                DUtil.setFavor(p, DUtil.getFavor(p) - PROMETHEUSULTIMATECOST);
            } else p.sendMessage("Firestorm requires " + PROMETHEUSULTIMATECOST + " Favor.");
        }
    }

    private void shootFireball(Location from, Location to, Player player) {
        player.getWorld().spawnEntity(from, EntityType.FIREBALL);
        for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
            if (entity instanceof Fireball) {
                Fireball fireball = (Fireball) entity;
                to.setX(to.getX() + .5);
                to.setY(to.getY() + .5);
                to.setZ(to.getZ() + .5);
                Vector path = to.toVector().subtract(from.toVector());
                Vector victor = from.toVector().add(from.getDirection().multiply(2));
                fireball.teleport(new Location(player.getWorld(), victor.getX(), victor.getY(), victor.getZ()));
                fireball.setDirection(path);
                fireball.setShooter(player);
            }
        }
    }

    private void defect(Player p) {
        int time = (int) Math.round(74.1755 * Math.pow(1.0307, DUtil.getLevel(p))); //seconds
        int cooldowntime = (int) Math.round(791.2634 * Math.pow(0.99097, DUtil.getLevel(p))); //seconds
        DUtil.setGod(p);
        DISPLAYNAME = p.getDisplayName();
        DEFECTTIME = System.currentTimeMillis() + cooldowntime * 1000;
        p.sendMessage(ChatColor.YELLOW + "You will be a God for " + time + " seconds unless revealed.");
        for (Player player : DUtil.getPlugin().getServer().getOnlinePlayers()) {
            if (DUtil.isFullParticipant(player)) {
                if (DUtil.isGod(player)) {
                    p.setDisplayName(player.getDisplayName());
                    if (!player.getDisplayName().equals(DISPLAYNAME))
                        p.sendMessage(ChatColor.YELLOW + "Your name has changed from " + DISPLAYNAME + " to " + player.getDisplayName() + ".");
                    break;
                }
            }
        }
        final Player pl = p;
        DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (DUtil.isGod(pl))
                    pl.sendMessage(ChatColor.YELLOW + "Your time as a God is half up.");
            }
        }, time / 2 * 20);
        DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (DUtil.isGod(pl))
                    pl.sendMessage(ChatColor.YELLOW + "You will be a God for 10 more seconds.");
            }
        }, time * 20 - 200);
        DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (DUtil.isGod(pl))
                    pl.sendMessage(ChatColor.YELLOW + "Your alliance has returned to Titan.");
                DUtil.setTitan(pl);
                pl.setDisplayName(DISPLAYNAME);
            }
        }, time * 20);
    }

    private int firestorm(Player p) {
        int total = 20 * (DUtil.getLevel(p) / 6 + 5);
        ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
        Vector ploc = p.getLocation().toVector();
        for (LivingEntity anEntity : p.getWorld().getLivingEntities()) {
            if (anEntity.getLocation().toVector().isInSphere(ploc, 50.0) && (anEntity.getUniqueId() != p.getUniqueId()))
                entitylist.add(anEntity);
        }
        final Player pl = p;
        final ArrayList<LivingEntity> enList = entitylist;
        for (int i = 0; i <= total; i += 20) {
            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (LivingEntity e1 : enList) {
                        Location up = new Location(e1.getWorld(), e1.getLocation().getX() + Math.random(), 126, e1.getLocation().getZ() + Math.random());
                        up.setPitch(90);
                        shootFireball(up, new Location(e1.getWorld(), e1.getLocation().getX() + Math.random(), e1.getLocation().getY(), e1.getLocation().getZ() + Math.random()), pl);
                    }
                }
            }, i);
        }
        return enList.size();
    }
}

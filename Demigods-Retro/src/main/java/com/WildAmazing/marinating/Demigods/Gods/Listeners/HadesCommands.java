package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Hades;
import com.WildAmazing.marinating.Demigods.OtherCommands.PhantomCommands;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HadesCommands {
    public static void onPlayerInteractHades(PlayerInteractEvent e, Demigods plugin) {
        Player player = e.getPlayer();
        if (plugin.isGod(player)) {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
            if (gpi.hasDeity(Divine.HADES)) {
                if (!gpi.isAlive()) return;
                Hades h = (Hades) gpi.getDeity(Divine.HADES);
                if (player.getItemInHand().getType() != Material.AIR) {
                    if (player.getItemInHand().getType() == h.getTargetItem()) {
                        targetcode(gpi, h, player, plugin);
                        return;
                    } else if (player.getItemInHand().getType() == h.getEntombItem()) {
                        entombcode(gpi, h, player, plugin);
                        return;
                    }
                }
                if (h.getTarget()) {
                    targetcode(gpi, h, player, plugin);
                }
                if (h.getEntomb()) {
                    entombcode(gpi, h, player, plugin);
                }
            }
        }
    }

    public static void onPlayerCommandPreprocessHades(PlayerCommandPreprocessEvent event, Demigods plugin) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
        if (plugin.isGod(player)) {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
            if (gpi.hasDeity(Divine.HADES)) {
                if (!gpi.isAlive()) return;
                Hades h = (Hades) gpi.getDeity(Divine.HADES);
                String[] sects = event.getMessage().split(" +", 2);
                String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
                int bind = 0;
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("bind")) bind = 1;
                    else if (args[0].equalsIgnoreCase("unbind")) bind = -1;
                }
                Commands cmd;
                try {
                    cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
                } catch (Exception ex) {
                    return;
                }
                try {
                    switch (cmd) {
                        case TARGET:
                            if (bind != 0) {
                                if (bind == 1) {
                                    if (player.getItemInHand().getType() == Material.AIR) {
                                        player.sendMessage("You are not holding an item.");
                                        break;
                                    }
                                    if (player.getItemInHand().getType() == h.getEntombItem()) {
                                        player.sendMessage(player.getItemInHand().getType().name() + ChatColor.YELLOW + " is already bound to Raising.");
                                        break;
                                    }
                                    if (h.getTargetItem() != player.getItemInHand().getType()) {
                                        h.setTargetItem(player.getItemInHand().getType());
                                        player.sendMessage("Targeting " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
                                    } else {
                                        player.sendMessage("Targeting " + ChatColor.YELLOW + "is already bound to " + h.getTargetItem().name() + ".");
                                    }
                                } else if (bind == -1) {
                                    if (h.getTargetItem() != null) {
                                        player.sendMessage("Targeting " + ChatColor.YELLOW + "is no longer bound to " + h.getTargetItem().name() + ".");
                                        h.setTargetItem(null);
                                    }
                                }
                                break;
                            }
                            if (h.getEntomb()) {
                                player.sendMessage("You are already in entombing mode.");
                                break;
                            }
                            if (gpi.getFavor() < 25) {
                                player.sendMessage("Undead targeting requires at least 25 Favor.");
                                break;
                            }
                            if (!h.getTarget()) {
                                h.setTarget(true);
                                player.sendMessage("Select a target for the undead.");
                            } else {
                                h.setTarget(false);
                                player.sendMessage("Targeting is no longer enabled.");
                            }
                            break;
                        case RAISEDEAD:
                            if (bind != 0) {
                                if (bind == 1) {
                                    if (player.getItemInHand().getType() == Material.AIR) {
                                        player.sendMessage("You are not holding an item.");
                                        break;
                                    }
                                    if (player.getItemInHand().getType() == h.getTargetItem()) {
                                        player.sendMessage(player.getItemInHand().getType().name() + ChatColor.YELLOW + " is already bound to Targeting.");
                                        break;
                                    }
                                    if (h.getEntombItem() != player.getItemInHand().getType()) {
                                        h.setEntombItem(player.getItemInHand().getType());
                                        player.sendMessage("Raising " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
                                    } else {
                                        player.sendMessage("Raising " + ChatColor.YELLOW + "is already bound to " + h.getEntombItem().name() + ".");
                                    }
                                } else if (bind == -1) {
                                    player.sendMessage("Raising " + ChatColor.YELLOW + "is no longer bound to " + h.getEntombItem().name() + ".");
                                    h.setEntombItem(Material.AIR);
                                }
                                break;
                            }
                            if (h.getTarget()) {
                                player.sendMessage("You are already in targeting mode.");
                                break;
                            }
                            if (!h.getEntomb()) {
                                if (gpi.getFavor() >= 50) {
                                    player.sendMessage("You can now click to raise the dead.");
                                    h.setEntomb(true);
                                } else {
                                    player.sendMessage("You need at least 50 Favor to do that.");
                                }
                            } else {
                                player.sendMessage("You are no longer raising the dead.");
                                h.setEntomb(false);
                            }
                            break;
                        case REVIVE:
                        case RESURRECT:
                            if ((player.getInventory().getArmorContents()[3].getType() == Material.GOLD_HELMET)) {
                                if (args.length == 1) {
                                    player.getInventory().setHelmet(null);
                                    try {
                                        Player p = plugin.getServer().getPlayer(args[0]);
                                        if (plugin.isGod(p) || plugin.isTitan(p)) {
                                            if (!plugin.getInfo(p).isAlive()) {
                                                plugin.getInfo(p).setAlive(true);
                                                PhantomCommands.unphantomize(p);
                                                p.sendMessage(ChatColor.GOLD + "You were resurrected by " + player.getName() + ".");
                                                player.sendMessage(p.getName() + " was resurrected by the " + ChatColor.DARK_RED + "Helm of Hades" + ChatColor.WHITE + ".");
                                                break;
                                            }
                                        }
                                        player.sendMessage("That player is not a phantom.");
                                    } catch (Exception error) {
                                        player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                                    }
                                } else
                                    player.sendMessage("Correct syntax: " + ChatColor.YELLOW + "/revive <player name>");
                            } else
                                player.sendMessage("You must be wearing the " + ChatColor.DARK_RED + "Helm of Hades" + ChatColor.WHITE + " to do that.");
                            break;
                        case TARTARUS:
                            tartarus(player, plugin);
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
        TARGET, RAISEDEAD, TARTARUS, REVIVE, RESURRECT
    }

    private static void tartarus(Player p, Demigods plugin) {
        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
        if (gpi.getFavor() < 1000) {
            p.sendMessage("You require at least 1000 Favor to call " + ChatColor.DARK_GRAY + "Hades" + ChatColor.WHITE + ".");
            return;
        }
        Hades h = (Hades) gpi.getDeity(Divine.HADES);
        if (h.getTime() > System.currentTimeMillis()) {
            p.sendMessage("You cannot use Hades' power again for " + (((h.getTime() / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
            p.sendMessage("and " + (((h.getTime() / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
            return;
        }
        ArrayList<Entity> entitylist = new ArrayList<Entity>();
        Vector ploc = p.getLocation().toVector();
        for (Entity anEntity : p.getWorld().getEntities()) {
            if (anEntity.getLocation().toVector().isInSphere(ploc, 50.0)) entitylist.add(anEntity);
        }
        int count = 0;
        for (Entity eee : entitylist) {
            try {
                LivingEntity e1 = (LivingEntity) eee;
                if (e1 instanceof Player) {
                    Player ptemp = (Player) e1;
                    if (!plugin.isGod(ptemp) && !ptemp.equals(p) && (ptemp.getLocation().toVector().distance(p.getLocation().toVector()) > 2)) {
                        drop(ptemp, plugin);
                        count++;
                    }
                } else {
                    if (e1.getLocation().toVector().distance(p.getLocation().toVector()) > 2) {
                        count++;
                        drop(e1, plugin);
                    }
                }
            } catch (Exception notAlive) {
            } // ignore stuff like minecarts
        }
        gpi.setFavor(gpi.getFavor() - 1000);
        h.setTime(System.currentTimeMillis() + 600000);
        p.sendMessage(ChatColor.DARK_GRAY + "Hades" + ChatColor.WHITE + " cackles as " + count + " entities fall to Tartarus.");
    }

    private static void drop(LivingEntity e, Demigods plugin) {
        ArrayList<Location> locs = new ArrayList<Location>();
        final ArrayList<BlockState> fix = new ArrayList<BlockState>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int height = 0; height <= e.getLocation().getBlockY() + 3; height++) {
                    locs.add(new Location(e.getWorld(), e.getLocation().getBlockX() + x, e.getLocation().getBlockY() - height, e.getLocation().getBlockZ() + z));
                    if (locs.get(locs.size() - 1).getBlock().getType() != Material.AIR)
                        fix.add(locs.get(locs.size() - 1).getBlock().getState());
                    locs.get(locs.size() - 1).getBlock().setType(Material.AIR);
                }
            }
        }
        e.teleport(new Location(e.getWorld(), e.getLocation().getBlockX(), e.getLocation().getBlockY() - 3, e.getLocation().getBlockZ()));
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                for (BlockState bs : fix) {
                    Block inQuestion = bs.getWorld().getBlockAt(new Location(bs.getWorld(), bs.getX(), bs.getY(), bs.getZ()));
                    inQuestion.setData(bs.getRawData());
                    inQuestion.setType(bs.getType());
                }
            }
        }, 120);
    }

    public static void onEntityDamage(EntityDamageEvent ee, Demigods plugin) {
        try {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
            Player p = null;
            if (e.getEntity() instanceof Player) {
                p = (Player) e.getEntity();
            }
            if (p != null && (e.getDamager() instanceof Zombie || e.getDamager() instanceof Skeleton)) {
                if (plugin.isGod(p)) {
                    GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                    if (gpi.hasDeity(Divine.HADES)) e.setCancelled(true);
                }
            }
        } catch (Exception notThisTypeOfEvent) {
        }
    }

    public static void onEntityTarget(EntityTargetEvent e, Demigods plugin) {
        Player p = null;
        if (e.getTarget() instanceof Player) {
            p = (Player) e.getTarget();
        }
        if (p != null && (e.getEntity() instanceof Zombie || e.getEntity() instanceof Skeleton)) {
            if (plugin.isGod(p)) {
                GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                if (gpi.hasDeity(Divine.HADES)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    public static void onEntityCombust(EntityCombustEvent e, Demigods plugin) {
        if (e.getEntity() instanceof Zombie || e.getEntity() instanceof Skeleton) {
            Monster m = (Monster) e.getEntity();
            for (Player p : m.getWorld().getPlayers()) {
                if (p.getLocation().toVector().isInSphere(m.getLocation().toVector(), 10)) {
                    if (plugin.isGod(p)) {
                        GodPlayerInfo gpi = (GodPlayerInfo) (plugin.getInfo(p));
                        if (!gpi.isAlive()) {
                            return;
                        }
                        if (gpi.hasDeity(Divine.HADES)) {
                            m.setFireTicks(0);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private static void targetcode(GodPlayerInfo gpi, Hades h, Player player, Demigods plugin) {
        LivingEntity target = null;
        for (Block b : (List<Block>) player.getLineOfSight((Set) null, 200)) {
            for (Entity ee : b.getChunk().getEntities()) {
                try {
                    LivingEntity e1 = (LivingEntity) ee;
                    if (e1.getLocation().toVector().distance(b.getLocation().toVector()) < 4) target = e1;
                } catch (Exception notAlive) {
                }
            }
        }
        if (target instanceof Player) {
            Player tar = (Player) target;
            if (tar.equals(player)) {
                player.sendMessage("You can't target yourself!");
                return;
            }
        }
        int count = 0;
        if (target != null) {
            for (Entity ent : target.getWorld().getEntities()) {
                try {
                    Monster m = (Monster) ent;
                    if (m.getLocation().toVector().isInSphere(target.getLocation().toVector(), 40)) {
                        if (m instanceof Skeleton || m instanceof Zombie || m instanceof PigZombie) {
                            count++;
                            m.setTarget(target);
                        }
                    }
                } catch (Exception uncastable) {
                }
            }
            String name = target.getClass().getSimpleName();
            name = name.substring(5);
            player.sendMessage(count + " monsters have been assigned to target: " + name);
        } else player.sendMessage("No valid targets found.");
    }

    private static void entombcode(GodPlayerInfo gpi, Hades h, Player player, Demigods plugin) {
        if (gpi.getFavor() < 50) {
            player.sendMessage("You have not enough Favor.");
            return;
        }
        Location target = player.getTargetBlock((Set) null, 200).getLocation();
        if (target == null) {
            player.sendMessage("Invalid target.");
            return;
        }
        int what = (int) (Math.random() * 4 + 1);
        EntityType choice;
        if (what == 1) choice = EntityType.PIG_ZOMBIE;
        else if (what == 2) choice = EntityType.SKELETON;
        else choice = EntityType.ZOMBIE;
        player.getWorld().spawnEntity(target, choice);
        player.sendMessage("A " + choice.getName().toLowerCase() + " was raised from Tartarus.");
        gpi.setFavor(gpi.getFavor() - 50);
    }
}

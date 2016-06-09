package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Gods.Poseidon;
import com.WildAmazing.marinating.Demigods.Utilities.Cuboid;
import com.WildAmazing.marinating.Demigods.Utilities.DeityLocale;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.GodPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PoseidonCommands {
    public static void onEnable(final World w, final Demigods plugin) {
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                if (w == null) return;
                for (Player player : w.getPlayers()) {
                    if (!player.isOnline()) return;
                    if (plugin.isGod(player)) {
                        if (plugin.getInfo(player).hasDeity(Divine.POSEIDON)) if (plugin.getInfo(player).isAlive())
                            if ((player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) || (player.getLocation().getBlock().getType() == Material.WATER) || (player.getRemainingAir() < player.getMaximumAir()))
                                if (player.getHealth() < 20) {
                                    if (player.getHealth() + 2 <= 20) player.setHealth(player.getHealth() + 2);
                                    else if (player.getHealth() + 2 > 20) player.setHealth(20);
                                }
                    }
                }
            }
        }, 66, 66);
    }

    public static void onPlayerInteractPoseidon(PlayerInteractEvent e, Demigods plugin) {
        Player player = e.getPlayer();
        if (!plugin.isGod(player)) return;
        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
        if (!gpi.hasDeity(Divine.POSEIDON)) return;
        if (!gpi.isAlive()) return;
        Poseidon p = (Poseidon) gpi.getDeity(Divine.POSEIDON);
        if (player.getItemInHand().getType() != Material.AIR) {
            if (player.getItemInHand().getType() == p.getLiquefyItem()) {
                liquefy(player, gpi, plugin);
                return;
            }
        }
        if (player.getRemainingAir() < player.getMaximumAir()) {
            if (p.getBreak()) {
                waterbreak(player, e, plugin);
            }
        }
        if (p.getLiquefy()) {
            liquefy(player, gpi, plugin);
        }
        if (p.getTrident()) {
            p.setTrident(callTrident(player, plugin));
        }
        if (player.getItemInHand().getType() == Material.FISHING_ROD) {
            if (reelIn(player)) e.setCancelled(true);
        }
    }

    public static void onPlayerCommandPreprocessPoseidon(PlayerCommandPreprocessEvent event, Demigods plugin) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        if (plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
        if (plugin.isGod(player)) {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
            if (gpi.hasDeity(Divine.POSEIDON)) {
                if (!gpi.isAlive()) {
                    return;
                }
                final Poseidon p = (Poseidon) gpi.getDeity(Divine.POSEIDON);
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
                        case WATERBREAK:
                            if (p.getLiquefy()) {
                                player.sendMessage("You are already in liquefy mode.");
                                break;
                            }
                            if (!p.getBreak()) {
                                if (gpi.getFavor() > 100) {
                                    player.sendMessage(ChatColor.AQUA + "Poseidon" + ChatColor.WHITE + " has granted you the ability to break blocks");
                                    player.sendMessage("instantly underwater for 30 seconds.");
                                    p.setBreak(true);
                                    p.breakTime = System.currentTimeMillis() + 30000;
                                    gpi.setFavor(gpi.getFavor() - 100);
                                } else
                                    player.sendMessage("Underwater breaking requires 100 Favor. You have " + gpi.getFavor() + " Favor.");
                            } else player.sendMessage("You are already in underwater breaking mode.");
                            break;
                        case LIQUEFY:
                            if (bind != 0) {
                                if (bind == 1) {
                                    if (player.getItemInHand().getType() == Material.AIR) {
                                        player.sendMessage("You are not holding an item.");
                                        break;
                                    }
                                    if (p.getLiquefyItem() != player.getItemInHand().getType()) {
                                        p.setLiquefyItem(player.getItemInHand().getType());
                                        player.sendMessage("Liquefaction " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
                                    } else {
                                        player.sendMessage("Liquefaction " + ChatColor.YELLOW + "is already bound to " + p.getLiquefyItem().name() + ".");
                                    }
                                } else if (bind == -1) {
                                    player.sendMessage("Liquefaction " + ChatColor.YELLOW + "is no longer bound to " + p.getLiquefyItem().name() + ".");
                                    p.setLiquefyItem(Material.AIR);
                                }
                                break;
                            }
                            if (p.getBreak()) {
                                player.sendMessage("You are already in waterbreaking mode.");
                                break;
                            }
                            if (!p.getLiquefy()) {
                                player.sendMessage("Liquefaction has been turned on.");
                                p.setLiquefy(true);
                            } else {
                                player.sendMessage("Liquefaction has been turned off.");
                                p.setLiquefy(false);
                            }
                            break;
                        case EARTHQUAKE:
                            if (player.getItemInHand().getType() == Material.FISHING_ROD) {
                                earthquake(player, plugin);
                                player.setItemInHand(null);
                                player.sendMessage(ChatColor.DARK_AQUA + "The earth trembles before Poseidon's might.");
                            } else
                                player.sendMessage("You must be holding " + ChatColor.DARK_AQUA + "Poseidon's Trident" + ChatColor.WHITE + " to do that.");
                            break;
                        case PRIME:
                            if (p.getLiquefy()) {
                                player.sendMessage("You are already in liquefy mode.");
                                break;
                            }
                            if (p.getBreak()) {
                                player.sendMessage("You are already in waterbreaking mode.");
                                break;
                            }
                            if (gpi.getFavor() < 800) {
                                player.sendMessage("You need at least 800 Favor to use this ability.");
                                break;
                            }
                            if (p.getTridentTime() > System.currentTimeMillis()) {
                                player.sendMessage("You cannot use Poseidon's trident again for " + (((p.getTridentTime() / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                                player.sendMessage("and " + (((p.getTridentTime() / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                                break;
                            }
                            if (p.getTridentTime() < System.currentTimeMillis()) {
                                if (args.length == 1) p.setDelay(Integer.parseInt(args[0]));
                                p.setTrident(true);
                                gpi.setFavor(gpi.getFavor() - 800);
                                player.sendMessage(ChatColor.AQUA + "Poseidon's" + ChatColor.WHITE + " trident has been armed.");
                                p.setTridentTime(System.currentTimeMillis() + 600000);
                            }
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
        WATERBREAK, LIQUEFY, PRIME, EARTHQUAKE
    }

    private static boolean callTrident(Player player, Demigods plugin) {
        try {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
            Poseidon p = (Poseidon) gpi.getDeity(Divine.POSEIDON);
            Location target = null;
            ArrayList<Location> prevLoc;
            for (Block b : (List<Block>) player.getLineOfSight((Set) null, 200))
                if (!b.getType().equals(Material.AIR)) {
                    target = b.getLocation();
                }
            if (plugin.isUnprotected(target)) {
                for (int height = 125; height > 10; height--) {
                    prevLoc = formTridentAt(new Location(target.getWorld(), target.getBlockX(), (int) height, target.getBlockZ()), false, p.getDelay(), plugin);
                    for (Location ll : prevLoc) {
                        if (ll.equals(target)) {
                            prevLoc = formTridentAt(new Location(target.getWorld(), target.getBlockX(), (int) height, target.getBlockZ()), true, p.getDelay(), plugin);
                            player.sendMessage(ChatColor.AQUA + "Poseidon" + ChatColor.WHITE + " flings his trident mightily at the target.");
                            return false; // if it works
                        }
                    }
                }
            } else {
                player.sendMessage("That is a protected location.");
                return true;
            }
        } catch (NullPointerException invalidTarget) {
            player.sendMessage("Location invalid.");
            return true;
        }
        player.sendMessage("Some error occured.");
        return true;
    }

    private static ArrayList<Location> formTridentAt(final Location l, boolean stop, int ddelay, final Demigods plugin) {
        final int delay = ddelay;
        final ArrayList<Location> t = new ArrayList<Location>(); // locations in trident
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        t.add(new Location(l.getWorld(), x, y, z));
        t.add(new Location(l.getWorld(), x, y - 1, z));
        t.add(new Location(l.getWorld(), x, y - 2, z));
        t.add(new Location(l.getWorld(), x, y - 3, z));
        t.add(new Location(l.getWorld(), x, y - 4, z));
        t.add(new Location(l.getWorld(), x, y - 5, z));
        t.add(new Location(l.getWorld(), x, y - 6, z));
        t.add(new Location(l.getWorld(), x, y - 7, z)); // main shaft
        t.add(new Location(l.getWorld(), x + 1, y - 3, z));
        t.add(new Location(l.getWorld(), x + 1, y - 4, z));
        t.add(new Location(l.getWorld(), x - 1, y - 3, z));
        t.add(new Location(l.getWorld(), x - 1, y - 4, z));
        t.add(new Location(l.getWorld(), x + 2, y - 4, z));
        t.add(new Location(l.getWorld(), x - 2, y - 4, z));
        t.add(new Location(l.getWorld(), x + 2, y - 5, z));
        t.add(new Location(l.getWorld(), x - 2, y - 5, z));
        t.add(new Location(l.getWorld(), x + 2, y - 6, z));
        t.add(new Location(l.getWorld(), x - 2, y - 6, z));
        t.add(new Location(l.getWorld(), x + 2, y - 7, z));
        t.add(new Location(l.getWorld(), x - 2, y - 7, z));
        for (Location loc : t) {
            l.getWorld().getBlockAt(loc).setType(Material.WOOL);
            l.getWorld().getBlockAt(loc).setData((byte) 0xB);
        }
        if (!stop) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    for (Location loc : t) {
                        l.getWorld().getBlockAt(loc).setType(Material.AIR);
                    }
                }
            }, 15);
        } else {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    t.clear();
                    int z = 0;
                    int y = 0;
                    int x = 0;
                    for (int zm = -1; zm < 2; zm++) {
                        x = l.getBlockX();
                        y = l.getBlockY();
                        z = l.getBlockZ();
                        t.add(new Location(l.getWorld(), x, y, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 1, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 2, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 3, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 4, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 5, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 6, z + zm));
                        t.add(new Location(l.getWorld(), x, y - 7, z + zm)); // main shaft
                        t.add(new Location(l.getWorld(), x + 1, y - 3, z + zm));
                        t.add(new Location(l.getWorld(), x + 1, y - 4, z + zm));
                        t.add(new Location(l.getWorld(), x - 1, y - 3, z + zm));
                        t.add(new Location(l.getWorld(), x - 1, y - 4, z + zm));
                        t.add(new Location(l.getWorld(), x + 2, y - 4, z + zm));
                        t.add(new Location(l.getWorld(), x - 2, y - 4, z + zm));
                        t.add(new Location(l.getWorld(), x + 2, y - 5, z + zm));
                        t.add(new Location(l.getWorld(), x - 2, y - 5, z + zm));
                        t.add(new Location(l.getWorld(), x + 2, y - 6, z + zm));
                        t.add(new Location(l.getWorld(), x - 2, y - 6, z + zm));
                        t.add(new Location(l.getWorld(), x + 2, y - 7, z + zm));
                        t.add(new Location(l.getWorld(), x - 2, y - 7, z + zm));
                    }
                    for (Location loc : t) {
                        if (loc.getBlockZ() == z && loc.getBlockY() <= (y - 4)) {
                            l.getWorld().getBlockAt(loc).setType(Material.TNT);
                        } else {
                            l.getWorld().getBlockAt(loc).setType(Material.WOOL);
                            l.getWorld().getBlockAt(loc).setData((byte) 0xB);
                        }
                    }
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            Location tar = l.getWorld().getBlockAt(t.get(26)).getLocation();
                            tar.getWorld().createExplosion(7, tar.getBlockX(), tar.getBlockY(), tar.getBlockZ(), true, true);
                            for (Location ranl : t)
                                if ((int) (Math.random() * 30) == 3)
                                    ranl.getWorld().getBlockAt(ranl).setType(Material.WATER);
                        }
                    }, delay * 20);
                }
            }, 16);
        }
        return t;
    }

    public static void onEntityDamage(EntityDamageEvent e, Demigods plugin) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!plugin.isGod(p)) return;
            if (plugin.getInfo(p).hasDeity(Divine.POSEIDON)) {
                if (e.getCause() == DamageCause.DROWNING) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private static void waterbreak(Player player, PlayerInteractEvent e, Demigods plugin) {
        Poseidon po = (Poseidon) plugin.getInfo(player).getDeity(Divine.POSEIDON);
        if (po.breakTime < System.currentTimeMillis()) {
            po.setBreak(false);
            player.sendMessage("Your time has ended.");
            return;
        }
        if (e.getClickedBlock() == null) return;
        Block b = e.getClickedBlock();
        if (!plugin.isUnprotected(b.getLocation())) return;
        if (b.getType() != Material.SIGN && b.getType() != Material.CHEST && b.getType() != Material.BEDROCK && b.getType() != Material.PORTAL && b.getType() != Material.LAVA && b.getType() != Material.STATIONARY_LAVA) {
            for (DeityLocale dl : plugin.getAllLocs()) {
                for (Cuboid c : dl.getLocale()) {
                    if (c.isInCuboid(b.getLocation())) {
                        if (!plugin.getInfo(e.getPlayer()).hasDeity(dl.getDeity())) {
                            e.getPlayer().sendMessage("This area is protected by " + dl.getDeity().name() + ".");
                            e.setCancelled(true);
                            return;
                        } else if (c.isShrine()) {
                            b.setType(Material.STATIONARY_WATER);
                            e.getPlayer().sendMessage("Broken blocks do not yield drops in shrines.");
                            return;
                        }
                    }
                }
            }
            MaterialData d = b.getState().getData();
            b.setType(Material.WATER);
            if (d.getItemType() != Material.WATER && d.getItemType() != Material.STATIONARY_WATER)
                b.getWorld().dropItemNaturally(b.getLocation(), d.toItemStack(1));
        } else player.sendMessage("You cannot break signs or chests in underwater breaking mode.");
    }

    private static void liquefy(Player player, GodPlayerInfo gpi, Demigods plugin) {
        LivingEntity target = null;
        if (gpi.getFavor() >= 25) {
            for (Block b : (List<Block>) player.getLineOfSight((Set) null, 200)) {
                for (Entity e1 : b.getChunk().getEntities()) {
                    try {
                        if (e1.getLocation().toVector().distance(b.getLocation().toVector()) < 3) // jumps to the nearest entity
                        {
                            LivingEntity ee = (LivingEntity) e1;
                            if (!(ee instanceof Player)) {
                                target = ee;
                                break;
                            }
                        }
                    } catch (Exception notAlive) {
                    }// ignore if not a livingentity
                }
            }
            try {
                target.setHealth(0);
                if (plugin.isUnprotected(target.getLocation()))
                    target.getWorld().getBlockAt(target.getLocation()).setType(Material.STATIONARY_WATER);
                gpi.setFavor(gpi.getFavor() - 25);
                player.sendMessage(ChatColor.AQUA + "Poof!" + ChatColor.WHITE + " You now have " + gpi.getFavor() + " Favor.");
            } catch (Exception nullpointer) {
                player.sendMessage("No valid targets found.");
            }
        } else player.sendMessage("Liquefaction costs 25 Favor. You have " + gpi.getFavor() + ".");
    }

    private static boolean reelIn(Player p) {
        LivingEntity target = p;
        Block b = p.getTargetBlock((Set) null, 16);
        for (LivingEntity le : p.getWorld().getLivingEntities()) {
            if (le.getLocation().toVector().distance(b.getLocation().toVector()) < 2) target = le;
        }
        if (target.equals(p)) return false;
        Vector v = p.getLocation().toVector().add(p.getLocation().getDirection().multiply(2));
        Vector victor = v.subtract(target.getLocation().toVector());
        victor.multiply(0.6);
        target.damage(1, p);
        target.setVelocity(victor);
        return true;
    }

    private static void earthquake(final Player p, Demigods plugin) {
        int count = 30; // shakes
        final ArrayList<LivingEntity> affected = new ArrayList<LivingEntity>();
        for (LivingEntity le : p.getWorld().getLivingEntities())
            if (le.getLocation().distance(p.getLocation()) < 16) {
                if (le instanceof Player) ((Player) le).sendMessage("Earthquake!");
                if (!le.equals(p)) affected.add(le);
            }
        for (int i = 0; i <= count * 6; i += 6) {
            final int c = i;
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    for (LivingEntity e : affected) {
                        if (c % 50 == 0) e.damage(2, p);
                        Location l = e.getLocation();
                        l.setPitch((float) ((Math.random() * 300)));
                        l.setYaw((float) ((Math.random() * 300)));
                        e.teleport(l);
                    }
                }
            }, i);
        }
    }
}

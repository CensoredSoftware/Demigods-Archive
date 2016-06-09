package com.WildAmazing.marinating.demigods.Gods;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Poseidon implements Deity {

    /* General */
    private static final long serialVersionUID = 2319323778421842381L;
    private final int REELCOST = 60;
    private final int REELDELAY = 1100;
    private final int drownCOST = 120;
    private final int drownDELAY = 2000;
    private final int ULTIMATECOST = 1000;
    private final int ULTIMATECOOLDOWNMAX = 800;
    private final int ULTIMATECOOLDOWNMIN = 220;

    /* Specific to player */
    private String PLAYER;
    public boolean REEL = false;
    private boolean drown = false;
    private long REELTIME, drownTIME, ULTIMATETIME;
    private Material drownBIND = null;

    public Poseidon(String name) {
        PLAYER = name;
        REELTIME = System.currentTimeMillis();
        drownTIME = System.currentTimeMillis();
        ULTIMATETIME = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Poseidon";
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
        if (DUtil.hasDeity(p, "Poseidon") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            //drown
            int radius = (int) Math.ceil(1.75772 * Math.pow(DUtil.getLevel(p), 0.4005359));
            int duration = (int) Math.round(9.91556217 * Math.pow(DUtil.getLevel(p), 0.45901927)); //seconds
            //ult
            int ultradius = (int) Math.floor(14.259344 * Math.pow(DUtil.getLevel(p), 0.25087087));
            int shiftmax = (int) Math.floor(3.3431826 * Math.pow(1.02574213, DUtil.getLevel(p)));
            int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Poseidon" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Immune to drowning.");
            p.sendMessage(":Grab an entity or block with a fishing rod. " + ChatColor.GREEN + "/reel");
            p.sendMessage(ChatColor.YELLOW + "Costs " + REELCOST + " Favor.");
            if (((Poseidon) (DUtil.getDeity(p, "Poseidon"))).REEL)
                p.sendMessage(ChatColor.AQUA + "    Reel is active.");
            p.sendMessage(":Create a temporary flood of water. " + ChatColor.GREEN + "/drown");
            p.sendMessage(ChatColor.YELLOW + "Costs " + drownCOST + " Favor.");
            p.sendMessage("Water has radius of " + radius + " for " + duration + " seconds.");
            if (((Poseidon) (DUtil.getDeity(p, "Poseidon"))).drownBIND != null)
                p.sendMessage(ChatColor.AQUA + "    drown bound to " + (((Poseidon) (DUtil.getDeity(p, "Poseidon"))).drownBIND).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Cause an earthquake centered at your location.");
            p.sendMessage("Range: " + ultradius + ". Maximum shift: " + shiftmax + ChatColor.GREEN + " /earthquake");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Poseidon");
        p.sendMessage("Passive: Immune to drowning.");
        p.sendMessage("Active: Reel in a block or entity with a fishing rod. " + ChatColor.GREEN + "/reel");
        p.sendMessage(ChatColor.YELLOW + "Costs " + REELCOST + " Favor.");
        p.sendMessage("Active: Create a temporary flood of water.");
        p.sendMessage(ChatColor.GREEN + "/drown " + ChatColor.YELLOW + "Costs " + drownCOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Cause an earthquake centered at your location.");
        p.sendMessage(ChatColor.GREEN + "/earthquake " + ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: water bucket");
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof EntityDamageEvent) {
            EntityDamageEvent e = (EntityDamageEvent) ee;
            if (e.getEntity() instanceof Player) {
                if (e.getCause() == DamageCause.DROWNING) {
                    Player p = (Player) e.getEntity();
                    if (!DUtil.isFullParticipant(p))
                        return;
                    if (!DUtil.hasDeity(p, "Poseidon"))
                        return;
                    e.setDamage(0);
                    e.setCancelled(true);
                }
            }
        } else if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Poseidon"))
                return;
            if (REEL) {
                if (p.getItemInHand().getType() == Material.FISHING_ROD) {
                    if (REELTIME > System.currentTimeMillis())
                        return;
                    if (DUtil.getFavor(p) > REELCOST) {
                        if (reel(p)) {
                            DUtil.setFavor(p, DUtil.getFavor(p) - REELCOST);
                            REELTIME = System.currentTimeMillis() + REELDELAY;
                        }
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                        REEL = false;
                    }
                }
            }
            if ((p.getItemInHand().getType() == drownBIND) || drown) {
                if (drownTIME > System.currentTimeMillis())
                    return;
                if (DUtil.getFavor(p) > drownCOST) {
                    drown(p);
                    DUtil.setFavor(p, DUtil.getFavor(p) - drownCOST);
                    drownTIME = System.currentTimeMillis() + drownDELAY;
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                    drown = false;
                }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (!DUtil.isFullParticipant(p))
            return;
        if (!DUtil.hasDeity(p, "Poseidon"))
            return;
        if (str.equalsIgnoreCase("reel")) {
            if (REEL) {
                REEL = false;
                p.sendMessage(ChatColor.YELLOW + "Reel is no longer active.");
            } else {
                REEL = true;
                p.sendMessage(ChatColor.YELLOW + "Reel is now active.");
                p.sendMessage(ChatColor.YELLOW + "It can only be used with a fishing rods.");
            }
        } else if (str.equalsIgnoreCase("drown")) {
            if (bind) {
                if (drownBIND == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        drownBIND = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Drown is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, drownBIND);
                    p.sendMessage(ChatColor.YELLOW + "Drown is no longer bound to " + drownBIND.name() + ".");
                    drownBIND = null;
                }
                return;
            }
            if (drown) {
                drown = false;
                p.sendMessage(ChatColor.YELLOW + "Drown is no longer active.");
            } else {
                drown = true;
                p.sendMessage(ChatColor.YELLOW + "Drown is now active.");
            }
        } else if (str.equalsIgnoreCase("earthquake")) {
            long TIME = ULTIMATETIME;
            if (System.currentTimeMillis() < TIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot use Earthquake again for " + ((((TIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= ULTIMATECOST) {
                int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                earthquake(p);
                p.sendMessage(ChatColor.DARK_AQUA + "Poseidon" + ChatColor.GRAY + " has caused an earthquake around you.");
                DUtil.setFavor(p, DUtil.getFavor(p) - ULTIMATECOST);
                ULTIMATETIME = System.currentTimeMillis() + t * 1000;
            } else p.sendMessage(ChatColor.YELLOW + "Earthquake requires " + ULTIMATECOST + " Favor.");
            return;
        }
    }

    private boolean reel(Player p) {
        LivingEntity le = DUtil.getTargetLivingEntity(p, 3);
        if (le != null) {
            if (DUtil.isProtectedShrine(le.getLocation())) {
                p.sendMessage(ChatColor.YELLOW + "That is a protected area.");
                return false;
            }
            Vector v = le.getLocation().toVector();
            Vector victor = p.getLocation().toVector().subtract(v);
            le.setVelocity(victor);
        } else {
            Location target = DUtil.getTargetLocation(p);
            if (DUtil.isProtectedDemigodsOnly(target)) {
                p.sendMessage(ChatColor.YELLOW + "That is a protected area");
                return false;
            }
            if ((target.getBlock().getType() == Material.BEDROCK) ||
                    (target.getBlock().getType() == Material.CHEST) ||
                    (target.getBlock().getType() == Material.FURNACE) ||
                    (target.getBlock().getType() == Material.DISPENSER) ||
                    (target.getBlock().getType() == Material.ENCHANTMENT_TABLE) ||
                    (target.getBlock().getType() == Material.STATIONARY_WATER) ||
                    (target.getBlock().getType() == Material.STATIONARY_LAVA) ||
                    (target.getBlock().getType() == Material.WATER) ||
                    (target.getBlock().getType() == Material.LAVA)) {
                p.sendMessage(ChatColor.YELLOW + "You cannot reel in this block.");
                return false;
            }
            Material type = target.getBlock().getType();
            target.getBlock().setType(Material.AIR);
            p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(type, 1));
        }
        return true;
    }

    private void drown(Player p) {
        //special values
        int radius = (int) Math.ceil(1.75772 * Math.pow(DUtil.getLevel(p), 0.4005359));
        int duration = (int) Math.round(9.91556217 * Math.pow(DUtil.getLevel(p), 0.45901927)); //seconds
        //
        Location target = DUtil.getTargetLocation(p);
        if (target == null) return;
        final ArrayList<Block> toreset = new ArrayList<Block>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = target.getWorld().getBlockAt(target.getBlockX() + x, target.getBlockY() + y, target.getBlockZ() + z);
                    if (block.getLocation().distance(target) <= radius) {
                        if ((block.getType() == Material.AIR) || (block.getType() == Material.WATER) || (block.getType() == Material.LAVA)) {
                            block.setType(Material.WATER);
                            block.setData((byte) (0x8));
                            toreset.add(block);
                        }
                    }
                }
            }
        }
        DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Block b : toreset)
                    if ((b.getType() == Material.WATER) || (b.getType() == Material.STATIONARY_WATER))
                        b.setType(Material.AIR);
            }
        }, duration * 20);
    }

    // ??
    private void earthquake(Player p) {
        //special values
        int radius = (int) Math.floor(14.259344 * Math.pow(DUtil.getLevel(p), 0.25087087));
        int shiftmax = (int) Math.floor(3.3431826 * Math.pow(1.02574213, DUtil.getLevel(p)));
        //
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = p.getWorld().getHighestBlockAt(p.getLocation().getBlockX() + x, p.getLocation().getBlockZ() + z);
                if (b.getLocation().distance(p.getLocation()) > radius) continue;
                int shiftamt = (new Random()).nextInt(shiftmax);
                if (Math.random() > 0.6)
                    shiftup(b, shiftamt, p);
                else clear(b, shiftamt);
            }
        }
    }

    private void shiftup(Block top, int shiftamt, Player p) {
        if (DUtil.isProtectedDemigodsOnly(top.getLocation()))
            return;
        ArrayList<Material> tomove = new ArrayList<Material>();
        for (LivingEntity le : top.getWorld().getLivingEntities()) {
            if ((le.getLocation().getBlockX() == top.getX()) && (le.getLocation().getBlockZ() == top.getZ())
                    && (le.getLocation().getBlockY() > top.getY() - shiftamt)) {
                Location loc = le.getLocation();
                loc.setY(le.getLocation().getY() + shiftamt);
                le.teleport(loc);
                if (le instanceof Player) {
                    Player P = (Player) le;
                    if (DUtil.isFullParticipant(P) && DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getAllegiance(P)))
                        continue;
                }
                le.damage((int) le.getLocation().distance(loc), p);
            }
        }
        for (int i = 0; i < shiftamt; i++) {
            tomove.add(top.getType());
            top = top.getRelative(BlockFace.DOWN);
        }
        int dy = 0;
        for (Material b : tomove) {
            top.getWorld().getBlockAt(top.getX(), top.getY() + shiftamt - dy, top.getZ()).setType(b);
            dy++;
        }
    }

    private void clear(Block top, int depth) {
        if (DUtil.isProtectedDemigodsOnly(top.getLocation()))
            return;
        Material original = top.getType();
        for (int i = 0; i < depth; i++) {
            top.setType(Material.AIR);
            top = top.getRelative(BlockFace.DOWN);
        }
        if (Math.random() > 0.5)
            top.setType(original);
    }
}

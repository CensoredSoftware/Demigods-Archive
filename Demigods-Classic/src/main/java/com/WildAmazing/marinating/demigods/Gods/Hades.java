package com.WildAmazing.marinating.demigods.Gods;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Set;

public class Hades implements Deity {

    /* General */
    private static final long serialVersionUID = 3647481847975286534L;
    private final int CHAINCOST = 100;
    private final int CHAINDELAY = 1500;
    private final int ENTOMBCOST = 100;
    private final int ENTOMBDELAY = 2000;
    private final int ULTIMATECOST = 1800;
    private final int ULTIMATECOOLDOWNMAX = 1000;
    private final int ULTIMATECOOLDOWNMIN = 420;

    /* Specific to player */
    private String PLAYER;
    private boolean CHAIN = false;
    private boolean ENTOMB = false;
    private long CHAINTIME, ENTOMBTIME, ULTIMATETIME;
    private Material CHAINBIND = null;
    private Material ENTOMBBIND = null;

    public Hades(String name) {
        PLAYER = name;
        CHAINTIME = System.currentTimeMillis();
        ENTOMBTIME = System.currentTimeMillis();
        ULTIMATETIME = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Hades";
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
        if (DUtil.hasDeity(p, "Hades") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            //chain
            int damage = DUtil.getLevel(p) / 10 + 2;
            int blindpower = DUtil.getLevel(p) / 5 + 1;
            int blindduration = (int) (1.8504557 * Math.pow(DUtil.getLevel(p), 0.5944279));
            //entomb
            int duration = (int) (4.4154629 * Math.pow(DUtil.getLevel(p), 0.4704706));
            //ult
            int ultrange = (int) (8.829033 * Math.pow(DUtil.getLevel(p), 0.402008));
            int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Hades" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Immune to skeleton and zombie attacks.");
            p.sendMessage(":Entomb an entity in obsidian. " + ChatColor.GREEN + "/entomb");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ENTOMBCOST + " Favor.");
            p.sendMessage("Duration: " + duration + " seconds.");
            if (((Hades) (DUtil.getDeity(p, "Hades"))).ENTOMBBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + (((Hades) (DUtil.getDeity(p, "Hades"))).ENTOMBBIND).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Fire a chain of smoke, causing damage and darkness. " + ChatColor.GREEN + "/chain");
            p.sendMessage(ChatColor.YELLOW + "Costs " + CHAINCOST + " Favor.");
            p.sendMessage(damage + " damage, causes level " + blindpower + " darkness for " + blindduration + " seconds.");
            if (((Hades) (DUtil.getDeity(p, "Hades"))).CHAINBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Chain bound to " + (((Hades) (DUtil.getDeity(p, "Hades"))).CHAINBIND).name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Drop nearby enemies to Tartarus.");
            p.sendMessage("Range: " + ultrange + ". " + ChatColor.GREEN + "/tartarus");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Hades");
        p.sendMessage("Passive: Immune to skeleton and zombie attacks.");
        p.sendMessage("Active: Entomb an entity in obsidian. " + ChatColor.GREEN + "/entomb");
        p.sendMessage(ChatColor.YELLOW + "Costs " + ENTOMBCOST + " Favor. Can bind.");
        p.sendMessage("Active: Fire a chain of smoke that damages and blinds.");
        p.sendMessage(ChatColor.GREEN + "/chain " + ChatColor.YELLOW + "Costs " + CHAINCOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Drop nearby enemies to Tartarus.");
        p.sendMessage(ChatColor.GREEN + "/tartarus " + ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: bone");
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Hades"))
                return;
            if (CHAIN || ((CHAINBIND != null) && (p.getItemInHand().getType() == CHAINBIND))) {
                if (System.currentTimeMillis() < CHAINTIME)
                    return;
                if (DUtil.getFavor(p) >= CHAINCOST) {
                    int damage = DUtil.getLevel(p) / 10 + 2;
                    int blindpower = DUtil.getLevel(p) / 5 + 1;
                    int blindduration = (int) (1.8504557 * Math.pow(DUtil.getLevel(p), 0.5944279));
                    chain(p.getTargetBlock((Set) null, 200).getLocation(), p.getEyeLocation(), 100, damage, blindpower, blindduration, p);
                    CHAINTIME = System.currentTimeMillis() + CHAINDELAY;
                    DUtil.setFavor(p, DUtil.getFavor(p) - CHAINCOST);
                } else {
                    CHAIN = false;
                    p.sendMessage(ChatColor.YELLOW + "You don't have enough Favor to do that.");
                }
            }
            if (ENTOMB || ((ENTOMBBIND != null) && (p.getItemInHand().getType() == ENTOMBBIND))) {
                if (System.currentTimeMillis() < ENTOMBTIME)
                    return;
                if ((DUtil.getFavor(p) >= ENTOMBCOST)) {
                    if (entomb(p)) {
                        ENTOMBTIME = System.currentTimeMillis() + ENTOMBDELAY;
                        DUtil.setFavor(p, DUtil.getFavor(p) - ENTOMBCOST);
                    } else
                        p.sendMessage(ChatColor.YELLOW + "No target found or area is protected.");
                } else {
                    ENTOMB = false;
                    p.sendMessage(ChatColor.YELLOW + "You don't have enough Favor to do that.");
                }
            }
        } else if (ee instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
            if (!(e.getEntity() instanceof Player))
                return;
            Player p = (Player) e.getEntity();
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.hasDeity(p, "Hades"))
                return;
            if ((e.getDamager() instanceof Zombie) || (e.getDamager() instanceof Skeleton)) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        } else if (ee instanceof EntityTargetEvent) {
            EntityTargetEvent e = (EntityTargetEvent) ee;
            if (e.getEntity() instanceof LivingEntity) {
                if (((LivingEntity) e.getEntity() instanceof Zombie) || ((LivingEntity) e.getEntity() instanceof Skeleton))
                    if (e.getTarget() instanceof Player) {
                        Player p = (Player) e.getTarget();
                        if (!DUtil.isFullParticipant(p))
                            return;
                        if (!DUtil.hasDeity(p, "Hades"))
                            return;
                        e.setTarget(null);
                        e.setCancelled(true);
                    }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (!DUtil.isFullParticipant(p))
            return;
        if (!DUtil.hasDeity(p, "Hades"))
            return;
        if (str.equalsIgnoreCase("chain")) {
            if (bind) {
                if (CHAINBIND == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        CHAINBIND = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Dark chain is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, CHAINBIND);
                    p.sendMessage(ChatColor.YELLOW + "Dark chain is no longer bound to " + CHAINBIND.name() + ".");
                    CHAINBIND = null;
                }
                return;
            }
            if (CHAIN) {
                CHAIN = false;
                p.sendMessage(ChatColor.YELLOW + "Dark chain is no longer active.");
            } else {
                CHAIN = true;
                p.sendMessage(ChatColor.YELLOW + "Dark chain is now active.");
            }
        } else if (str.equalsIgnoreCase("entomb")) {
            if (bind) {
                if (ENTOMBBIND == null) {
                    if (DUtil.isBound(p, p.getItemInHand().getType()))
                        p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                    if (p.getItemInHand().getType() == Material.AIR)
                        p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                    else {
                        DUtil.registerBind(p, p.getItemInHand().getType());
                        ENTOMBBIND = p.getItemInHand().getType();
                        p.sendMessage(ChatColor.YELLOW + "Entomb is now bound to " + p.getItemInHand().getType().name() + ".");
                    }
                } else {
                    DUtil.removeBind(p, ENTOMBBIND);
                    p.sendMessage(ChatColor.YELLOW + "Entomb is no longer bound to " + ENTOMBBIND.name() + ".");
                    ENTOMBBIND = null;
                }
                return;
            }
            if (ENTOMB) {
                ENTOMB = false;
                p.sendMessage(ChatColor.YELLOW + "Entomb is no longer active.");
            } else {
                ENTOMB = true;
                p.sendMessage(ChatColor.YELLOW + "Entomb is now active.");
            }
        } else if (str.equalsIgnoreCase("tartarus")) {
            long TIME = ULTIMATETIME;
            if (System.currentTimeMillis() < TIME) {
                p.sendMessage(ChatColor.YELLOW + "You cannot use Tartarus again for " + ((((TIME) / 1000) -
                        (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                return;
            }
            if (DUtil.getFavor(p) >= ULTIMATECOST) {
                int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) *
                        ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                int amt = tartarus(p);
                if (amt > 0) {
                    p.sendMessage(ChatColor.DARK_RED + "Hades" + ChatColor.GRAY + " cackles as " + amt + " enemies fall to Tartarus.");
                    DUtil.setFavor(p, DUtil.getFavor(p) - ULTIMATECOST);
                    ULTIMATETIME = System.currentTimeMillis() + t * 1000;
                } else p.sendMessage(ChatColor.YELLOW + "There were no valid targets.");
            } else p.sendMessage(ChatColor.YELLOW + "Tartarus requires " + ULTIMATECOST + " Favor.");
            return;
        }
    }

    private void chain(Location target, Location current, int attempts, int damage, int blindpower, int blindduration, Player source) {
        if (!target.getWorld().equals(current.getWorld()))
            return;
        if ((current.distance(target) < 1) || (attempts == 0)) {
            for (LivingEntity le : target.getWorld().getLivingEntities()) {
                if (le.getLocation().distance(current) < 2.5) {
                    le.damage(damage, source);
                    if (le instanceof Player) {
                        Player p = (Player) le;
                        if (!DUtil.isFullParticipant(p) || (DUtil.isFullParticipant(p) && !DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getAllegiance(source)))) {
                            final Player pt = p;
                            final PotionEffect dark = new PotionEffect(PotionEffectType.BLINDNESS, blindduration * 20, blindpower);
                            pt.addPotionEffect(dark);
                            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    pt.removePotionEffect(PotionEffectType.BLINDNESS);
                                }
                            }, blindduration * 20);
                        }
                    }
                }
            }
            return;
        }
        current.getWorld().playEffect(current, Effect.SMOKE, 4);
        BlockFace lowest = BlockFace.SELF;
        double distanceaway = Double.MAX_VALUE;
        for (BlockFace bf : BlockFace.values()) {
            if (current.getBlock().getRelative(bf).getLocation().distance(target) < distanceaway) {
                lowest = bf;
                distanceaway = current.getBlock().getRelative(bf).getLocation().distance(target);
            }
        }
        chain(target, current.getBlock().getRelative(lowest).getLocation(), attempts - 1, damage, blindpower, blindduration, source);
    }

    private boolean entomb(Player p) {
        int duration = (int) (4.4154629 * Math.pow(DUtil.getLevel(p), 0.4704706)); //seconds
        LivingEntity le = DUtil.getTargetLivingEntity(p, 2);
        if (le == null)
            return false;
        if (DUtil.isProtectedShrine(le.getLocation()))
            return false;
        final ArrayList<Block> tochange = new ArrayList<Block>();
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    Block block = p.getWorld().getBlockAt(le.getLocation().getBlockX() + x, le.getLocation().getBlockY() + y,
                            le.getLocation().getBlockZ() + z);
                    if ((block.getLocation().distance(le.getLocation()) > 2) && (block.getLocation().distance(le.getLocation()) < 3.5))
                        if ((block.getType() == Material.AIR) || (block.getType() == Material.WATER) || (block.getType() == Material.LAVA)) {
                            block.setType(Material.OBSIDIAN);
                            tochange.add(block);
                        }
                }
            }
        }
        DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Block b : tochange)
                    if (b.getType() == Material.OBSIDIAN)
                        b.setType(Material.AIR);
            }
        }, duration * 20);
        return true;
    }

    private int tartarus(Player p) {
        int range = (int) (8.829033 * Math.pow(DUtil.getLevel(p), 0.402008));
        ArrayList<Entity> entitylist = new ArrayList<Entity>();
        Vector ploc = p.getLocation().toVector();
        for (Entity anEntity : p.getWorld().getEntities()) {
            if (anEntity.getLocation().toVector().isInSphere(ploc, range))
                entitylist.add(anEntity);
        }
        int count = 0;
        for (Entity eee : entitylist) {
            try {
                LivingEntity e1 = (LivingEntity) eee;
                if (e1 instanceof Player) {
                    Player ptemp = (Player) e1;
                    if (!DUtil.isGod(ptemp) && !ptemp.equals(p) && (ptemp.getLocation().toVector().distance(p.getLocation().toVector()) > 2)) {
                        drop(ptemp);
                        count++;
                    }
                } else {
                    if (e1.getLocation().toVector().distance(p.getLocation().toVector()) > 2) {
                        count++;
                        drop(e1);
                    }
                }
            } catch (Exception notAlive) {
            } //ignore stuff like minecarts
        }
        return count;
    }

    private void drop(LivingEntity e) {
        if (DUtil.getShrineInvincible(e.getLocation()) != null)
            return;
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
        e.setVelocity(new Vector(0, -10, 0));
        DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (BlockState bs : fix) {
                    Block inQuestion = bs.getWorld().getBlockAt(new Location(bs.getWorld(), bs.getX(), bs.getY(), bs.getZ()));
                    inQuestion.setData(bs.getRawData());
                    inQuestion.setType(bs.getType());
                }
            }
        }, 120);
    }
}

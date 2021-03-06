package com.clashnia.Demigods.Deities.Giants;

import com.WildAmazing.marinating.Demigods.DFixes;
import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Util.DSave;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Typhon implements Deity {
    private static final long serialVersionUID = -7376781567872708495L;

    private final String PLAYER;

    private static final int SKILLCOST = 120;
    private static final int SKILLDELAY = 1250; // milliseconds
    private static final int EXPLOSIONSIZE = 3;
    private boolean SKILL = false;

    private final Material SKILLBIND = null;
    private long SKILLTIME;
    private long LASTCHECK;

    public Typhon(String name) {
        PLAYER = name;
        SKILLTIME = System.currentTimeMillis();
        LASTCHECK = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Typhon";
    }

    @Override
    public String getPlayerName() {
        return PLAYER;
    }

    @Override
    public String getDefaultAlliance() {
        return "Giant";
    }

    @Override
    public void printInfo(Player p) {
        if (DMiscUtil.isFullParticipant(p) && DMiscUtil.hasDeity(p, getName())) {
            int devotion = DMiscUtil.getDevotion(p, getName());
            p.sendMessage("--" + ChatColor.GOLD + getName() + ChatColor.GRAY + "[" + devotion + "]");
            return;
        }
        p.sendMessage("--" + getName());
        p.sendMessage("Passive: Explodes on death caused by PvP.");
        p.sendMessage("Active: /CHARGE - Knockback increased.");
        p.sendMessage(ChatColor.YELLOW + "Select item: gunpowder");
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DMiscUtil.isFullParticipant(p) || !DMiscUtil.hasDeity(p, getName())) return;
            if (SKILL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SKILLBIND))) {
                if (SKILLTIME > System.currentTimeMillis()) return;
                SKILLTIME = System.currentTimeMillis() + SKILLDELAY;
                if (DMiscUtil.getFavor(p) >= SKILLCOST) {
                    /*
					 * Skill
					 */
                    DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - SKILLCOST);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                    SKILL = false;
                }
            }
        } else if (ee instanceof EntityDeathEvent) {
            EntityDeathEvent e = (EntityDeathEvent) ee;
            if (e.getEntity() instanceof Player) {
                EntityDamageEvent ede = e.getEntity().getLastDamageCause();
                DamageCause dc = ede.getCause();
                if (dc == DamageCause.ENTITY_ATTACK || dc == DamageCause.SUICIDE) {
                    Player p = (Player) e.getEntity();
                    if (!DMiscUtil.hasDeity(p, "Typhon")) return;
                    if (DMiscUtil.canWorldGuardPVP(p.getLocation()) && (DMiscUtil.getNearbyShrine(e.getEntity().getLocation()) == null || !DMiscUtil.getDeityAtShrine(DMiscUtil.getNearbyShrine(e.getEntity().getLocation())).equals("Typhon")))
                        p.getWorld().createExplosion(p.getLocation(), EXPLOSIONSIZE);
                }
            }
        } else if (ee instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
            if (e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                Entity attacked = e.getEntity();
                if (!DMiscUtil.canTarget(p, p.getLocation())) return;
                if (!DMiscUtil.canTarget(attacked, attacked.getLocation())) return;
                if (!DMiscUtil.hasDeity(p, "Typhon")) return;
                LivingEntity le = (LivingEntity) e.getEntity();
                Vector v = p.getLocation().toVector();
                Vector victor = le.getLocation().toVector().subtract(v);

                if (DSave.hasData(p, "CHARGE")) {
                    if (DSave.getData(p, "CHARGE") != null) {
                        long STARTTIME = (Long) DSave.getData(p, "CHARGE");
                        long ENDTIME = System.currentTimeMillis();
                        int PLAYERPUSH = (int) ((ENDTIME - STARTTIME) / 5000);
                        int ITEMDAMAGE = 1;
                        ItemStack hand = p.getItemInHand();
                        if (hand == null) ITEMDAMAGE = 1;
                        else if (hand.getType() == Material.WOOD_SWORD || hand.getType() == Material.GOLD_SWORD)
                            ITEMDAMAGE = 4;
                        else if (hand.getType() == Material.STONE_SWORD) ITEMDAMAGE = 5;
                        else if (hand.getType() == Material.IRON_SWORD) ITEMDAMAGE = 6;
                        else if (hand.getType() == Material.DIAMOND_SWORD) ITEMDAMAGE = 7;
                        int DAMAGE = (ITEMDAMAGE * PLAYERPUSH);
                        if (DAMAGE > 1025) DAMAGE = 1025;
                        victor.multiply(PLAYERPUSH);
                        if (le instanceof Player) {
                            Player pl = (Player) le;
                            if (DMiscUtil.isFullParticipant(pl)) {
                                if (e.getEntity().isDead()) return;
                            }
                        }
                        le.setVelocity(victor); // super kb
                        DMiscUtil.damageDemigods(p, le, DAMAGE, DamageCause.ENTITY_ATTACK);
                        DFixes.checkAndCancel(e);
                        DSave.removeData(p, "CHARGE");
                        p.sendMessage(ChatColor.YELLOW + "Your charged attack has dealt " + ChatColor.DARK_RED + DAMAGE + ChatColor.YELLOW + " damage.");
                    } else {
                        DSave.removeData(p, "CHARGE");
                    }
                }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (DMiscUtil.hasDeity(p, getName())) {
            if (str.equalsIgnoreCase("charge")) {
                if (args.length >= 1) {
                    // STOP CHARGE
                    if (args[0].equals("stop")) {
                        if (DSave.hasData(p, "CHARGE")) {
                            DSave.removeData(p, "CHARGE");
                            p.sendMessage(ChatColor.YELLOW + "You are no longer charging.");
                        }
                        return;
                    }
                }
                if (!DSave.hasData(p, "CHARGE")) {
                    DSave.saveData(p, "CHARGE", System.currentTimeMillis());
                    p.sendMessage(ChatColor.YELLOW + "You are now charging your attack.");
                } else {
                    long STARTTIME = (Long) DSave.getData(p, "CHARGE");
                    int FAKEDAMAGE = (int) ((System.currentTimeMillis() - STARTTIME) / 5000);
                    int ITEMDAMAGE = 1;
                    String ExtraDamage = ChatColor.DARK_GREEN + " * ";
                    ItemStack hand = p.getItemInHand();
                    if (hand == null) {
                        // Prevents NullPointerException.
                    } else if (hand.getType() == Material.WOOD_SWORD) {
                        ITEMDAMAGE = 4;
                        ExtraDamage = ChatColor.YELLOW + " * ";
                    } else if (hand.getType() == Material.GOLD_SWORD) {
                        ITEMDAMAGE = 4;
                        ExtraDamage = ChatColor.GOLD + " * ";
                    } else if (hand.getType() == Material.STONE_SWORD) {
                        ITEMDAMAGE = 5;
                        ExtraDamage = ChatColor.DARK_GRAY + " * ";
                    } else if (hand.getType() == Material.IRON_SWORD) {
                        ITEMDAMAGE = 6;
                        ExtraDamage = ChatColor.GRAY + " * ";
                    } else if (hand.getType() == Material.DIAMOND_SWORD) {
                        ITEMDAMAGE = 7;
                        ExtraDamage = ChatColor.AQUA + " * ";
                    }

                    int TOTALFAKEDAMAGE = FAKEDAMAGE * ITEMDAMAGE;

                    if (TOTALFAKEDAMAGE > 1025) {
                        FAKEDAMAGE = (1025 / ITEMDAMAGE);
                        TOTALFAKEDAMAGE = 1025;
                    }

                    p.sendMessage(ChatColor.DARK_GREEN + "Charged Damage: " + ChatColor.RED + FAKEDAMAGE + ExtraDamage + ITEMDAMAGE + ChatColor.DARK_GREEN + " = " + ChatColor.DARK_RED + TOTALFAKEDAMAGE);
                }
            }
        }
    }

    @Override
    public void onTick(long timeSent) {
        if (timeSent > LASTCHECK + 1000) {
            LASTCHECK = timeSent;
        }
    }
}

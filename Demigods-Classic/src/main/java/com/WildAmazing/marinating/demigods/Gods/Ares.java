package com.WildAmazing.marinating.demigods.Gods;

import com.WildAmazing.marinating.demigods.Utilities.DUtil;
import com.WildAmazing.marinating.demigods.Utilities.Deity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/*
 * Affected by level:
 * Amount of EXP gained for overkill
 * Damage, range, slow amount, slow duration of strike
 * Duration of bloodthirst
 * Range, damage, and cooldown of ultimate
 */

public class Ares implements Deity {
    private static final long serialVersionUID = -5825867521620334951L;
    private String PLAYER;
    /*
     * Needs to be loaded out of config
     */
    private static final int STRIKECOST = 25;
    private static final int STRIKEDELAY = 1250; //milliseconds
    private static final int ARESULTIMATECOST = 500;
    private static final int ARESULTIMATECOOLDOWNMAX = 180; //seconds
    private static final int ARESULTIMATECOOLDOWNMIN = 60;

    private boolean STRIKE = false;
    private Material STRIKEBIND = null;
    private long STRIKETIME;
    private long ARESULTIMATETIME;

    public Ares(String player) {
        PLAYER = player;
        ARESULTIMATETIME = System.currentTimeMillis();
        STRIKETIME = System.currentTimeMillis();
    }

    @Override
    public String getDefaultAlliance() {
        return "God";
    }

    @Override
    public void printInfo(Player p) {
        if (DUtil.hasDeity(p, "Ares") && DUtil.isFullParticipant(p)) {
            /*
             * Calculate special values first
			 */
            int dmg = (int) (2.14 * Math.pow(1.04, DUtil.getLevel(p)));
            final int slowpower = (int) (6 * (double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap());
            int duration = (int) (Math.round(17.627 * Math.pow(1.0279, DUtil.getLevel(p))));
            int targets = (int) Math.ceil(3.08 * (Math.pow(1.05, DUtil.getLevel(p))));
            int range = (int) Math.ceil(7.17 * Math.pow(1.035, DUtil.getLevel(p)));
            int damage = (int) Math.ceil(1.929 * Math.pow(DUtil.getLevel(p), 0.48028));
            int confuseduration = (int) (1.0354 * Math.pow(DUtil.getLevel(p), 0.4177)) * 20;
            int t = (int) (ARESULTIMATECOOLDOWNMAX - ((ARESULTIMATECOOLDOWNMAX - ARESULTIMATECOOLDOWNMIN) *
                    ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
			/*
			 * The printed text
			 */
            p.sendMessage("--" + ChatColor.GOLD + "Ares" + ChatColor.GRAY + "[" + DUtil.getLevel(p) + "]");
            p.sendMessage(":Up to " + DUtil.getLevel(p) + " additional Favor per hit on overkill.");
            p.sendMessage(":Strike an enemy from afar with your sword, slowing them down.");
            p.sendMessage("Slow: " + slowpower + " for " + duration + " seconds. Damage: " +
                    dmg + ChatColor.GREEN + " /strike " + ChatColor.YELLOW + "Costs " + STRIKECOST + " Favor.");
            if (STRIKEBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + STRIKEBIND.name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage(":Ares flings up to " + targets + " targets within range " + range + " to you, dealing");
            p.sendMessage(damage + " damage to each and confusing them for " + confuseduration + " seconds." + ChatColor.GREEN + " /crash");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ARESULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + "Ares");
        p.sendMessage("Passive: Gain favor for overkill attacks.");
        p.sendMessage("Active: Strike at an enemy from afar with your sword, with");
        p.sendMessage("a slowing effect. " + ChatColor.GREEN + "/strike");
        p.sendMessage(ChatColor.YELLOW + "Costs " + STRIKECOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Ares flings nearby enemies towards you. Damages and");
        p.sendMessage("confuses targets. " + ChatColor.GREEN + "/crash");
        p.sendMessage(ChatColor.YELLOW + "Costs " + ARESULTIMATECOST + " Favor. Has cooldown.");
        p.sendMessage(ChatColor.YELLOW + "Select item: gold sword");
    }

    @Override
    public String getName() {
        return "Ares";
    }

    @Override
    public String getPlayerName() {
        return PLAYER;
    }

    @Override
    public void onEvent(Event ee) {
        if (ee instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) ee;
            Player p = e.getPlayer();
            if (!DUtil.hasDeity(p, "Ares") || !DUtil.isFullParticipant(p))
                return;
            if ((p.getItemInHand() != null) && p.getItemInHand().getType().name().contains("SWORD")) {
                if (STRIKE || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == STRIKEBIND))) {
                    if (STRIKETIME > System.currentTimeMillis())
                        return;
                    STRIKETIME = System.currentTimeMillis() + STRIKEDELAY;
                    if (DUtil.getFavor(p) >= STRIKECOST) {
                        strike(p);
                        DUtil.setFavor(p, DUtil.getFavor(p) - STRIKECOST);
                        return;
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                        STRIKE = false;
                    }
                }
            }
        } else if (ee instanceof EntityDamageByEntityEvent) {
            try {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ee;
                if (e.getDamager() instanceof Player) {
                    Player p = (Player) e.getDamager();
                    if (!DUtil.hasDeity(p, "Ares") || !DUtil.isFullParticipant(p))
                        return;
                    try {
                        LivingEntity le = (LivingEntity) e.getEntity();
                        if (le.getHealth() - e.getDamage() <= 0) {
                            if ((int) (Math.random() * 3) == 1) {
                                int reward = 1 + (int) (Math.random() * DUtil.getLevel(p));
                                p.sendMessage(ChatColor.RED + "Finishing bonus: +" + reward);
                                DUtil.setFavor(p, DUtil.getFavor(p) + reward);
                            }
                        }
                    } catch (Exception notliving) {
                    }
                }
            } catch (Exception notthatevent) {
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
        if (DUtil.hasDeity(p, "Ares")) {
            if (str.equalsIgnoreCase("strike")) {
                if (bind) {
                    if (STRIKEBIND == null) {
                        if (DUtil.isBound(p, p.getItemInHand().getType()))
                            p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                        if (p.getItemInHand().getType() == Material.AIR)
                            p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                        if (!p.getItemInHand().getType().name().contains("SWORD"))
                            p.sendMessage(ChatColor.YELLOW + "You must bind this skill to a sword.");
                        else {
                            DUtil.registerBind(p, p.getItemInHand().getType());
                            STRIKEBIND = p.getItemInHand().getType();
                            p.sendMessage(ChatColor.YELLOW + "Strike is now bound to " + p.getItemInHand().getType().name() + ".");
                        }
                    } else {
                        DUtil.removeBind(p, STRIKEBIND);
                        p.sendMessage(ChatColor.YELLOW + "Strike is no longer bound to " + STRIKEBIND.name() + ".");
                        STRIKEBIND = null;
                    }
                    return;
                }
                if (STRIKE) {
                    STRIKE = false;
                    p.sendMessage(ChatColor.YELLOW + "Strike is no longer active.");
                } else {
                    STRIKE = true;
                    p.sendMessage(ChatColor.YELLOW + "Strike is now active.");
                }
            } else if (str.equalsIgnoreCase("crash")) {
                long TIME = ARESULTIMATETIME;
                if (System.currentTimeMillis() < TIME) {
                    p.sendMessage(ChatColor.YELLOW + "You cannot use the power crash again for " + ((((TIME) / 1000) -
                            (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                    p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                    return;
                }
                if (DUtil.getFavor(p) >= ARESULTIMATECOST) {
                    int t = (int) (ARESULTIMATECOOLDOWNMAX - ((ARESULTIMATECOOLDOWNMAX - ARESULTIMATECOOLDOWNMIN) *
                            ((double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap())));
                    ARESULTIMATETIME = System.currentTimeMillis() + (t * 1000);
                    p.sendMessage("In exchange for " + ChatColor.AQUA + ARESULTIMATECOST + ChatColor.WHITE + " Favor, ");
                    p.sendMessage(ChatColor.GOLD + "Ares" + ChatColor.WHITE + " has unleashed his powers on " + crash(p) + " non-allied entities.");
                    DUtil.setFavor(p, DUtil.getFavor(p) - ARESULTIMATECOST);
                } else p.sendMessage(ChatColor.YELLOW + "Power crash requires " + ARESULTIMATECOST + " Favor.");
                return;
            }
        }
    }

    /*
     * ---------------
     * Helper methods
     * ---------------
     */
    private boolean strike(Player p) {
        LivingEntity target = DUtil.getTargetLivingEntity(p, 2);
        if (target == null) {
            p.sendMessage(ChatColor.YELLOW + "No target found.");
            return false;
        }
		/*
		 * Calculate special values
		 */
        int damage = (int) (2.14 * Math.pow(1.04, DUtil.getLevel(p)));
        final int slowpower = (int) (6 * (double) DUtil.getLevel(p) / DUtil.getPlugin().getLevelCap());
        int duration = (int) (Math.round(17.627 * Math.pow(1.0279, DUtil.getLevel(p))));
		/*
		 * Deal damage and slow if player
		 */
        target.damage(damage, p);
        if (target instanceof Player) {
            final Player pl = (Player) target;
            pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, slowpower));
            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    pl.removePotionEffect(PotionEffectType.SLOW);
                }
            }, duration);
        }
        return true;
    }

    private int crash(Player p) {
		/*
		 * Calculate specials.
		 * Range: distance in a circle
		 * Damage: done instantly
		 * Confusion: how long players remain dizzied
		 */
        int range = (int) (7.17 * Math.pow(1.035, DUtil.getLevel(p)));
        int damage = (int) (1.929 * Math.pow(DUtil.getLevel(p), 0.48028));
        int confuseduration = (int) (1.0354 * Math.pow(DUtil.getLevel(p), 0.4177)) * 20;
		/*
		 * The ultimate
		 */
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        ArrayList<Player> confuse = new ArrayList<Player>();
        for (LivingEntity le : p.getWorld().getLivingEntities()) {
            if (le.getLocation().distance(p.getLocation()) <= range) {
                if (le instanceof Player) {
                    Player pt = (Player) le;
                    if (DUtil.isGod(pt) || pt.equals(p))
                        continue;
                    targets.add(le);
                    confuse.add(pt);
                } else targets.add(le);
            }
        }
        if (targets.size() > 0) {
            for (LivingEntity le : targets) {
                Vector v = le.getLocation().toVector();
                Vector victor = p.getLocation().toVector().subtract(v);
                le.setVelocity(victor);
                le.damage(damage);
            }
        }
        if (confuse.size() > 0) {
            for (Player pl : confuse) {
                pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 4));
                final Player p1 = pl;
                DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        p1.removePotionEffect(PotionEffectType.CONFUSION);
                    }
                }, confuseduration);
            }
        }
        return targets.size();
    }
}

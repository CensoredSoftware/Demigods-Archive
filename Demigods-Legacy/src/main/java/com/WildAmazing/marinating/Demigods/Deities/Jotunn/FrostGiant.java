package com.WildAmazing.marinating.Demigods.Deities.Jotunn;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class FrostGiant implements Deity {
    private static final long serialVersionUID = -2472769863144336856L;
    private final UUID PLAYER;

    private static final String skillname = "Ice";
    private static final int SKILLCOST = 225;
    private static final int SKILLDELAY = 2000; // milliseconds
    private static final int ULTIMATECOST = 2000;
    private static final int ULTIMATECOOLDOWNMAX = 700; // seconds
    private static final int ULTIMATECOOLDOWNMIN = 400;

    private boolean SKILL = false;
    private Material SKILLBIND = null;
    private long SKILLTIME;
    private long ULTIMATETIME;
    private long LASTCHECK;

    public FrostGiant(UUID player) {
        PLAYER = player;
        SKILLTIME = System.currentTimeMillis();
        ULTIMATETIME = System.currentTimeMillis();
        LASTCHECK = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return "Frost Giant";
    }

    @Override
    public UUID getPlayerId() {
        return PLAYER;
    }

    @Override
    public String getDefaultAlliance() {
        return "Jotunn";
    }

    @Override
    public void printInfo(Player p) {
        if (DMiscUtil.isFullParticipant(p) && DMiscUtil.hasDeity(p, getName())) {
            // heal amount
            int healamt = (int) Math.ceil(0.1 * Math.pow(10000, 0.297));
            // heal interval
            int healinterval = 10 - (int) (Math.round(Math.pow(10000, 0.125))); // seconds
            if (healinterval < 1) healinterval = 1;
            // squid radius
            float radius = 1 + (int) Math.round(Math.pow(10000, 0.1142));
            // ult
            int duration = (int) Math.round(40 * Math.pow(10000, 0.15)); // seconds
            int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / DMiscUtil.ASCENSIONCAP)));
            /*
             * Print text
			 */
            p.sendMessage("--" + ChatColor.GOLD + getName());
            p.sendMessage(":While in the snow, heal " + healamt + " HP every " + healinterval + " seconds.");
            p.sendMessage(":Left-click to throw an ice bomb that explodes with " + radius + " radius." + ChatColor.GREEN + " /ice " + ChatColor.YELLOW + "Costs " + SKILLCOST + " Favor.");
            if (((FrostGiant) DMiscUtil.getDeity(p, getName())).SKILLBIND != null)
                p.sendMessage(ChatColor.AQUA + "    Bound to " + ((FrostGiant) DMiscUtil.getDeity(p, getName())).SKILLBIND.name());
            else p.sendMessage(ChatColor.AQUA + "    Use /bind to bind this skill to an item.");
            p.sendMessage("Your divine frost causes a snowstorm lasting " + duration + " seconds, transforming the nearby land into tundra." + ChatColor.GREEN + " /chill");
            p.sendMessage(ChatColor.YELLOW + "Costs " + ULTIMATECOST + " Favor. Cooldown time: " + t + " seconds.");
            return;
        }
        p.sendMessage("--" + ChatColor.GOLD + getName());
        p.sendMessage("Passive: Increased healing while in snow.");
        p.sendMessage("Active: Launch an ice bomb at the target location. " + ChatColor.GREEN + "/ice");
        p.sendMessage(ChatColor.YELLOW + "Costs " + SKILLCOST + " Favor. Can bind.");
        p.sendMessage("Ultimate: Cause a snowstorm in the current world, transforming nearby land into tundra." + ChatColor.GREEN + "/chill");
        p.sendMessage(ChatColor.YELLOW + "Select item: bottle of water");
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
                    if (iceSpawn(p)) DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - SKILLCOST);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                    SKILL = false;
                }
            }
        }
    }

    @Override
    public void onCommand(Player P, String str, String[] args, boolean bind) {
        final Player p = P;
        if (DMiscUtil.hasDeity(p, getName())) {
            if (str.equalsIgnoreCase(skillname)) {
                if (bind) {
                    if (SKILLBIND == null) {
                        if (DMiscUtil.isBound(p, p.getItemInHand().getType()))
                            p.sendMessage(ChatColor.YELLOW + "That item is already bound to a skill.");
                        if (p.getItemInHand().getType() == Material.AIR)
                            p.sendMessage(ChatColor.YELLOW + "You cannot bind a skill to air.");
                        else {
                            DMiscUtil.registerBind(p, p.getItemInHand().getType());
                            SKILLBIND = p.getItemInHand().getType();
                            p.sendMessage(ChatColor.YELLOW + "" + skillname + " is now bound to " + p.getItemInHand().getType().name() + ".");
                        }
                    } else {
                        DMiscUtil.removeBind(p, SKILLBIND);
                        p.sendMessage(ChatColor.YELLOW + "" + skillname + " is no longer bound to " + SKILLBIND.name() + ".");
                        SKILLBIND = null;
                    }
                    return;
                }
                if (SKILL) {
                    SKILL = false;
                    p.sendMessage(ChatColor.YELLOW + "" + skillname + " is no longer active.");
                } else {
                    SKILL = true;
                    p.sendMessage(ChatColor.YELLOW + "" + skillname + " is now active.");
                }
            } else if (str.equalsIgnoreCase("chill")) {
                long TIME = ULTIMATETIME;
                if (System.currentTimeMillis() < TIME) {
                    p.sendMessage(ChatColor.YELLOW + "You cannot use chill again for " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000))) / 60 + " minutes");
                    p.sendMessage(ChatColor.YELLOW + "and " + ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                    return;
                }
                if (DMiscUtil.getFavor(p) >= ULTIMATECOST) {
                    int t = (int) (ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN) * ((double) DMiscUtil.getAscensions(p) / DMiscUtil.ASCENSIONCAP)));
                    ULTIMATETIME = System.currentTimeMillis() + (t * 1000);
                    int x = p.getLocation().getChunk().getX(), z = p.getLocation().getChunk().getZ();
                    p.getWorld().setBiome(x, z, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x - 1, z, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x, z - 1, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x + 1, z, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x, z + 1, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x - 1, z - 1, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x + 1, z - 1, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x + 1, z + 1, Biome.MEGA_TAIGA);
                    p.getWorld().setBiome(x - 1, z + 1, Biome.MEGA_TAIGA);
                    p.getWorld().setStorm(true);
                    p.getWorld().setThundering(true);
                    p.getWorld().setWeatherDuration((int) Math.round(40 * Math.pow(10000, 0.15)) * 20);
                    p.sendMessage("In exchange for " + ChatColor.AQUA + ULTIMATECOST + ChatColor.WHITE + " Favor, ");
                    p.sendMessage(ChatColor.GOLD + "your divine frost" + ChatColor.WHITE + " has started a snowstorm on your world.");
                    DMiscUtil.setFavor(p, DMiscUtil.getFavor(p) - ULTIMATECOST);
                } else p.sendMessage(ChatColor.YELLOW + "Chill requires " + ULTIMATECOST + " Favor.");
            }
        }
    }

    @Override
    public void onTick(long timeSent) {
        int healinterval = 10 - (int) (Math.round(Math.pow(10000, 0.125))); // seconds
        if (healinterval < 1) healinterval = 1;
        if (timeSent > LASTCHECK + (healinterval * 1000)) {
            LASTCHECK = timeSent;
            if ((DMiscUtil.getOnlinePlayer(getPlayerId()) != null) && DMiscUtil.getOnlinePlayer(getPlayerId()).getWorld().hasStorm()) {
                Player p = DMiscUtil.getOnlinePlayer(getPlayerId());
                int x = p.getLocation().getChunk().getX(), z = p.getLocation().getChunk().getZ();
                if (p.getWorld().getBiome(x, z).name().contains("TAIGA") || p.getWorld().getBiome(x, z).name().contains("COLD")) {
                    double healamt = Math.ceil(0.1 * Math.pow(10000, 0.297));
                    if (DMiscUtil.getHP(getPlayerId()) + healamt > DMiscUtil.getMaxHP(getPlayerId()))
                        healamt = DMiscUtil.getMaxHP(getPlayerId()) - DMiscUtil.getHP(getPlayerId());
                    DMiscUtil.setHP(getPlayerId(), DMiscUtil.getHP(getPlayerId()) + healamt);
                }
            }
        }
    }

    @Override
    public boolean canTribute() {
        return false;
    }

    private boolean iceSpawn(Player p) {
        if (!DMiscUtil.canTarget(p, p.getLocation())) {
            p.sendMessage(ChatColor.YELLOW + "You can't do that from a no-PVP zone.");
            return false;
        }
        Location target = DMiscUtil.getTargetLocation(p);
        if (target == null) return false;
        if (!DMiscUtil.canLocationPVP(target)) return false;
        FallingBlock ice = p.getWorld().spawnFallingBlock(p.getLocation(), Material.PACKED_ICE, (byte) 0);
        Vector v = p.getLocation().toVector();
        Vector victor = target.toVector().subtract(v);
        ice.setVelocity(victor);
        ice.setDropItem(false);
        final FallingBlock ss = ice;
        DMiscUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DMiscUtil.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ss.remove();
                ss.getWorld().createExplosion(ss.getLocation(), 1 + Math.round(Math.pow(DMiscUtil.getDevotion(getPlayerId(), getName()), 0.1142)));
            }
        }, 60);
        return true;
    }
}

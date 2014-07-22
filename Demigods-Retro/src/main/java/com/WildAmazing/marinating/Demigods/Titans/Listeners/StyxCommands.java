package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.OtherCommands.PhantomCommands;
import com.WildAmazing.marinating.Demigods.Titans.Styx;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class StyxCommands {
    public static void onEnableStyx(final World w, final Demigods plugin) {
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                if (w == null) return;
                for (Player p : w.getPlayers()) {
                    if (p.isOnline()) if (plugin.isTitan(p)) {
                        if (plugin.getInfo(p).hasDeity(Divine.STYX)) {
                            if (plugin.getInfo(p).isAlive()) {
                                if (p.getHealth() < 20) {
                                    for (Player P : w.getPlayers()) {
                                        if (P.getLocation().distance(p.getLocation()) < 3 && !p.equals(P)) {
                                            p.setHealth(p.getHealth() + 2);
                                            if (P.getHealth() > 1) {
                                                P.setHealth(P.getHealth() - 1);
                                                P.sendMessage("The presence of Styx drains you of health.");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 600, 600);
    }

    public static void onPlayerCommandPreprocessStyx(PlayerCommandPreprocessEvent event, final Demigods plugin) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        if (plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
        if (plugin.isTitan(player)) {
            TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
            if (tpi.hasDeity(Divine.STYX)) {
                if (!tpi.isAlive()) {
                    return;
                }
                final Styx s = (Styx) tpi.getDeity(Divine.STYX);
                String[] sects = event.getMessage().split(" +", 2);
                String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
                Commands cmd;
                try {
                    cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
                } catch (Exception ex) {
                    return;
                }
                try {
                    switch (cmd) {
                        case INVINCIBLE:
                            if (tpi.getPower() < 150) {
                                player.sendMessage("This skill requires at least 150 Power.");
                                break;
                            } else {
                                if (args.length != 1) {
                                    player.sendMessage("/invincible <playername>");
                                    break;
                                }
                                try {
                                    Player target = plugin.getServer().getPlayer(args[0]);
                                    if (target.equals(player)) {
                                        player.sendMessage(ChatColor.RED + "You can't use this ability on yourself.");
                                        break;
                                    }
                                    if (!target.getWorld().equals(player.getWorld())) {
                                        player.sendMessage(ChatColor.RED + "That player is not in your world.");
                                        break;
                                    }
                                    if (plugin.isTitan(target)) {
                                        s.setInvinciblePlayer(target);
                                        s.setTime(System.currentTimeMillis() + 25000);
                                        tpi.setPower(tpi.getPower() - 150);
                                        target.sendMessage(ChatColor.GOLD + "By the blessing of Styx, you are invulnerable for 25 seconds.");
                                        player.sendMessage(ChatColor.GOLD + target.getName() + " will be invincible for 25 seconds.");
                                        player.sendMessage("You now have " + tpi.getPower() + " Power.");
                                    } else player.sendMessage(ChatColor.RED + target.getName() + " is not a Titan.");
                                } catch (Exception not) {
                                    player.sendMessage(ChatColor.RED + "Error. Was the player's name spelled correctly?");
                                }
                            }
                            break;
                        case REVIVE:
                        case RESURRECT:
                            if (tpi.getPower() >= 500) {
                                if (System.currentTimeMillis() < s.getReviveTime()) {
                                    player.sendMessage("You cannot use that skill for " + (s.getReviveTime() - System.currentTimeMillis()) / 1000 + " seconds.");
                                    break;
                                }
                                if (args.length == 1) {
                                    player.getInventory().setHelmet(null);
                                    try {
                                        Player p = plugin.getServer().getPlayer(args[0]);
                                        if (plugin.isTitan(p)) {
                                            if (!plugin.getInfo(p).isAlive()) {
                                                s.setReviveTime(System.currentTimeMillis() + 30000);
                                                tpi.setPower(tpi.getPower() - 500);
                                                plugin.getInfo(p).setAlive(true);
                                                PhantomCommands.unphantomize(p);
                                                p.sendMessage(ChatColor.GOLD + "You were resurrected by " + player.getName() + ".");
                                                player.sendMessage(p.getName() + " was resurrected. You now have " + tpi.getPower() + " Power.");
                                                break;
                                            }
                                        }
                                        player.sendMessage("That player is not a phantom or not a titan.");
                                    } catch (Exception error) {
                                        player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                                    }
                                } else
                                    player.sendMessage("Correct syntax: " + ChatColor.YELLOW + "/revive <player name>");
                            } else {
                                player.sendMessage("You require 500 Power to use that ability.");
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
        INVINCIBLE, REVIVE, RESURRECT
    }

    public static void onEntityDamage(EntityDamageEvent e, Demigods plugin) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.isTitan(p)) {
                for (Player pl : p.getWorld().getPlayers()) {
                    if (plugin.isTitan(pl)) {
                        if (plugin.getInfo(pl).hasDeity(Divine.STYX)) {
                            Styx s = (Styx) plugin.getInfo(pl).getDeity(Divine.STYX);
                            if (s.getTime() > System.currentTimeMillis())
                                if (s.getInvinciblePlayerId().equals(p.getUniqueId())) e.setDamage(0);
                        }
                    }
                }
            }
        }
    }
}

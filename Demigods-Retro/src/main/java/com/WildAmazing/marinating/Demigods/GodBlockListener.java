package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.OtherCommands.PhantomCommands;
import com.WildAmazing.marinating.Demigods.Utilities.Cuboid;
import com.WildAmazing.marinating.Demigods.Utilities.DeityLocale;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class GodBlockListener implements Listener {
    private Demigods plugin;

    public GodBlockListener(Demigods instance) {
        plugin = instance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        PhantomCommands.onPhantomBlockBreak(e, plugin);
        if (e.getBlock() == null || e.isCancelled()) return;
        Player p = e.getPlayer();
        if (!(plugin.isGod(p) || plugin.isTitan(p))) {
            for (DeityLocale dl : plugin.getAllLocs()) {
                for (Cuboid c : dl.getLocale()) {
                    if (c.isInCuboid(e.getBlock().getLocation())) {
                        e.getPlayer().sendMessage("This area is protected by a higher power.");
                        e.setCancelled(true);
                    }
                }
            }
            return;
        } else {
            for (DeityLocale dl : plugin.getAllLocs()) {
                for (Cuboid c : dl.getLocale()) {
                    if (c.isInCuboid(e.getBlock().getLocation())) {
                        if (!plugin.getInfo(e.getPlayer()).hasDeity(dl.getDeity())) {
                            e.getPlayer().sendMessage("This area is protected by " + dl.getDeity().name() + ".");
                            e.setCancelled(true);
                        } else if (c.isShrine()) {
                            e.getBlock().setType(Material.AIR);
                            e.getPlayer().sendMessage("Broken blocks do not yield drops in shrines.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        PhantomCommands.onPhantomBlockPlace(e, plugin);
        if (e.getBlock() == null || e.isCancelled()) return;
        Player p = e.getPlayer();
        if (!(plugin.isGod(p) || plugin.isTitan(p))) {
            for (DeityLocale dl : plugin.getAllLocs()) {
                for (Cuboid c : dl.getLocale()) {
                    if (c.isInCuboid(e.getBlock().getLocation())) {
                        e.getPlayer().sendMessage("This area is protected by " + dl.getDeity().name() + ".");
                        e.setCancelled(true);
                    }
                }
            }
        } else {
            for (DeityLocale dl : plugin.getAllLocs()) {
                if (!plugin.getInfo(e.getPlayer()).hasDeity(dl.getDeity())) {
                    for (Cuboid c : dl.getLocale()) {
                        if (c.isInCuboid(e.getBlock().getLocation())) {
                            e.getPlayer().sendMessage("This area is protected by " + dl.getDeity().name() + ".");
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
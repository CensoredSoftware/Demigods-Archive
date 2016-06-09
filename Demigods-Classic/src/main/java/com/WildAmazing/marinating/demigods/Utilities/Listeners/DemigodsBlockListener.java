package com.WildAmazing.marinating.demigods.Utilities.Listeners;

import com.WildAmazing.marinating.demigods.Demigods;
import com.WildAmazing.marinating.demigods.Utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class DemigodsBlockListener implements Listener {
    Demigods plugin;

    public DemigodsBlockListener(Demigods inst) {
        plugin = inst;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        //Player
        Block b = e.getBlock();
        Location l = b.getLocation();
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
        if ((b.getType() == Material.SIGN_POST) || (b.getType() == Material.WALL_SIGN)) {
            for (Shrine s : DSave.getShrines()) {
                if (s.getCenter().getWorld().equals(l.getWorld().getName())) {
                    for (WriteLocation wl : s.getExtensions()) {
                        if (l.distance(DUtil.toLocation(wl)) < 0.1) {
                            s.getExtensions().remove(wl);
                            p.sendMessage(ChatColor.YELLOW + "You have broken an extension of shrine protection.");
                            return;
                        }

                    }
                }
            }
        }
        if (DUtil.isProtectedShrine(l)) {
            p.sendMessage("You may not disturb the sacred ground of the shrine.");
            e.setCancelled(true);
        } else if (!DUtil.canPlayerManipulate(e.getPlayer(), l)) {
            p.sendMessage("This area is protected by a shrine's extension.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setBuildable(false);
        if (DUtil.isShrineCenter(l))
            e.setBuildable(true);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        //Player
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
        if (!DUtil.canPlayerManipulate(e.getPlayer(), l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        //Player
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
        if (e.getCause() == IgniteCause.FLINT_AND_STEEL)
            if (!DUtil.canPlayerManipulate(e.getPlayer(), l))
                e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        Location l = e.getRetractLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        //Player
        Block b = e.getBlock();
        Location l = b.getLocation();
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
        if (DUtil.isShrineCenter(l)) {
            for (Shrine s : DSave.getShrines()) {
                if (s.getCenter().getWorld().equals(l.getWorld().getName()))
                    if (DUtil.toLocation(s.getCenter()).distance(l) < 1) {
                        if (!s.claim(p, e.getBlock().getType())) {
                            e.setCancelled(true);
                        }
                        return;
                    }
            }
        }
        if (DUtil.isProtectedShrine(l)) {
            p.sendMessage("You may not disturb the sacred ground of the shrine.");
            e.setCancelled(true);
        } else if (!DUtil.canPlayerManipulate(e.getPlayer(), l)) {
            p.sendMessage("This area is protected by a shrine's extension.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (DUtil.isProtectedShrine(l))
            e.setCancelled(true);
    }
}

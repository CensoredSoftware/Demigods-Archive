package com.WildAmazing.marinating.Demigods.Gods.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GodCommands {
    public static void onEntityDeath(EntityDeathEvent ee, final Demigods plugin) {
        if (ee.getEntity() instanceof Player) {
            final Player p1 = (Player) ee.getEntity();
            try {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) p1.getLastDamageCause();
                if (e.getDamager() instanceof Player) {
                    Player p2 = (Player) e.getDamager();
                    if (plugin.isGod(p2) && plugin.isTitan(p1)) {
                        if (p2.isOnline()) {
                            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p2);
                            TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p1);
                            int add = 100 + (int) (Math.random() * tpi.getGlory());
                            if (add > 1000) add = 1000;
                            gpi.setBlessing(gpi.getBlessing() + add);
                            gpi.setFavor(gpi.getFavor() + add);
                            p1.getWorld().dropItem(p1.getLocation(), new ItemStack(Material.BONE, (int) (Math.random() * 4 + 1)));
                            p2.sendMessage("You have been rewarded with " + add + " Favor for slaying " + p1.getDisplayName() + ".");
                            p2.sendMessage("You now have " + gpi.getFavor() + " Favor.");
                            for (Player pla : p1.getWorld().getPlayers())
                                pla.sendMessage(ChatColor.GOLD + "[GODS] " + ChatColor.GREEN + p2.getName() + ChatColor.WHITE + " has felled the Titanspawn " + ChatColor.RED + p1.getName() + ChatColor.WHITE + ".");
                            gpi.setKills(gpi.getKills() + 1);
                            tpi.setDeaths(tpi.getDeaths() + 1);
                        }
                    } else if (plugin.isGod(p2) && plugin.isGod(p1)) {
                        for (Player p : p1.getWorld().getPlayers()) {
                            p.sendMessage(ChatColor.GOLD + "[GODS] " + ChatColor.GREEN + p1.getName() + ChatColor.WHITE + " was betrayed by " + ChatColor.RED + p2.getName() + ChatColor.WHITE + ".");
                        }
                    }
                }
            } catch (Exception uncastable) {
            }
            if (plugin.isTitan(p1) && plugin.getConfigHandler().getPhantom()) {
                plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        plugin.getInfo(p1).setAlive(false);
                    }
                }, 4);
                plugin.getInfo(p1).setLastLoc(plugin.toWriteLocation(p1.getLocation()));
            }
        }
    }

    public static void onEntityDamage(EntityDamageEvent e, Demigods plugin) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.isGod(p)) {
                if (e.getCause() != DamageCause.ENTITY_ATTACK) {
                    if (p.getHealth() <= e.getDamage()) {
                        e.setDamage(p.getHealth() - 1);
                    }
                }
            }
        }
    }

    public static void onPlayerInteractGod(PlayerInteractEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
                if (plugin.isGod(p)) shrineCode(e.getPlayer(), e, plugin);
            } else if (e.getClickedBlock().getType() == Material.IRON_BLOCK) {
                claimCode(e, plugin);
            } else if (e.getClickedBlock().getType() == Material.GOLD_BLOCK) {
                additionalCode(e, plugin);
            } else if (e.getClickedBlock().getType() == Material.DIAMOND_BLOCK && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                warpCode(e, plugin);
            } else if (e.getClickedBlock().getType() == Material.CHEST && e.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK && plugin.isGod(p)) {
                GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                for (DeityLocale d : plugin.getAllLocs()) {
                    if (gpi.hasDeity(d.getDeity())) {
                        for (Cuboid c : d.getLocale()) {
                            if (c.isInCuboid(e.getClickedBlock().getLocation()) && c.isShrine()) {
                                tributeCode(e, d.getDeity(), plugin);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static ArrayList<Location> getSurroundings(Block b, int r) {
        ArrayList<Location> al = new ArrayList<Location>();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    al.add(new Location(b.getWorld(), b.getX() + x, b.getY() + y, b.getZ() + z));
                }
            }
        }
        return al;
    }

    private static int checkSurroundings(Block b, int r) {
        ArrayList<Material> am = new ArrayList<Material>();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    am.add(new Location(b.getWorld(), b.getX() + x, b.getY() + y, b.getZ() + z).getBlock().getType());
                }
            }
        }
        if (!am.contains(Material.GOLD_BLOCK)) return -1;
        int c = 0;
        for (Material m : am) {
            switch (m) {
                case GOLD_BLOCK:
                    c += 20;
                    break;
                case STONE:
                    c += 2;
                    break;
                case DIRT:
                    c += 1;
                    break;
                case BOOKSHELF:
                    c += 15;
                    break;
                case COBBLESTONE:
                    c += 1;
                    break;
                case LOG:
                    c += 2;
                    break;
                case SAND:
                    c += 1;
                    break;
                case MOSSY_COBBLESTONE:
                    c += 9;
                    break;
                case DIAMOND_BLOCK:
                    c += 190;
                    break;
                case IRON_BLOCK:
                    c += 75;
                    break;
                case JUKEBOX:
                    c += 15;
                    break;
                case CAKE_BLOCK:
                    c += 7;
                    break;
                case LAPIS_BLOCK:
                    c += 20;
                    break;
                case WATER:
                    c += 2;
                    break;
                case LAVA:
                    c += 2;
                    break;
                case OBSIDIAN:
                    c += 8;
                    break;
                case GLOWSTONE:
                    c += 8;
                    break;
            }
        }
        return c;
    }

    private static void shrineCode(Player p, PlayerInteractEvent e, Demigods plugin) {
        Sign s = (Sign) e.getClickedBlock().getState();
        if (!s.getLine(0).equalsIgnoreCase("shrine")) return;
        else if (!s.getLine(1).equalsIgnoreCase("dedicate")) return;
        Divine dedicate;
        try {
            dedicate = Divine.valueOf(s.getLine(2).toUpperCase());
        } catch (Exception impossible) {
            p.sendMessage("That is not a known deity's name.");
            return;
        }
        int r = 3;
        try {
            r = Integer.parseInt(s.getLine(3)) / 2;
        } catch (Exception notAnInt) {
            r = 3;
        }
        if (r < 3) r = 3;
        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
        if (!gpi.hasDeity(dedicate)) {
            p.sendMessage(ChatColor.GREEN + dedicate.toString() + ChatColor.YELLOW + " is not in your allegiance!");
            return;
        } else {
            for (Location l : getSurroundings(e.getClickedBlock(), r)) {
                for (DeityLocale dl : plugin.getAllLocs()) {
                    for (Cuboid c : dl.getLocale()) {
                        if (c.isInCuboid(l)) {
                            e.getClickedBlock().setType(Material.AIR);
                            e.getPlayer().sendMessage("This area is already dedicated to " + dl.getDeity().name() + ".");
                            return;
                        }
                    }
                }
            }
            int value = checkSurroundings(e.getClickedBlock(), r);
            if (value >= (Math.pow((r * 2 + 1), 3) + 5 * r)) {
                p.getWorld().strikeLightningEffect(e.getClickedBlock().getRelative(BlockFace.DOWN).getLocation());
                Sign newsign = (Sign) e.getClickedBlock().getState();
                newsign.setLine(0, ChatColor.GRAY + "This region ");
                newsign.setLine(1, ChatColor.GRAY + "protected by");
                newsign.setLine(2, ChatColor.RED + dedicate.name());
                newsign.setLine(3, "");
                newsign.update();
                p.sendMessage(ChatColor.GREEN + dedicate.toString() + ChatColor.YELLOW + " has blessed this shrine.");
                Block b = e.getClickedBlock();
                Location corner1 = new Location(b.getWorld(), b.getX() + r, b.getY() + r, b.getZ() + r);
                Location corner2 = new Location(b.getWorld(), b.getX() - r, b.getY() - r, b.getZ() - r);
                if (plugin.getLoc(dedicate) == null) plugin.addToMaster(new DeityLocale(dedicate));
                plugin.getLoc(dedicate).addToLocale(new Cuboid(p.getWorld().getName(), plugin.toWriteLocation(corner1), plugin.toWriteLocation(corner2), true));
            } else {
                if (value < 0) {
                    p.sendMessage(ChatColor.RED + "You cannot create a shrine without a Gold Block.");
                    return;
                }
                p.sendMessage(ChatColor.GREEN + dedicate.toString() + ChatColor.YELLOW + " is unsatisfied with the value of the shrine.");
                p.sendMessage(ChatColor.RED + "" + (value / (Math.pow((r * 2 + 1), 3) + 5 * r)) * 100 + "% of required material value.");
            }
        }
    }

    private static void claimCode(PlayerInteractEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if (plugin.isGod(p) || plugin.isTitan(p)) return;
        ArrayList<BlockState> ab = new ArrayList<BlockState>();
        BlockState b = e.getClickedBlock().getRelative(BlockFace.NORTH).getState();
        BlockState bb = e.getClickedBlock().getState();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                ab.add((new Location(bb.getWorld(), bb.getX() + x, bb.getY(), bb.getZ() + z)).getBlock().getState());
            }
        }
        for (BlockState bt : ab) {
            if (!(bt.getType().equals(b.getType()) && bt.getRawData() == b.getRawData()) && bt.getType() != Material.IRON_BLOCK) {
                return;
            }
        }
        Divine choice = null;
        switch (b.getType()) {
            case AIR:
                choice = Divine.ZEUS;
                break;
            case WATER:
            case STATIONARY_WATER:
                choice = Divine.POSEIDON;
                break;
            case DIRT:
                choice = Divine.HADES;
                break;
        }
        if (choice != null) {
            p.sendMessage(ChatColor.GRAY + "You have been accepted to the lineage of " + ChatColor.GOLD + choice.name() + ChatColor.GRAY + ".");
            e.getClickedBlock().setType(b.getType());
            e.getClickedBlock().setData(b.getRawData());
            plugin.addToMaster(new GodPlayerInfo(p, choice));
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
            gpi.setFavor(50);
            gpi.setBlessing(50);
        }
    }

    private static void tributeCode(PlayerInteractEvent e, Divine deity, Demigods plugin) {
        Chest c = (Chest) e.getClickedBlock().getState();
        Inventory i = c.getInventory();
        ItemStack[] contents = i.getContents();
        Player p = e.getPlayer();
        ArrayList<ItemStack> sacrifices = new ArrayList<ItemStack>();
        for (ItemStack ii : contents) {
            if (ii != null) if (ii.getType() != Material.AIR) {
                sacrifices.add(ii);
            }
        }
        if (sacrifices.size() == 0) { // ignore empty chest
            p.sendMessage("That chest contains no tributes.");
        } else {
            GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
            int val = 0;
            for (ItemStack ii : sacrifices) {
                switch (ii.getType()) {
                    case STONE:
                        val += ii.getAmount() * 0.8;
                        break;
                    case DIRT:
                        val += ii.getAmount() * 0.1;
                        break;
                    case LOG:
                        val += ii.getAmount() * 1;
                        break;
                    case WATER_BUCKET:
                        val += ii.getAmount() * 5.5;
                        break;
                    case LAVA_BUCKET:
                        val += ii.getAmount() * 7;
                        break;
                    case GLASS:
                        val += ii.getAmount() * 1.1;
                        break;
                    case LAPIS_BLOCK:
                        val += ii.getAmount() * 85;
                        break;
                    case SANDSTONE:
                        val += ii.getAmount() * 1.1;
                        break;
                    case GOLD_BLOCK:
                        val += ii.getAmount() * 170;
                        break;
                    case IRON_BLOCK:
                        val += ii.getAmount() * 120;
                        break;
                    case BRICK:
                        val += ii.getAmount() * 10;
                        break;
                    case TNT:
                        val += ii.getAmount() * 10;
                        break;
                    case MOSSY_COBBLESTONE:
                        val += ii.getAmount() * 10;
                        break;
                    case OBSIDIAN:
                        val += ii.getAmount() * 10;
                        break;
                    case DIAMOND_BLOCK:
                        val += ii.getAmount() * 300;
                        break;
                    case CACTUS:
                        val += ii.getAmount() * 1.7;
                        break;
                    case PUMPKIN:
                        val += ii.getAmount() * 1.4;
                        break;
                    case CAKE:
                        val += ii.getAmount() * 42;
                        break;
                    case APPLE:
                        val += ii.getAmount() * 5;
                        break;
                    case COAL:
                        val += ii.getAmount() * 2.5;
                        break;
                    case DIAMOND:
                        val += ii.getAmount() * 30;
                        break;
                    case IRON_INGOT:
                        val += ii.getAmount() * 12;
                        break;
                    case GOLD_INGOT:
                        val += ii.getAmount() * 18;
                        break;
                    case STRING:
                        val += ii.getAmount() * 2.4;
                        break;
                    case WHEAT:
                        val += ii.getAmount() * 1.9;
                        break;
                    case BREAD:
                        val += ii.getAmount() * 7.8;
                        break;
                    case RAW_FISH:
                        val += ii.getAmount() * 2.4;
                        break;
                    case PORK:
                        val += ii.getAmount() * 2.4;
                        break;
                    case COOKED_FISH:
                        val += ii.getAmount() * 3.4;
                        break;
                    case GRILLED_PORK:
                        val += ii.getAmount() * 3.4;
                        break;
                    case GOLDEN_APPLE:
                        val += ii.getAmount() * 190;
                        break;
                    case GOLD_RECORD:
                        val += ii.getAmount() * 60;
                        break;
                    case GREEN_RECORD:
                        val += ii.getAmount() * 60;
                        break;
                    case GLOWSTONE:
                        val += ii.getAmount() * 1.7;
                        break;
                    case REDSTONE:
                        val += ii.getAmount() * 3.3;
                        break;
                    case EGG:
                        val += ii.getAmount() * 0.3;
                        break;
                    case SUGAR:
                        val += ii.getAmount() * 1.2;
                        break;
                    case BONE:
                        val += ii.getAmount() * 3;
                        break;
                    default:
                        val += ii.getAmount() * 1;
                        break;
                }
            }
            val = (int) (val * (1 + Math.random())); // multiplier
            gpi.setFavor(gpi.getFavor() + val);
            gpi.setBlessing(gpi.getBlessing() + val);
            p.sendMessage(ChatColor.GREEN + deity.name() + ChatColor.YELLOW + " has graciously accepted your offerings.");
            p.sendMessage(ChatColor.GOLD + "You have been rewarded with " + val + " Favor and Blessing.");
            i.clear();
        }
    }

    private static void additionalCode(PlayerInteractEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if ((!plugin.isGod(p)) || plugin.isTitan(p)) return;
        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
        if (!gpi.isEligible()) return;
        ItemStack i = p.getItemInHand();
        Divine choice = null;
        switch (i.getType()) {
            case DIAMOND_SWORD:
                if (p.hasPermission("god.ares") || p.hasPermission("god.all")) choice = Divine.ARES;
                break;
            case BOOKSHELF:
                if (p.hasPermission("god.athena") || p.hasPermission("god.all")) choice = Divine.ATHENA;
                break;
            case LAVA_BUCKET:
                if (p.hasPermission("god.hephaestus") || p.hasPermission("god.all")) choice = Divine.HEPHAESTUS;
                break;
            case ARROW:
                if (p.hasPermission("god.apollo") || p.hasPermission("god.all")) choice = Divine.APOLLO;
                break;
        /*
		 * case WOOL: if (i.getData().getData()==((byte)0x6)) choice = Divine.APHRODITE; break;
		 * case ARROW: choice = Divine.APOLLO; break;
		 * case BOW: choice = Divine.ARTEMIS; break;
		 * case WHEAT: choice = Divine.DEMETER; break;
		 * case MILK_BUCKET: choice = Divine.DIONYSUS; break;
		 * case GLOWSTONE: choice = Divine.HELIOS; break;
		 * case LAVA_BUCKET: choice = Divine.HEPHAESTUS; break;
		 * case GOLDEN_APPLE: choice = Divine.HERA; break;
		 * case FEATHER: choice = Divine.HERMES; break;
		 * case FLINT_AND_STEEL: choice = Divine.HESTIA; break;
		 * case BED: choice = Divine.MORPHEUS; break;
		 * case CACTUS: choice = Divine.NEMESIS; break;
		 * case GOLD_SWORD: choice = Divine.NIKE; break;
		 */
        }
        if (choice != null) {
            p.sendMessage(ChatColor.GRAY + "You have been accepted to the lineage of " + ChatColor.GOLD + choice.name() + ChatColor.GRAY + ".");
            if (i.getAmount() > 1) p.setItemInHand(new ItemStack(i.getType(), i.getAmount() - 1));
            else p.setItemInHand(null);
            e.getClickedBlock().setType(Material.AIR);
            gpi.addToAllegiance(choice);
            gpi.setFavor(gpi.getFavor() + 100);
            gpi.setBlessing(gpi.getBlessing() + 100);
            p.sendMessage(ChatColor.YELLOW + "You have gained 100 Favor.");
            e.setCancelled(true);
        }
    }

    public static void onPlayerChat(AsyncPlayerChatEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if (!plugin.isGod(p)) return;
        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
        if (e.getMessage().equals("g")) {
            if (gpi.isChat()) {
                gpi.setChat(false);
                p.sendMessage(ChatColor.GRAY + "God Chat has been toggled off.");
            } else {
                gpi.setChat(true);
                p.sendMessage(ChatColor.GRAY + "God Chat has been toggled on.");
            }
            e.setCancelled(true);
            return;
        }
        if (gpi.isChat()) {
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                if (plugin.isGod(pl))
                    pl.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.WHITE + ": " + e.getMessage());
            }
            e.setCancelled(true);
        }
    }

    private static void warpCode(PlayerInteractEvent e, Demigods plugin) {
        Player p = e.getPlayer();
        if (!p.hasPermission("demigods.warp")) return;
        if (!plugin.isGod(p)) return;
        DeityLocale allShrines = null;
        Cuboid thisShrine = null;
        ArrayList<Cuboid> possibleLocations = new ArrayList<Cuboid>();
        for (DeityLocale d : plugin.getAllLocs()) {
            for (Cuboid c : d.getLocale()) {
                if (c.isInCuboid(e.getClickedBlock().getLocation())) {
                    allShrines = d;
                    thisShrine = c;
                    break;
                }
            }
        }
        if (allShrines == null) return;
        if (plugin.getInfo(p).hasDeity(allShrines.getDeity())) {
            for (Cuboid c : allShrines.getLocale()) {
                if (c.getWorld(plugin.getServer()).equals(p.getWorld())) possibleLocations.add(c);
            }
        }
        if (possibleLocations.size() < 2) {
            p.sendMessage("There are no other shrines to warp to.");
            return;
        }
        int currentShrine = 0;
        for (int i = 0; i < possibleLocations.size(); i++) {
            if (possibleLocations.get(i).getCorner1() == thisShrine.getCorner1() && possibleLocations.get(i).getCorner2() == thisShrine.getCorner2()) {
                currentShrine = i;
                break;
            }
        }
        int target = currentShrine + 1;
        if (target >= possibleLocations.size()) target = 0;
        Location teleTo = null;
        Cuboid destination = possibleLocations.get(target);
        for (Block b : destination.allBlocks(p.getWorld())) {
            if (b.getType() == Material.AIR && b.getRelative(BlockFace.UP).getType() == Material.AIR && b.getRelative(BlockFace.DOWN).getType() != Material.AIR && b.getRelative(BlockFace.DOWN).getType() != Material.LAVA && b.getRelative(BlockFace.DOWN).getType() != Material.STATIONARY_LAVA && b.getLightLevel() > 4) {
                teleTo = b.getLocation();
                break;
            }
        }
        if (teleTo == null) {
            p.sendMessage("The destination shrine is blocked.");
            return;
        }
        teleTo.setYaw(p.getLocation().getYaw());
        teleTo.setPitch(p.getLocation().getPitch());
        e.setCancelled(true);
        p.teleport(teleTo);
    }
}

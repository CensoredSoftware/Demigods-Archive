package com.WildAmazing.marinating.demigods.Utilities.Listeners;

import com.WildAmazing.marinating.demigods.Demigods;
import com.WildAmazing.marinating.demigods.Gods.Ares;
import com.WildAmazing.marinating.demigods.Gods.Hades;
import com.WildAmazing.marinating.demigods.Gods.Poseidon;
import com.WildAmazing.marinating.demigods.Gods.Zeus;
import com.WildAmazing.marinating.demigods.Titans.Atlas;
import com.WildAmazing.marinating.demigods.Titans.Cronus;
import com.WildAmazing.marinating.demigods.Titans.Prometheus;
import com.WildAmazing.marinating.demigods.Titans.Rhea;
import com.WildAmazing.marinating.demigods.Utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.logging.Logger;

public class DemigodsPlayerListener implements Listener {
    /*
     * -------------------
     * Set from config
     * -------------------
     */
    String CLAIMSTRING = "[PROTECT]";
    double SHRINEBONUS = 1.75;
    private double TRIBUTEMULTIPLIER = 2;
    private int GOLDDURABILITY = 45;
    private int OBSIDIANDURABILITY = 20;
    private int DIAMONDDURABILITY = 75;
    private int BEDROCKDURABILITY = 100;
    private double ADVANTAGEPERCENT = 1.35;
    private boolean RESPAWNATLASTSHRINE = true;
    private double DAMAGEMULTIPLIER = 0.2; //% DURABILITY LOST PER HIT
    private Material SELECTMATERIAL = Material.ENCHANTMENT_TABLE; //must be a block

    private Demigods plugin;

    public DemigodsPlayerListener(Demigods p) {
        plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) { //sync to master file
        final Player p = e.getPlayer();
        p.sendMessage("This server is running Demigods v" + ChatColor.YELLOW + plugin.getDescription().getVersion() + ChatColor.WHITE + ".");
        p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "/dg" + ChatColor.GRAY + " for more info.");
        if (!DSave.hasPlayer(p)) {
            Logger.getLogger("Minecraft").info("[Demigods] " + p.getName() + " joined and no save was detected. Creating new file.");
            DSave.addPlayer(p);
        }
        DSave.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
        if (DUtil.isFullParticipant(p))
            if (DSave.hasData(p, "ALLIANCECHAT")) {
                if ((Boolean) DSave.getData(p, "ALLIANCECHAT")) {
                    e.setCancelled(true);
                    Logger.getLogger("Minecraft").info("[" + DUtil.getAllegiance(p) + "] " + p.getName() + ": " + e.getMessage());
                    for (Player pl : DUtil.getPlugin().getServer().getOnlinePlayers()) {
                        if (DUtil.isFullParticipant(pl) && DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
                            pl.sendMessage(ChatColor.GREEN + "[" + DUtil.getLevel(p) + "]" + p.getName() + ": " + e.getMessage());
                    }
                }
            }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
        /*
         * Deity / ascension
		 */
        claimFirstDeity(p, e);
        ascendCode(p, e);
		/*
		 * Shrine related
		 */
        shrineExtension(p, e);
        if ((DUtil.getAllegiance(p) != null) && (p.getItemInHand().getType() != Material.AIR)) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				/*
				 * Deity selection code
				 */
                if (!claimNextDeity(p, e))
                    shrineTribute(p, e);
                shrineAttack(p, e);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if ((DUtil.getDeities(p) != null) && (DUtil.getDeities(p).size() > 0)) {
            for (Deity d : DUtil.getDeities(p))
                d.onEvent(e);
        }
        if (!RESPAWNATLASTSHRINE)
            return;
        final Player pl = p;
        if (DUtil.isFullParticipant(p)) {
            DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (DSave.hasData(pl, "LASTUSEDSHRINE")) {
                        pl.sendMessage(ChatColor.YELLOW + "Returning to last tributed shrine...");
                        Shrine s = (Shrine) DSave.getData(pl, "LASTUSEDSHRINE");
                        pl.teleport(DUtil.toLocation(s.getCenter()).getBlock().getRelative(BlockFace.SOUTH_EAST)
                                .getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.UP).getLocation());
                    }
                }
            }, 20);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getFrom().distance(e.getTo()) > 0.01) {
            if (!(DUtil.isProtectedDemigodsOnly(e.getFrom()) && DUtil.isProtectedDemigodsOnly(e.getTo())))
                for (Shrine s : DSave.getShrines()) {
                    if (p.getWorld().getName().equals(s.getCenter().getWorld())) {
                        for (WriteLocation w : s.getExtensions()) {
							/*
							 * Outside coming in
							 */
                            if (e.getFrom().distance(DUtil.toLocation(w)) > s.getRange()) {
                                if (e.getTo().distance(DUtil.toLocation(w)) <= s.getRange()) {
                                    if (s.getAlliance() != null) {
                                        p.sendMessage(ChatColor.GRAY + "You are entering a protected zone of the " + ChatColor.YELLOW +
                                                s.getAlliance() + ChatColor.GRAY + " alliance.");
                                    } else
                                        p.sendMessage(ChatColor.GRAY + "You are entering an unclaimed protected zone.");
                                    return;
                                }
                            }
							/*
							 * Leaving
							 */
                            if (e.getFrom().distance(DUtil.toLocation(w)) <= s.getRange()) {
                                if (e.getTo().distance(DUtil.toLocation(w)) > s.getRange()) {
                                    if (s.getAlliance() != null)
                                        p.sendMessage(ChatColor.GRAY + "You are leaving a protected zone of the " + ChatColor.YELLOW +
                                                s.getAlliance() + ChatColor.GRAY + " alliance.");
                                    else p.sendMessage(ChatColor.GRAY + "You are leaving an unclaimed protected zone.");
                                    return;
                                }
                            }
                        }
						/*
						 * Outside coming in
						 */
                        if (e.getFrom().distance(DUtil.toLocation(s.getCenter())) > s.getRadius()) {
                            if (DUtil.toLocation(s.getCenter()).distance(e.getTo()) <= s.getRadius()) {
                                if (s.getAlliance() != null)
                                    p.sendMessage(ChatColor.GRAY + "You are entering a shrine of the " + ChatColor.YELLOW +
                                            s.getAlliance() + ChatColor.GRAY + " alliance.");
                                else p.sendMessage(ChatColor.GRAY + "You are entering an unclaimed shrine.");
                                return;
                            }
                        }
						/*
						 * Leaving
						 */
                        else if (e.getFrom().distance(DUtil.toLocation(s.getCenter())) <= s.getRadius()) {
                            if (DUtil.toLocation(s.getCenter()).distance(e.getTo()) > s.getRadius()) {
                                if (s.getAlliance() != null)
                                    p.sendMessage(ChatColor.GRAY + "You are leaving a shrine of the " + ChatColor.YELLOW +
                                            s.getAlliance() + ChatColor.GRAY + " alliance.");
                                else p.sendMessage(ChatColor.GRAY + "You are leaving an unclaimed shrine.");
                                return;
                            }
                        }
                    }
                }
        }
    }

    private void shrineAttack(Player p, PlayerInteractEvent e) {
        if (!DUtil.isFullParticipant(p))
            return;
        for (Shrine s : DSave.getShrines()) {
            if (!(p.getWorld().getName().equals(s.getCenter().getWorld())))
                continue;
            if (s.getAlliance() == null)
                continue;
            if (DUtil.getAllegiance(p).equalsIgnoreCase(s.getAlliance()))
                continue;
            if (!e.getClickedBlock().getLocation().equals(DUtil.toLocation(s.getCenter())))
                continue;
            if (!p.getItemInHand().getType().name().contains("SWORD")) {
                p.sendMessage(ChatColor.YELLOW + "A shrine can only be damaged by a sword.");
                continue;
            }
            if (s.getTime() + s.getInvincibleSeconds() * 1000 > System.currentTimeMillis()) {
                p.sendMessage(ChatColor.YELLOW + "The shrine is temporarily impervious.");
                return;
            }
            Block b = e.getClickedBlock(); //shrine block
            if (b.getType() == Material.BEDROCK) {
                int count = 0;
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (DUtil.isFullParticipant(player)) {
                        if (DUtil.getAllegiance(player).equalsIgnoreCase(s.getAlliance()))
                            count++;
                    }
                    if (count == 0) {
                        p.sendMessage(ChatColor.YELLOW + "This shrine is impervious to attack while its alliance is offline.");
                        return;
                    }
                }
            }
            if (s.getDurability() < GOLDDURABILITY) {
                //iron level
                if (!p.getItemInHand().getType().name().contains("IRON") &&
                        !p.getItemInHand().getType().name().contains("GOLD") &&
                        !p.getItemInHand().getType().name().contains("DIAMOND")) {
                    p.sendMessage(ChatColor.YELLOW + "You must use an iron sword or better to damage the shrine.");
                    return;
                }
            } else if (s.getDurability() < DIAMONDDURABILITY) {
                //gold level
                if (!p.getItemInHand().getType().name().contains("GOLD") &&
                        !p.getItemInHand().getType().name().contains("DIAMOND")) {
                    p.sendMessage(ChatColor.YELLOW + "You must use a gold sword or better to damage the shrine.");
                    return;
                }
            } else if (s.getDurability() >= DIAMONDDURABILITY) {
                if (!p.getItemInHand().getType().name().contains("DIAMOND")) {
                    p.sendMessage(ChatColor.YELLOW + "You must use a diamond sword to damage the shrine.");
                    return;
                }
            }
            int damage = 0;
            switch (p.getItemInHand().getType()) {
                case IRON_SWORD:
                    damage = 1;
                    break;
                case GOLD_SWORD:
                    damage = 2;
                    break;
                case DIAMOND_SWORD:
                    damage = 3;
                    break;
            }
            if (s.getDurability() - damage < 0) damage = s.getDurability();
            s.setDurability(s.getDurability() - damage);
            s.setDamageTime(System.currentTimeMillis());
            p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() * (1 - DAMAGEMULTIPLIER)));
            if (s.getDurability() < 1) {
                b.setType(Material.AIR);
                DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + " has destroyed a shrine of the " +
                        s.getAlliance() + " alliance.");
                s.setAlliance(null);
                s.setDurability(0);
            } else {
                p.sendMessage(ChatColor.YELLOW + "The shrine has been reduced to " + s.getDurability() + " durability.");
                for (Player player : p.getWorld().getPlayers()) {
                    if (DUtil.isFullParticipant(player))
                        if (DUtil.getAllegiance(player).equalsIgnoreCase(s.getAlliance()))
                            player.sendMessage(ChatColor.YELLOW + s.getName() + " is under attack!");
                }
                if (s.getDurability() < OBSIDIANDURABILITY) {
                    b.setType(Material.IRON_BLOCK);
                } else if ((s.getDurability() >= OBSIDIANDURABILITY) && (s.getDurability() < GOLDDURABILITY)) {
                    b.setType(Material.OBSIDIAN);
                } else if ((s.getDurability() >= GOLDDURABILITY) && (s.getDurability() < DIAMONDDURABILITY)) {
                    b.setType(Material.GOLD_BLOCK);
                } else if ((s.getDurability() >= DIAMONDDURABILITY) && (s.getDurability() < BEDROCKDURABILITY)) {
                    b.setType(Material.DIAMOND_BLOCK);
                } else if (s.getDurability() >= BEDROCKDURABILITY) {
                    b.setType(Material.BEDROCK);
                }
            }
        }
    }

    private void shrineTribute(Player p, PlayerInteractEvent e) {
        for (Shrine s : DSave.getShrines()) {
            if (p.getWorld().getName().equals(s.getCenter().getWorld()) && (s.getAlliance() != null)) {
                if (s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
                    if (e.getClickedBlock().getLocation().equals(DUtil.toLocation(s.getCenter()))) {
                        if (p.getItemInHand().getType().name().contains("SWORD") || p.getItemInHand().getType().name().contains("BOW")) {
                            p.sendMessage(ChatColor.YELLOW + "That item cannot be tributed.");
                            return;
                        }
                        p.sendMessage("Your offering of " + p.getItemInHand().getType() + " has been graciously accepted.");
                        int total = (int) (Math.ceil(DUtil.getValue(p.getItemInHand()) * SHRINEBONUS * TRIBUTEMULTIPLIER));
                        p.sendMessage(ChatColor.YELLOW + "You have gained " + total + " Favor.");
                        DSave.saveData(p, "LASTUSEDSHRINE", s);
                        DUtil.setFavor(p, DUtil.getFavor(p) + total);
                        p.setItemInHand(null);
                        //shrine benefits
                        if (total >= s.getDurability() * 15) {
                            if (s.getDurability() < 100) {
                                p.sendMessage(ChatColor.YELLOW + "The shrine's durability has increased.");
                                s.setDurability(s.getDurability() + 1);
                                if (s.getDurability() >= 15) {
                                    e.getClickedBlock().setType(Material.IRON_BLOCK);
                                }
                                if (s.getDurability() >= 20) {
                                    e.getClickedBlock().setType(Material.OBSIDIAN);
                                }
                                if (s.getDurability() >= 45) {
                                    e.getClickedBlock().setType(Material.GOLD_BLOCK);
                                }
                                if (s.getDurability() >= 75) {
                                    e.getClickedBlock().setType(Material.DIAMOND_BLOCK);
                                }
                                if (s.getDurability() == 100) {
                                    e.getClickedBlock().setType(Material.BEDROCK);
                                }
                            }
                        }
                        if (total >= s.getRange() * 25) {
                            s.setRange(s.getRange() + 1);
                            p.sendMessage(ChatColor.YELLOW + "Extensions of this shrine's protection have increased range.");
                        }
                        if (total >= s.getInvincibleSeconds() * 15) {
                            s.setInvincibleSeconds(s.getInvincibleSeconds() + 1);
                            p.sendMessage(ChatColor.YELLOW + "The shrine has become more resistant to attack.");
                        }
                        if (total >= s.getInvincibleRadius() * 150) {
                            s.setInvincibleRadius(s.getInvincibleRadius() + 1);
                            p.sendMessage(ChatColor.YELLOW + "The shrine's protective radius has increased.");
                        }
                        //
                        if (total > 25) total = 25;
                        for (int i = 0; i < total; i++)
                            p.getWorld().spawn(DUtil.toLocation(s.getCenter()), ExperienceOrb.class);
                    }
                }
            }
        }
    }

    private void shrineExtension(Player p, PlayerInteractEvent e) {
		/*
		 * Shrine extension code
		 */
        if ((e.getClickedBlock() != null) && ((e.getClickedBlock().getType() == Material.WALL_SIGN) || (e.getClickedBlock().getType() == Material.SIGN_POST))) {
            if (DUtil.getAllegiance(p) == null)
                return;
            Sign sign = (Sign) e.getClickedBlock().getState();
            for (String string : sign.getLines()) {
                if (string.trim().equals(CLAIMSTRING)) {
                    int closest = Integer.MAX_VALUE;
                    Shrine close = null;
                    for (Shrine shrine : DSave.getShrines()) {
                        if (p.getWorld().getName().equals(shrine.getCenter().getWorld()) && (shrine.getAlliance() != null)) {
                            if (shrine.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
                                if (DUtil.toLocation(shrine.getCenter()).distance(e.getClickedBlock().getLocation()) < closest) {
                                    closest = (int) DUtil.toLocation(shrine.getCenter()).distance(e.getClickedBlock().getLocation());
                                    close = shrine;
                                }
                            }
                        }
                    }
                    if (close == null) {
                        p.sendMessage("Could not find any valid shrines.");
                    } else if (DUtil.getFavor(p) < (closest)) {
                        p.sendMessage("It costs " + closest + " Favor to extend the shrine's protection to here.");
                    } else if (p.getLocation().distance(DUtil.toLocation(close.getCenter())) < close.getRadius() + 2) {
                        close.getExtensions().add(DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
                        p.sendMessage("The shrine's protection has been extended to a new area.");
                        sign.setLine(0, "");
                        sign.setLine(1, ChatColor.GRAY + "Protected by ");
                        sign.setLine(2, ChatColor.YELLOW + DUtil.getAllegiance(p));
                        sign.setLine(3, "");
                        sign.update();
                        p.getWorld().strikeLightningEffect(e.getClickedBlock().getLocation());
                        DUtil.setFavor(p, DUtil.getFavor(p) - closest);
                        break;
                    } else if (!DUtil.isProtected(e.getClickedBlock().getLocation())) {
                        p.sendMessage("Could not find the shrine's protection to extend.");
                    } else if (close.getExtensions().size() >= close.getMaxExtensions()) {
                        p.sendMessage("The shrine's protection cannot be extended further.");
                    } else {
                        close.getExtensions().add(DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
                        p.sendMessage("The shrine's protection has been extended to a new area.");
                        sign.setLine(0, "");
                        sign.setLine(1, ChatColor.GRAY + "Protected by ");
                        sign.setLine(2, ChatColor.YELLOW + DUtil.getAllegiance(p));
                        sign.setLine(3, "");
                        sign.update();
                        p.getWorld().strikeLightningEffect(e.getClickedBlock().getRelative(BlockFace.DOWN).getLocation());
                        DUtil.setFavor(p, DUtil.getFavor(p) - closest);
                    }
                    break;
                }
            }
        }
    }

    private void claimFirstDeity(Player p, PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if ((p.getItemInHand() == null) || (e.getClickedBlock().getType() != SELECTMATERIAL))
            return;
        if ((DUtil.getAllegiance(p) == null) && ((DUtil.getDeities(p) == null) || (DUtil.getDeities(p).size() == 0))) {
            Deity choice = null;
            //first deity
            switch (p.getItemInHand().getType()) {
                case IRON_INGOT:
                    choice = new Zeus(p.getName());
                    break;
                case SOUL_SAND:
                    choice = new Cronus(p.getName());
                    break;
                case CLAY_BALL:
                    choice = new Prometheus(p.getName());
                    break;
                case LOG:
                    choice = new Rhea(p.getName());
                    break;
                case BONE:
                    choice = new Hades(p.getName());
                    break;
                case WATER_BUCKET:
                    choice = new Poseidon(p.getName());
                    break;
            }
            if (choice != null) {
                p.sendMessage(ChatColor.YELLOW + "The Fates ponder your decision...");
                final Deity fchoice = choice;
                final Player pl = p;
                if (DUtil.hasAdvantage(fchoice.getDefaultAlliance(), ADVANTAGEPERCENT)) {
                    pl.sendMessage(ChatColor.RED + "The Fates have determined that your selection would");
                    pl.sendMessage(ChatColor.RED + "unbalance the order of the universe. Try again");
                    pl.sendMessage(ChatColor.RED + "later or select a different deity.");
                    return;
                }
                pl.sendMessage(ChatColor.YELLOW + "You have been accepted to the lineage of " + fchoice.getName() + ".");
                DUtil.setAllegiance(pl, fchoice.getDefaultAlliance());
                DUtil.addDeity(pl, fchoice);
                DUtil.setFavor(pl, 0);
                DUtil.setDeaths(pl, 0);
                DUtil.setKills(pl, 0);
                DUtil.setLevel(pl, 1);
                pl.getWorld().strikeLightningEffect(pl.getLocation());
                for (int i = 0; i < 20; i++) pl.getWorld().spawn(pl.getLocation(), ExperienceOrb.class);
            }
            e.setCancelled(true);
        }
    }

    private boolean claimNextDeity(Player p, PlayerInteractEvent e) {
        if (p.getItemInHand().getType() == Material.AIR)
            return false;
        if (!DUtil.isFullParticipant(p))
            return false;
        if (DUtil.getLevel(p) < DUtil.costForNextDeity(p))
            return false;
        if (e.getClickedBlock() == null)
            return false;
        for (Shrine s : DSave.getShrines()) {
            if (s.getCenter().getWorld().equals(p.getWorld().getName())) {
                if (DUtil.toLocation(s.getCenter()).distance(e.getClickedBlock().getLocation()) < 0.1) {
                    if (!s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p)))
                        return false;
                    else break;
                }
            }
        }
        Deity choice = null;
        switch (p.getItemInHand().getType()) {
            case IRON_INGOT:
                choice = new Zeus(p.getName());
                break;
            case GOLD_SWORD:
                choice = new Ares(p.getName());
                break;
            case SOUL_SAND:
                choice = new Cronus(p.getName());
                break;
            case CLAY_BALL:
                choice = new Prometheus(p.getName());
                break;
            case BONE:
                choice = new Hades(p.getName());
                break;
            case LOG:
                choice = new Rhea(p.getName());
                break;
            case OBSIDIAN:
                choice = new Atlas(p.getName());
                break;
        }
        if (choice == null)
            return false;
        if (!choice.getDefaultAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
            p.sendMessage(ChatColor.RED + "That deity is not of your alliance.");
            return false;
        }
        if (DUtil.hasDeity(p, choice.getName())) {
            p.sendMessage(ChatColor.RED + "You are already allianced to " + choice.getName() + ".");
            return false;
        }
        DUtil.addDeity(p, choice);
        DUtil.setFavor(p, DUtil.getFavor(p) + 100);
        p.getWorld().strikeLightningEffect(e.getClickedBlock().getLocation());
        p.sendMessage(ChatColor.YELLOW + "You have been accepted to the lineage of " + choice.getName() + ".");
        return true;
    }

    private void ascendCode(Player p, PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() != Material.AIR)
                return;
            if (!DUtil.isFullParticipant(p))
                return;
            if (!DUtil.isProtectedShrine(e.getClickedBlock().getLocation()))
                return;
            Shrine inside = null;
            for (Shrine s : DSave.getShrines()) {
                if (s.getCenter().getWorld().equals(p.getWorld().getName()))
                    if (DUtil.toLocation(s.getCenter()).distance(p.getLocation()) <= s.getRadius()) {
                        inside = s;
                        break;
                    }
            }
            if ((inside != null) && DUtil.canLevelUp(p)) {
                if (DUtil.getFavor(p) >= DUtil.costForNextLevel(p)) {
                    DUtil.setFavor(p, DUtil.getFavor(p) - DUtil.costForNextLevel(p));
                    DUtil.setLevel(p, DUtil.getLevel(p) + 1);
                    p.getWorld().strikeLightningEffect(DUtil.toLocation(inside.getCenter()));
                    for (int i = 0; i < 20; i++) {
                        p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
                    }
                    p.sendMessage(ChatColor.YELLOW + "You feel uplifted as a mystical energy surges through you.");
                    p.sendMessage(ChatColor.YELLOW + "You now have " + DUtil.getLevel(p) + " Ascensions.");
                } else {
                    if (DUtil.isTitan(p))
                        p.sendMessage(ChatColor.YELLOW + "" + DUtil.costForNextLevel(p) + " Power is required to level.");
                    else
                        p.sendMessage(ChatColor.YELLOW + "" + DUtil.costForNextLevel(p) + " Favor is required to level.");
                }
            }
        }
    }
	/*
	private void helpcode(String[] args, Player p){
		if (args.length == 0){
			p.sendMessage(ChatColor.GREEN+"You have accessed the Demigods help file.");
			p.sendMessage(ChatColor.WHITE+"Choose a specific subcategory:");
			p.sendMessage(ChatColor.GRAY+"/dg general");
			p.sendMessage(ChatColor.GRAY+"/dg protect");
			p.sendMessage(ChatColor.GRAY+"/dg unprotect");
			p.sendMessage(ChatColor.GRAY+"/dg shrine");
			p.sendMessage(ChatColor.GRAY+"/dg tribute");
			p.sendMessage(ChatColor.GRAY+"/dg gods");
			p.sendMessage(ChatColor.GRAY+"/dg titans");
			p.sendMessage(ChatColor.GRAY+"/dg bind");
			p.sendMessage(ChatColor.GRAY+"/dg stats");
			if (DUtil.hasPermissionOrOP(p, "demigods.admin"))
				p.sendMessage(ChatColor.GRAY+"/dg admin");
		} else if (args.length == 1){
			if (args[0].equalsIgnoreCase("general")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("/check - Check your rank, Favor/Power, and Blessing/Glory.");
				p.sendMessage("Titans lose 10% of their Power when they die, but gain");
				p.sendMessage("more Power as a reward from killing Gods in combat.");
				p.sendMessage("Blessing and Glory are a measure of the total Favor/Power");
				p.sendMessage("you have earned. They are required to add minor Gods and");
				p.sendMessage("Titans to your allegiance.");
				p.sendMessage("To leave the alliance of a God or Titan, use "+ChatColor.GREEN+"/forsake");
			} else if (args[0].equalsIgnoreCase("protect")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("To protect an area you must have the allegiance of at least");
				p.sendMessage("one God or Titan. Type "+ChatColor.YELLOW+"/protect <deityname>"+ChatColor.WHITE+" and then choose");
				p.sendMessage("the two corners of the cuboid that you want protection for.");
				p.sendMessage("Protection requires Favor/Power proportional to the");
				p.sendMessage("volume of the selected cuboid.");
				p.sendMessage("Blocks in a protected area may only be placed or broken by");
				p.sendMessage("players who have an allegiance with that God or Titan.");
			} else if (args[0].equalsIgnoreCase("unprotect")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("To unprotect a protected zone or a shrine, you must have the");
				p.sendMessage("allegiance of the deity whose land you are unprotecting.");
				p.sendMessage("Type "+ChatColor.YELLOW+"/unprotect <deityname>"+ChatColor.WHITE+" and then click");
				p.sendMessage("on any block inside the zone you want to unprotect. If you");
				p.sendMessage("did everything correctly it will no longer be protected.");
			} else if (args[0].equalsIgnoreCase("shrine")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("Shrines are special protected zones. Tributes can only");
				p.sendMessage("happen in shrines for Gods/Titans in your allegiance. ");
				p.sendMessage("To create a shrine, place a sign with the following text:");
				p.sendMessage("Line 1: "+ChatColor.GREEN+"shrine"+ChatColor.WHITE+" Line 2: "+ChatColor.GREEN+"dedicate"+ChatColor.WHITE+" Line 3: "+ChatColor.GREEN+
						"<deityname>"+ChatColor.WHITE+" Line 4: "+ChatColor.GREEN+"<size>");
				p.sendMessage("example: [shrine][dedicate][Zeus][4] (The size defaults to 3)");
				p.sendMessage("A shrine must contain a gold block and rare blocks with");
				p.sendMessage("combined value proportional to the size of the shrine.");
			} else if (args[0].equalsIgnoreCase("tribute")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("To tribute, a chest must be placed above a gold block in");
				p.sendMessage("an established shrine to a God or Titan in your allegiance.");
				p.sendMessage("Simply place the items you wish to tribute in the chest, ");
				p.sendMessage("and click the chest to sacrifice the items to your deities.");
				p.sendMessage("You will be rewarded with Favor/Power proportional to the");
				p.sendMessage("value of the items you placed in the chest. Remember that ");
				p.sendMessage("blocks are preferable to tools.");
			} else if (args[0].equalsIgnoreCase("gods")||args[0].equalsIgnoreCase("god")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("Gods grant one passive ability, two active abilities, and");
				p.sendMessage("an ultimate. You may only choose one primary God.");
				p.sendMessage("To select a God, place an iron block and click on it while");
				p.sendMessage("it is surrounded in a 5x5 square of a single material.");
				p.sendMessage("To choose Zeus, surround the iron block with air.");
				p.sendMessage("To choose Poseidon, surround the block with water.");
				p.sendMessage("To choose Hades, surround the block with dirt.");
				p.sendMessage("To choose additional Gods, place a gold block and click on");
				p.sendMessage("it with a special item in hand.");
				p.sendMessage("For detailed information on the Gods, type "+ChatColor.YELLOW+"/dg god list");
			} else if (args[0].equalsIgnoreCase("titans")||args[0].equalsIgnoreCase("titan")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("Titans grant two passive ability, one active ability, and");
				p.sendMessage("an ultimate. You may only choose one primary Titan.");
				p.sendMessage("To select a Titan, place obsidian and click on it while");
				p.sendMessage("it is surrounded in a 5x5 square of a single material.");
				p.sendMessage("To choose Cronus, surround the obsidian block with soul sand.");
				p.sendMessage("To choose Prometheus, surround the block with lava or clay.");
				p.sendMessage("To choose Rhea, surround the block with leaves/logs.");
				p.sendMessage("For detailed info on the Titans, type "+ChatColor.YELLOW+"/dg titan list");
			} else if (args[0].equalsIgnoreCase("bind")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("Binding is applicable on certain abilities only. To bind");
				p.sendMessage("a skill to an item, simply hold an item in your hand and ");
				p.sendMessage("type the skill name as though you were activating it, and");
				p.sendMessage("add 'bind' to the end. For example, '/lightning bind'. To");
				p.sendMessage("unbind an item from a skill, use 'unbind'. For example,");
				p.sendMessage("you would use '/lightning unbind'. Not all skills are");
				p.sendMessage("compatible with binding, so try them out for yourself.");
			} else if (args[0].equalsIgnoreCase("admin")&&DUtil.hasPermissionOrOP(p, "demigods.admin")){
				p.sendMessage(ChatColor.GRAY+"---");
				p.sendMessage("/removedeity <playername> <deityname>"+ChatColor.GRAY+" Remove a player's");
				p.sendMessage(ChatColor.GRAY+"deity.");
				p.sendMessage("/givedeity <playername> <deityname>"+ChatColor.GRAY+" Give a player a deity.");
				p.sendMessage("/checkplayer <playername> "+ChatColor.GRAY+"Do a "+ChatColor.WHITE+"/check"+ChatColor.GRAY+" on a player.");
				p.sendMessage("/setfavor <playername> <amount> "+ChatColor.GRAY+"Set a player's Power");
				p.sendMessage(ChatColor.GRAY+"or Favor to a certain amount.");
				p.sendMessage("/reviveplayer <playername> "+ChatColor.GRAY+"Bring a phantom player back to life.");
				p.sendMessage("Pro tip: To protect a large area from the general public for");
				p.sendMessage("free, give yourself the deity Omni with "+ChatColor.GOLD+"/givedeity <name> omni");
				p.sendMessage("then use "+ChatColor.GOLD+"/protect omni"+ChatColor.WHITE+" to protect cuboids.");
				p.sendMessage("Great for spawn or big arenas. (Omni has no other use)");
			} else if (args[0].equalsIgnoreCase("stats")){
				int numtitans = 0;
				int numgods = 0;
				int numtitankills = 0;
				int numgodkills = 0;
				int numtitandeaths = 0;
				int numgoddeaths = 0;
				int numtitansonline = 0;
				int numgodsonline = 0;
				ArrayList<String> titans = new ArrayList<String>();
				ArrayList<String> gods = new ArrayList<String>();
				for (PlayerInfo pi : plugin.getMaster()){
					if (plugin.isGod(pi.getPlayer())){
						numgods++;
						numgodkills+=pi.getKills();
						numgoddeaths+=pi.getDeaths();
						if (DUtil.getPlayer(pi.getPlayer()).isOnline()){
							numgodsonline++;
							gods.add(pi.getPlayer());
						}
					} else if (plugin.isTitan(pi.getPlayer())){
						numtitans++;
						numtitankills+=pi.getKills();
						numtitandeaths+=pi.getDeaths();
						if (DUtil.getPlayer(pi.getPlayer()).isOnline()){
							numtitansonline++;
							titans.add(pi.getPlayer());
						}
					}
				}
				p.sendMessage(ChatColor.GRAY+"----Stats----");
				String str1 = "";
				if (gods.size()>0){
					for (String g : gods){
						str1 += g+", ";
					}
					str1 = str1.substring(0, str1.length()-2);
				}
				String str2 = "";
				if (titans.size()>0){
					for (String t : titans){
						str2 += t+", ";
					}
					str2 = str2.substring(0, str2.length()-2);
				}
				p.sendMessage("There are "+ChatColor.GREEN+numgodsonline+"/"+ChatColor.YELLOW+numgods+ChatColor.WHITE+" Gods online: "+ChatColor.GOLD+str1);
				p.sendMessage("There are "+ChatColor.GREEN+numtitansonline+"/"+ChatColor.YELLOW+numtitans+ChatColor.WHITE+" Titans online: "+ChatColor.GOLD+str2);
				p.sendMessage("Total God kills: "+ChatColor.GREEN+numgodkills+ChatColor.YELLOW+" --- "+ChatColor.WHITE+" Total Titan kills: "+ChatColor.RED+numtitankills);
				p.sendMessage("God K/D Ratio: "+ChatColor.GREEN+((float)numgodkills/numgoddeaths)+ChatColor.YELLOW+" --- "+ChatColor.WHITE+
						" Titan K/D Ratio: "+ChatColor.RED+((float)numtitankills/numtitandeaths));
				if (plugin.isTitan(p)||plugin.isGod(p)){
					p.sendMessage("Your kills: "+ChatColor.GREEN+plugin.getInfo(p).getKills()+ChatColor.WHITE+" Your deaths: "+ChatColor.RED+plugin.getInfo(p).getDeaths());
					p.sendMessage("Your K/D Ratio: "+ChatColor.YELLOW+((float)plugin.getInfo(p).getKills()/plugin.getInfo(p).getDeaths()));
				}
			}
		} else if (args.length == 2){
			if (args[0].equalsIgnoreCase("god")){
				if (args[1].equalsIgnoreCase("list")){
					p.sendMessage(ChatColor.GRAY+"---");
					p.sendMessage(ChatColor.GOLD+"Primary Gods:");
					p.sendMessage("Zeus - God of the sky.");
					p.sendMessage("Poseidon - God of the seas.");
					p.sendMessage("Hades - God of the underworld.");
					p.sendMessage(ChatColor.GOLD+"Minor Gods:");
					p.sendMessage("Ares - God of war.");
					p.sendMessage("Athena - God of wisdom.");
					p.sendMessage("Hephaestus - God of the forge.");
					p.sendMessage("For more info on a specific God, type "+ChatColor.YELLOW+"/dg god <name>");
				} else if (args[1].equalsIgnoreCase("zeus")){
					p.sendMessage(ChatColor.GRAY+"---Zeus");
					p.sendMessage("Passive: Take no fall damage.");
					p.sendMessage("Active: Call a lightning strike at a target location. "+ChatColor.GREEN+"/lightning");
					p.sendMessage("Costs 25 Favor. Can bind.");
					p.sendMessage("Active: Lift a target entity up in the air. "+ChatColor.GREEN+"/lift");
					p.sendMessage("Costs 50 Favor. Can bind.");
					p.sendMessage("Ultimate: Cause a lightning storm, killing nearby entities. "+ChatColor.GREEN+"/storm");
					p.sendMessage("Costs 1000 Favor, 25 minute cooldown.");
				} else if (args[1].equalsIgnoreCase("poseidon")){
					p.sendMessage(ChatColor.GRAY+"---Poseidon");
					p.sendMessage("Passive: Cannot drown, and heals quickly underwater.");
					p.sendMessage("Active: For 30 seconds, break blocks underwater instantly. "+ChatColor.GREEN+"/waterbreak");
					p.sendMessage("Costs 100 Favor.");
					p.sendMessage("Active: Liquefy a non-player entity, turning them into water. "+ChatColor.GREEN+"/liquefy");
					p.sendMessage("Costs 25 Favor. Can bind.");
					p.sendMessage("Ultimate: Fling a TNT loaded trident to the ground at target location. "+ChatColor.GREEN+"/prime");
					p.sendMessage("Costs 800 Favor, 25 minute cooldown.");
				} else if (args[1].equalsIgnoreCase("hades")){
					p.sendMessage(ChatColor.GRAY+"---Hades");
					p.sendMessage("Passive: Immune to zombie and skeleton damage.");
					p.sendMessage("Passive: Nearby zombies and skeletons do not burn in sunlight.");
					p.sendMessage("Active: Cause nearby zombies and skeletons to attack a target. ");
					p.sendMessage(ChatColor.GREEN+"/target"+ChatColor.WHITE+" Can bind.");
					p.sendMessage("Active: Summon a servant from the depths of Tartarus. ");
					p.sendMessage(""+ChatColor.GREEN+"/raisedead"+ChatColor.WHITE+" Costs 50 Favor. Can bind.");
					p.sendMessage("Active: Freeze a target entity in place for 5 seconds.");
					p.sendMessage(ChatColor.GREEN+"/snare"+ChatColor.WHITE+" Can bind.");
					p.sendMessage("Ultimate: Drop nearby entities into the Void. "+ChatColor.GREEN+"/tartarus");
					p.sendMessage("Costs 1000 Favor, 25 minute cooldown.");
				} else if (args[1].equalsIgnoreCase("ares")){
					p.sendMessage(ChatColor.GRAY+"---Ares");
					p.sendMessage("Passive: Attacking an entity continuously gives a finishing");
					p.sendMessage("bonus of Favor.");
					p.sendMessage("Active: For one minute, heal equal to the damage you deal.");
					p.sendMessage("Costs 50 Favor. "+ChatColor.GREEN+"/bloodthirst");
					p.sendMessage("Active: Fling the sword in your hand at a target (ranged damage).");
					p.sendMessage("Costs 4 Favor. Can bind. "+ChatColor.GREEN+"/fling");
					p.sendMessage("Ultimate: Call up to 20 tamed wolves to your side.");
					p.sendMessage("Costs 50 Favor per wolf. "+ChatColor.GREEN+"/pound");
					p.sendMessage(ChatColor.RED+"Selection item: Diamond Sword");
				} else if (args[1].equalsIgnoreCase("athena")){
					p.sendMessage(ChatColor.GRAY+"---Athena");
					p.sendMessage("Passive: Athena may protect you from attack.");
					p.sendMessage("Active: Spy on a player. Learn their deities and amount of");
					p.sendMessage("health. Costs no Favor. "+ChatColor.GREEN+"/spy <playername>");
					p.sendMessage("Active: Call a ceasefire for 30 seconds. No damage can be");
					p.sendMessage("dealt during this time. Costs 80 Favor. "+ChatColor.GREEN+"/ceasefire");
					p.sendMessage("Ultimate: Double the attack power and halve the amount of da-");
					p.sendMessage("mage taken by nearby allies for one minute. Costs 600 Favor.");
					p.sendMessage(ChatColor.GREEN+"/specialtactics");
					p.sendMessage(ChatColor.RED+"Selection item: Bookshelf");
				} else if (args[1].equalsIgnoreCase("hephaestus")){
					p.sendMessage(ChatColor.GRAY+"---Hephaestus");
					p.sendMessage("Passive: Nearby furnaces produce twice as many items.");
					p.sendMessage("Active: Repair the item you're holding in hand. Cost is ");
					p.sendMessage("determined by the durability of the item. "+ChatColor.GREEN+"/fix");
					p.sendMessage("Active: Smith a block of iron, gold, or diamond into a ");
					p.sendMessage("full suit of armor. Costs 50 Favor. "+ChatColor.GREEN+"/forge");
					p.sendMessage("Ultimate: Reduce the durability of the items nearby titans ");
					p.sendMessage("and mortals are holding in hand. If the item is a tool, it");
					p.sendMessage("will have 1 use left. Costs 600 Favor. "+ChatColor.GREEN+"/shatter");
					p.sendMessage(ChatColor.RED+"Selection item: Lava Bucket");
				} else if (args[1].equalsIgnoreCase("rank")||args[1].equalsIgnoreCase("ranks")||args[1].equalsIgnoreCase("ranking")){
					Collection<PlayerInfo> list = plugin.getMaster();
					if (list.size()<1){
						p.sendMessage("There are no players to rank.");
						return;
					}
					ArrayList<PlayerInfo> gods = new ArrayList<PlayerInfo>();
					for (PlayerInfo pi : list){
						if (pi.isGod())
							gods.add(pi);
					}
					PlayerInfo[] Gods = new PlayerInfo[gods.size()];
					for (int i=0;i<gods.size();i++)
						Gods[i] = gods.get(i);
					for (int i=0;i<Gods.length;i++){
						int highestIndex = i;
						int highestBless = Gods[i].getRanking();
						for (int j=i;j<Gods.length;j++){
							if (Gods[j].getRanking() > highestBless){
								highestIndex = j;
								highestBless = Gods[j].getRanking();
							}
						}
						PlayerInfo temp = Gods[i];
						Gods[i] =  Gods[highestIndex];
						Gods[highestIndex] = temp;
					}
					p.sendMessage(ChatColor.GOLD+"Top 10 Gods:");
					if (Gods.length >= 5)
						for (int i=0;i<5;i++){
							p.sendMessage((i+1)+"- "+Gods[i].getPlayer()+": "+Gods[i].getFavor()+" Favor, "+Gods[i].getBlessing()+" Blessing, "+Gods[i].getAllegiance().size()+" Gods.");
						}
					else
						for (int i=0;i<Gods.length;i++){
							p.sendMessage((i+1)+"- "+Gods[i].getPlayer()+": "+Gods[i].getFavor()+" Favor, "+Gods[i].getBlessing()+" Blessing, "+Gods[i].getAllegiance().size()+" Gods.");
						}
				}
			} else if (args[0].equalsIgnoreCase("titan")){
				if (args[1].equalsIgnoreCase("list")){
					p.sendMessage(ChatColor.GRAY+"---");
					p.sendMessage(ChatColor.GOLD+"Primary Titans:");
					p.sendMessage("Cronus - Titan of chaos and time.");
					p.sendMessage("Prometheus - Titan of fire, wisdom, and humanity.");
					p.sendMessage("Rhea - Titan of the earth.");
					p.sendMessage(ChatColor.GOLD+"Minor Titans:");
					p.sendMessage("Hyperion - Titan of light and the sun.");
					p.sendMessage("Typhon - Titan of power, father of monsters.");
					p.sendMessage("Oceanus - Titan of the seas.");
					p.sendMessage("For more info on a specific Titan, type "+ChatColor.YELLOW+"/dg titan <name>");
				} else if (args[1].equalsIgnoreCase("cronus")){
					p.sendMessage(ChatColor.GRAY+"---Cronus");
					p.sendMessage("Passive: Take reduced damage from all sources. Can only die ");
					p.sendMessage("to direct attacks.");
					p.sendMessage("Passive: Attacking with a sickle (hoe) does extra damage.");
					p.sendMessage("Active: Use a powerful draining attack. "+ChatColor.GREEN+"/cleave");
					p.sendMessage("Costs 10 Power per hit.");
					p.sendMessage("Ultimate: Reduce movement speed of all entities for one minute. "+ChatColor.GREEN+"/slow");
					p.sendMessage("Costs 800 Power.");
				} else if (args[1].equalsIgnoreCase("prometheus")){
					p.sendMessage(ChatColor.GRAY+"---Prometheus");
					p.sendMessage("Passive: Immune to fire.");
					p.sendMessage("Passive: Slowly recovers health.");
					p.sendMessage("Active: Shoot a fireball. "+ChatColor.GREEN+"/fireball");
					p.sendMessage("Costs 15 Power. Can bind.");
					p.sendMessage("Ultimate: Fireballs fall like meteors to smite nearby entities. ");
					p.sendMessage(ChatColor.GREEN+"/firestorm");
					p.sendMessage("Costs 1000 Power.");
				} else if (args[1].equalsIgnoreCase("rhea")){
					p.sendMessage(ChatColor.GRAY+"---Rhea");
					p.sendMessage("Passive: Clicking saplings and crops makes them grow fully.");
					p.sendMessage("Right clicking a sapling changes its type.");
					p.sendMessage("Passive: Eating a log (left click) heals half a heart.");
					p.sendMessage("Active: Place a tree that can be detonated. "+ChatColor.GREEN+"/plant");
					p.sendMessage("Multiple trees can be spawned and all detonated at once.");
					p.sendMessage("Costs 100 Power. Can bind.");
					p.sendMessage("Ultimate: Nature reclaims its land by covering an existing chunk");
					p.sendMessage("with stone, dirt, and trees. "+ChatColor.GREEN+"/terraform");
					p.sendMessage("Costs 1000 Power.");
				} else if (args[1].equalsIgnoreCase("hyperion")){
					p.sendMessage(ChatColor.GRAY+"---Hyperion");
					p.sendMessage("Passive: Deal 1.5x damage while in a well lit area.");
					p.sendMessage("Passive: Right click netherrack or soul sand to transmute it");
					p.sendMessage("into glowstone.");
					p.sendMessage("Active: Cause a non-player entity to combust into flames. ");
					p.sendMessage(ChatColor.GREEN+"/combust"+ChatColor.WHITE+" Costs 25 Power. Can bind.");
					p.sendMessage("Ultimate: Strike down your foes with the power of the sun.");
					p.sendMessage(ChatColor.GREEN+"/smite"+ChatColor.WHITE+" Costs 1100 Power.");
					p.sendMessage(ChatColor.RED+"Selection item: Glowstone");
				} else if (args[1].equalsIgnoreCase("typhon")){
					p.sendMessage(ChatColor.GRAY+"---Typhon");
					p.sendMessage("Passive: Super knockback when attacking others.");
					p.sendMessage("Passive: Create a huge explosion on death.");
					p.sendMessage("Active: Teleport to a target entity and deal damage to them.");
					p.sendMessage(ChatColor.GREEN+"/charge"+ChatColor.WHITE+" Costs 20 Power. Can bind.");
					p.sendMessage("Ultimate: Attack all nearby entities, striking each with");
					p.sendMessage("a crushing blow. "+ChatColor.GREEN+"/fury "+ChatColor.WHITE+"Costs 750 Power. 1 minute cooldown.");
					p.sendMessage(ChatColor.RED+"Selection item: Diamond Sword");
				} else if (args[1].equalsIgnoreCase("oceanus")){
					p.sendMessage(ChatColor.GRAY+"---Oceanus");
					p.sendMessage("Passive: Heal as long it is raining.");
					p.sendMessage("Passive: Punching enemies deals critical damage if they are");
					p.sendMessage("standing in water.");
					p.sendMessage("Passive: Right click an enemy with no item equipped to create");
					p.sendMessage("a rapidly shrinking pool of water under them.");
					p.sendMessage("Active: Launch a terribly inaccurate exploding squid at a");
					p.sendMessage("target entity. "+ChatColor.GREEN+"/squid"+ChatColor.WHITE+" Costs 75 Power. Can bind.");
					p.sendMessage("Ultimate: Cause a rainstorm in your world.");
					p.sendMessage(ChatColor.GREEN+"/makeitrain"+ChatColor.WHITE+" Costs 250 Power.");
					p.sendMessage(ChatColor.RED+"Selection item: Bucket of Water");
				} else if (args[1].equalsIgnoreCase("rank")||args[1].equalsIgnoreCase("ranks")||args[1].equalsIgnoreCase("ranking")){
					Collection<PlayerInfo> list = plugin.getMaster();
					if (list.size()<1){
						p.sendMessage("There are no players to rank.");
						return;
					}
					ArrayList<PlayerInfo> titans = new ArrayList<PlayerInfo>();
					for (PlayerInfo pi : list){
						if (pi.isTitan())
							titans.add(pi);
					}
					PlayerInfo[] Titans = new PlayerInfo[titans.size()];
					for (int i=0;i<titans.size();i++)
						Titans[i] = titans.get(i);
					for (int i=0;i<Titans.length;i++){
						int highestIndex = i;
						int highestBless = Titans[i].getRanking();
						for (int j=i;j<Titans.length;j++){
							if (Titans[j].getRanking() > highestBless){
								highestIndex = j;
								highestBless = Titans[j].getRanking();
							}
						}
						PlayerInfo temp = Titans[i];
						Titans[i] =  Titans[highestIndex];
						Titans[highestIndex] = temp;
					}
					p.sendMessage(ChatColor.GOLD+"Top 10 Titans:");
					if (Titans.length >= 10)
						for (int i=0;i<10;i++){
							p.sendMessage((i+1)+"- "+Titans[i].getPlayer()+": "+Titans[i].getPower()+" Power, "+Titans[i].getGlory()+" Glory, "+Titans[i].getAllegiance().size()+" Titans.");
						}
					else
						for (int i=0;i<Titans.length;i++){
							p.sendMessage((i+1)+"- "+Titans[i].getPlayer()+": "+Titans[i].getPower()+" Power, "+Titans[i].getGlory()+" Glory, "+Titans[i].getAllegiance().size()+" Titans.");
						}
				}
			}
		}
	}

	private void onPlayerCommandPreprocessDemigods(PlayerCommandPreprocessEvent event){
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		String[] sects = event.getMessage().split(" +", 2);
		String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
		Commands cmd;
		try {
			cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
		} catch (Exception ex) {
			return;
		}
		try {
			switch (cmd){
			case CHECK:
				if (plugin.isGod(player)){
					PlayerInfo pi = plugin.getInfo(player);
					player.sendMessage(ChatColor.YELLOW+"--"+player.getName()+"--"+pi.getRank()+"");
					String send = "Your deities are: ";
					for (int i=0;i<pi.getAllegiance().size();i++){
						send+=pi.getAllegiance().get(i).name()+" ";
					}
					player.sendMessage(send);
					player.sendMessage("Current favor: "+pi.getFavor());
					player.sendMessage("Current blessing: "+pi.getBlessing());
					if (pi.isEligible()){
						player.sendMessage(ChatColor.GREEN+"You are eligible for alliance with another God.");
					} else if (!pi.getRank().equals("Olympian") && !pi.isEligible())
						player.sendMessage("<"+pi.blessingTillNext()+" Blessing until next rank>");
				} else if (plugin.isTitan(player)){
					PlayerInfo pi = plugin.getInfo(player);
					player.sendMessage(ChatColor.YELLOW+"--"+player.getName()+"--"+pi.getRank()+"");
					String send = "Your deities are: ";
					for (int i=0;i<pi.getAllegiance().size();i++){
						send+=pi.getAllegiance().get(i).name()+" ";
					}
					player.sendMessage(send);
					player.sendMessage("Current power: "+pi.getPower());
					player.sendMessage("Current glory: "+pi.getGlory());
					if (pi.costForNext()<=pi.getGlory())
						player.sendMessage(ChatColor.GREEN+"You have enough Glory to purchase a new alliance.");
					else
						player.sendMessage("<"+(pi.costForNext()-pi.getGlory())+" Glory until next rank>");
				} else {
					player.sendMessage(ChatColor.YELLOW+"--"+player.getName()+"--Mortal--");
					player.sendMessage("You are not affiliated with any Gods or Titans.");
				}
				break;
			case PROTECT:
				if (args.length != 1){
					player.sendMessage("Correct syntax: "+ChatColor.YELLOW+"/protect <deityname>");
					break;
				}
				if (!plugin.isGod(player)&&!plugin.isTitan(player))
					break;
				try {
					Divine d = Divine.valueOf(args[0].toUpperCase());
					if (plugin.getInfo(player).hasDeity(d)){
						if (plugin.getInfo(player).getProtect()==0){
							player.sendMessage("You are requesting cuboid protection from "+d.name()+".");
							plugin.getInfo(player).startProtect();
							plugin.getInfo(player).setTempDeity(d);
							player.sendMessage("Click the first corner block.");
						} else player.sendMessage("You are already in the process of cuboid protection.");
					} else player.sendMessage("You can't protect land in the name of deities you don't have.");
				} catch (Exception nullpointer){
					player.sendMessage("Error. Does that deity exist?");
				}
				break;
			case UNPROTECT:
				if (args.length != 1){
					player.sendMessage("Correct syntax: "+ChatColor.YELLOW+"/unprotect <deityname>");
					break;
				}
				try {
					Divine dd = Divine.valueOf(args[0].toUpperCase());
					if (plugin.getInfo(player).getUnprotect()){
						player.sendMessage("Click a block protected by "+dd.name()+" in the area to unprotect.");
						break;
					}
					if (plugin.getInfo(player).hasDeity(dd)){
						if (plugin.getInfo(player).getProtect()==0){
							player.sendMessage("Click a block protected by "+dd.name()+" in the area to unprotect.");
							plugin.getInfo(player).setUnprotect(true);
							plugin.getInfo(player).setTempDeity(dd);
						}
					} else {
						player.sendMessage("You cannot unprotect other deities' zones.");
					}
				} catch (Exception nullpointer){
					player.sendMessage("Error. Does that deity exist?");
				}
				break;
			case DG:
			case DEMIGODS:
				helpcode(args, player);
				break;
			case CHECKPLAYER:
				if (DUtil.hasPermissionOrOP(player, "demigods.check")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length == 1){
						try {
							Player pp;
							pp = plugin.getServer().getPlayer(args[0]);
							if (pp == null)
								pp = plugin.getServer().matchPlayer(args[0]).get(0);
							if (plugin.isGod(pp)){
								PlayerInfo pi = plugin.getInfo(pp);
								player.sendMessage(ChatColor.YELLOW+"--"+pp.getName()+"--"+pi.getRank()+"--");
								String send = pp.getName()+"'s deities are: ";
								for (int i=0;i<pi.getAllegiance().size();i++){
									send+=pi.getAllegiance().get(i).name()+" ";
								}
								player.sendMessage(send);
								player.sendMessage("Current favor: "+pi.getFavor());
								player.sendMessage("Current blessing: "+pi.getBlessing());
							} else if (plugin.isTitan(pp)){
								PlayerInfo pi = plugin.getInfo(pp);
								player.sendMessage(ChatColor.YELLOW+"--"+pp.getName()+"--"+pi.getRank()+"--");
								String send = pp.getName()+"'s deities are: ";
								for (int i=0;i<pi.getAllegiance().size();i++){
									send+=pi.getAllegiance().get(i).name()+" ";
								}
								player.sendMessage(send);
								player.sendMessage("Current power: "+pi.getPower());
								player.sendMessage("Current glory: "+pi.getGlory());
							} else {
								player.sendMessage(ChatColor.YELLOW+"--"+pp.getName()+"--Mortal--");
								player.sendMessage(pp.getName()+" is not affiliated with any Gods or Titans.");
							}
						} catch (Exception error){
							player.sendMessage(ChatColor.RED+"Error. Was player's name typed correctly?");
						}
					} else {
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/checkplayer <playername>");
					}
				}
				break;
			case GIVEDEITY:
				if (DUtil.hasPermissionOrOP(player, "demigods.give")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length == 2){
						try {
							Player p = plugin.getServer().getPlayer(args[0]);
							if (plugin.isGod(p)||plugin.isTitan(p)){
								PlayerInfo pi = plugin.getInfo(p);
								if (!pi.hasDeity(Divine.valueOf(args[1].toUpperCase()))){
									pi.addToAllegiance(Divine.valueOf(args[1].toUpperCase()));
									player.sendMessage(p.getName()+" now has the powers of "+args[1].toUpperCase());
									player.sendMessage("Skills may not work if there is a god/titan mismatch.");
								} else {
									player.sendMessage(p.getName()+" already has that deity.");
								}
							} else {
								Divine de = Divine.valueOf(args[1].toUpperCase());
								boolean god;
								if (de != null){
									switch (de){
									case ZEUS: case POSEIDON: case HADES: god = true; break;
									default: god = false; break;
									}
									if (god){
										plugin.addToMaster(new PlayerInfo(p,"God",de));
										player.sendMessage("Success!");
									} else {
										plugin.addToMaster(new PlayerInfo(p,"Titan",de));
										player.sendMessage("Success!");
									}
									plugin.saveAll();
								} else {
									player.sendMessage("That is not a recognized deity.");
									break;
								}
							}
						} catch (Exception error){
							player.sendMessage(ChatColor.RED+"Error. Was player's name typed correctly?");
						}
					} else {
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/givedeity <playername> <deityname>");
						player.sendMessage("Giving a God aligned player a Titan deity will not work.");
						player.sendMessage("Giving a Titan aligned player a God deity will not work.");
					}
				}
				break;
			case REMOVEDEITY:
				if (DUtil.hasPermissionOrOP(player,"demigods.remove")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length != 2){
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/removedeity <playername> <deityname>");
						break;
					}
					try {
						Player p = plugin.getServer().getPlayer(args[0]);
						Divine de = Divine.valueOf(args[1].toUpperCase());
						if ((p!=null) && (de!=null)){
							PlayerInfo pi = plugin.getInfo(p);
							if (pi.hasDeity(de)){
								pi.removeFromAllegiance(de);
								if (pi.getAllegiance().size()==0){
									plugin.removeFromMaster(p);
								}
								plugin.saveAll();
								player.sendMessage("Success!");
							} else {
								player.sendMessage(p.getName()+" doesn't have that deity.");
							}
						} else {
							player.sendMessage("Error. Were the player and deity names typed correctly?");
						}
					} catch (Exception error){
						player.sendMessage(ChatColor.RED+"Error. Was player's name typed correctly?");
					}
				}
				break;
			case SETFAVOR: case SETPOWER: case SETGLORY: case SETBLESSING:
				if (DUtil.hasPermissionOrOP(player,"demigods.set")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length != 2){
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/setfavor <playername> <amount of favor>");
						break;
					}
					try {
						Player p = plugin.getServer().getPlayer(args[0]);
						int amt = Integer.parseInt(args[1]);
						if (plugin.isGod(p)){
							PlayerInfo gpi = plugin.getInfo(p);
							gpi.setFavor(amt);
							gpi.setBlessing(amt);
							player.sendMessage("Success!");
						} else if (plugin.isTitan(p)){
							PlayerInfo tpi = plugin.getInfo(p);
							tpi.setPower(amt);
							tpi.setGlory(amt);
							player.sendMessage("Success!");
						} else {
							player.sendMessage("That player is not a God or a Titan.");
						}
					} catch (Exception error){
						player.sendMessage(ChatColor.RED+"Error. Did you mis-spell something?");
					}
				}
				break;
			case FORSAKE:
				if (!plugin.isGod(player)&&!plugin.isTitan(player))
					break;
				if (!DUtil.hasPermissionOrOP(player, "demigods.admin")&&!DUtil.hasPermission(player, "demigods.forsake"))
					break;
				if (args.length != 1){
					player.sendMessage("Syntax: "+ChatColor.YELLOW+"/forsake <deityname>"+ChatColor.WHITE+" or "+ChatColor.YELLOW+"/forsake all");
					break;
				}
				try {
					if (args[0].equalsIgnoreCase("all")){
						plugin.removeFromMaster(player);
						player.sendMessage(ChatColor.RED+"You are no longer a demigods/Demititan.");
					} else {
						Divine remove = Divine.valueOf(args[0].toUpperCase());
						if (plugin.getInfo(player).hasDeity(remove)){
							if (plugin.getInfo(player).getAllegiance().size()>1){
								if (plugin.isGod(player)){
									PlayerInfo gpi = plugin.getInfo(player);
									gpi.removeFromAllegiance(remove);
									gpi.setBlessing(gpi.getBlessing()/2);
									gpi.setFavor(gpi.getFavor()/2);
									player.sendMessage(ChatColor.RED+"You have forsaken "+remove.name()+".");
									player.sendMessage(ChatColor.RED+"Your Blessing and Favor have been halved.");
								} else if (plugin.isTitan(player)) {
									PlayerInfo tpi = plugin.getInfo(player);
									tpi.removeFromAllegiance(remove);
									tpi.setGlory(tpi.getGlory()/2);
									tpi.setPower(tpi.getPower()/2);
									player.sendMessage(ChatColor.RED+"You have forsaken "+remove.name()+".");
									player.sendMessage(ChatColor.RED+"Your Power and Glory have been halved.");
								}
							} else {
								plugin.removeFromMaster(player);
								player.sendMessage(ChatColor.RED+"You have forsaken "+remove.name()+".");
							}
						}
					}
				} catch (Exception error){
					player.sendMessage(ChatColor.RED+"Error. Was the deity's name spelled right?");
				}
				break;
			case ADDFAVOR: case ADDPOWER:
				if (DUtil.hasPermissionOrOP(player,"demigods.add")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length != 2){
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/addfavor <playername> <amount of favor>");
						break;
					}
					try {
						Player p = plugin.getServer().getPlayer(args[0]);
						int amt = Integer.parseInt(args[1]);
						if (plugin.isGod(p)){
							PlayerInfo gpi = plugin.getInfo(p);
							gpi.setFavor(gpi.getFavor()+amt);
							player.sendMessage("Success! "+p.getName()+" now has "+gpi.getFavor()+" Favor.");
						} else if (plugin.isTitan(p)){
							PlayerInfo tpi = plugin.getInfo(p);
							tpi.setPower(tpi.getPower()+amt);
							player.sendMessage("Success! "+p.getName()+" now has "+tpi.getPower()+" Power.");
						} else {
							player.sendMessage("That player is not a God or a Titan.");
						}
					} catch (Exception error){
						player.sendMessage(ChatColor.RED+"Error. Did you mis-spell something?");
					}
				}
				break;
			case ADDBLESSING: case ADDGLORY:
				if (DUtil.hasPermissionOrOP(player,"demigods.add")||DUtil.hasPermissionOrOP(player, "demigods.admin")){
					if (args.length != 2){
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/addblessing <playername> <amount of blessing>");
						break;
					}
					try {
						Player p = plugin.getServer().getPlayer(args[0]);
						int amt = Integer.parseInt(args[1]);
						if (plugin.isGod(p)){
							PlayerInfo gpi = plugin.getInfo(p);
							gpi.setBlessing(gpi.getBlessing()+amt);
							player.sendMessage("Success! "+p.getName()+" now has "+gpi.getBlessing()+" Blessing.");
						} else if (plugin.isTitan(p)){
							PlayerInfo tpi = plugin.getInfo(p);
							tpi.setGlory(tpi.getGlory()+amt);
							player.sendMessage("Success! "+p.getName()+" now has "+tpi.getGlory()+" Glory.");
						} else {
							player.sendMessage("That player is not a God or a Titan.");
						}
					} catch (Exception error){
						player.sendMessage(ChatColor.RED+"Error. Did you mis-spell something?");
					}
				}
				break;
			case UNSTICK:
				if (DUtil.hasPermissionOrOP(player, "demigods.admin")) {
					if (args.length != 1) {
						player.sendMessage("Syntax: "+ChatColor.YELLOW+"/unstick <playername>");
						break;
					}
					try {
						Player p = plugin.getServer().getPlayer(args[0]);
						plugin.removeFromMaster(p);
						player.sendMessage(p.getName()+" has been removed from the active list.");
					} catch (Exception error) {
						player.sendMessage(ChatColor.RED+"Error. Did you mis-spell something?");
					}
				}
			}
		} catch (NoSuchMethodError ex) {
			player.sendMessage("The plugin for " + sects[0].toLowerCase() + " is broken or out of date.");
		}  catch (Exception ex) {
			player.sendMessage("�cError: " + ex.getMessage());
			ex.printStackTrace();
		}
		event.setCancelled(true);
	}
	public enum Commands {
		CHECK, PROTECT, UNPROTECT, DG, DEMIGODS, GIVEDEITY, REMOVEDEITY, CHECKPLAYER,
		SETFAVOR, SETPOWER, SETGLORY, SETBLESSING, FORSAKE, ADDFAVOR, ADDGLORY,
		ADDPOWER, ADDBLESSING, UNSTICK
	}
	private void onPlayerInteractDemigods(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (!(plugin.isGod(p)||plugin.isTitan(p)))
			return;
		if (plugin.getInfo(p).getUnprotect()){
			for (DeityLocale dl : plugin.getAllLocs()){
				PlayerInfo pi = plugin.getInfo(p);
				if (pi.hasDeity(dl.getDeity())){
					for (Cuboid c : dl.getLocale()){
						if (c.isInCuboid(e.getClickedBlock().getLocation())){
							dl.removeCuboid(c);
							e.setCancelled(true);
							p.sendMessage("This area is no longer protected by "+ChatColor.YELLOW+pi.getTempDeity().name()+ChatColor.WHITE+".");
							plugin.getInfo(p).setUnprotect(false);
							return;
						}
					}
				}
			}
			p.sendMessage("Did not detect a shrine or protected area to unprotect.");
			plugin.getInfo(p).setUnprotect(false);
		}
		if (!(plugin.isGod(p)||plugin.isTitan(p)))
			return;
		if (e.getClickedBlock()!=null){
			if (plugin.getInfo(p).getProtect()==0)
				return;
			else if (plugin.getInfo(p).getProtect()==1){
				p.sendMessage("First corner selected.");
				plugin.getInfo(p).setCorner(DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
				plugin.getInfo(p).incrementProtect();
				e.setCancelled(true);
			}
			else if (plugin.getInfo(p).getProtect()==2){
				p.sendMessage("Second corner selected...");
				Cuboid cu = new Cuboid(p.getWorld().getName(),plugin.getInfo(p).getCorner(),DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
				if (plugin.getInfo(p).getTempDeity()==Divine.OMNI){
					p.sendMessage(ChatColor.YELLOW+""+plugin.getInfo(p).getTempDeity()+ChatColor.WHITE+" has protected the "+cu.getVolume()+" blocks");
					if (plugin.getLoc(plugin.getInfo(p).getTempDeity())==null)
						plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
					plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
				} else if (plugin.isGod(p)){
					PlayerInfo gpi = plugin.getInfo(p);
					if (gpi.getFavor() >= (cu.getVolume()*(0.7))){
						gpi.setFavor(gpi.getFavor()-cu.getVolume());
						p.sendMessage(ChatColor.YELLOW+""+gpi.getTempDeity()+ChatColor.WHITE+" has protected the "+cu.getVolume()+" blocks");
						p.sendMessage("in this cuboid at the cost of "+cu.getVolume()+" Favor.");
						if (plugin.getLoc(plugin.getInfo(p).getTempDeity())==null)
							plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
						plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
					} else
						p.sendMessage("You need "+cu.getVolume()+" Favor to protect this area.");
				}
				else if (plugin.isTitan(p)){
					PlayerInfo tpi = plugin.getInfo(p);
					if (tpi.getPower() >= (cu.getVolume()*(0.7))){
						tpi.setPower(tpi.getPower()-cu.getVolume());
						p.sendMessage(ChatColor.YELLOW+""+tpi.getTempDeity()+ChatColor.WHITE+" has protected the "+cu.getVolume()+" blocks");
						p.sendMessage("in this cuboid at the cost of "+cu.getVolume()+" Power.");
						if (plugin.getLoc(plugin.getInfo(p).getTempDeity())==null)
							plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
						plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
					} else
						p.sendMessage("You need "+cu.getVolume()+" Power to protect this area.");
				}
				plugin.getInfo(p).doneProtect();
				e.setCancelled(true);
			}
		}
	}
	 */
}
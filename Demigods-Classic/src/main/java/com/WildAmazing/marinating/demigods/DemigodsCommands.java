package com.WildAmazing.marinating.demigods;

import com.WildAmazing.marinating.demigods.Gods.Ares;
import com.WildAmazing.marinating.demigods.Gods.Hades;
import com.WildAmazing.marinating.demigods.Gods.Poseidon;
import com.WildAmazing.marinating.demigods.Gods.Zeus;
import com.WildAmazing.marinating.demigods.Titans.Cronus;
import com.WildAmazing.marinating.demigods.Titans.Prometheus;
import com.WildAmazing.marinating.demigods.Titans.Rhea;
import com.WildAmazing.marinating.demigods.Utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DemigodsCommands implements CommandExecutor {

    int WARPCOOLDOWNTIME = 120; //seconds
    double TRIBUTEMULTIPLIER = 2;
    double TRANSFERTAX = 0.0825;
    double SHRINEBONUS = 1.75;
    private Material SELECTMATERIAL = Material.ENCHANTMENT_TABLE;
    Demigods plugin;

    public DemigodsCommands(Demigods d) {
        plugin = d;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        Player p = null;
        if (sender instanceof Player) p = (Player) sender;
        if (c.getName().equalsIgnoreCase("dg") && (p != null)) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.YELLOW + "[Demigods] Information Directory");
                p.sendMessage(ChatColor.GRAY + "/dg god");
                p.sendMessage(ChatColor.GRAY + "/dg titan");
                p.sendMessage(ChatColor.GRAY + "/dg claim");
                p.sendMessage(ChatColor.GRAY + "/dg shrine");
                p.sendMessage(ChatColor.GRAY + "/dg tribute");
                p.sendMessage(ChatColor.GRAY + "/dg player");
                p.sendMessage(ChatColor.GRAY + "/dg stats");
                p.sendMessage("To see your own information, use " + ChatColor.YELLOW + "/check");
                p.sendMessage("To see more shrine information, use " + ChatColor.YELLOW + "/shrine");
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("god")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] God Help File");
                    p.sendMessage(ChatColor.GRAY + "For more information on the Gods, use /dg <name>");
                    p.sendMessage(ChatColor.GOLD + "----Tier 1");
                    p.sendMessage(ChatColor.GRAY + "Zeus - God of lightning and air.");
                    p.sendMessage(ChatColor.GRAY + "Poseidon - God of the seas.");
                    p.sendMessage(ChatColor.GRAY + "Hades - God of the underworld.");
                    p.sendMessage(ChatColor.GOLD + "----Tier 2");
                    p.sendMessage(ChatColor.GRAY + "Ares - God of war.");
                    /*
					p.sendMessage(ChatColor.GRAY+"Athena - Goddess of wisdom.");
					p.sendMessage(ChatColor.GRAY+"Hephaestus - God of the forge.");
					p.sendMessage(ChatColor.GRAY+"Apollo - God of archery and healing.");
					p.sendMessage(ChatColor.GOLD+"----Tier 3");
					p.sendMessage(ChatColor.GRAY+"Artemis - Goddess of the hunt.");
					p.sendMessage(ChatColor.GRAY+"Demeter - Goddess of the harvest.");
					p.sendMessage(ChatColor.GRAY+"Dionysus - God of wine.");
					p.sendMessage(ChatColor.GRAY+"Hermes - God of travel and thievery.");
					p.sendMessage(ChatColor.GRAY+"Hestia - Goddess of cooking and the home.");
					 */
                } else if (args[0].equalsIgnoreCase("titan")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] Titan Help File");
                    p.sendMessage(ChatColor.GRAY + "For more information on the Gods, use /dg <name>");
                    p.sendMessage(ChatColor.GOLD + "----Tier 1");
                    p.sendMessage(ChatColor.GRAY + "Cronus - Titan of time.");
                    p.sendMessage(ChatColor.GRAY + "Rhea - Titaness of nature.");
                    p.sendMessage(ChatColor.GRAY + "Prometheus - Titan of fire.");
                    p.sendMessage(ChatColor.GOLD + "----Tier 2");
                    p.sendMessage(ChatColor.GRAY + "Atlas - Titan of enduring.");
					/*
					p.sendMessage(ChatColor.GRAY+"Oceanus - Titan of the oceans.");
					p.sendMessage(ChatColor.GRAY+"Hyperion - Titan of light.");
					p.sendMessage(ChatColor.GRAY+"Themis - Titaness of order and foresight.");
					p.sendMessage(ChatColor.GOLD+"----Tier 3");
					p.sendMessage(ChatColor.GRAY+"Lelantos - Titan of craftiness.");
					p.sendMessage(ChatColor.GRAY+"Perses - Titan of destruction.");
					p.sendMessage(ChatColor.GRAY+"Iapetus - Titan of mortality.");
					p.sendMessage(ChatColor.GRAY+"Helios - Titan of the sun and keeper of oaths.");
					p.sendMessage(ChatColor.GRAY+"Koios - Titan of intelligence.");
					 */
                } else if (args[0].equalsIgnoreCase("claim")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] Claim Help File");
                    p.sendMessage(ChatColor.GRAY + "To claim your first deity, left click on " + SELECTMATERIAL.name().toLowerCase() + " with");
                    p.sendMessage(ChatColor.GRAY + "a 'select item' in your hand. The 'select item' varies for each");
                    p.sendMessage(ChatColor.GRAY + "deity and can be found at /dg <deity name>.");
                    p.sendMessage(ChatColor.GRAY + "To claim additional deities, go to a shrine when you are");
                    p.sendMessage(ChatColor.GRAY + "eligible. Click the center block with the 'select item' in hand.");
                } else if (args[0].equalsIgnoreCase("shrine")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] Shrine Help File");
                    p.sendMessage(ChatColor.GRAY + "Shrines are controlled by alliances. They are found");
                    p.sendMessage(ChatColor.GRAY + "unclaimed in nature and may be claimed by placing an");
                    p.sendMessage(ChatColor.GRAY + "iron, gold, obsidian, or diamond block in the center.");
                    p.sendMessage(ChatColor.GRAY + "The area around a shrine is protected and unbreakable.");
                    p.sendMessage(ChatColor.GRAY + "Its protection can be extended by placing signs");
                    p.sendMessage(ChatColor.GRAY + "anywhere within the protected radius, including the");
                    p.sendMessage(ChatColor.GRAY + "protection of other signs.");
                    p.sendMessage(ChatColor.GRAY + "Shrines can be destroyed by members of other alliances.");
                    p.sendMessage(ChatColor.GRAY + "A shrine may be strengthened by offering tributes.");
                    p.sendMessage(ChatColor.GRAY + "To see how many shrines there are and other useful");
                    p.sendMessage(ChatColor.GRAY + "information, use " + ChatColor.YELLOW + "/shrine");
                } else if (args[0].equalsIgnoreCase("tribute")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] Tribute Help File");
                    p.sendMessage(ChatColor.GRAY + "Tributing is the primary way to gain Favor, the other");
                    p.sendMessage(ChatColor.GRAY + "being PVP. To tribute, hold the item or items you want");
                    p.sendMessage(ChatColor.GRAY + "to offer in hand, and type " + ChatColor.YELLOW + "/offer");
                    p.sendMessage(ChatColor.GRAY + "The entire stack of items will be removed from your hand");
                    p.sendMessage(ChatColor.GRAY + "and you will gain an amount of Favor.");
                    p.sendMessage(ChatColor.GRAY + "Offering tributes at a shrine will gain you more Favor");
                    p.sendMessage(ChatColor.GRAY + "than using the command. Offering better items may also");
                    p.sendMessage(ChatColor.GRAY + "make the shrine stronger and extend its protection range.");
                    p.sendMessage(ChatColor.GRAY + "At a shrine, left click the center block with the items");
                    p.sendMessage(ChatColor.GRAY + "you want to tribute in hand. The shrine must be allied");
                    p.sendMessage(ChatColor.GRAY + "to you.");
                } else if (args[0].equalsIgnoreCase("player")) {
                    p.sendMessage(ChatColor.YELLOW + "[Demigods] Player Help File");
                    p.sendMessage(ChatColor.GRAY + "As a player, you may choose to ally with the Gods or");
                    p.sendMessage(ChatColor.GRAY + "the Titans. Once you have made an allegiance, you may");
                    p.sendMessage(ChatColor.GRAY + "not break it without forsaking all the deities you have.");
                    p.sendMessage(ChatColor.GRAY + "The two major attributes you have are:");
                    p.sendMessage(ChatColor.YELLOW + "Favor " + ChatColor.GRAY + "- A measure of power, used for abilities and a");
                    p.sendMessage(ChatColor.GRAY + "requirement for making Ascensions. Gained by tributing");
                    p.sendMessage(ChatColor.GRAY + "items and defeating enemy players in PVP.");
                    p.sendMessage(ChatColor.YELLOW + "Ascensions " + ChatColor.GRAY + "- Equivalent to levels. Players become");
                    p.sendMessage(ChatColor.GRAY + "eligible for Ascensions when they have a certain number");
                    p.sendMessage(ChatColor.GRAY + "of kills and have accumulated an amount of Favor.");
                    p.sendMessage(ChatColor.GRAY + "Ascensions make skills more powerful and unlock more");
                    p.sendMessage(ChatColor.GRAY + "deities. Once eligible, left click the center block of");
                    p.sendMessage(ChatColor.GRAY + "an allied shrine to Ascend.");
                } else if (args[0].equalsIgnoreCase("stats")) {
                    int titancount = 0;
                    int godcount = 0;
                    int othercount = 0;
                    int titankills = 0;
                    int godkills = 0;
                    int otherkills = 0;
                    int titandeaths = 0;
                    int goddeaths = 0;
                    int otherdeaths = 0;
                    ArrayList<String> onlinegods = new ArrayList<String>();
                    ArrayList<String> onlinetitans = new ArrayList<String>();
                    ArrayList<String> onlineother = new ArrayList<String>();
                    for (String id : DSave.getCompleteData().keySet()) {
                        try {
                            if (!DUtil.isFullParticipant(id)) continue;
                            if (DSave.hasData(id, "LASTLOGINTIME"))
                                if ((Long) DSave.getData(id, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000)
                                    continue;
                            if (DUtil.isTitan(id)) {
                                titancount++;
                                titankills += DUtil.getKills(id);
                                titandeaths += DUtil.getDeaths(id);
                                if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
                                    onlinetitans.add(id);
                                }
                            } else if (DUtil.isGod(id)) {
                                if (!DUtil.isFullParticipant(id)) continue;
                                if (DUtil.isGod(id)) {
                                    godcount++;
                                    godkills += DUtil.getKills(id);
                                    goddeaths += DUtil.getDeaths(id);
                                    if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
                                        onlinegods.add(id);
                                    }
                                }
                            } else {
                                if (!DUtil.isFullParticipant(id)) continue;
                                othercount++;
                                otherkills += DUtil.getKills(id);
                                otherdeaths += DUtil.getDeaths(id);
                                if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
                                    onlineother.add(id);
                                }
                            }
                        } catch (NullPointerException error) {
                        }
                    }
					/*
					 * Print data
					 */
                    p.sendMessage(ChatColor.GRAY + "----Stats----");
                    String str1 = "";
                    if (onlinegods.size() > 0) {
                        for (String g : onlinegods) {
                            str1 += g + ", ";
                        }
                        str1 = str1.substring(0, str1.length() - 2);
                    }
                    String str2 = "";
                    if (onlinetitans.size() > 0) {
                        for (String t : onlinetitans) {
                            str2 += t + ", ";
                        }
                        str2 = str2.substring(0, str2.length() - 2);
                    }
                    String str3 = "";
                    if (onlineother.size() > 0) {
                        for (String o : onlineother) {
                            str3 += o + ", ";
                        }
                        str3 = str3.substring(0, str3.length() - 2);
                    }
                    p.sendMessage("There are " + ChatColor.GREEN + onlinegods.size() + "/" + ChatColor.YELLOW + godcount + ChatColor.WHITE + " Gods online: " + ChatColor.GOLD + str1);
                    p.sendMessage("There are " + ChatColor.GREEN + onlinetitans.size() + "/" + ChatColor.YELLOW + titancount + ChatColor.WHITE + " Titans online: " + ChatColor.GOLD + str2);
                    if (othercount > 0)
                        p.sendMessage("There are " + ChatColor.GREEN + onlineother.size() + "/" + ChatColor.YELLOW + othercount + ChatColor.WHITE + " other" +
                                " alliance members online: " + ChatColor.GOLD + str3);
                    p.sendMessage("Total God kills: " + ChatColor.GREEN + godkills + ChatColor.YELLOW + " --- " + ChatColor.WHITE + " Total Titan kills: " + ChatColor.RED + titankills);
                    p.sendMessage("God K/D Ratio: " + ChatColor.GREEN + ((float) godkills / goddeaths) + ChatColor.YELLOW + " --- " + ChatColor.WHITE +
                            " Titan K/D Ratio: " + ChatColor.RED + ((float) titankills / titandeaths));
                    if (othercount > 0) {
                        p.sendMessage("Total Other kills: " + ChatColor.GREEN + otherkills + ChatColor.YELLOW);
                        p.sendMessage("Other K/D Ratio: " + ChatColor.YELLOW + ((float) otherkills / otherdeaths));
                    }
                } else {
                    for (Deity deity : DSave.getGlobalList()) {
                        if (deity.getName().equalsIgnoreCase(args[0]))
                            deity.printInfo(p);
                    }
                }
            } else if (args.length == 3) {
                int one = Integer.parseInt(args[0]);
                int two = Integer.parseInt(args[1]);
                int three = Integer.parseInt(args[2]);
                p.teleport(new Location(p.getWorld(), one, two, three));
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("check")) {
            if (DUtil.isGod(p) || DUtil.isTitan(p)) {
                p.sendMessage(ChatColor.YELLOW + "--" + p.getName() + "--" + DUtil.getRank(p) + "");
                //List deities
                String send = "Your deities are: ";
                for (int i = 0; i < DUtil.getDeities(p).size(); i++) {
                    send += DUtil.getDeities(p).get(i).getName() + " ";
                }
                p.sendMessage(send);
                //Display Favor/Ascensions and K/D
                p.sendMessage("Favor: " + DUtil.getFavor(p) + ChatColor.YELLOW + "/" + DUtil.getFavorCap(p));
                p.sendMessage("Ascensions: " + DUtil.getLevel(p));
                p.sendMessage("Kills: " + ChatColor.GREEN + DUtil.getKills(p) + ChatColor.WHITE + " // " +
                        "Deaths: " + ChatColor.RED + DUtil.getDeaths(p));
                //Deity information
                if (DUtil.costForNextDeity(p) > DUtil.getLevel(p))
                    p.sendMessage("You may form a new alliance at " + ChatColor.GOLD +
                            DUtil.costForNextDeity(p) + ChatColor.WHITE + " Ascensions.");
                else {
                    p.sendMessage(ChatColor.AQUA + "You are eligible for a new alliance.");
                    p.sendMessage(ChatColor.GRAY + "Go to a shrine for more information.");
                }
                //Ascension information
                if (DUtil.canLevelUp(p)) {
                    p.sendMessage(ChatColor.AQUA + "You are eligible for an Ascension.");
                } else {
                    String str = ".";
                    if (DUtil.getLevel(p) > 10)
                        str = " and " + ChatColor.GOLD + (DUtil.getLevel(p) - 10) + ChatColor.GRAY + " Kills.";
                    p.sendMessage(ChatColor.GRAY + "You may Ascend when you have " + ChatColor.GOLD +
                            DUtil.costForNextLevel(p) + ChatColor.GRAY + " Favor" + str);
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "--" + p.getName() + "--Mortal--");
                p.sendMessage("You are not affiliated with any Gods or Titans.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("transfer")) {
            if (!DUtil.isFullParticipant(p))
                return true;
            if (args.length == 1) {
                try {
                    int give = Integer.parseInt(args[0]);
                    if (DUtil.getFavor(p) < give) {
                        p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                        return true;
                    }
                    for (Block b : (List<Block>) p.getLineOfSight((Set) null, 5)) {
                        for (Player pl : p.getWorld().getPlayers()) {
                            if (pl.getLocation().distance(b.getLocation()) < 0.8) {
                                if (!DUtil.isFullParticipant(pl))
                                    continue;
                                if (!DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
                                    continue;
                                DUtil.setFavor(pl, DUtil.getFavor(pl) + give);
                                DUtil.setFavor(p, DUtil.getFavor(p) - give);
                                p.sendMessage(ChatColor.YELLOW + "Successfully transferred " + give + " Favor to " + pl.getName() + ".");
                                pl.sendMessage(ChatColor.YELLOW + "Received " + give + " Favor from " + p.getName() + ".");
                                return true;
                            }
                        }
                    }
                    p.sendMessage(ChatColor.YELLOW + "No players found. You may only transfer Favor within your alliance.");
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else if (args.length == 2) {
                try {
                    Player pl = DUtil.getPlayer(args[0]);
                    if (pl.getUniqueId().equals(p.getUniqueId())) {
                        p.sendMessage(ChatColor.YELLOW + "You cannot send Favor to yourself.");
                        return true;
                    }
                    int give = Integer.parseInt(args[1]);
                    int tax = (int) (TRANSFERTAX * give);
                    if (DUtil.getFavor(p) < (give + tax)) {
                        p.sendMessage(ChatColor.YELLOW + "You do not have enough Favor.");
                        p.sendMessage(ChatColor.YELLOW + "The tax for this long-distance transfer is " + tax + ".");
                        return true;
                    }
                    if (!DUtil.isFullParticipant(pl))
                        return true;
                    if (!DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
                        return true;
                    DUtil.setFavor(pl, DUtil.getFavor(pl) + give);
                    DUtil.setFavor(p, DUtil.getFavor(p) - give - tax);
                    p.sendMessage(ChatColor.YELLOW + "Successfully transferred " + give + " Favor to " + pl.getName() + ".");
                    if (tax > 0)
                        p.sendMessage(ChatColor.YELLOW + "You lost " + tax + " Favor in tax for a long-distance transfer.");
                    pl.sendMessage(ChatColor.YELLOW + "Received " + give + " Favor from " + p.getName() + ".");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("alliance")) {
            if (!DUtil.isFullParticipant(p))
                return true;
            if (DSave.hasData(p, "ALLIANCECHAT")) {
                if ((Boolean) DSave.getData(p, "ALLIANCECHAT")) {
                    p.sendMessage(ChatColor.YELLOW + "Alliance chat has been turned off.");
                    DSave.saveData(p, "ALLIANCECHAT", false);
                    return true;
                }
            }
            p.sendMessage(ChatColor.YELLOW + "Alliance chat has been turned on.");
            DSave.saveData(p, "ALLIANCECHAT", true);
            return true;
        } else if (c.getName().equalsIgnoreCase("checkplayer")) {
            if (!(DUtil.hasPermission(p, "demigods.checkplayer") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 1)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                if (DUtil.isGod(target) || DUtil.isTitan(target)) {
                    p.sendMessage(ChatColor.YELLOW + "--" + target.getName() + "--" + DUtil.getRank(p) + "");
                    //List deities
                    String send = target.getName() + "'s deities are: ";
                    for (int i = 0; i < DUtil.getDeities(target).size(); i++) {
                        send += DUtil.getDeities(target).get(i).getName() + " ";
                    }
                    p.sendMessage(send);
                    //Display Favor/Ascensions and K/D
                    p.sendMessage("Favor: " + DUtil.getFavor(target) + ChatColor.YELLOW + "/" + DUtil.getFavorCap(target));
                    p.sendMessage("Ascensions: " + DUtil.getLevel(target));
                    p.sendMessage("Kills: " + ChatColor.GREEN + DUtil.getKills(target) + ChatColor.WHITE + " // " +
                            "Deaths: " + ChatColor.RED + DUtil.getDeaths(target));
                    //Deity information
                    if (DUtil.costForNextDeity(target) > DUtil.getLevel(target))
                        p.sendMessage(target.getName() + " may form a new alliance at " + ChatColor.GOLD +
                                DUtil.costForNextDeity(target) + ChatColor.WHITE + " Ascensions.");
                    else {
                        p.sendMessage(ChatColor.AQUA + target.getName() + " is eligible for a new alliance.");
                    }
                    //Ascensions
                    if (DUtil.canLevelUp(target)) {
                        p.sendMessage(ChatColor.AQUA + target.getName() + " is eligible for an Ascension.");
                    } else {
                        String str = ".";
                        if (DUtil.getLevel(target) > 10)
                            str = " and " + ChatColor.GOLD + (DUtil.getLevel(target) - 10) + ChatColor.GRAY + " Kills.";
                        p.sendMessage(ChatColor.GRAY + target.getName() + " may Ascend when they have " + ChatColor.GOLD +
                                DUtil.costForNextLevel(target) + ChatColor.GRAY + " Favor" + str);
                    }
                } else {
                    p.sendMessage(ChatColor.YELLOW + "--" + target.getName() + "--Mortal--");
                    p.sendMessage(target.getName() + " is not affiliated with any Gods or Titans.");
                }
            } catch (NullPointerException name) {
                p.sendMessage(ChatColor.YELLOW + "Player not found.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("shrine")) {
            int titancount = 0;
            int godcount = 0;
            int othercount = 0;
            int neutrals = 0;
            int total = 0;
            for (Shrine s : DSave.getShrines()) {
                total++;
                if (s.getAlliance() == null)
                    neutrals++;
                else if (s.getAlliance().equalsIgnoreCase("god"))
                    godcount++;
                else if (s.getAlliance().equalsIgnoreCase("titan"))
                    titancount++;
                else othercount++;
            }
            p.sendMessage(ChatColor.GRAY + "--Shrines");
            p.sendMessage("There are " + total + " Shrines:");
            p.sendMessage(ChatColor.GOLD + "Gods: " + ChatColor.WHITE + godcount);
            p.sendMessage(ChatColor.DARK_RED + "Titans: " + ChatColor.WHITE + titancount);
            if (othercount > 0)
                p.sendMessage(ChatColor.AQUA + "Other: " + ChatColor.WHITE + othercount);
            p.sendMessage(ChatColor.YELLOW + "Neutral (Unclaimed): " + ChatColor.WHITE + neutrals);
            boolean cont = true;
            for (Shrine s : DSave.getShrines()) {
                if (p.getWorld().getName().equals(s.getCenter().getWorld())) {
                    if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) <= s.getRadius()) {
                        if (s.getAlliance() != null) {
                            p.sendMessage(ChatColor.YELLOW + "--" + s.getName());
                            p.sendMessage(ChatColor.GRAY + "You are in a shrine of the " + s.getAlliance() + " alliance.");
                            p.sendMessage(ChatColor.GRAY + "This shrine has " + ChatColor.WHITE + s.getDurability() +
                                    ChatColor.GRAY + " durability and " + ChatColor.WHITE + s.getRange() + ChatColor.GRAY + " range for " +
                                    ChatColor.WHITE + s.getExtensions().size());
                            p.sendMessage(ChatColor.GRAY + " protection extensions.");
                        } else {
                            p.sendMessage(ChatColor.YELLOW + "--" + s.getName());
                            p.sendMessage(ChatColor.GRAY + "You are in an unclaimed shrine. Place a block on the post to claim it.");
                        }
                        cont = false;
                    }
                    for (WriteLocation w : s.getExtensions()) {
                        if (p.getLocation().distance(DUtil.toLocation(w)) <= s.getRange()) {
                            cont = false;
                            if (s.getAlliance() != null) {
                                p.sendMessage(ChatColor.GRAY + "You are in a an extended protection area of");
                                p.sendMessage(ChatColor.GRAY + "the " + s.getAlliance() + " alliance.");
                            } else
                                p.sendMessage(ChatColor.GRAY + "You are in an area protected by a neutral shrine.");
                            break;
                        }
                    }
                }
            }
            if (cont && (DUtil.getAllegiance(p) != null)) {
                ArrayList<Shrine> friendlies = new ArrayList<Shrine>();
                ArrayList<Shrine> neutral = new ArrayList<Shrine>();
                for (Shrine s : DSave.getShrines()) {
                    if (p.getWorld().getName().equals(s.getCenter().getWorld())) {
                        if (s.getAlliance() != null) {
                            if (s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p)))
                                friendlies.add(s);
                        } else neutral.add(s);
                    }
                }
                int closest = Integer.MAX_VALUE;
                Location close = null;
                if (friendlies.size() > 0) {
                    for (Shrine s : friendlies) {
                        if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) < closest) {
                            closest = (int) p.getLocation().distance(DUtil.toLocation(s.getCenter()));
                            close = DUtil.toLocation(s.getCenter());
                        }
                    }
                    if (close != null) {
                        String point = "";
                        if (close.getBlockX() > p.getLocation().getBlockX())
                            point += 'S';
                        else if (close.getBlockX() < p.getLocation().getBlockX())
                            point += 'N';
                        if (close.getBlockZ() > p.getLocation().getBlockZ())
                            point += 'W';
                        else if (close.getBlockZ() < p.getLocation().getBlockZ())
                            point += 'E';
                        p.sendMessage(ChatColor.GREEN + "The nearest friendly shrine is " + closest + " blocks " + point + ".");
                    }
                } else if (neutral.size() > 0) {
                    for (Shrine s : neutral) {
                        if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) < closest) {
                            closest = (int) p.getLocation().distance(DUtil.toLocation(s.getCenter()));
                            close = DUtil.toLocation(s.getCenter());
                        }
                    }
                    if (close != null) {
                        String point = "";
                        if (close.getBlockX() > p.getLocation().getBlockX())
                            point += 'S';
                        else if (close.getBlockX() < p.getLocation().getBlockX())
                            point += 'N';
                        if (close.getBlockZ() > p.getLocation().getBlockZ())
                            point += 'W';
                        else if (close.getBlockZ() < p.getLocation().getBlockZ())
                            point += 'E';
                        p.sendMessage(ChatColor.GREEN + "The nearest unclaimed shrine is " + closest + " blocks " + point + ".");
                    }
                }
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("givedeity")) {
            if (!(DUtil.hasPermission(p, "demigods.givedeity") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            Player target = DUtil.getPlayer(args[0]);
            if (DUtil.hasDeity(target, args[1])) {
                target.sendMessage(ChatColor.YELLOW + "" + target + " already has that deity.");
                return true;
            } else {
                String s = args[1].toLowerCase();
                if (s.equals("zeus")) DUtil.addDeity(target, new Zeus(target.getName()));
                else if (s.equals("ares")) DUtil.addDeity(target, new Ares(target.getName()));
                else if (s.equals("cronus")) DUtil.addDeity(target, new Cronus(target.getName()));
                else if (s.equals("prometheus")) DUtil.addDeity(target, new Prometheus(target.getName()));
                else if (s.equals("rhea")) DUtil.addDeity(target, new Rhea(target.getName()));
                else if (s.equals("hades")) DUtil.addDeity(target, new Hades(target.getName()));
                else if (s.equals("poseidon")) DUtil.addDeity(target, new Poseidon(target.getName()));
                else if (s.equals("atlas")) DUtil.addDeity(target, new Poseidon(target.getName()));
                p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " now has the deity " + args[1] + ".");
                p.sendMessage(ChatColor.YELLOW + "Skills may not work if you mismatch Titans and Gods.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("removedeity")) {
            if (!(DUtil.hasPermission(p, "demigods.removedeity") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            Player target = DUtil.getPlayer(args[0]);
            if (!DUtil.hasDeity(target, args[1])) {
                p.sendMessage(ChatColor.YELLOW + "" + target.getName() + " does not have that deity.");
            } else {
                DUtil.getDeities(target).remove(DUtil.getDeity(target, args[1]));
                p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " no longer has that deity.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("forsake")) {
            if (!DUtil.isFullParticipant(p))
                return true;
            if (args.length != 1)
                return false;
            if (args[0].equalsIgnoreCase("all")) {
                p.sendMessage(ChatColor.RED + "You are mortal.");
                DSave.removePlayer(p);
                DSave.addPlayer(p);
                return true;
            }
            if (!DUtil.hasDeity(p, args[0])) {
                p.sendMessage(ChatColor.YELLOW + "You do not have that deity.");
            } else {
                if (DUtil.getDeities(p).size() >= 2) {
                    String str = "";
                    Deity toremove = DUtil.getDeity(p, args[0]);
                    if (DUtil.getLevel(p) > 1) {
                        DUtil.setLevel(p, DUtil.getLevel(p) - 1);
                        str = " Your level has been reduced.";
                    }
                    p.sendMessage(ChatColor.YELLOW + "You have forsaken " + toremove.getName() + "." + str);
                    DUtil.getDeities(p).remove(toremove);
                } else {
                    Deity toremove = DUtil.getDeity(p, args[0]);
                    p.sendMessage(ChatColor.YELLOW + "You have forsaken " + toremove.getName() + ".");
                    p.sendMessage(ChatColor.RED + "You are mortal.");
                    DSave.removePlayer(p);
                    DSave.addPlayer(p);
                }
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("setfavor")) {
            if (!(DUtil.hasPermission(p, "demigods.setfavor") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                int amt = Integer.parseInt(args[1]);
                if (amt < 0) {
                    p.sendMessage(ChatColor.YELLOW + "The amount must be greater than 0.");
                    return true;
                }
                if (DSave.hasPlayer(target)) {
                    DUtil.setFavor(target, amt);
                    p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " now has " + amt + " Favor/Power.");
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("setlevel") || c.getName().equalsIgnoreCase("setascensions")) {
            if (!(DUtil.hasPermission(p, "demigods.setlevel") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                int amt = Integer.parseInt(args[1]);
                if (amt < 0) {
                    p.sendMessage(ChatColor.YELLOW + "The level must be greater than 0.");
                    return true;
                } else if (amt > DUtil.getPlugin().getLevelCap()) {
                    p.sendMessage(ChatColor.YELLOW + "The level must be under the level cap (" + DUtil.getPlugin().getLevelCap() + ").");
                    return true;
                }
                if (DSave.hasPlayer(target)) {
                    DUtil.setLevel(target, amt);
                    p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " is now level " + amt + ".");
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("setkills")) {
            if (!(DUtil.hasPermission(p, "demigods.setkills") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                int amt = Integer.parseInt(args[1]);
                if (amt < 0) {
                    p.sendMessage(ChatColor.YELLOW + "The amount must be greater than 0.");
                    return true;
                }
                if (DSave.hasPlayer(target)) {
                    DUtil.setKills(target, amt);
                    p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " now has " + amt + " kills.");
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("setdeaths")) {
            if (!(DUtil.hasPermission(p, "demigods.setdeaths") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                int amt = Integer.parseInt(args[1]);
                if (amt < 0) {
                    p.sendMessage(ChatColor.YELLOW + "The amount must be greater than 0.");
                    return true;
                }
                if (DSave.hasPlayer(target)) {
                    DUtil.setDeaths(target, amt);
                    p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " now has " + amt + " deaths.");
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("setradius")) {
            if (!(DUtil.hasPermission(p, "demigods.setradius") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 1)
                return false;
            try {
                Shrine shrine = null;
                for (Shrine s : DSave.getShrines()) {
                    if (p.getLocation().getWorld().getName().equals(s.getCenter().getWorld()))
                        if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) <= s.getRadius()) {
                            shrine = s;
                            break;
                        }
                }
                if (shrine != null) {
                    int newrad = Integer.parseInt(args[0]);
                    shrine.setRadius(newrad);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You must stand in the shrine.");
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("setallegiance") || c.getName().equalsIgnoreCase("setalliance")) {
            if (!(DUtil.hasPermission(p, "demigods.setalliance") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 2)
                return false;
            try {
                Player target = DUtil.getPlayer(args[0]);
                String allegiance = args[1];
                if (allegiance.equalsIgnoreCase("god"))
                    DUtil.setGod(target);
                else if (allegiance.equalsIgnoreCase("titan"))
                    DUtil.setTitan(target);
                else DUtil.setAllegiance(target, allegiance);
                p.sendMessage(ChatColor.YELLOW + "Success! " + target.getName() + " is now in the " + DUtil.getAllegiance(target) + " allegiance.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("createshrine")) {
            if (!(DUtil.hasPermission(p, "demigods.createshrine") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            new Shrine(p.getLocation());
            p.sendMessage(ChatColor.YELLOW + "You have created a new shrine at your position.");
        } else if (c.getName().equalsIgnoreCase("removeplayer")) {
            if (!(DUtil.hasPermission(p, "demigods.removeplayer") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length != 1)
                return false;
            Player toremove = plugin.getServer().getPlayer(args[0]);
            if (DSave.hasPlayer(toremove)) {
                p.sendMessage(ChatColor.YELLOW + toremove.getName() + " was successfully removed from the save.");
                DSave.removePlayer(toremove);
                DSave.removeItem("plugins/Demigods/Players/" + toremove.getName() + ".dem");
            } else p.sendMessage(ChatColor.YELLOW + "That player is not in the save.");
            return true;
        } else if (c.getName().equalsIgnoreCase("removeshrine")) {
            if (!(DUtil.hasPermission(p, "demigods.removeshrine") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            Shrine shrine = null;
            for (Shrine s : DSave.getShrines()) {
                if (p.getLocation().getWorld().getName().equals(s.getCenter().getWorld()))
                    if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) <= s.getRadius()) {
                        shrine = s;
                        break;
                    }
            }
            if (shrine != null) {
                p.sendMessage(ChatColor.YELLOW + "This shrine was successfully removed from the save.");
                DSave.getShrines().remove(shrine);
                DSave.removeItem("plugins/Demigods/Shrines/" + shrine.getName() + ".loc");
            } else {
                p.sendMessage(ChatColor.YELLOW + "You must stand in the shrine you want to remove.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("nameshrine")) {
            if (!(DUtil.hasPermission(p, "demigods.nameshrine") || DUtil.hasPermission(p, "demigods.admin")))
                return true;
            if (args.length == 0)
                return false;
            Shrine shrine = null;
            for (Shrine s : DSave.getShrines()) {
                if (p.getLocation().getWorld().getName().equals(s.getCenter().getWorld()))
                    if (p.getLocation().distance(DUtil.toLocation(s.getCenter())) <= s.getRadius()) {
                        shrine = s;
                        break;
                    }
            }
            if (shrine != null) {
                DSave.removeItem("plugins/Demigods/Shrines/" + shrine.getName() + ".loc");
                p.sendMessage(ChatColor.YELLOW + "This shrine has successfully been renamed.");
                String totalname = "";
                for (String str : args)
                    totalname += str + " ";
                shrine.setName(totalname.substring(0, totalname.length() - 1));
                DSave.saveItem("plugins/Demigods/Shrines/" + shrine.getName() + ".loc", shrine);
            } else {
                p.sendMessage(ChatColor.YELLOW + "You must stand in the shrine you want to rename.");
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("warpshrine")) {
            //			if (!(DUtil.hasPermission(p, "demigods.warpshrine") || DUtil.hasPermission(p, "demigods.admin")))
            //				return true;
            if (DUtil.getAllegiance(p) == null)
                return true;
            int ct = 0;
            if (args.length == 0) {
                String all = ChatColor.YELLOW + "Available shrines: ";
                for (Shrine s : DSave.getShrines()) {
                    if (s.getAlliance() != null)
                        if (s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
                            ct++;
                            all += s.getName() + ", ";
                        }
                }
                if (ct > 0)
                    p.sendMessage(all.substring(0, all.length() - 2));
                else p.sendMessage(ChatColor.YELLOW + "Your alliance has no shrines to warp to.");
                return true;
            } else {
                String totalname = "";
                for (String str : args)
                    totalname += str + " ";
                totalname = totalname.substring(0, totalname.length() - 1);
                for (Shrine s : DSave.getShrines()) {
                    try {
                        if (s.getAlliance() != null)
                            if (s.getAlliance().equalsIgnoreCase(DUtil.getAllegiance(p)))
                                if (s.getName().equalsIgnoreCase(totalname)) {
                                    if (DSave.hasData(p, "SHRINEWARPCOOLDOWN")) {
                                        long lasttime = (Long) DSave.getData(p, "SHRINEWARPCOOLDOWN");
                                        if (System.currentTimeMillis() > lasttime + WARPCOOLDOWNTIME * 1000) {
                                            p.sendMessage(ChatColor.YELLOW + "Warping...");
                                            p.teleport(DUtil.toLocation(s.getCenter()));
                                            DSave.saveData(p, "SHRINEWARPCOOLDOWN", System.currentTimeMillis());
                                        } else {
                                            long TIME = lasttime + WARPCOOLDOWNTIME * 1000;
                                            p.sendMessage(ChatColor.YELLOW + "You cannot warp again for " + ((((TIME) / 1000) -
                                                    (System.currentTimeMillis() / 1000))) / 60 + " minute(s) and " +
                                                    ((((TIME) / 1000) - (System.currentTimeMillis() / 1000)) % 60) + " seconds.");
                                            return true;
                                        }
                                    } else {
                                        DSave.saveData(p, "SHRINEWARPCOOLDOWN", System.currentTimeMillis());
                                        p.sendMessage(ChatColor.YELLOW + "Warping...");
                                        p.teleport(s.getTeleportSpot().getBlock().getRelative(BlockFace.UP).getLocation());
                                    }
                                    return true;
                                }
                    } catch (NullPointerException er) {
                    }
                }
                return false;
            }
        } else if (c.getName().equalsIgnoreCase("offer")) {
            if ((DUtil.getAllegiance(p) != null) && (p.getItemInHand().getType() != Material.AIR)) {
                if (p.getItemInHand().getType().name().contains("SWORD") || p.getItemInHand().getType().name().contains("BOW")) {
                    p.sendMessage(ChatColor.YELLOW + "That item cannot be tributed.");
                    return true;
                }
                p.sendMessage("Your offering of " + p.getItemInHand().getType() + " has been graciously accepted.");
                int total = (int) (Math.ceil(DUtil.getValue(p.getItemInHand())) * TRIBUTEMULTIPLIER);
                p.sendMessage(ChatColor.YELLOW + "You have gained " + total + " Favor.");
                DUtil.setFavor(p, DUtil.getFavor(p) + total);
                p.setItemInHand(null);
            }
        } else if (c.getName().equalsIgnoreCase("value")) {
            if (DUtil.isFullParticipant(p))
                if (p.getItemInHand() != null)
                    p.sendMessage(ChatColor.YELLOW + p.getItemInHand().getType().name() + " x" + p.getItemInHand().getAmount() + " is worth " +
                            (int) (DUtil.getValue(p.getItemInHand()) * TRIBUTEMULTIPLIER) + " Favor by /offer and " +
                            (int) (DUtil.getValue(p.getItemInHand()) * TRIBUTEMULTIPLIER * SHRINEBONUS) + " at a shrine.");
        } else if (c.isRegistered()) {
            boolean bind = false;
            if (args.length == 1)
                if (args[0].contains("bind"))
                    bind = true;
            if (DUtil.getDeities(p) != null)
                for (Deity d : DUtil.getDeities(p))
                    d.onCommand(p, c.getName(), args, bind);
        }
        return false;
    }

}

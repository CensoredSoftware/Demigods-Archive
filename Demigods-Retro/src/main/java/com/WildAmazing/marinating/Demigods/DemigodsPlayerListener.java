package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.Gods.Listeners.*;
import com.WildAmazing.marinating.Demigods.OtherCommands.PhantomCommands;
import com.WildAmazing.marinating.Demigods.Titans.Listeners.*;
import com.WildAmazing.marinating.Demigods.Utilities.*;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Collection;

public class DemigodsPlayerListener implements Listener {
    private Demigods plugin;

    public DemigodsPlayerListener(Demigods p) {
        plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        boolean isDG = plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld());
        if (isDG) {
            // set new name
            if (Demigods.MASTERLIST.containsKey(p.getUniqueId())) {
                PlayerInfo info = Demigods.MASTERLIST.get(p.getUniqueId());
                info.setPlayerName(p.getName());
                Demigods.MASTERLIST.put(p.getUniqueId(), info);
            } else {
                p.sendMessage(ChatColor.RED + "To get started:");
                FancyMessage message = new FancyMessage("Choose (click on) a side: ").
                        color(ChatColor.WHITE);
                message.then("God ").
                        color(ChatColor.GOLD).
                        tooltip("Are you a demigod?").
                        command("/dg click god");
                message.then("Titan").
                        color(ChatColor.DARK_PURPLE).
                        tooltip("Are you a titanspawn?").
                        command("/dg click titan");
                message.send(p);
            }
        }

        // motd
        if (plugin.getConfigHandler().getMOTD()) {
            p.sendMessage("This server is running Demigods v" + ChatColor.YELLOW + plugin.getDescription().getVersion() + ChatColor.WHITE + ".");
            p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "/dg" + ChatColor.GRAY + " for more info.");
        }
        if (isDG) PhantomCommands.onPlayerJoinPhantom(e, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.saveAll();
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
        if (e.isCancelled()) return;
        PhantomCommands.onPhantomItemPickup(e, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().equalsIgnoreCase("s")) {
            e.getPlayer().chat("/dg stats");
            e.setCancelled(true);
        } else if (e.getMessage().equalsIgnoreCase("c")) {
            e.getPlayer().chat("/check");
            e.setCancelled(true);
        } else {
            if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
            PhantomCommands.onPhantomChat(e, plugin);
            GodCommands.onPlayerChat(e, plugin);
            TitanCommands.onPlayerChat(e, plugin);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
        if (e.isCancelled()) return;
        PhantomCommands.onPhantomItemDrop(e, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
        // overall
        onPlayerInteractDemigods(e);
        GodCommands.onPlayerInteractGod(e, plugin);
        TitanCommands.onPlayerInteractTitan(e, plugin);
        PhantomCommands.onPhantomInteract(e, plugin);
        // gods
        ZeusCommands.onPlayerInteract(e, plugin);
        PoseidonCommands.onPlayerInteractPoseidon(e, plugin);
        HadesCommands.onPlayerInteractHades(e, plugin);
        AresCommands.onPlayerInteractAres(e, plugin);
        HephaestusCommands.onPlayerInteractHephaestus(e, plugin);
        ApolloCommands.onPlayerInteractApollo(e, plugin);
        // titans
        PrometheusCommands.onPlayerInteractPrometheus(e, plugin);
        RheaCommands.onPlayerInteractRhea(e, plugin);
        HyperionCommands.onPlayerInteractHyperion(e, plugin);
        TyphonCommands.onPlayerInteractTyphon(e, plugin);
        OceanusCommands.onPlayerInteract(e, plugin);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
        if (e.isCancelled()) return;
        OceanusCommands.onPlayerInteractEntity(e, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // overall
        onPlayerCommandPreprocessDemigods(event);
        if (!plugin.getConfigHandler().isParticipating(event.getPlayer().getWorld())) return;
        // gods
        ZeusCommands.onPlayerCommandPreprocess(event, plugin);
        PoseidonCommands.onPlayerCommandPreprocessPoseidon(event, plugin);
        HadesCommands.onPlayerCommandPreprocessHades(event, plugin);
        AresCommands.onPlayerCommandPreprocessAres(event, plugin);
        AthenaCommands.onPlayerCommandPreprocessAthena(event, plugin);
        HephaestusCommands.onPlayerCommandPreprocessHephaestus(event, plugin);
        ApolloCommands.onPlayerCommandPreprocessApollo(event, plugin);
        // titans
        CronusCommands.onPlayerCommandPreprocessCronus(event, plugin);
        PrometheusCommands.onPlayerCommandPreprocessPrometheus(event, plugin);
        RheaCommands.onPlayerCommandPreprocessRhea(event, plugin);
        HyperionCommands.onPlayerCommandPreprocessHyperion(event, plugin);
        TyphonCommands.onPlayerCommandPreprocessTyphon(event, plugin);
        OceanusCommands.onPlayerCommandPreprocessOceanus(event, plugin);
        StyxCommands.onPlayerCommandPreprocessStyx(event, plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (!plugin.getConfigHandler().isParticipating(e.getPlayer().getWorld())) return;
        PhantomCommands.onPlayerRespawnPhantom(e, plugin);
    }

    /**
     * @param args
     * @param p
     */
    private void helpcode(String[] args, Player p) {
        if (!plugin.getConfigHandler().isParticipating(p.getWorld()) || args.length == 1 && args[0].equalsIgnoreCase("stats")) {
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
            for (PlayerInfo pi : plugin.getMaster()) {
                if (plugin.isGod(pi.getPlayerId())) {
                    numgods++;
                    numgodkills += pi.getKills();
                    numgoddeaths += pi.getDeaths();
                    if (plugin.isOnline(pi.getPlayerName())) {
                        numgodsonline++;
                        gods.add(pi.getPlayerName());
                    }
                } else if (plugin.isTitan(pi.getPlayerId())) {
                    numtitans++;
                    numtitankills += pi.getKills();
                    numtitandeaths += pi.getDeaths();
                    if (plugin.isOnline(pi.getPlayerName())) {
                        numtitansonline++;
                        titans.add(pi.getPlayerName());
                    }
                }
            }
            p.sendMessage(ChatColor.GRAY + "----Stats----");
            String str1 = "";
            if (gods.size() > 0) {
                for (String g : gods) {
                    str1 += g + ", ";
                }
                str1 = str1.substring(0, str1.length() - 2);
            }
            String str2 = "";
            if (titans.size() > 0) {
                for (String t : titans) {
                    str2 += t + ", ";
                }
                str2 = str2.substring(0, str2.length() - 2);
            }
            p.sendMessage("There are " + ChatColor.GREEN + numgodsonline + "/" + ChatColor.YELLOW + numgods + ChatColor.WHITE + " Æsir online: " + ChatColor.GOLD + str1);
            p.sendMessage("There are " + ChatColor.GREEN + numtitansonline + "/" + ChatColor.YELLOW + numtitans + ChatColor.WHITE + " Jotunn online: " + ChatColor.GOLD + str2);
            p.sendMessage("Total God kills: " + ChatColor.GREEN + numgodkills + ChatColor.YELLOW + " --- " + ChatColor.WHITE + " Total Titan kills: " + ChatColor.RED + numtitankills);
            p.sendMessage("God K/D Ratio: " + ChatColor.GREEN + ((float) numgodkills / numgoddeaths) + ChatColor.YELLOW + " --- " + ChatColor.WHITE + " Titan K/D Ratio: " + ChatColor.RED + ((float) numtitankills / numtitandeaths));
            if (plugin.isTitan(p) || plugin.isGod(p)) {
                p.sendMessage("Your kills: " + ChatColor.GREEN + plugin.getInfo(p).getKills() + ChatColor.WHITE + " Your deaths: " + ChatColor.RED + plugin.getInfo(p).getDeaths());
                p.sendMessage("Your K/D Ratio: " + ChatColor.YELLOW + ((float) plugin.getInfo(p).getKills() / plugin.getInfo(p).getDeaths()));
            }
        } else if (args.length == 0) {
            p.sendMessage(ChatColor.GREEN + "You have accessed the Demigods help file.");
            p.sendMessage(ChatColor.WHITE + "Choose a specific subcategory:");
            p.sendMessage(ChatColor.GRAY + "/dg general");
            p.sendMessage(ChatColor.GRAY + "/dg protect");
            p.sendMessage(ChatColor.GRAY + "/dg unprotect");
            p.sendMessage(ChatColor.GRAY + "/dg shrine");
            p.sendMessage(ChatColor.GRAY + "/dg tribute");
            p.sendMessage(ChatColor.GRAY + "/dg gods");
            p.sendMessage(ChatColor.GRAY + "/dg titans");
            p.sendMessage(ChatColor.GRAY + "/dg bind");
            p.sendMessage(ChatColor.GRAY + "/dg stats");
            if (p.isOp() || p.hasPermission("demigods.admin")) p.sendMessage(ChatColor.GRAY + "/dg admin");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("general")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("/check - Check your rank, Favor/Power, and Blessing/Glory.");
                p.sendMessage("Jotunn lose 10% of their Power when they die, but gain");
                p.sendMessage("more Power as a reward from killing Æsir in combat.");
                p.sendMessage("Blessing and Glory are a measure of the total Favor/Power");
                p.sendMessage("you have earned. They are required to add minor Æsir and");
                p.sendMessage("Jotunn to your allegiance.");
                p.sendMessage("To leave the alliance of a God or Titan, use " + ChatColor.GREEN + "/forsake");
            } else if (args[0].equalsIgnoreCase("protect")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("To protect an area you must have the allegiance of at least");
                p.sendMessage("one God or Titan. Type " + ChatColor.YELLOW + "/protect <deityname>" + ChatColor.WHITE + " and then choose");
                p.sendMessage("the two corners of the cuboid that you want protection for.");
                p.sendMessage("Protection requires Favor/Power proportional to the");
                p.sendMessage("volume of the selected cuboid.");
                p.sendMessage("Blocks in a protected area may only be placed or broken by");
                p.sendMessage("players who have an allegiance with that God or Titan.");
            } else if (args[0].equalsIgnoreCase("unprotect")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("To unprotect a protected zone or a shrine, you must have the");
                p.sendMessage("allegiance of the deity whose land you are unprotecting.");
                p.sendMessage("Type " + ChatColor.YELLOW + "/unprotect <deityname>" + ChatColor.WHITE + " and then click");
                p.sendMessage("on any block inside the zone you want to unprotect. If you");
                p.sendMessage("did everything correctly it will no longer be protected.");
            } else if (args[0].equalsIgnoreCase("shrine")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("Shrines are special protected zones. Tributes can only");
                p.sendMessage("happen in shrines for Æsir/Jotunn in your allegiance. ");
                p.sendMessage("To create a shrine, place a sign with the following text:");
                p.sendMessage("Line 1: " + ChatColor.GREEN + "shrine" + ChatColor.WHITE + " Line 2: " + ChatColor.GREEN + "dedicate" + ChatColor.WHITE + " Line 3: " + ChatColor.GREEN + "<deityname>" + ChatColor.WHITE + " Line 4: " + ChatColor.GREEN + "<size>");
                p.sendMessage("example: [shrine][dedicate][Zeus][4] (The size defaults to 3)");
                p.sendMessage("A shrine must contain a gold block and rare blocks with");
                p.sendMessage("combined value proportional to the size of the shrine.");
            } else if (args[0].equalsIgnoreCase("tribute")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("To tribute, a chest must be placed above a gold block in");
                p.sendMessage("an established shrine to a God or Titan in your allegiance.");
                p.sendMessage("Simply place the items you wish to tribute in the chest, ");
                p.sendMessage("and click the chest to sacrifice the items to your deities.");
                p.sendMessage("You will be rewarded with Favor/Power proportional to the");
                p.sendMessage("value of the items you placed in the chest. Remember that ");
                p.sendMessage("blocks are preferable to tools.");
            } else if (args[0].equalsIgnoreCase("gods") || args[0].equalsIgnoreCase("god")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("Æsir grant one passive ability, two active abilities, and");
                p.sendMessage("an ultimate. You may only choose one primary God.");
                p.sendMessage("To select a God, place an iron block and click on it while");
                p.sendMessage("it is surrounded in a 5x5 square of a single material.");
                p.sendMessage("To choose Zeus, surround the iron block with air.");
                p.sendMessage("To choose Poseidon, surround the block with water.");
                p.sendMessage("To choose Hades, surround the block with dirt.");
                p.sendMessage("To choose additional Æsir, place a gold block and click on");
                p.sendMessage("it with a special item in hand.");
                p.sendMessage("For detailed information on the Æsir, type " + ChatColor.YELLOW + "/dg god list");
            } else if (args[0].equalsIgnoreCase("titans") || args[0].equalsIgnoreCase("titan")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("Jotunn grant two passive ability, one active ability, and");
                p.sendMessage("an ultimate. You may only choose one primary Titan.");
                p.sendMessage("To select a Titan, place obsidian and click on it while");
                p.sendMessage("it is surrounded in a 5x5 square of a single material.");
                p.sendMessage("To choose Cronus, surround the obsidian block with soul sand.");
                p.sendMessage("To choose Prometheus, surround the block with lava or clay.");
                p.sendMessage("To choose Rhea, surround the block with leaves/logs.");
                p.sendMessage("For detailed info on the Jotunn, type " + ChatColor.YELLOW + "/dg titan list");
            } else if (args[0].equalsIgnoreCase("bind")) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("Binding is applicable on certain abilities only. To bind");
                p.sendMessage("a skill to an item, simply hold an item in your hand and ");
                p.sendMessage("type the skill name as though you were activating it, and");
                p.sendMessage("add 'bind' to the end. For example, '/lightning bind'. To");
                p.sendMessage("unbind an item from a skill, use 'unbind'. For example,");
                p.sendMessage("you would use '/lightning unbind'. Not all skills are");
                p.sendMessage("compatible with binding, so try them out for yourself.");
            } else if (args[0].equalsIgnoreCase("admin") && (p.isOp() || p.hasPermission("demigods.admin"))) {
                p.sendMessage(ChatColor.GRAY + "---");
                p.sendMessage("/removedeity <playername> <deityname>" + ChatColor.GRAY + " Remove a player's");
                p.sendMessage(ChatColor.GRAY + "deity.");
                p.sendMessage("/givedeity <playername> <deityname>" + ChatColor.GRAY + " Give a player a deity.");
                p.sendMessage("/checkplayer <playername> " + ChatColor.GRAY + "Do a " + ChatColor.WHITE + "/check" + ChatColor.GRAY + " on a player.");
                p.sendMessage("/setfavor <playername> <amount> " + ChatColor.GRAY + "Set a player's Power");
                p.sendMessage(ChatColor.GRAY + "or Favor to a certain amount.");
                p.sendMessage("/reviveplayer <playername> " + ChatColor.GRAY + "Bring a phantom player back to life.");
                p.sendMessage("Pro tip: To protect a large area from the general public for");
                p.sendMessage("free, give yourself the deity Omni with " + ChatColor.GOLD + "/givedeity <name> omni");
                p.sendMessage("then use " + ChatColor.GOLD + "/protect omni" + ChatColor.WHITE + " to protect cuboids");
                p.sendMessage("for free. Great for spawn or big arenas. (Omni has no other use)");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("click")) {
                FancyMessage message = new FancyMessage("Choose (click on) a deity: ").
                        color(ChatColor.WHITE);
                if (args[1].equalsIgnoreCase("god")) {
                    message.then("Zeus ").
                            color(ChatColor.YELLOW).
                            tooltip("God of the sky.").
                            command("/dg click for zeus");
                    message.then("Poseidon ").
                            color(ChatColor.AQUA).
                            tooltip("God of the seas.").
                            command("/dg click for poseidon");
                    message.then("Hades").
                            color(ChatColor.GRAY).
                            tooltip("God of the underworld.").
                            command("/dg click for hades");
                } else {
                    message.then("Cronus ").
                            color(ChatColor.DARK_GRAY).
                            tooltip("Titan of chaos and time.").
                            command("/dg click for cronus");
                    message.then("Prometheus ").
                            color(ChatColor.RED).
                            tooltip("Titan of fire, wisdom, and humanity.").
                            command("/dg click for prometheus");
                    message.then("Rhea").
                            color(ChatColor.DARK_GREEN).
                            tooltip("Titan of the earth.").
                            command("/dg click for rhea");
                }
                message.send(p);
            } else if (args[0].equalsIgnoreCase("god")) {
                if (args[1].equalsIgnoreCase("list")) {
                    p.sendMessage(ChatColor.GRAY + "---");
                    p.sendMessage(ChatColor.GOLD + "Primary Æsir:");
                    p.sendMessage("Zeus - God of the sky.");
                    p.sendMessage("Poseidon - God of the seas.");
                    p.sendMessage("Hades - God of the underworld.");
                    p.sendMessage(ChatColor.GOLD + "Minor Æsir:");
                    p.sendMessage("Ares - God of war.");
                    p.sendMessage("Athena - God of wisdom.");
                    p.sendMessage("Hephaestus - God of the forge.");
                    p.sendMessage("Apollo - God of healing and archery.");
                    p.sendMessage("For more info on a specific God, type " + ChatColor.YELLOW + "/dg god <name>");
                } else if (args[1].equalsIgnoreCase("zeus")) {
                    p.sendMessage(ChatColor.GRAY + "---Zeus");
                    p.sendMessage("Passive: Take no fall damage.");
                    p.sendMessage("Active: Call a lightning strike at a target location. " + ChatColor.GREEN + "/lightning");
                    p.sendMessage("Costs 25 Favor. Can bind.");
                    p.sendMessage("Active: Lift a target entity high up in the air. " + ChatColor.GREEN + "/lift");
                    p.sendMessage("Costs 150 Favor. Can bind.");
                    p.sendMessage("Ultimate: Cause a lightning storm, killing nearby entities. " + ChatColor.GREEN + "/storm");
                    p.sendMessage("Costs 1000 Favor, 25 minute cooldown.");
                } else if (args[1].equalsIgnoreCase("poseidon")) {
                    p.sendMessage(ChatColor.GRAY + "---Poseidon");
                    p.sendMessage("Passive: Cannot drown, and heals quickly underwater.");
                    p.sendMessage("Active: For 30 seconds, break blocks underwater instantly. " + ChatColor.GREEN + "/waterbreak");
                    p.sendMessage("Costs 100 Favor.");
                    p.sendMessage("Active: Liquefy a non-player entity, turning them into water. " + ChatColor.GREEN + "/liquefy");
                    p.sendMessage("Costs 25 Favor. Can bind.");
                    p.sendMessage("Ultimate: Fling a TNT loaded trident to the ground at target location. " + ChatColor.GREEN + "/prime");
                    p.sendMessage("Costs 800 Favor, 25 minute cooldown.");
                } else if (args[1].equalsIgnoreCase("hades")) {
                    p.sendMessage(ChatColor.GRAY + "---Hades");
                    p.sendMessage("Passive: Immune to zombie and skeleton damage.");
                    p.sendMessage("Passive: Nearby zombies and skeletons do not burn in sunlight.");
                    p.sendMessage("Active: Cause nearby zombies and skeletons to attack a target. ");
                    p.sendMessage(ChatColor.GREEN + "/target" + ChatColor.WHITE + " Can bind.");
                    p.sendMessage("Active: Summon a servant from the depths of Tartarus. ");
                    p.sendMessage("" + ChatColor.GREEN + "/raisedead" + ChatColor.WHITE + " Costs 50 Favor. Can bind.");
                    p.sendMessage("Ultimate: Drop nearby entities into the Void. " + ChatColor.GREEN + "/tartarus");
                    p.sendMessage("Costs 1000 Favor, 25 minute cooldown.");
                } else if (args[1].equalsIgnoreCase("ares")) {
                    p.sendMessage(ChatColor.GRAY + "---Ares");
                    p.sendMessage("Passive: Attacking an entity continuously gives a finishing");
                    p.sendMessage("bonus of Favor.");
                    p.sendMessage("Active: For one minute, heal equal to the damage you deal.");
                    p.sendMessage("Costs 50 Favor. " + ChatColor.GREEN + "/bloodthirst");
                    p.sendMessage("Active: Fling the sword in your hand at a target (ranged damage).");
                    p.sendMessage("Costs 4 Favor. Can bind. " + ChatColor.GREEN + "/fling");
                    p.sendMessage("Ultimate: Call up to 20 tamed wolves to your side.");
                    p.sendMessage("Costs 50 Favor per wolf. " + ChatColor.GREEN + "/pound");
                    p.sendMessage(ChatColor.RED + "Selection item: Diamond Sword");
                } else if (args[1].equalsIgnoreCase("athena")) {
                    p.sendMessage(ChatColor.GRAY + "---Athena");
                    p.sendMessage("Passive: Athena may protect you from attack.");
                    p.sendMessage("Active: Spy on a player. Learn their deities and amount of");
                    p.sendMessage("health. Costs no Favor. " + ChatColor.GREEN + "/spy <playername>");
                    p.sendMessage("Active: Call a ceasefire for 30 seconds. No damage can be");
                    p.sendMessage("dealt during this time. Costs 80 Favor. " + ChatColor.GREEN + "/ceasefire");
                    p.sendMessage("Ultimate: Double the attack power and halve the amount of da-");
                    p.sendMessage("mage taken by nearby allies for one minute. Costs 600 Favor.");
                    p.sendMessage(ChatColor.GREEN + "/specialtactics");
                    p.sendMessage(ChatColor.RED + "Selection item: Bookshelf");
                } else if (args[1].equalsIgnoreCase("hephaestus")) {
                    p.sendMessage(ChatColor.GRAY + "---Hephaestus");
                    p.sendMessage("Passive: Nearby furnaces produce twice as many items.");
                    p.sendMessage("Active: Repair the item you're holding in hand. Cost is ");
                    p.sendMessage("determined by the durability of the item. " + ChatColor.GREEN + "/fix");
                    p.sendMessage("Active: Smith a block of iron, gold, or diamond into a ");
                    p.sendMessage("full suit of armor. Costs 50 Favor. " + ChatColor.GREEN + "/forge");
                    p.sendMessage("Ultimate: Reduce the durability of the items nearby titans ");
                    p.sendMessage("and mortals are holding in hand. If the item is a tool, it");
                    p.sendMessage("will have 1 use left. Costs 600 Favor. " + ChatColor.GREEN + "/shatter");
                    p.sendMessage(ChatColor.RED + "Selection item: Lava Bucket");
                } else if (args[1].equalsIgnoreCase("apollo")) {
                    p.sendMessage(ChatColor.GRAY + "---Apollo");
                    p.sendMessage("Passive: Immune to damage from arrows.");
                    p.sendMessage("Active: Arrows you shoot heal the targets they hit.");
                    p.sendMessage("Costs 20 Favor per arrow. " + ChatColor.GREEN + "/healarrow");
                    p.sendMessage("Active: Shoot an arrow that homes in on targets and may");
                    p.sendMessage("ignite on impact. Costs 45 Favor. No arrows required.");
                    p.sendMessage(ChatColor.GREEN + "/firearrow");
                    p.sendMessage("Ultimate: Fire infinite arrows for 30 seconds.");
                    p.sendMessage("No cooldown. Costs 400 Favor. " + ChatColor.GREEN + "/soularrow");
                    p.sendMessage(ChatColor.RED + "Selection item: Arrow");
                } else if (args[1].equalsIgnoreCase("rank") || args[1].equalsIgnoreCase("ranks") || args[1].equalsIgnoreCase("ranking")) {
                    Collection<PlayerInfo> list = plugin.getMaster();
                    if (list.size() < 1) {
                        p.sendMessage("There are no players to rank.");
                        return;
                    }
                    ArrayList<PlayerInfo> gods = new ArrayList<PlayerInfo>();
                    for (PlayerInfo pi : list) {
                        if (pi instanceof GodPlayerInfo) gods.add(pi);
                    }
                    GodPlayerInfo[] Gods = new GodPlayerInfo[gods.size()];
                    for (int i = 0; i < gods.size(); i++)
                        Gods[i] = (GodPlayerInfo) gods.get(i);
                    for (int i = 0; i < Gods.length; i++) {
                        int highestIndex = i;
                        int highestBless = Gods[i].getRanking();
                        for (int j = i; j < Gods.length; j++) {
                            if (Gods[j].getRanking() > highestBless) {
                                highestIndex = j;
                                highestBless = Gods[j].getRanking();
                            }
                        }
                        PlayerInfo temp = Gods[i];
                        Gods[i] = Gods[highestIndex];
                        Gods[highestIndex] = (GodPlayerInfo) temp;
                    }
                    p.sendMessage(ChatColor.GOLD + "Top 10 Æsir:");
                    if (Gods.length >= 5) for (int i = 0; i < 5; i++) {
                        p.sendMessage((i + 1) + "- " + Gods[i].getPlayerName() + ": " + Gods[i].getFavor() + " Favor, " + Gods[i].getBlessing() + " Blessing, " + Gods[i].getAllegiance().size() + " Æsir.");
                    }
                    else for (int i = 0; i < Gods.length; i++) {
                        p.sendMessage((i + 1) + "- " + Gods[i].getPlayerName() + ": " + Gods[i].getFavor() + " Favor, " + Gods[i].getBlessing() + " Blessing, " + Gods[i].getAllegiance().size() + " Æsir.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("titan")) {
                if (args[1].equalsIgnoreCase("list")) {
                    p.sendMessage(ChatColor.GRAY + "---");
                    p.sendMessage(ChatColor.GOLD + "Primary Jotunn:");
                    p.sendMessage("Cronus - Titan of chaos and time.");
                    p.sendMessage("Prometheus - Titan of fire, wisdom, and humanity.");
                    p.sendMessage("Rhea - Titan of the earth.");
                    p.sendMessage(ChatColor.GOLD + "Minor Jotunn:");
                    p.sendMessage("Hyperion - Titan of light and the sun.");
                    p.sendMessage("Typhon - Titan of power, father of monsters.");
                    p.sendMessage("Oceanus - Titan of the seas.");
                    p.sendMessage("Styx - Titan of death.");
                    p.sendMessage("For more info on a specific Titan, type " + ChatColor.YELLOW + "/dg titan <name>");
                } else if (args[1].equalsIgnoreCase("cronus")) {
                    p.sendMessage(ChatColor.GRAY + "---Cronus");
                    p.sendMessage("Passive: Take reduced damage from all sources. Can only die ");
                    p.sendMessage("to direct attacks.");
                    p.sendMessage("Passive: Attacking with a sickle (hoe) does extra damage.");
                    p.sendMessage("Active: Change the world time. " + ChatColor.GREEN + "/ctime <dawn/day/dusk/night>");
                    p.sendMessage("Costs 50 Power.");
                    p.sendMessage("Ultimate: Reduce movement speed of all entities for one minute. " + ChatColor.GREEN + "/slow");
                    p.sendMessage("Costs 800 Power.");
                } else if (args[1].equalsIgnoreCase("prometheus")) {
                    p.sendMessage(ChatColor.GRAY + "---Prometheus");
                    p.sendMessage("Passive: Immune to fire.");
                    p.sendMessage("Passive: Slowly recovers health.");
                    p.sendMessage("Active: Shoot a fireball. " + ChatColor.GREEN + "/fireball");
                    p.sendMessage("Costs 15 Power. Can bind.");
                    p.sendMessage("Ultimate: Fireballs fall like meteors to smite nearby entities. ");
                    p.sendMessage(ChatColor.GREEN + "/firestorm");
                    p.sendMessage("Costs 1000 Power.");
                } else if (args[1].equalsIgnoreCase("rhea")) {
                    p.sendMessage(ChatColor.GRAY + "---Rhea");
                    p.sendMessage("Passive: Clicking saplings and crops makes them grow fully.");
                    p.sendMessage("Right clicking a sapling changes its type.");
                    p.sendMessage("Passive: Eating a log (left click) heals half a heart.");
                    p.sendMessage("Active: Place a tree that can be detonated. " + ChatColor.GREEN + "/plant");
                    p.sendMessage("Multiple trees can be spawned and all detonated at once.");
                    p.sendMessage("Costs 100 Power. Can bind.");
                    p.sendMessage("Ultimate: Nature reclaims its land by covering an existing chunk");
                    p.sendMessage("with stone, dirt, and trees. " + ChatColor.GREEN + "/terraform");
                    p.sendMessage("Costs 1000 Power.");
                } else if (args[1].equalsIgnoreCase("hyperion")) {
                    p.sendMessage(ChatColor.GRAY + "---Hyperion");
                    p.sendMessage("Passive: Deal 1.5x damage while in a well lit area.");
                    p.sendMessage("Passive: Right click netherrack or soul sand to transmute it");
                    p.sendMessage("into glowstone.");
                    p.sendMessage("Active: Cause a non-player entity to combust into flames. ");
                    p.sendMessage(ChatColor.GREEN + "/combust" + ChatColor.WHITE + " Costs 25 Power. Can bind.");
                    p.sendMessage("Ultimate: Strike down your foes with the power of the sun.");
                    p.sendMessage(ChatColor.GREEN + "/smite" + ChatColor.WHITE + " Costs 1100 Power.");
                    p.sendMessage(ChatColor.RED + "Selection item: Glowstone");
                } else if (args[1].equalsIgnoreCase("typhon")) {
                    p.sendMessage(ChatColor.GRAY + "---Typhon");
                    p.sendMessage("Passive: Super knockback when attacking others.");
                    p.sendMessage("Passive: Create a huge explosion on death.");
                    p.sendMessage("Active: Teleport to a target entity and deal damage to them.");
                    p.sendMessage(ChatColor.GREEN + "/charge" + ChatColor.WHITE + " Costs 20 Power. Can bind.");
                    p.sendMessage("Ultimate: Attack all nearby entities, striking each with");
                    p.sendMessage("a crushing blow. " + ChatColor.GREEN + "/fury " + ChatColor.WHITE + "Costs 750 Power. 1 minute cooldown.");
                    p.sendMessage(ChatColor.RED + "Selection item: Diamond Sword");
                } else if (args[1].equalsIgnoreCase("oceanus")) {
                    p.sendMessage(ChatColor.GRAY + "---Oceanus");
                    p.sendMessage("Passive: Heal as long it is raining.");
                    p.sendMessage("Passive: Punching enemies deals critical damage if they are");
                    p.sendMessage("standing in water.");
                    p.sendMessage("Passive: Right click an enemy with no item equipped to create");
                    p.sendMessage("a rapidly shrinking pool of water under them.");
                    p.sendMessage("Active: Launch a terribly inaccurate exploding squid at a");
                    p.sendMessage("target entity. " + ChatColor.GREEN + "/squid" + ChatColor.WHITE + " Costs 75 Power. Can bind.");
                    p.sendMessage("Ultimate: Cause a rainstorm in your world.");
                    p.sendMessage(ChatColor.GREEN + "/makeitrain" + ChatColor.WHITE + " Costs 250 Power.");
                    p.sendMessage(ChatColor.RED + "Selection item: Bucket of Water");
                } else if (args[1].equalsIgnoreCase("styx")) {
                    p.sendMessage(ChatColor.GRAY + "---Styx");
                    p.sendMessage("Passive: Can see phantoms.");
                    p.sendMessage("Passive: Slowly leeches life from all nearby players.");
                    p.sendMessage("Active: Make a titan invulnerable for 25 seconds.");
                    p.sendMessage(ChatColor.GREEN + "/invincible <player name>" + ChatColor.WHITE + " Costs 150 Power.");
                    p.sendMessage("Ultimate: Revive a fallen titan. Cooldown of 30 seconds.");
                    p.sendMessage(ChatColor.GREEN + "/revive" + ChatColor.WHITE + " Costs 500 Power.");
                    p.sendMessage(ChatColor.RED + "Selection item: Bone");
                } else if (args[1].equalsIgnoreCase("rank") || args[1].equalsIgnoreCase("ranks") || args[1].equalsIgnoreCase("ranking")) {
                    Collection<PlayerInfo> list = plugin.getMaster();
                    if (list.size() < 1) {
                        p.sendMessage("There are no players to rank.");
                        return;
                    }
                    ArrayList<PlayerInfo> titans = new ArrayList<PlayerInfo>();
                    for (PlayerInfo pi : list) {
                        if (pi instanceof TitanPlayerInfo) titans.add(pi);
                    }
                    TitanPlayerInfo[] Titans = new TitanPlayerInfo[titans.size()];
                    for (int i = 0; i < titans.size(); i++)
                        Titans[i] = (TitanPlayerInfo) titans.get(i);
                    for (int i = 0; i < Titans.length; i++) {
                        int highestIndex = i;
                        int highestBless = Titans[i].getRanking();
                        for (int j = i; j < Titans.length; j++) {
                            if (Titans[j].getRanking() > highestBless) {
                                highestIndex = j;
                                highestBless = Titans[j].getRanking();
                            }
                        }
                        PlayerInfo temp = Titans[i];
                        Titans[i] = Titans[highestIndex];
                        Titans[highestIndex] = (TitanPlayerInfo) temp;
                    }
                    p.sendMessage(ChatColor.GOLD + "Top 10 Jotunn:");
                    if (Titans.length >= 10) for (int i = 0; i < 10; i++) {
                        p.sendMessage((i + 1) + "- " + Titans[i].getPlayerName() + ": " + Titans[i].getPower() + " Power, " + Titans[i].getGlory() + " Glory, " + Titans[i].getAllegiance().size() + " Jotunn.");
                    }
                    else for (int i = 0; i < Titans.length; i++) {
                        p.sendMessage((i + 1) + "- " + Titans[i].getPlayerName() + ": " + Titans[i].getPower() + " Power, " + Titans[i].getGlory() + " Glory, " + Titans[i].getAllegiance().size() + " Jotunn.");
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("click") && args[1].equals("for")) {
                if (plugin.isGod(p) || plugin.isTitan(p)) return;
                Divine choice = null;
                try {
                    choice = Divine.valueOf(args[2].toUpperCase());
                } catch (Exception ignored) {
                }
                if (choice != null && choice.isTier1()) {
                    p.sendMessage(ChatColor.GRAY + "You have been accepted to the lineage of " + ChatColor.GOLD + choice.name() + ChatColor.GRAY + ".");

                    if (choice.isGod()) {
                        plugin.addToMaster(new GodPlayerInfo(p, choice));
                        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                        gpi.setFavor(50);
                        gpi.setBlessing(50);
                    } else {
                        plugin.addToMaster(new TitanPlayerInfo(p, choice));
                        TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
                        tpi.setPower(50);
                        tpi.setGlory(50);
                    }
                }
            }
        }
    }

    private void onPlayerCommandPreprocessDemigods(PlayerCommandPreprocessEvent event) {
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
            switch (cmd) {
                case DG:
                case DEMIGODS:
                    helpcode(args, player);
                    event.setCancelled(true);
                    return;
                case CHECK:
                    if (plugin.isGod(player)) {
                        GodPlayerInfo pi = (GodPlayerInfo) plugin.getInfo(player);
                        player.sendMessage(ChatColor.YELLOW + "--" + player.getName() + "--" + pi.getRank() + "");
                        String send = "Your deities are: ";
                        for (int i = 0; i < pi.getAllegiance().size(); i++) {
                            send += pi.getAllegiance().get(i).name() + " ";
                        }
                        player.sendMessage(send);
                        player.sendMessage("Current favor: " + pi.getFavor());
                        player.sendMessage("Current blessing: " + pi.getBlessing());
                        if (pi.isEligible()) {
                            player.sendMessage(ChatColor.GREEN + "You are eligible for alliance with another God.");
                        } else if (!pi.getRank().equals("Olympian") && !pi.isEligible())
                            player.sendMessage("<" + pi.blessingTillNext() + " Blessing until next rank>");
                        if (!pi.isAlive()) player.sendMessage(ChatColor.GRAY + "You are a phantom.");
                    } else if (plugin.isTitan(player)) {
                        TitanPlayerInfo pi = (TitanPlayerInfo) plugin.getInfo(player);
                        player.sendMessage(ChatColor.YELLOW + "--" + player.getName() + "--" + pi.getRank() + "");
                        String send = "Your deities are: ";
                        for (int i = 0; i < pi.getAllegiance().size(); i++) {
                            send += pi.getAllegiance().get(i).name() + " ";
                        }
                        player.sendMessage(send);
                        player.sendMessage("Current power: " + pi.getPower());
                        player.sendMessage("Current glory: " + pi.getGlory());
                        if (pi.costForNext() <= pi.getGlory())
                            player.sendMessage(ChatColor.GREEN + "You have enough Glory to purchase a new alliance.");
                        else player.sendMessage("<" + (pi.costForNext() - pi.getGlory()) + " Glory until next rank>");
                        if (!pi.isAlive()) player.sendMessage(ChatColor.GRAY + "You are a phantom.");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "--" + player.getName() + "--Mortal--");
                        player.sendMessage("You are not affiliated with any Æsir or Jotunn.");
                    }
                    event.setCancelled(true);
                    return;
            }
            if (!plugin.getConfigHandler().isParticipating(event.getPlayer().getWorld())) {
                return;
            }
            switch (cmd) {
                case PROTECT:
                    if (args.length != 1) {
                        player.sendMessage("Correct syntax: " + ChatColor.YELLOW + "/protect <deityname>");
                        break;
                    }
                    if (!plugin.isGod(player) && !plugin.isTitan(player)) break;
                    try {
                        Divine d = Divine.valueOf(args[0].toUpperCase());
                        if (plugin.getInfo(player).hasDeity(d)) {
                            if (plugin.getInfo(player).getProtect() == 0) {
                                player.sendMessage("You are requesting cuboid protection from " + d.name() + ".");
                                plugin.getInfo(player).startProtect();
                                plugin.getInfo(player).setTempDeity(d);
                                player.sendMessage("Click the first corner block.");
                            } else player.sendMessage("You are already in the process of cuboid protection.");
                        } else player.sendMessage("You can't protect land in the name of deities you don't have.");
                    } catch (Exception nullpointer) {
                        player.sendMessage("Error. Does that deity exist?");
                    }
                    break;
                case UNPROTECT:
                    if (args.length != 1) {
                        player.sendMessage("Correct syntax: " + ChatColor.YELLOW + "/unprotect <deityname>");
                        break;
                    }
                    try {
                        Divine dd = Divine.valueOf(args[0].toUpperCase());
                        if (plugin.getInfo(player).getUnprotect()) {
                            player.sendMessage("Click a block protected by " + dd.name() + " in the area to unprotect.");
                            break;
                        }
                        if (plugin.getInfo(player).hasDeity(dd)) {
                            if (plugin.getInfo(player).getProtect() == 0) {
                                player.sendMessage("Click a block protected by " + dd.name() + " in the area to unprotect.");
                                plugin.getInfo(player).setUnprotect(true);
                                plugin.getInfo(player).setTempDeity(dd);
                            }
                        } else {
                            player.sendMessage("You cannot unprotect other deities' zones.");
                        }
                    } catch (Exception nullpointer) {
                        player.sendMessage("Error. Does that deity exist?");
                    }
                    break;
                case CHECKPLAYER:
                    if (player.isOp() || player.hasPermission("demigods.check") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length == 1) {
                            try {
                                Player pp;
                                pp = plugin.getServer().getPlayer(args[0]);
                                if (pp == null) pp = plugin.getServer().matchPlayer(args[0]).get(0);
                                if (plugin.isGod(pp)) {
                                    GodPlayerInfo pi = (GodPlayerInfo) plugin.getInfo(pp);
                                    player.sendMessage(ChatColor.YELLOW + "--" + pp.getName() + "--" + pi.getRank() + "--");
                                    String send = pp.getName() + "'s deities are: ";
                                    for (int i = 0; i < pi.getAllegiance().size(); i++) {
                                        send += pi.getAllegiance().get(i).name() + " ";
                                    }
                                    player.sendMessage(send);
                                    player.sendMessage("Current favor: " + pi.getFavor());
                                    player.sendMessage("Current blessing: " + pi.getBlessing());
                                    if (!pi.isAlive()) player.sendMessage(pp.getName() + " is a phantom.");
                                } else if (plugin.isTitan(pp)) {
                                    TitanPlayerInfo pi = (TitanPlayerInfo) plugin.getInfo(pp);
                                    player.sendMessage(ChatColor.YELLOW + "--" + pp.getName() + "--" + pi.getRank() + "--");
                                    String send = pp.getName() + "'s deities are: ";
                                    for (int i = 0; i < pi.getAllegiance().size(); i++) {
                                        send += pi.getAllegiance().get(i).name() + " ";
                                    }
                                    player.sendMessage(send);
                                    player.sendMessage("Current power: " + pi.getPower());
                                    player.sendMessage("Current glory: " + pi.getGlory());
                                    if (!pi.isAlive()) player.sendMessage(pp.getName() + " is a phantom.");
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "--" + pp.getName() + "--Mortal--");
                                    player.sendMessage(pp.getName() + " is not affiliated with any Æsir or Jotunn.");
                                }
                            } catch (Exception error) {
                                player.sendMessage(ChatColor.RED + "Error. Was player's name typed correctly?");
                            }
                        } else {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/checkplayer <playername>");
                        }
                    }
                    break;
                case GIVEDEITY:
                    if (player.isOp() || player.hasPermission("demigods.give") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length == 2) {
                            try {
                                Player p = plugin.getServer().getPlayer(args[0]);
                                if (plugin.isGod(p) || plugin.isTitan(p)) {
                                    PlayerInfo pi = plugin.getInfo(p);
                                    if (!pi.hasDeity(Divine.valueOf(args[1].toUpperCase()))) {
                                        pi.addToAllegiance(Divine.valueOf(args[1].toUpperCase()));
                                        player.sendMessage(p.getName() + " now has the powers of " + args[1].toUpperCase());
                                        player.sendMessage("Skills may not work if there is a god/titan mismatch.");
                                    } else {
                                        player.sendMessage(p.getName() + " already has that deity.");
                                    }
                                } else {
                                    Divine de = Divine.valueOf(args[1].toUpperCase());
                                    boolean god;
                                    if (de != null) {
                                        switch (de) {
                                            case ZEUS:
                                            case POSEIDON:
                                            case HADES:
                                                god = true;
                                                break;
                                            default:
                                                god = false;
                                                break;
                                        }
                                        if (god) {
                                            plugin.addToMaster(new GodPlayerInfo(p, de));
                                            player.sendMessage("Success!");
                                        } else {
                                            plugin.addToMaster(new TitanPlayerInfo(p, de));
                                            player.sendMessage("Success!");
                                        }
                                        plugin.saveAll();
                                    } else {
                                        player.sendMessage("That is not a recognized deity.");
                                        break;
                                    }
                                }
                            } catch (Exception error) {
                                player.sendMessage(ChatColor.RED + "Error. Was player's name typed correctly?");
                            }
                        } else {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/givedeity <playername> <deityname>");
                            player.sendMessage("Giving a God aligned player a Titan deity will not work.");
                            player.sendMessage("Giving a Titan aligned player a God deity will not work.");
                        }
                    }
                    break;
                case REMOVEDEITY:
                    if (player.isOp() || player.hasPermission("demigods.remove") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length != 2) {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/removedeity <playername> <deityname>");
                            break;
                        }
                        try {
                            Player p = plugin.getServer().getPlayer(args[0]);
                            Divine de = Divine.valueOf(args[1].toUpperCase());
                            if (p != null && de != null) {
                                PlayerInfo pi = plugin.getInfo(p);
                                if (pi.hasDeity(de)) {
                                    pi.removeFromAllegiance(de);
                                    if (pi.getAllegiance().size() == 0) {
                                        plugin.removeFromMaster(p);
                                    }
                                    plugin.saveAll();
                                    player.sendMessage("Success!");
                                } else {
                                    player.sendMessage(p.getName() + " doesn't have that deity.");
                                }
                            } else {
                                player.sendMessage("Error. Were the player and deity names typed correctly?");
                            }
                        } catch (Exception error) {
                            player.sendMessage(ChatColor.RED + "Error. Was player's name typed correctly?");
                        }
                    }
                    break;
                case SETFAVOR:
                case SETPOWER:
                case SETGLORY:
                case SETBLESSING:
                    if (player.isOp() || player.hasPermission("demigods.set") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length != 2) {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/setfavor <playername> <amount of favor>");
                            break;
                        }
                        try {
                            Player p = plugin.getServer().getPlayer(args[0]);
                            int amt = Integer.parseInt(args[1]);
                            if (plugin.isGod(p)) {
                                GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                                gpi.setFavor(amt);
                                gpi.setBlessing(amt);
                                player.sendMessage("Success!");
                            } else if (plugin.isTitan(p)) {
                                TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
                                tpi.setPower(amt);
                                tpi.setGlory(amt);
                                player.sendMessage("Success!");
                            } else {
                                player.sendMessage("That player is not a God or a Titan.");
                            }
                        } catch (Exception error) {
                            player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                        }
                    }
                    break;
                case FORSAKE:
                    if (!plugin.isGod(player) && !plugin.isTitan(player)) break;
                    if (args.length != 1) {
                        player.sendMessage("Syntax: " + ChatColor.YELLOW + "/forsake <deityname>" + ChatColor.WHITE + " or " + ChatColor.YELLOW + "/forsake all");
                        break;
                    }
                    try {
                        if (args[0].equalsIgnoreCase("all")) {
                            plugin.removeFromMaster(player);
                            player.sendMessage(ChatColor.RED + "You are no longer a Demigod/Titanspawn.");
                        } else {
                            Divine remove = Divine.valueOf(args[0].toUpperCase());
                            if (plugin.getInfo(player).hasDeity(remove)) {
                                if (plugin.getInfo(player).getAllegiance().size() > 1) {
                                    if (plugin.isGod(player)) {
                                        GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(player);
                                        gpi.removeFromAllegiance(remove);
                                        gpi.setBlessing(gpi.getBlessing() / 2);
                                        gpi.setFavor(gpi.getFavor() / 2);
                                        player.sendMessage(ChatColor.RED + "You have forsaken " + remove.name() + ".");
                                        player.sendMessage(ChatColor.RED + "Your Blessing and Favor have been halved.");
                                    } else if (plugin.isTitan(player)) {
                                        TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
                                        tpi.removeFromAllegiance(remove);
                                        tpi.setGlory(tpi.getGlory() / 2);
                                        tpi.setPower(tpi.getPower() / 2);
                                        player.sendMessage(ChatColor.RED + "You have forsaken " + remove.name() + ".");
                                        player.sendMessage(ChatColor.RED + "Your Power and Glory have been halved.");
                                    }
                                } else {
                                    plugin.removeFromMaster(player);
                                    player.sendMessage(ChatColor.RED + "You have forsaken " + remove.name() + ".");
                                }
                            }
                        }
                    } catch (Exception error) {
                        player.sendMessage(ChatColor.RED + "Error. Was the deity's name spelled right?");
                    }
                    break;
                case REVIVEPLAYER:
                    if (player.isOp() || player.hasPermission("demigods.revive") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length != 1) {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/reviveplayer <playername>");
                            break;
                        }
                        try {
                            Player p = plugin.getServer().getPlayer(args[0]);
                            if (plugin.isGod(p) || plugin.isTitan(p)) {
                                if (!plugin.getInfo(p).isAlive()) {
                                    plugin.getInfo(p).setAlive(true);
                                    PhantomCommands.unphantomize(p);
                                    p.sendMessage(ChatColor.GOLD + "You were resurrected by " + player.getName() + ".");
                                    player.sendMessage(p.getName() + " was resurrected.");
                                    break;
                                }
                            }
                            player.sendMessage("That player is not a phantom.");
                        } catch (Exception error) {
                            player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                        }
                    }
                    break;
                case ADDFAVOR:
                case ADDPOWER:
                    if (player.isOp() || player.hasPermission("demigods.add") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length != 2) {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/addfavor <playername> <amount of favor>");
                            break;
                        }
                        try {
                            Player p = plugin.getServer().getPlayer(args[0]);
                            int amt = Integer.parseInt(args[1]);
                            if (plugin.isGod(p)) {
                                GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                                gpi.setFavor(gpi.getFavor() + amt);
                                player.sendMessage("Success! " + p.getName() + " now has " + gpi.getFavor() + " Favor.");
                            } else if (plugin.isTitan(p)) {
                                TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
                                tpi.setPower(tpi.getPower() + amt);
                                player.sendMessage("Success! " + p.getName() + " now has " + tpi.getPower() + " Power.");
                            } else {
                                player.sendMessage("That player is not a God or a Titan.");
                            }
                        } catch (Exception error) {
                            player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                        }
                    }
                    break;
                case ADDBLESSING:
                case ADDGLORY:
                    if (player.isOp() || player.hasPermission("demigods.add") || player.isOp() || player.hasPermission("demigods.admin")) {
                        if (args.length != 2) {
                            player.sendMessage("Syntax: " + ChatColor.YELLOW + "/addblessing <playername> <amount of blessing>");
                            break;
                        }
                        try {
                            Player p = plugin.getServer().getPlayer(args[0]);
                            int amt = Integer.parseInt(args[1]);
                            if (plugin.isGod(p)) {
                                GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                                gpi.setBlessing(gpi.getBlessing() + amt);
                                player.sendMessage("Success! " + p.getName() + " now has " + gpi.getBlessing() + " Blessing.");
                            } else if (plugin.isTitan(p)) {
                                TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
                                tpi.setGlory(tpi.getGlory() + amt);
                                player.sendMessage("Success! " + p.getName() + " now has " + tpi.getGlory() + " Glory.");
                            } else {
                                player.sendMessage("That player is not a God or a Titan.");
                            }
                        } catch (Exception error) {
                            player.sendMessage(ChatColor.RED + "Error. Did you mis-spell something?");
                        }
                    }
                    break;
            }
        } catch (NoSuchMethodError ex) {
            player.sendMessage("The plugin for " + sects[0].toLowerCase() + " is broken or out of date.");
        } catch (Exception ex) {
            player.sendMessage("�cError: " + ex.getMessage());
            ex.printStackTrace();
        }
        event.setCancelled(true);
    }

    public enum Commands {
        CHECK, PROTECT, UNPROTECT, DG, DEMIGODS, GIVEDEITY, REMOVEDEITY, CHECKPLAYER, SETFAVOR, SETPOWER, SETGLORY, SETBLESSING, FORSAKE, REVIVEPLAYER, ADDFAVOR, ADDGLORY, ADDPOWER, ADDBLESSING
    }

    private void onPlayerInteractDemigods(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!(plugin.isGod(p) || plugin.isTitan(p))) return;
        if (plugin.getInfo(p).getUnprotect()) {
            for (DeityLocale dl : plugin.getAllLocs()) {
                PlayerInfo pi = plugin.getInfo(p);
                if (pi.hasDeity(dl.getDeity())) {
                    for (Cuboid c : dl.getLocale()) {
                        if (c.isInCuboid(e.getClickedBlock().getLocation())) {
                            dl.removeCuboid(c);
                            e.setCancelled(true);
                            p.sendMessage("This area is no longer protected by " + ChatColor.YELLOW + pi.getTempDeity().name() + ChatColor.WHITE + ".");
                            plugin.getInfo(p).setUnprotect(false);
                            return;
                        }
                    }
                }
            }
            p.sendMessage("Did not detect a shrine or protected area to unprotect.");
            plugin.getInfo(p).setUnprotect(false);
        }
        if (!(plugin.isGod(p) || plugin.isTitan(p))) return;
        if (e.getClickedBlock() != null) {
            if (plugin.getInfo(p).getProtect() == 0) return;
            else if (plugin.getInfo(p).getProtect() == 1) {
                p.sendMessage("First corner selected.");
                plugin.getInfo(p).setCorner(plugin.toWriteLocation(e.getClickedBlock().getLocation()));
                plugin.getInfo(p).incrementProtect();
                e.setCancelled(true);
            } else if (plugin.getInfo(p).getProtect() == 2) {
                p.sendMessage("Second corner selected...");
                Cuboid cu = new Cuboid(p.getWorld().getName(), plugin.getInfo(p).getCorner(), plugin.toWriteLocation(e.getClickedBlock().getLocation()));
                if (plugin.getInfo(p).getTempDeity() == Divine.OMNI) {
                    p.sendMessage(ChatColor.YELLOW + "" + plugin.getInfo(p).getTempDeity() + ChatColor.WHITE + " has protected the " + cu.getVolume() + " blocks");
                    if (plugin.getLoc(plugin.getInfo(p).getTempDeity()) == null)
                        plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
                    plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
                } else if (plugin.isGod(p)) {
                    GodPlayerInfo gpi = (GodPlayerInfo) plugin.getInfo(p);
                    if (gpi.getFavor() >= (cu.getVolume() * (0.7))) {
                        gpi.setFavor(gpi.getFavor() - (int) cu.getVolume());
                        p.sendMessage(ChatColor.YELLOW + "" + gpi.getTempDeity() + ChatColor.WHITE + " has protected the " + cu.getVolume() + " blocks");
                        p.sendMessage("in this cuboid at the cost of " + cu.getVolume() + " Favor.");
                        if (plugin.getLoc(plugin.getInfo(p).getTempDeity()) == null)
                            plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
                        plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
                    } else p.sendMessage("You need " + cu.getVolume() + " Favor to protect this area.");
                } else if (plugin.isTitan(p)) {
                    TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
                    if (tpi.getPower() >= (cu.getVolume() * (0.7))) {
                        tpi.setPower(tpi.getPower() - (int) cu.getVolume());
                        p.sendMessage(ChatColor.YELLOW + "" + tpi.getTempDeity() + ChatColor.WHITE + " has protected the " + cu.getVolume() + " blocks");
                        p.sendMessage("in this cuboid at the cost of " + cu.getVolume() + " Power.");
                        if (plugin.getLoc(plugin.getInfo(p).getTempDeity()) == null)
                            plugin.addToMaster(new DeityLocale(plugin.getInfo(p).getTempDeity()));
                        plugin.getLoc(plugin.getInfo(p).getTempDeity()).addToLocale(cu);
                    } else p.sendMessage("You need " + cu.getVolume() + " Power to protect this area.");
                }
                plugin.getInfo(p).doneProtect();
                e.setCancelled(true);
            }
        }
    }
}
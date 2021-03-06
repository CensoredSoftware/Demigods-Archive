package com.WildAmazing.marinating.Demigods.Listeners;

import com.WildAmazing.marinating.Demigods.DFixes;
import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Util.DMiscUtil;
import com.WildAmazing.marinating.Demigods.Util.DSettings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DDamage implements Listener {
    /*
     * This handler deals with non-Demigods damage (all of that will go directly to DMiscUtil's built in damage function) and converts it
     * to Demigods HP, using individual multipliers for balance purposes.
     *
     * The adjusted value should be around/less than 1 to adjust for the increased health, but not ridiculous
     */
    private static final boolean FRIENDLYFIRE = DSettings.getSettingBoolean("friendly_fire");

    public static void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!DMiscUtil.isFullParticipant(p)) return;
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if (!DMiscUtil.canTarget(p, p.getLocation())) {
            DFixes.checkAndCancel(e);
            return;
        }

        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            if (ee.getDamager() instanceof Player) {
                if (!FRIENDLYFIRE && DMiscUtil.areAllied(p, (Player) ee.getDamager())) {
                    if (DSettings.getSettingBoolean("friendly_fire_message"))
                        ((Player) ee.getDamager()).sendMessage(ChatColor.YELLOW + "No friendly fire.");
                    DFixes.checkAndCancel(e);
                    return;
                }
                if (!DMiscUtil.canTarget(ee.getDamager(), ee.getDamager().getLocation())) {
                    DFixes.checkAndCancel(e);
                    return;
                }
                DMiscUtil.damageDemigods((Player) ee.getDamager(), p, e.getDamage(), DamageCause.ENTITY_ATTACK);
                return;
            }
        }

        if (e.getCause() == DamageCause.LAVA) {
            DFixes.checkAndCancel(e);
            return;
        }

        if ((e.getCause() != DamageCause.ENTITY_ATTACK) && (e.getCause() != DamageCause.PROJECTILE))
            DMiscUtil.damageDemigodsNonCombat(p, e.getDamage(), e.getCause());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        if (DMiscUtil.isFullParticipant(e.getPlayer()))
            DMiscUtil.setHP(e.getPlayer(), DMiscUtil.getMaxHP(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHeal(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!DMiscUtil.isFullParticipant(p)) return;
        DMiscUtil.setHP(p, DMiscUtil.getHP(p) + e.getAmount());
    }

    public static void syncHealth(Player p) {
        double current = DMiscUtil.getHP(p);
        if (current < 1) { // if player should be dead
            p.setHealth(0.0);
            return;
        }
        double ratio = current / DMiscUtil.getMaxHP(p);
        double disp = Math.ceil(ratio * 20);
        if (disp < 1) disp = 1.0;
        p.setHealth(disp);
    }

    @SuppressWarnings("incomplete-switch")
    public static int armorReduction(Player p) {
        if (p.getLastDamageCause() != null)
            if ((p.getLastDamageCause().getCause() == DamageCause.FIRE) || (p.getLastDamageCause().getCause() == DamageCause.FIRE_TICK) || (p.getLastDamageCause().getCause() == DamageCause.SUFFOCATION) || (p.getLastDamageCause().getCause() == DamageCause.LAVA) || (p.getLastDamageCause().getCause() == DamageCause.DROWNING) || (p.getLastDamageCause().getCause() == DamageCause.STARVATION) || (p.getLastDamageCause().getCause() == DamageCause.FALL) || (p.getLastDamageCause().getCause() == DamageCause.VOID) || (p.getLastDamageCause().getCause() == DamageCause.POISON) || (p.getLastDamageCause().getCause() == DamageCause.MAGIC) || (p.getLastDamageCause().getCause() == DamageCause.SUICIDE)) {
                return 0;
            }
        double reduction = 0.0;
        if ((p.getInventory().getBoots() != null) && (p.getInventory().getBoots().getType() != Material.AIR)) {
            switch (p.getInventory().getBoots().getType()) {
                case LEATHER_BOOTS:
                    reduction += 0.3;
                    break;
                case IRON_BOOTS:
                    reduction += 0.6;
                    break;
                case GOLD_BOOTS:
                    reduction += 0.5;
                    break;
                case DIAMOND_BOOTS:
                    reduction += 0.8;
                    break;
                case CHAINMAIL_BOOTS:
                    reduction += 0.7;
                    break;
            }
            p.getInventory().getBoots().setDurability((short) (p.getInventory().getBoots().getDurability() + 1));
            if (p.getInventory().getBoots().getDurability() > p.getInventory().getBoots().getType().getMaxDurability())
                p.getInventory().setBoots(null);
        }
        if ((p.getInventory().getLeggings() != null) && (p.getInventory().getLeggings().getType() != Material.AIR)) {
            switch (p.getInventory().getLeggings().getType()) {
                case LEATHER_LEGGINGS:
                    reduction += 0.5;
                    break;
                case IRON_LEGGINGS:
                    reduction += 1;
                    break;
                case GOLD_LEGGINGS:
                    reduction += 0.8;
                    break;
                case DIAMOND_LEGGINGS:
                    reduction += 1.4;
                    break;
                case CHAINMAIL_LEGGINGS:
                    reduction += 1.1;
                    break;
            }
            p.getInventory().getLeggings().setDurability((short) (p.getInventory().getLeggings().getDurability() + 1));
            if (p.getInventory().getLeggings().getDurability() > p.getInventory().getLeggings().getType().getMaxDurability())
                p.getInventory().setLeggings(null);
        }
        if ((p.getInventory().getChestplate() != null) && (p.getInventory().getChestplate().getType() != Material.AIR)) {
            switch (p.getInventory().getChestplate().getType()) {
                case LEATHER_CHESTPLATE:
                    reduction += 0.8;
                    break;
                case IRON_CHESTPLATE:
                    reduction += 1.6;
                    break;
                case GOLD_CHESTPLATE:
                    reduction += 1.4;
                    break;
                case DIAMOND_CHESTPLATE:
                    reduction += 2;
                    break;
                case CHAINMAIL_CHESTPLATE:
                    reduction += 1.8;
                    break;
            }
            p.getInventory().getChestplate().setDurability((short) (p.getInventory().getChestplate().getDurability() + 1));
            if (p.getInventory().getChestplate().getDurability() > p.getInventory().getChestplate().getType().getMaxDurability())
                p.getInventory().setChestplate(null);
        }
        if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() != Material.AIR)) {
            switch (p.getInventory().getHelmet().getType()) {
                case LEATHER_HELMET:
                    reduction += 0.4;
                    break;
                case IRON_HELMET:
                    reduction += 0.8;
                    break;
                case GOLD_HELMET:
                    reduction += 0.7;
                    break;
                case DIAMOND_HELMET:
                    reduction += 1.3;
                    break;
                case CHAINMAIL_HELMET:
                    reduction += 1;
                    break;
            }
            p.getInventory().getHelmet().setDurability((short) (p.getInventory().getHelmet().getDurability() + 1));
            if (p.getInventory().getHelmet().getDurability() > p.getInventory().getHelmet().getType().getMaxDurability())
                p.getInventory().setHelmet(null);
        }
        return (int) (Math.round(reduction));
    }

    public static double specialReduction(Player p, double amount) {
        if (DMiscUtil.getActiveEffectsList(p.getName()) == null) return amount;
        if (DMiscUtil.getActiveEffectsList(p.getName()).contains("Invincible")) {
            amount *= 0.5;
        }
        if (DMiscUtil.getActiveEffectsList(p.getName()).contains("Ceasefire")) {
            amount *= 0;
        }
        return amount;
    }

    private static ItemStack getBestSoul(Player p) {
        // Define Immortal Soul Fragment
        ItemStack health = new ItemStack(Material.GHAST_TEAR);

        String name = "Immortal Soul Fragment";
        List<String> lore = new ArrayList<String>();
        lore.add("Brings you back to life.");
        lore.add("You regain full heath!");

        ItemMeta item = health.getItemMeta();
        item.setDisplayName(name);
        item.setLore(lore);

        health.setItemMeta(item);

        // Define Immortal Soul Dust
        ItemStack halfHealth = new ItemStack(Material.GLOWSTONE_DUST);

        String halfName = "Immortal Soul Dust";
        List<String> halfLore = new ArrayList<String>();
        halfLore.add("Brings you back to life.");
        halfLore.add("You regain half heath!");

        ItemMeta halfItem = halfHealth.getItemMeta();
        halfItem.setDisplayName(halfName);
        halfItem.setLore(halfLore);

        halfHealth.setItemMeta(halfItem);

        // Define Mortal Soul
        ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET);

        String mortalName = "Mortal Soul";
        List<String> mortalLore = new ArrayList<String>();
        mortalLore.add("Brings you back to life.");
        mortalLore.add("You regain 20 health.");

        ItemMeta mortalItem = mortalHealth.getItemMeta();
        mortalItem.setDisplayName(mortalName);
        mortalItem.setLore(mortalLore);

        mortalHealth.setItemMeta(mortalItem);

        // Player inventory
        ItemStack[] invItems = p.getInventory().getContents();

        // Has soul?
        boolean hasFull = false;
        boolean hasHalf = false;
        boolean hasMortal = false;

        for (ItemStack invItem : invItems) {
            if (invItem == null) continue;
            if (!invItem.hasItemMeta()) continue;

            if (invItem.isSimilar(health)) {
                hasFull = true;
            } else if (invItem.isSimilar(halfHealth)) {
                hasHalf = true;
            } else if (invItem.isSimilar(mortalHealth)) {
                hasMortal = true;
            }
        }

        if (hasFull) return health;
        else if (hasHalf) return halfHealth;
        else if (hasMortal) return mortalHealth;
        else return null;
    }

    public static Boolean cancelSoulDamage(Player p, double damage) {
        if (damage >= DMiscUtil.getHP(p)) {
            // Define Immortal Soul Fragment
            ItemStack health = new ItemStack(Material.GHAST_TEAR);

            String name = "Immortal Soul Fragment";
            List<String> lore = new ArrayList<String>();
            lore.add("Brings you back to life.");
            lore.add("You regain full heath!");

            ItemMeta item = health.getItemMeta();
            item.setDisplayName(name);
            item.setLore(lore);

            health.setItemMeta(item);

            // Define Immortal Soul Dust
            ItemStack halfHealth = new ItemStack(Material.GLOWSTONE_DUST);

            String halfName = "Immortal Soul Dust";
            List<String> halfLore = new ArrayList<String>();
            halfLore.add("Brings you back to life.");
            halfLore.add("You regain half heath!");

            ItemMeta halfItem = halfHealth.getItemMeta();
            halfItem.setDisplayName(halfName);
            halfItem.setLore(halfLore);

            halfHealth.setItemMeta(halfItem);

            // Define Mortal Soul
            ItemStack mortalHealth = new ItemStack(Material.GOLD_NUGGET);

            String mortalName = "Mortal Soul";
            List<String> mortalLore = new ArrayList<String>();
            mortalLore.add("Brings you back to life.");
            mortalLore.add("You regain 20 health.");

            ItemMeta mortalItem = mortalHealth.getItemMeta();
            mortalItem.setDisplayName(mortalName);
            mortalItem.setLore(mortalLore);

            mortalHealth.setItemMeta(mortalItem);

            ItemStack[] invItems = p.getInventory().getContents();

            if (getBestSoul(p) == null) return false;

            for (ItemStack invItem : invItems) {
                if (invItem == null) continue;
                if (!invItem.hasItemMeta()) continue;

                if (invItem.isSimilar(getBestSoul(p))) {
                    int amount = invItem.getAmount();
                    p.getInventory().removeItem(invItem);
                    invItem.setAmount(amount - 1);
                    p.getInventory().addItem(invItem);

                    if (getBestSoul(p) == health) hasFull(p);
                    else if (getBestSoul(p) == halfHealth) {
                        hasHalf(p);
                        lessFakeDeath(p);
                    } else if (getBestSoul(p) == mortalHealth) {
                        hasMortal(p);
                        fakeDeath(p);
                    }

                    return true;
                }
            }
        }
        return false;
    }

    private static void hasFull(Player p) {
        DMiscUtil.setHP(p, DMiscUtil.getMaxHP(p));
    }

    private static void hasHalf(Player p) {
        DMiscUtil.setHP(p, (DMiscUtil.getMaxHP(p) / 2));
    }

    private static void hasMortal(Player p) {
        DMiscUtil.setHP(p, 20);
    }

    private static void fakeDeath(Player p) {
        double reduced = 0.1; // TODO
        long before = DMiscUtil.getDevotion(p);
        for (Deity d : DMiscUtil.getDeities(p)) {
            int reduceamt = (int) Math.round(DMiscUtil.getDevotion(p, d) * reduced * DLevels.MULTIPLIER);
            if (reduceamt > DLevels.LOSSLIMIT) reduceamt = DLevels.LOSSLIMIT;
            DMiscUtil.setDevotion(p, d, DMiscUtil.getDevotion(p, d) - reduceamt);
        }
        if (DMiscUtil.getDeities(p).size() < 2)
            p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to " + DMiscUtil.getDeities(p).get(0).getName() + ".");
        else p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to your deities.");
        p.sendMessage(ChatColor.DARK_RED + "Your Devotion has been reduced by " + (before - DMiscUtil.getDevotion(p)) + ".");
    }

    private static void lessFakeDeath(Player p) {
        double reduced = 0.025; // TODO
        long before = DMiscUtil.getDevotion(p);
        for (Deity d : DMiscUtil.getDeities(p)) {
            int reduceamt = (int) Math.round(DMiscUtil.getDevotion(p, d) * reduced * DLevels.MULTIPLIER);
            if (reduceamt > DLevels.LOSSLIMIT) reduceamt = DLevels.LOSSLIMIT;
            DMiscUtil.setDevotion(p, d, DMiscUtil.getDevotion(p, d) - reduceamt);
        }
        if (DMiscUtil.getDeities(p).size() < 2)
            p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to " + DMiscUtil.getDeities(p).get(0).getName() + ".");
        else p.sendMessage(ChatColor.DARK_RED + "You have failed in your service to your deities.");
        p.sendMessage(ChatColor.DARK_RED + "Your Devotion has been reduced by " + (before - DMiscUtil.getDevotion(p)) + ".");
    }
}

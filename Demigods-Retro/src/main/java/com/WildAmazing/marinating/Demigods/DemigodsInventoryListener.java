package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.Gods.Listeners.HephaestusCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class DemigodsInventoryListener implements Listener {
    private Demigods plugin;

    public DemigodsInventoryListener(Demigods instance) {
        plugin = instance;
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        HephaestusCommands.onFurnaceSmelt(e, plugin);
    }
}
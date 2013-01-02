package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DCrafting implements Listener
{	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerCraft(CraftItemEvent e)
    {
		// Define Immortal Soul Fragment
		ItemStack health = new ItemStack(Material.GOLD_NUGGET);
		
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
		
    	InventoryType type = e.getInventory().getType();   
    	Player player = (Player) e.getWhoClicked();
    	
    	if (type.equals(InventoryType.CRAFTING) || type.equals(InventoryType.WORKBENCH))
    	{
            ItemStack[] invItems = e.getInventory().getContents();
            
            for (ItemStack invItem : invItems)
            {         	
				if (invItem.isSimilar(health) || invItem.isSimilar(halfHealth) || invItem.isSimilar(mortalHealth))
				{
					player.sendMessage(ChatColor.DARK_RED + "You cannot craft with souls.");
	                e.setCancelled(true);
	                break;
				}
            }
        }
    }
}
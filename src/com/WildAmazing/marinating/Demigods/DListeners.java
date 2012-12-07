package com.WildAmazing.marinating.Demigods;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DListeners implements Listener
{
	// Player listeners
	@EventHandler (priority = EventPriority.HIGH)
	public static void onPlayerInteract(PlayerInteractEvent e)
	{
		// Shrines
		DShrines.createShrine(e);
		DShrines.playerTribute(e);
		
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public static void onPlayerMove(PlayerMoveEvent e)
	{
		// Shrines
		DShrines.shrineAlerts(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onPlayerJoin(PlayerJoinEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onPlayerPickupItem(PlayerPickupItemEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onPlayerRespawn(PlayerRespawnEvent e)
	{
		
	}
	
	// Chat listeners
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onPlayerChat(AsyncPlayerChatEvent e)
	{
		
	}
	
	// Block listeners
	@EventHandler (priority = EventPriority.HIGH)
	public static void onBlockBreak(BlockBreakEvent e)
	{
		// Shrines
		DShrines.destroyShrine(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockPlace(BlockPlaceEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockDamage(BlockDamageEvent e)
	{
		// Shrines
		DShrines.stopShrineDamage(e);
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockIgnitek(BlockIgniteEvent e)
	{
		// Shrines
		DShrines.stopShrineIgnite(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockBurn(BlockBurnEvent e)
	{
		// Shrines
		DShrines.stopShrineBurn(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockPistonExtend(BlockPistonExtendEvent e)
	{
		// Shrines
		DShrines.stopShrinePistonExtend(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onBlockPistonRetract(BlockPistonRetractEvent e)
	{
		// Shrines
		DShrines.stopShrinePistonRetract(e);
	}
	
	// Entity listeners
	@EventHandler (priority = EventPriority.HIGHEST)
	public static void onEntityDamgageByEntity(EntityDamageByEntityEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onEntityDamage(EntityDamageEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public static void onEntityDeath(EntityDeathEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public static void onEntityTarget(EntityTargetEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public static void onEntityRegainHealth(EntityRegainHealthEvent e)
	{
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onEntityExplode(EntityExplodeEvent e)
	{
		// Shrines
		DShrines.shrineExplode(e);
	}
	
	// Inventory listeners
	@EventHandler (priority = EventPriority.MONITOR)
	public static void onInventoryClose(InventoryCloseEvent e)
	{
		// Shrines
		DShrines.tributeSuccess(e);
		
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public static void onFurnaceSmelt(FurnaceSmeltEvent e)
	{
		
	}
}

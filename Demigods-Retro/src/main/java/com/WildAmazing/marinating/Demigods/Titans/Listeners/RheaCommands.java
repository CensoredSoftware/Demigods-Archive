package com.WildAmazing.marinating.Demigods.Titans.Listeners;

import com.WildAmazing.marinating.Demigods.Demigods;
import com.WildAmazing.marinating.Demigods.Titans.Rhea;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;
import com.WildAmazing.marinating.Demigods.Utilities.TitanPlayerInfo;
import com.WildAmazing.marinating.Demigods.Utilities.WriteLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RheaCommands
{
	public static void onPlayerInteractRhea(PlayerInteractEvent e, Demigods plugin)
	{
		final Player p = e.getPlayer();
		if(!plugin.isTitan(p)) return;
		TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(p);
		if(!tpi.hasDeity(Divine.RHEA)) return;
		if(!tpi.isAlive())
		{
			return;
		}
		if(p.getItemInHand().getType() != Material.AIR)
		{
			Rhea r = (Rhea) tpi.getDeity(Divine.RHEA);
			if(p.getItemInHand().getType() == r.getPlantItem())
			{
				plantTree(p, r, tpi, plugin);
				return;
			}
			else if(p.getItemInHand().getType() == r.getDetonateItem())
			{
				detonatecode(p, r, plugin);
				return;
			}
			if(p.getHealth() < 20)
			{
				if(p.getItemInHand().getType() == Material.LOG && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK))
				{
					ItemStack newi;
					if(p.getItemInHand().getAmount() > 1)
					{
						newi = new ItemStack(p.getItemInHand().getType(), p.getItemInHand().getAmount() - 1);
						newi.setData(p.getItemInHand().getData());
					}
					else newi = null;
					p.setItemInHand(newi);
					if(p.getHealth() < 20) p.setHealth(p.getHealth() + 1);
				}
			}
		}
		if(e.getClickedBlock() == null) return;
		Block b = e.getClickedBlock();
		if(b.getType() == Material.SAPLING)
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				switch(b.getData())
				{
					case 0:
						b.setData((byte) 1);
						break;
					case 1:
						b.setData((byte) 2);
						break;
					case 2:
						b.setData((byte) 0);
						break;
					default:
						b.setData((byte) 0);
				}
			}
			else if(e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				byte Y = b.getData();
				b.setType(Material.AIR);
				switch(Y)
				{
					case 0:
						p.getWorld().generateTree(b.getLocation(), TreeType.TREE);
						break;
					case 1:
						p.getWorld().generateTree(b.getLocation(), TreeType.REDWOOD);
						break;
					case 2:
						p.getWorld().generateTree(b.getLocation(), TreeType.BIRCH);
						break;
					default:
						p.getWorld().generateTree(b.getLocation(), TreeType.TREE);
				}
				e.setCancelled(true);
			}
		}
		else if(b.getType() == Material.CROPS)
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) b.setData((byte) 0x7);
		}
		else if(b.getType() == Material.GRASS)
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getItemInHand().getType() == Material.AIR) grow(b.getRelative(BlockFace.UP), 3);
		}
		else if(b.getType() == Material.DIRT)
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getItemInHand().getType() == Material.AIR) b.setType(Material.GRASS);
		}
	}

	public static void onPlayerCommandPreprocessRhea(PlayerCommandPreprocessEvent event, Demigods plugin)
	{
		if(event.isCancelled()) return;
		final Player player = event.getPlayer();
		if(plugin.getInfo(player) == null) return; // ignore if player isn't involved in demigods
		if(plugin.isTitan(player))
		{
			TitanPlayerInfo tpi = (TitanPlayerInfo) plugin.getInfo(player);
			if(tpi.hasDeity(Divine.RHEA))
			{
				if(!tpi.isAlive())
				{
					return;
				}
				final Rhea r = (Rhea) tpi.getDeity(Divine.RHEA);
				String[] sects = event.getMessage().split(" +", 2);
				String[] args = (sects.length > 1 ? sects[1].split(" +") : new String[0]);
				int bind = 0;
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("bind")) bind = 1;
					else if(args[0].equalsIgnoreCase("unbind")) bind = -1;
				}
				Commands cmd;
				try
				{
					cmd = Commands.valueOf(sects[0].substring(1).toUpperCase());
				}
				catch(Exception ex)
				{
					return;
				}
				try
				{
					switch(cmd)
					{
						case TERRAFORM:
							if(tpi.getPower() < 1000)
							{
								player.sendMessage("This ability requires 1000 Power.");
								break;
							}
							if(player.getTargetBlock(null, 200) != null)
							{
								Block b = player.getTargetBlock(null, 200);
								if(b.getType() != Material.AIR && b.getType() != Material.BEDROCK)
								{
									terraform(b.getChunk(), plugin, player);
								}
								else
								{
									player.sendMessage("Invalid target.");
								}
							}
							break;
						case PLANT:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(player.getItemInHand().getType() == r.getDetonateItem())
									{
										player.sendMessage(player.getItemInHand().getType().name() + " is already bound to Detonate.");
										break;
									}
									if(r.getPlantItem() != player.getItemInHand().getType())
									{
										r.setPlantItem(player.getItemInHand().getType());
										player.sendMessage("Plant " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Plant " + ChatColor.YELLOW + "is already bound to " + r.getPlantItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Plant " + ChatColor.YELLOW + "is no longer bound to " + r.getPlantItem().name() + ".");
									r.setPlantItem(Material.AIR);
								}
								break;
							}
							try
							{
								plantTree(player, r, tpi, plugin);
							}
							catch(Exception e)
							{}
							break;
						case DETONATE:
							if(bind != 0)
							{
								if(bind == 1)
								{
									if(player.getItemInHand().getType() == Material.AIR)
									{
										player.sendMessage("You are not holding an item.");
										break;
									}
									if(player.getItemInHand().getType() == r.getPlantItem())
									{
										player.sendMessage(player.getItemInHand().getType().name() + " is already bound to Plant.");
										break;
									}
									if(r.getDetonateItem() != player.getItemInHand().getType())
									{
										r.setDetonateItem(player.getItemInHand().getType());
										player.sendMessage("Detonate " + ChatColor.YELLOW + "has been bound to " + player.getItemInHand().getType().name() + ".");
									}
									else
									{
										player.sendMessage("Detonate " + ChatColor.YELLOW + "is already bound to " + r.getDetonateItem().name() + ".");
									}
								}
								else if(bind == -1)
								{
									player.sendMessage("Detonate " + ChatColor.YELLOW + "is no longer bound to " + r.getDetonateItem().name() + ".");
									r.setDetonateItem(Material.AIR);
								}
								break;
							}
							detonatecode(player, r, plugin);
							break;
					}
				}
				catch(NoSuchMethodError ex)
				{
					player.sendMessage("The plugin for " + sects[0].toLowerCase() + " is broken or out of date.");
				}
				catch(Exception ex)
				{
					player.sendMessage("�cError: " + ex.getMessage());
					ex.printStackTrace();
				}
				event.setCancelled(true);
			}
		}
	}

	private enum Commands
	{
		TERRAFORM, PLANT, DETONATE
	}

	private static void terraform(Chunk c, Demigods plugin, Player player)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				if(!plugin.isUnprotected(c, player.getWorld()))
				{
					player.sendMessage("That's a protected area.");
					return;
				}
			}
		}
		player.getWorld().regenerateChunk(c.getX(), c.getZ());
		((TitanPlayerInfo) plugin.getInfo(player)).setPower(((TitanPlayerInfo) plugin.getInfo(player)).getPower() - 1000);
		player.sendMessage("This region has been restored to its natural state.");
		/*
		 * final Chunk ch = c;
		 * for (int i=0;i<=120;i+=40){
		 * plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		 * public void run(){
		 * layer(Material.STONE,ch);
		 * }
		 * },i);
		 * }
		 * for (int i=140;i<=260;i+=40){
		 * plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		 * public void run(){
		 * layer(Material.DIRT,ch);
		 * }
		 * },i);
		 * }
		 * plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		 * public void run(){
		 * layer(Material.GRASS,ch);
		 * }
		 * },370);
		 * plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		 * public void run(){
		 * ChunkSnapshot cs = ch.getChunkSnapshot();
		 * for (int x=0; x<16; x++){
		 * for (int z=0; z<16; z++){
		 * Location here = ch.getBlock(x, cs.getHighestBlockYAt(x,z), z).getLocation();
		 * if ((int)(Math.random()*10)==2)
		 * ch.getWorld().generateTree(here, TreeType.TREE);
		 * }
		 * }
		 * }
		 * },400);
		 */
	}

	/*
	 * private static void layer(Material m, Chunk c){
	 * ChunkSnapshot cs = c.getChunkSnapshot();
	 * for (int x=0; x<16; x++){
	 * for (int z=0; z<16; z++){
	 * try {
	 * c.getBlock(x, cs.getHighestBlockYAt(x,z), z).setType(m);
	 * } catch (Exception sorryfoxcantletyoudothat){}
	 * }
	 * }
	 * }
	 */
	private static void plantTree(Player player, Rhea r, TitanPlayerInfo tpi, Demigods plugin)
	{
		if(tpi.getPower() < 100)
		{
			player.sendMessage("You need 100 Power to do that.");
			return;
		}
		Block b = player.getTargetBlock(null, 200);
		if(b != null && plugin.isUnprotected(b.getLocation()))
		{
			if(player.getWorld().generateTree(b.getRelative(BlockFace.UP).getLocation(), TreeType.TREE))
			{
				player.sendMessage("Use /detonate to create an explosion at this tree.");
				r.addTree(plugin.toWriteLocation(b.getRelative(BlockFace.UP).getLocation()));
				tpi.setPower(tpi.getPower() - 100);
			}
			else player.sendMessage("A tree cannot be placed there.");
		}
		else player.sendMessage("That is a protected zone or an invalid location.");
	}

	private static void detonatecode(Player player, Rhea r, Demigods plugin)
	{
		if(r.getTree() != null && r.getTree().size() > 0)
		{
			for(WriteLocation w : r.getTree())
			{
				Location l = plugin.toLocation(w);
				if(l.getBlock().getType() == Material.LOG) l.getWorld().createExplosion(l, 4);
			}
			player.sendMessage("Successfully detonated " + r.getTree().size() + " tree(s).");
			r.resetTree();
		}
		else player.sendMessage("You have not placed an exploding tree.");
	}

	private static void grow(Block b, int run)
	{
		if((b.getType() == Material.AIR && b.getRelative(BlockFace.DOWN).getType() == Material.GRASS) && run > 0)
		{
			switch((int) (Math.random() * 50))
			{
				case 0:
				case 1:
				case 2:
					b.setType(Material.RED_ROSE);
					break;
				case 3:
				case 6:
				case 10:
					b.setType(Material.YELLOW_FLOWER);
					break;
				case 8:
					b.setType(Material.PUMPKIN);
					break;
				case 12:
				case 13:
				case 14:
					b.setType(Material.LONG_GRASS);
					break;
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
					b.setType(Material.LONG_GRASS);
					b.setData((byte) (0x1));
					break;
			}
			grow(b.getRelative(BlockFace.NORTH), run - 1);
			grow(b.getRelative(BlockFace.EAST), run - 1);
			grow(b.getRelative(BlockFace.WEST), run - 1);
			grow(b.getRelative(BlockFace.SOUTH), run - 1);
			grow(b.getRelative(BlockFace.NORTH_EAST), run - 1);
			grow(b.getRelative(BlockFace.NORTH_WEST), run - 1);
			grow(b.getRelative(BlockFace.SOUTH_EAST), run - 1);
			grow(b.getRelative(BlockFace.SOUTH_WEST), run - 1);
		}
	}
}

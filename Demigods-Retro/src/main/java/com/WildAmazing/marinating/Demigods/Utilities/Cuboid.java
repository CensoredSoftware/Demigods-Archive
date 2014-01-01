package com.WildAmazing.marinating.Demigods.Utilities;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.ArrayList;

public class Cuboid implements Serializable
{
	private static final long serialVersionUID = 2223442354741118452L;
	WriteLocation CORNER1, CORNER2;
	String WORLD;
	boolean SHRINE;

	public Cuboid(String world, WriteLocation corner1, WriteLocation corner2)
	{
		CORNER1 = corner1;
		CORNER2 = corner2;
		WORLD = world;
		SHRINE = false;
	}

	public Cuboid(String world, WriteLocation corner1, WriteLocation corner2, boolean shrine)
	{
		CORNER1 = corner1;
		CORNER2 = corner2;
		WORLD = world;
		SHRINE = shrine;
	}

	public boolean isShrine()
	{
		return SHRINE;
	}

	public WriteLocation getCorner1()
	{
		return CORNER1;
	}

	public WriteLocation getCorner2()
	{
		return CORNER2;
	}

	public World getWorld(Server s)
	{
		return s.getWorld(WORLD);
	}

	public int getVolume()
	{
		int x = Math.abs(CORNER1.getX() - CORNER2.getX()) + 1;
		int y = Math.abs(CORNER1.getY() - CORNER2.getY()) + 1;
		int z = Math.abs(CORNER1.getZ() - CORNER2.getZ()) + 1;
		return(x * y * z);
	}

	public boolean isInCuboid(Location l)
	{
		if(!l.getWorld().getName().equals(WORLD)) return false;
		if((l.getBlockX() <= CORNER1.getX() && l.getBlockX() >= CORNER2.getX()) || (l.getBlockX() >= CORNER1.getX() && l.getBlockX() <= CORNER2.getX())) if((l.getBlockY() <= CORNER1.getY() && l.getBlockY() >= CORNER2.getY()) || (l.getBlockY() >= CORNER1.getY() && l.getBlockY() <= CORNER2.getY())) if((l.getBlockZ() <= CORNER1.getZ() && l.getBlockZ() >= CORNER2.getZ()) || (l.getBlockZ() >= CORNER1.getZ() && l.getBlockZ() <= CORNER2.getZ())) return true;
		return false;
	}

	public boolean isInCuboid(Chunk c, World world)
	{
		if(!world.getName().equals(WORLD)) return false;
		if(c.getX() * 16 <= CORNER1.getX() && (((c.getX() + 1) * 16) - 1) >= CORNER2.getX() || c.getX() * 16 >= CORNER1.getX() && (((c.getX() + 1) * 16) - 1) <= CORNER2.getX()) return true;
		if((((c.getZ() + 1) * 16) - 1) <= CORNER1.getZ() && c.getZ() * 16 >= CORNER2.getZ() || (((c.getZ() + 1) * 16) - 1) >= CORNER1.getZ() && c.getZ() * 16 <= CORNER2.getZ()) return true;
		return false;
	}

	public ArrayList<Block> allBlocks(World w)
	{
		ArrayList<Block> ab = new ArrayList<Block>();
		int xStart = 0;
		int xEnd = 0;
		int yStart = 0;
		int yEnd = 0;
		int zStart = 0;
		int zEnd = 0;
		int X1 = CORNER1.getX();
		int X2 = CORNER2.getX();
		int Y1 = CORNER1.getY();
		int Y2 = CORNER2.getY();
		int Z1 = CORNER1.getZ();
		int Z2 = CORNER2.getZ();
		if(X1 < X2)
		{
			xStart = X1;
			xEnd = X2;
		}
		if(X1 > X2)
		{
			xStart = X2;
			xEnd = X1;
		}
		if(X1 == X2)
		{
			xStart = X1;
			xEnd = X1;
		}

		if(Z1 < Z2)
		{
			zStart = Z1;
			zEnd = Z2;
		}
		if(Z1 > Z2)
		{
			zStart = Z2;
			zEnd = Z1;
		}
		if(Z1 == Z2)
		{
			zStart = Z1;
			zEnd = Z1;
		}

		if(Y1 < Y2)
		{
			yStart = Y1;
			yEnd = Y2;
		}
		if(Y1 > Y2)
		{
			yStart = Y2;
			yEnd = Y1;
		}
		if(Y1 == Y2)
		{
			yStart = Y1;
			yEnd = Y1;
		}
		for(int i = xStart; i <= xEnd; i++)
		{
			for(int j = yStart; j <= yEnd; j++)
			{
				for(int k = zStart; k <= zEnd; k++)
					ab.add(w.getBlockAt(i, j, k));
			}
		}
		return ab;
	}
}

package com.censoredsoftware.demigods.hype;

import java.util.*;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

public class AltarDemo
{
	public final static Schematic ALTAR = new Schematic()
	{
		{
			// Create roof
			add(new Selection().include(2, 3, 2).setBlockData(Selection.BuildingBlock.stoneBrickSlabTop));
			add(new Selection().include(-2, 3, -2).setBlockData(Selection.BuildingBlock.stoneBrickSlabTop));
			add(new Selection().include(2, 3, -2).setBlockData(Selection.BuildingBlock.stoneBrickSlabTop));
			add(new Selection().include(-2, 3, 2).setBlockData(Selection.BuildingBlock.stoneBrickSlabTop));
			add(new Selection().include(2, 4, 2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-2, 4, -2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(2, 4, -2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-2, 4, 2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(2, 5, 2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-2, 5, -2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(2, 5, -2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-2, 5, 2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(0, 6, 0).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-1, 5, -1, 1, 5, 1).setBlockData(Selection.BuildingBlock.spruceWood));

			// Create outer steps
			add(new Selection().include(3, 0, 3).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(-3, 0, -3).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(3, 0, -3).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(-3, 0, 3).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(4, 0, -2, 4, 0, 2).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(-4, 0, -2, -4, 0, 2).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(-2, 0, -4, 2, 0, -4).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));
			add(new Selection().include(-2, 0, 4, 2, 0, 4).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));

			// Create inner steps
			add(new Selection().include(3, 0, -1, 3, 0, 1).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-1, 0, 3, 1, 0, 3).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-3, 0, -1, -3, 0, 1).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-1, 0, -3, 1, 0, -3).setBlockData(Selection.BuildingBlock.stoneBrick));

			// Create pillars
			add(new Selection().include(3, 4, 2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(3, 4, -2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(2, 4, 3).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-2, 4, 3).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-3, 4, 2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-3, 4, -2).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(2, 4, -3).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(-2, 4, -3).setBlockData(Selection.BuildingBlock.spruceSlab));
			add(new Selection().include(3, 0, 2, 3, 3, 2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(3, 0, -2, 3, 3, -2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(2, 0, 3, 2, 3, 3).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-2, 0, 3, -2, 3, 3).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-3, 0, 2, -3, 3, 2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-3, 0, -2, -3, 3, -2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(2, 0, -3, 2, 3, -3).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-2, 0, -3, -2, 3, -3).setBlockData(Selection.BuildingBlock.stoneBrick));

			// Left beam
			add(new Selection().include(1, 4, -2, -1, 4, -2).exclude(0, 4, -2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(0, 4, -2).setBlockData(Selection.BuildingBlock.stoneBrickSpecial));
			add(new Selection().include(-1, 5, -2, 1, 5, -2).setBlockData(Selection.BuildingBlock.spruceSlab));

			// Right beam
			add(new Selection().include(1, 4, 2, -1, 4, 2).exclude(0, 4, 2).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(0, 4, 2).setBlockData(Selection.BuildingBlock.stoneBrickSpecial));
			add(new Selection().include(-1, 5, 2, 1, 5, 2).setBlockData(Selection.BuildingBlock.spruceSlab));

			// Top beam
			add(new Selection().include(2, 4, 1, 2, 4, -1).exclude(2, 4, 0).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(2, 4, 0).setBlockData(Selection.BuildingBlock.stoneBrickSpecial));
			add(new Selection().include(2, 5, -1, 2, 5, 1).setBlockData(Selection.BuildingBlock.spruceSlab));

			// Bottom beam
			add(new Selection().include(-2, 4, 1, -2, 4, -1).exclude(-2, 4, 0).setBlockData(Selection.BuildingBlock.stoneBrick));
			add(new Selection().include(-2, 4, 0).setBlockData(Selection.BuildingBlock.stoneBrickSpecial));
			add(new Selection().include(-2, 5, -1, -2, 5, 1).setBlockData(Selection.BuildingBlock.spruceSlab));

			// Create main platform
			add(new Selection().include(-2, 1, -2, 2, 1, 2).exclude(0, 1, 0).setBlockData(Selection.BuildingBlock.stoneBrickSlabBottom));

			// Create the enchantment table
			add(new Selection().include(0, 2, 0).setBlockData(Selection.BuildingBlock.enchantTable));

			// Create magical table stand
			add(new Selection().include(0, 1, 0).setBlockData(Selection.BuildingBlock.stoneBrick));
		}
	};

	public static class Schematic extends ArrayList<Selection>
	{
		public boolean generate(final Location reference, boolean check)
		{
			if(check && !canGenerateStrict(reference, 3)) return false;
			for(Selection cuboid : this)
				cuboid.generate(reference);
			for(Item drop : reference.getWorld().getEntitiesByClass(Item.class))
				if(reference.distance(drop.getLocation()) <= (9)) drop.remove();
			return true;
		}

		public Set<Location> getLocations(final Location reference)
		{
			Set toReturn = Sets.newHashSet();
			for(Selection cuboid : this)
			{
				toReturn.addAll(cuboid.getBlockLocations(reference));
			}
			return toReturn;
		}

		public static boolean canGenerateStrict(Location reference, int area)
		{
			Location location = reference.clone();
			location.subtract(0, 1, 0);
			location.add((area / 3), 0, (area / 2));

			// Check ground
			for(int i = 0; i < area; i++)
			{
				if(!location.getBlock().getType().isSolid()) return false;
				location.subtract(1, 0, 0);
			}

			// Check ground adjacent
			for(int i = 0; i < area; i++)
			{
				if(!location.getBlock().getType().isSolid()) return false;
				location.subtract(0, 0, 1);
			}

			// Check ground adjacent again
			for(int i = 0; i < area; i++)
			{
				if(!location.getBlock().getType().isSolid()) return false;
				location.add(1, 0, 0);
			}

			location.add(0, 1, 0);

			// Check air diagonally
			for(int i = 0; i < area + 1; i++)
			{
				if(!location.getBlock().getType().isTransparent()) return false;
				location.add(0, 1, 1);
				location.subtract(1, 0, 0);
			}

			return true;
		}
	}

	public static class Selection
	{
		private Set<RelativeBlockLocation> include;
		private Set<RelativeBlockLocation> exclude;
		private List<BlockData> blockData;

		public Selection()
		{
			include = Sets.newHashSet();
			exclude = Sets.newHashSet();
			blockData = Lists.newArrayList();
		}

		/**
		 * Set Selection (non-cuboid), useful for getting 1 location back.
		 * 
		 * @param X The relative X coordinate of the schematic from the reference location.
		 * @param Y The relative Y coordinate of the schematic from the reference location.
		 * @param Z The relative Z coordinate of the schematic from the reference location.
		 */
		public Selection include(int X, int Y, int Z)
		{
			include.add(new RelativeBlockLocation(X, Y, Z));
			return this;
		}

		/**
		 * Constructor for a Selection (cuboid), useful for getting only locations back.
		 * 
		 * @param X The relative X coordinate of the schematic from the reference location.
		 * @param Y The relative Y coordinate of the schematic from the reference location.
		 * @param Z The relative Z coordinate of the schematic from the reference location.
		 * @param XX The second relative X coordinate of the schematic from the reference location, creating a cuboid.
		 * @param YY The second relative Y coordinate of the schematic from the reference location, creating a cuboid.
		 * @param ZZ The second relative Z coordinate of the schematic from the reference location, creating a cuboid.
		 */
		public Selection include(int X, int Y, int Z, int XX, int YY, int ZZ)
		{
			include.addAll(rangeLoop(X, XX, Y, YY, Z, ZZ));
			return this;
		}

		/**
		 * Excluding for a Selection (non-cuboid).
		 * 
		 * @param X The relative X coordinate of the schematic from the reference location.
		 * @param Y The relative Y coordinate of the schematic from the reference location.
		 * @param Z The relative Z coordinate of the schematic from the reference location.
		 * @return This schematic.
		 */
		public Selection exclude(int X, int Y, int Z)
		{
			exclude.add(new RelativeBlockLocation(X, Y, Z));
			return this;
		}

		/**
		 * Set Blockdata for a Selection (cuboid).
		 * 
		 * @param data The data being set.
		 * @return This schematic.
		 */
		public Selection setBlockData(List<BlockData> data)
		{
			this.blockData = data;
			return this;
		}

		/**
		 * Get the material of the object (a random material is chosen based on the configured odds).
		 * 
		 * @return The block data.
		 */
		public BlockData getBlockData()
		{
			if(blockData.isEmpty()) return new BlockData(Material.AIR);
			final int roll = generateIntRange(1, 100);
			Collection<BlockData> check = Collections2.filter(blockData, new Predicate<BlockData>()
			{
				@Override
				public boolean apply(@Nullable BlockData blockData)
				{
					return blockData.getOdds() >= roll;
				}
			});
			if(check.isEmpty()) return getBlockData();
			return Lists.newArrayList(check).get(generateIntRange(0, check.size() - 1));
		}

		public static int generateIntRange(int min, int max)
		{
			return new Random().nextInt(max - min + 1) + min;
		}

		/**
		 * Get the block locations in this object.
		 * 
		 * @param reference The reference location.
		 * @return A set of locations.
		 */
		public Set<Location> getBlockLocations(final Location reference)
		{
			return new HashSet<Location>()
			{
				{
					for(RelativeBlockLocation rbl : Sets.difference(include, exclude))
						add(rbl.getFrom(reference));
				}
			};
		}

		/**
		 * Generate this schematic.
		 * 
		 * @param reference The reference Location.
		 */
		public void generate(Location reference)
		{
			for(Location location : getBlockLocations(reference))
			{
				BlockData data = getBlockData();
				location.getBlock().setTypeIdAndData(data.getMaterial().getId(), data.getData(), false);
			}
		}

		/**
		 * Get a cuboid selection as a HashSet.
		 * 
		 * @param X The relative X coordinate.
		 * @param XX The second relative X coordinate.
		 * @param Y The relative Y coordinate.
		 * @param YY The second relative Y coordinate.
		 * @param Z The relative Z coordinate.
		 * @param ZZ The second relative Z coordinate.
		 * @return The HashSet collection of a cuboid selection.
		 */
		public Set<RelativeBlockLocation> rangeLoop(final int X, final int XX, final int Y, final int YY, final int Z, final int ZZ)
		{
			return new HashSet<RelativeBlockLocation>()
			{
				{
					for(int x : Ranges.closed(X < XX ? X : XX, X < XX ? XX : X).asSet(DiscreteDomains.integers()))
						for(int y : Ranges.closed(Y < YY ? Y : YY, Y < YY ? YY : Y).asSet(DiscreteDomains.integers()))
							for(int z : Ranges.closed(Z < ZZ ? Z : ZZ, Z < ZZ ? ZZ : Z).asSet(DiscreteDomains.integers()))
								add(new RelativeBlockLocation(x, y, z));
				}
			};
		}

		public static class RelativeBlockLocation
		{
			int X;
			int Y;
			int Z;

			RelativeBlockLocation(int X, int Y, int Z)
			{
				this.X = X;
				this.Y = Y;
				this.Z = Z;
			}

			Location getFrom(Location location)
			{
				return location.clone().add(X, Y, Z);
			}
		}

		public static class BlockData
		{
			private Material material;
			private byte data;
			private int odds;
			private boolean physics;

			/**
			 * Constructor for BlockData with only Material given.
			 * 
			 * @param material Material of the block.
			 */
			public BlockData(Material material)
			{
				this.material = material;
				this.data = 0;
				this.odds = 100;
				this.physics = false;
			}

			/**
			 * Constructor for BlockData with only Material given and odds given.
			 * 
			 * @param material Material of the block.
			 * @param odds The odds of this object being generated.
			 */
			public BlockData(Material material, int odds)
			{
				this.material = material;
				this.data = 100;
				this.odds = odds;
				this.physics = false;
			}

			/**
			 * Constructor for BlockData with only Material and byte data given.
			 * 
			 * @param material Material of the block.
			 * @param data Byte data of the block.
			 */
			public BlockData(Material material, byte data)
			{
				this.material = material;
				this.data = data;
				this.odds = 100;
				this.physics = false;
			}

			/**
			 * Constructor for BlockData with Material, byte data, and odds given.
			 * 
			 * @param material Material of the block.
			 * @param data Byte data of the block.
			 * @param odds The odds of this object being generated.
			 */
			public BlockData(Material material, byte data, int odds)
			{
				this.material = material;
				this.data = data;
				this.odds = odds;
				this.physics = false;
			}

			/**
			 * Get the Material of this object.
			 * 
			 * @return A Material.
			 */
			public Material getMaterial()
			{
				return this.material;
			}

			/**
			 * Get the byte data of this object.
			 * 
			 * @return Byte data.
			 */
			public byte getData()
			{
				return this.data;
			}

			/**
			 * Get the odds of this object generating.
			 * 
			 * @return Odds (as an integer, out of 5).
			 */
			public int getOdds()
			{
				return this.odds;
			}

			/**
			 * Get the physics boolean.
			 * 
			 * @return If physics should apply on generation.
			 */
			public boolean getPhysics()
			{
				return this.physics;
			}
		}

		public static class BuildingBlock // TODO: Rename these to make more sense. // Shouldn't this be in the episode data? - HQM
		{
			public final static List<BlockData> enchantTable = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.ENCHANTMENT_TABLE));
				}
			};
			public final static List<BlockData> stoneBrick = new ArrayList<BlockData>(3)
			{
				{
					add(new BlockData(Material.SMOOTH_BRICK, 80));
					add(new BlockData(Material.SMOOTH_BRICK, (byte) 1, 10));
					add(new BlockData(Material.SMOOTH_BRICK, (byte) 2, 10));
				}
			};
			public final static List<BlockData> stoneBrickSlabBottom = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.getMaterial(44), (byte) 5));
				}
			};
			public final static List<BlockData> stoneBrickSlabTop = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.getMaterial(44), (byte) 13));
				}
			};
			public final static List<BlockData> stoneBrickSpecial = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.getMaterial(98), (byte) 3));
				}
			};
			public final static List<BlockData> spruceWood = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.getMaterial(5), (byte) 1));
				}
			};
			public final static List<BlockData> spruceSlab = new ArrayList<BlockData>(1)
			{
				{
					add(new BlockData(Material.getMaterial(126), (byte) 1));
				}
			};
		}
	}
}

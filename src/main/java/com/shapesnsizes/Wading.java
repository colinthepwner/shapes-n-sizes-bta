package com.shapesnsizes;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Wading {

	private static final float KNEE = 0.3f;

	private static final double MOVING = 0.01;

	private static final int MAX_HALF_WIDTH = 6;
	private static final int MAX_DEPTH_PROBE = 24;

	private Wading() {}

	public static final class State {
		private final Map<TilePos, Block<?>> held = new HashMap<>();
		private boolean spent = false;
		private float spentDepth = Float.MAX_VALUE;
	}

	public static void tick(Player player, State state) {
		World world = player.world;
		if (world == null || world.isClientSide) return;

		float depth = depthAtFeet(world, player);
		boolean inWater = depth > 0.0f;

		if (!Behemoth.is(player) || !inWater || !PlayerScale.waterDisplacement(world)) {
			releaseAll(world, state);

			if (!inWater) {
				state.spent = false;
				state.spentDepth = Float.MAX_VALUE;
			}
			return;
		}

		float knee = KNEE * player.bbHeight;

		if (state.spent && depth < state.spentDepth) {
			state.spent = false;
			state.spentDepth = Float.MAX_VALUE;
		}
		if (state.spent) {
			releaseAll(world, state);
			return;
		}

		boolean moving = stepLength(player) > MOVING;
		if (depth > knee || !moving) {

			releaseAll(world, state);
			state.spent = true;
			state.spentDepth = depth;
			return;
		}

		holdAside(world, player, state, knee);
	}

	private static void holdAside(World world, Player player, State state, float knee) {
		int centreX = MathHelper.floor(player.x);
		int centreZ = MathHelper.floor(player.z);
		int minX = Math.max(MathHelper.floor(player.bb.minX), centreX - MAX_HALF_WIDTH);
		int maxX = Math.min(MathHelper.floor(player.bb.maxX), centreX + MAX_HALF_WIDTH);
		int minZ = Math.max(MathHelper.floor(player.bb.minZ), centreZ - MAX_HALF_WIDTH);
		int maxZ = Math.min(MathHelper.floor(player.bb.maxZ), centreZ + MAX_HALF_WIDTH);
		int minY = MathHelper.floor(player.bb.minY);
		int maxY = MathHelper.floor(player.bb.minY + knee);

		Iterator<Map.Entry<TilePos, Block<?>>> it = state.held.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<TilePos, Block<?>> entry = it.next();
			TilePos pos = entry.getKey();
			boolean stillInside = pos.x >= minX && pos.x <= maxX && pos.y >= minY && pos.y <= maxY
				&& pos.z >= minZ && pos.z <= maxZ;
			if (!stillInside) {
				restore(world, pos, entry.getValue());
				it.remove();
			}
		}

		TilePos probe = new TilePos();
		for (int x = minX; x <= maxX; ++x) {
			for (int y = minY; y <= maxY; ++y) {
				for (int z = minZ; z <= maxZ; ++z) {
					probe.set(x, y, z);
					Block<?> block = world.getBlockType(probe);
					if (block == null || !block.hasTag(BlockTags.IS_WATER)) continue;
					TilePos key = new TilePos(x, y, z);
					state.held.put(key, block);
					shapesnsizes$setQuietly(world, key, Blocks.AIR);
				}
			}
		}
	}

	public static void releaseAll(World world, State state) {
		if (state.held.isEmpty()) return;
		for (Map.Entry<TilePos, Block<?>> entry : state.held.entrySet()) {
			restore(world, entry.getKey(), entry.getValue());
		}
		state.held.clear();
	}

	private static void restore(World world, TilePos pos, Block<?> was) {
		Block<?> now = world.getBlockType(pos);
		if (now == null || now == Blocks.AIR) {
			shapesnsizes$setQuietly(world, pos, was);
		}
	}

	private static void shapesnsizes$setQuietly(World world, TilePos pos, Block<?> block) {
		world.setBlockTypeDataRaw(pos, block, 0);
		world.markBlockNeedsUpdate(pos);
	}

	private static double stepLength(Player player) {
		if (player instanceof SizeTicker) return ((SizeTicker) player).shapesnsizes$stepLength();
		return Math.abs(player.x - player.xo) + Math.abs(player.z - player.zo);
	}

	private static float depthAtFeet(World world, Player player) {
		int x = MathHelper.floor(player.x);
		int z = MathHelper.floor(player.z);
		int feet = MathHelper.floor(player.bb.minY);
		TilePos probe = new TilePos();
		int depth = 0;
		for (int i = 0; i < MAX_DEPTH_PROBE; ++i) {
			probe.set(x, feet + i, z);
			Block<?> block = world.getBlockType(probe);
			boolean water = block != null && block.hasTag(BlockTags.IS_WATER);

			if (!water && !isHeldHere(player, probe)) break;
			++depth;
		}
		return depth;
	}

	private static boolean isHeldHere(Player player, TilePos pos) {
		if (!(player instanceof Wader)) return false;
		State state = ((Wader) player).shapesnsizes$wadingState();
		return state != null && state.held.containsKey(pos);
	}

	public interface Wader {
		State shapesnsizes$wadingState();
	}
}

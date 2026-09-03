package com.shapesnsizes;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public final class Foliage {

	public static final float WADE = 2.0f;

	public static final float BREAK_LEAVES = 5.0f;

	public static final float BREAK_LOGS = 9.0f;

	private static final double BASE_KEEP = 0.35;

	private static final int MAX_HALF_WIDTH = 8;
	private static final int MAX_HEIGHT = 32;

	private Foliage() {}

	public static boolean wadesThroughLeaves(Player player) {
		return PlayerScale.isSmall(player) || PlayerScale.get(player) >= WADE;
	}

	public static double leafDrag(Player player) {
		float scale = PlayerScale.get(player);
		if (scale < WADE) return 1.0;
		return 1.0 - (1.0 - BASE_KEEP) / (scale / WADE);
	}

	public static void tick(Player player) {
		World world = player.world;
		if (world == null) return;
		float scale = PlayerScale.get(player);
		if (scale < WADE) return;
		if (Trample.isIntangible(player)) return;
		if (!stirring(player)) return;

		boolean destroy = !world.isClientSide
			&& scale >= BREAK_LEAVES
			&& PlayerScale.sizeGriefing(world)
			&& !Trample.treadsCarefully(player)
			&& !Behemoth.wearingSoftBoots(player);

		if (!sweep(player, world, scale, destroy)) return;

		double keep = leafDrag(player);
		player.xd *= keep;
		player.zd *= keep;

		if (player.yd < 0.0) player.yd *= 1.0 - (1.0 - keep) * 0.5;
	}

	private static boolean stirring(Player player) {
		double dx = player.x - player.xo;
		double dz = player.z - player.zo;
		boolean moved = dx * dx + dz * dz > STIRRING * STIRRING
			|| player.yd < -STIRRING || player.yd > STIRRING;
		if (!(player instanceof Walker)) return moved;
		Walker walker = (Walker) player;
		if (moved) {
			walker.shapesnsizes$setCoasting(COASTING);
			return true;
		}
		int left = walker.shapesnsizes$coasting();
		if (left <= 0) return false;
		walker.shapesnsizes$setCoasting(left - 1);
		return true;
	}

	private static final double STIRRING = 0.01;

	private static final int COASTING = 5;

	public interface Walker {
		int shapesnsizes$coasting();

		void shapesnsizes$setCoasting(int ticks);
	}

	private static boolean sweep(Player player, World world, float scale, boolean destroy) {
		int centreX = MathHelper.floor(player.x);
		int centreZ = MathHelper.floor(player.z);
		int minX = Math.max(MathHelper.floor(player.bb.minX), centreX - MAX_HALF_WIDTH);
		int maxX = Math.min(MathHelper.floor(player.bb.maxX), centreX + MAX_HALF_WIDTH);
		int minZ = Math.max(MathHelper.floor(player.bb.minZ), centreZ - MAX_HALF_WIDTH);
		int maxZ = Math.min(MathHelper.floor(player.bb.maxZ), centreZ + MAX_HALF_WIDTH);
		int minY = MathHelper.floor(player.bb.minY);
		int maxY = Math.min(MathHelper.floor(player.bb.maxY), minY + MAX_HEIGHT);

		boolean logsToo = scale >= BREAK_LOGS;
		boolean foundLeaves = false;
		boolean brokeLeaves = false;
		boolean brokeLogs = false;
		TilePos pos = new TilePos();

		for (int x = minX; x <= maxX; ++x) {
			for (int y = minY; y <= maxY; ++y) {
				for (int z = minZ; z <= maxZ; ++z) {
					pos.set(x, y, z);
					Block<?> block = world.getBlockType(pos);
					if (block == null || block == Blocks.AIR) continue;
					boolean leaf = block.getLogic() instanceof BlockLogicLeavesBase;
					boolean log = block.getLogic() instanceof BlockLogicLog;
					if (!leaf && !log) continue;
					if (leaf) foundLeaves = true;
					if (!destroy) continue;
					if (log && !logsToo) continue;
					shatter(world, pos, block);
					if (leaf) {
						brokeLeaves = true;
					} else {
						brokeLogs = true;
					}
				}
			}
		}

		if (brokeLogs) {
			world.playBlockSoundEffect(null, player.x, player.bb.minY, player.z,
				Blocks.LOG_OAK, EnumBlockSoundEffectType.MINE);
		} else if (brokeLeaves) {
			world.playBlockSoundEffect(null, player.x, player.bb.minY, player.z,
				Blocks.LEAVES_OAK, EnumBlockSoundEffectType.MINE);
		}
		return foundLeaves;
	}

	private static void shatter(World world, TilePos pos, Block<?> block) {
		TileEntity tileEntity = world.getTileEntity(pos);
		block.dropWithCause(world, EnumDropCause.EXPLOSION, pos, world.getBlockData(pos), tileEntity, null);
		world.setBlockTypeNotify(pos, Blocks.AIR);
	}
}

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

	private static final int MAX_HEIGHT = 16;

	private static final int SWEEP_INTERVAL = 3;

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

		State state = player instanceof Walker ? ((Walker) player).shapesnsizes$foliageState() : null;
		if (state != null && --state.countdown > 0) return;
		if (state != null) state.countdown = SWEEP_INTERVAL;

		double backX = 0.0;
		double backZ = 0.0;
		if (state != null && state.swept) {
			backX = player.x - state.sweptX;
			backZ = player.z - state.sweptZ;
		}
		if (state != null) {
			state.sweptX = player.x;
			state.sweptZ = player.z;
			state.swept = true;
		}

		if (!sweep(player, world, scale, destroy, backX, backZ)) return;

		double keep = leafDrag(player);
		player.xd *= keep;
		player.zd *= keep;

		if (player.yd < 0.0) player.yd *= 1.0 - (1.0 - keep) * 0.5;
	}

	private static boolean stirring(Player player) {
		double step = player instanceof SizeTicker
			? ((SizeTicker) player).shapesnsizes$stepLength()
			: Math.hypot(player.x - player.xo, player.z - player.zo);
		boolean moved = step > STIRRING || player.yd < -STIRRING || player.yd > STIRRING;
		if (!(player instanceof Walker)) return moved;
		State state = ((Walker) player).shapesnsizes$foliageState();
		if (moved) {
			state.coasting = COASTING;
			return true;
		}
		if (state.coasting <= 0) return false;
		--state.coasting;
		return true;
	}

	private static final double STIRRING = 0.01;

	private static final int COASTING = 5;

	public static final class State {

		int coasting;

		int countdown;

		double sweptX;
		double sweptZ;
		boolean swept;
	}

	public interface Walker {
		State shapesnsizes$foliageState();
	}

	private static boolean sweep(Player player, World world, float scale, boolean destroy,
			double backX, double backZ) {

		double loX = Math.min(player.bb.minX, player.bb.minX - backX);
		double hiX = Math.max(player.bb.maxX, player.bb.maxX - backX);
		double loZ = Math.min(player.bb.minZ, player.bb.minZ - backZ);
		double hiZ = Math.max(player.bb.maxZ, player.bb.maxZ - backZ);

		int centreX = MathHelper.floor(player.x);
		int centreZ = MathHelper.floor(player.z);
		int minX = Math.max(MathHelper.floor(loX), centreX - MAX_HALF_WIDTH);
		int maxX = Math.min(MathHelper.floor(hiX), centreX + MAX_HALF_WIDTH);
		int minZ = Math.max(MathHelper.floor(loZ), centreZ - MAX_HALF_WIDTH);
		int maxZ = Math.min(MathHelper.floor(hiZ), centreZ + MAX_HALF_WIDTH);
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

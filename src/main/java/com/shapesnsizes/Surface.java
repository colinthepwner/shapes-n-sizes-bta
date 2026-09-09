package com.shapesnsizes;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.World;

public final class Surface {

	private static final String SOUND = "mob.slime";

	private static final float STRIDE = 1.67f;

	private static final float VOLUME = 0.2f;

	private static final float MAX_PITCH = 2.0f;

	private static final double MOVING = 0.01;

	private static final int FOOTFALL_DROPS = 3;

	private Surface() {}

	public static float strideLength(Player player) {
		return STRIDE * PlayerScale.get(player);
	}

	public static void step(Player player) {
		World world = player.world;
		if (world == null || world.isClientSide) return;

		float scale = Math.max(PlayerScale.MIN, PlayerScale.get(player));
		float pitch = Math.min(MAX_PITCH, (float) (1.0 / Math.sqrt(scale)));
		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS,
			player.x, player.bb.minY, player.z, SOUND, VOLUME, pitch);

		splash(player, FOOTFALL_DROPS, 0.0, 0.0);
	}

	public static void wake(Player player, double stepX, double stepZ) {
		World world = player.world;
		if (world == null || world.isClientSide) return;
		double travelled = Math.sqrt(stepX * stepX + stepZ * stepZ);
		if (travelled < MOVING) return;

		double width = player.bb.maxX - player.bb.minX;
		double behind = width + 0.05;
		splash(player, 1, -stepX / travelled * behind, -stepZ / travelled * behind);
	}

	private static void splash(Player player, int drops, double offX, double offZ) {
		World world = player.world;
		double width = player.bb.maxX - player.bb.minX;
		for (int i = 0; i < drops; ++i) {
			double x = player.x + offX + (world.rand.nextDouble() - 0.5) * width;
			double z = player.z + offZ + (world.rand.nextDouble() - 0.5) * width;

			world.spawnParticle("splash", x, player.bb.minY, z,
				0.0, 0.0, 0.0, world.dimension.id, true);
		}
	}
}

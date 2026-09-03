package com.shapesnsizes;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public final class PortalSizes {

	public static final int MIN_WIDTH = 2;
	public static final int MIN_HEIGHT = 3;

	public static final int MAX_SIDE = 21;

	private PortalSizes() {}

	public static final class Shape {
		public final int width;
		public final int height;

		public Shape(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public boolean isBiggerThanDefault() {
			return this.width > MIN_WIDTH || this.height > MIN_HEIGHT;
		}
	}

	public static Shape measure(World world, TilePosc pos) {
		Block<?> portal = world.getBlockType(pos);
		if (portal == null || !(portal.getLogic() instanceof BlockLogicPortal)) return null;

		int x = pos.x(), y = pos.y(), z = pos.z();
		int spanX = run(world, portal, x, y, z, 1, 0) + run(world, portal, x, y, z, -1, 0) + 1;
		int spanZ = run(world, portal, x, y, z, 0, 1) + run(world, portal, x, y, z, 0, -1) + 1;
		int spanY = runY(world, portal, x, y, z, 1) + runY(world, portal, x, y, z, -1) + 1;

		int width = Math.max(spanX, spanZ);
		return new Shape(clampSide(width), clampSide(spanY));
	}

	private static int run(World world, Block<?> portal, int x, int y, int z, int stepX, int stepZ) {
		int found = 0;
		for (int i = 1; i <= MAX_SIDE; ++i) {
			if (world.getBlockType(new TilePos(x + stepX * i, y, z + stepZ * i)) != portal) break;
			++found;
		}
		return found;
	}

	private static int runY(World world, Block<?> portal, int x, int y, int z, int step) {
		int found = 0;
		for (int i = 1; i <= MAX_SIDE; ++i) {
			if (world.getBlockType(new TilePos(x, y + step * i, z)) != portal) break;
			++found;
		}
		return found;
	}

	private static int clampSide(int side) {
		return Math.max(1, Math.min(MAX_SIDE, side));
	}

	public static void enlargeFrame(World world, TilePosc corner, Block<?> frameBlock, Shape shape) {
		int x = corner.x(), y = corner.y(), z = corner.z();
		int stepX;
		int stepZ;
		if (world.getBlockType(new TilePos(x - 1, y, z)) == frameBlock
			&& world.getBlockType(new TilePos(x + MIN_WIDTH, y, z)) == frameBlock) {
			stepX = 1;
			stepZ = 0;
		} else if (world.getBlockType(new TilePos(x, y, z - 1)) == frameBlock
			&& world.getBlockType(new TilePos(x, y, z + MIN_WIDTH)) == frameBlock) {
			stepX = 0;
			stepZ = 1;
		} else {

			return;
		}

		int width = Math.max(MIN_WIDTH, shape.width);
		int height = Math.max(MIN_HEIGHT, shape.height);
		for (int across = -1; across <= width; ++across) {
			for (int up = -1; up <= height; ++up) {
				boolean isFrame = across == -1 || across == width || up == -1 || up == height;
				world.setBlockWithNotify(x + across * stepX, y + up, z + across * stepZ,
					isFrame ? frameBlock.id() : 0);
			}
		}
	}

	public static Shape remembered(Entity entity) {
		if (!(entity instanceof PortalSized)) return null;
		return ((PortalSized) entity).shapesnsizes$lastPortalShape();
	}

	public static void remember(Entity entity, Shape shape) {
		if (entity instanceof PortalSized) {
			((PortalSized) entity).shapesnsizes$setLastPortalShape(shape);
		}
	}

	public static boolean carries(Entity entity) {
		return entity instanceof Player;
	}
}

package com.shapesnsizes;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public final class Cursor {

	public static final int STATE_ON = 22;
	public static final int STATE_OFF = 23;

	public static final float MIN_SCALE = 2.0f;

	private Cursor() {}

	public static int edge(Player player) {
		if (player == null) return 1;
		float scale = PlayerScale.get(player);
		if (scale < MIN_SCALE) return 1;
		return (int) scale;
	}

	public static boolean allowed(World world) {
		if (world == null) return false;
		Boolean on = world.getGameRuleValue(ScalingRules.SCALED_CURSOR);
		return on != null && on;
	}

	public static boolean enabled(Player player) {
		if (player == null || player.world == null) return false;
		return allowed(player.world) && PlayerScale.isCursorOn(player) && edge(player) > 1;
	}

	public static int blocks(Player player) {
		if (!enabled(player)) return 1;
		int e = edge(player);
		return e * e * e;
	}

	public enum Depth {

		INTO,

		OUT_OF
	}

	public static TilePos[] cube(Player player, int x, int y, int z, Side side, Depth depth) {
		if (!enabled(player) || side == null) return null;
		int n = edge(player);

		int ux;
		int uy;
		int uz;
		int vx;
		int vy;
		int vz;
		if (side.offsetY() != 0) {
			ux = 1; uy = 0; uz = 0;
			vx = 0; vy = 0; vz = 1;
		} else if (side.offsetX() != 0) {
			ux = 0; uy = 1; uz = 0;
			vx = 0; vy = 0; vz = 1;
		} else {
			ux = 1; uy = 0; uz = 0;
			vx = 0; vy = 1; vz = 0;
		}

		int sign = depth == Depth.INTO ? -1 : 1;
		int wx = side.offsetX() * sign;
		int wy = side.offsetY() * sign;
		int wz = side.offsetZ() * sign;

		int lo = -((n - 1) / 2);
		TilePos[] out = new TilePos[n * n * n];
		int i = 1;
		for (int a = lo; a < lo + n; ++a) {
			for (int b = lo; b < lo + n; ++b) {

				for (int d = 0; d < n; ++d) {
					int px = x + ux * a + vx * b + wx * d;
					int py = y + uy * a + vy * b + wy * d;
					int pz = z + uz * a + vz * b + wz * d;
					if (a == 0 && b == 0 && d == 0) {
						out[0] = new TilePos(px, py, pz);
					} else {
						out[i++] = new TilePos(px, py, pz);
					}
				}
			}
		}
		return out;
	}

	public static TilePos[] around(Player player, int x, int y, int z, Side side, Depth depth) {
		TilePos[] all = cube(player, x, y, z, side, depth);
		if (all == null || all.length < 2) return null;
		TilePos[] rest = new TilePos[all.length - 1];
		System.arraycopy(all, 1, rest, 0, rest.length);
		return rest;
	}

	public static TilePos[] around(Player player, int x, int y, int z, Side side) {
		return around(player, x, y, z, side, Depth.INTO);
	}
}

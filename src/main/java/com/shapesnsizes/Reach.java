package com.shapesnsizes;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;

public final class Reach {

	private static Player measuring;

	private static double allowance = 1.0;

	private Reach() {}

	public static void begin(Player player, float factor) {
		measuring = player;
		allowance = (double) factor * (double) factor;
	}

	public static void end() {
		measuring = null;
		allowance = 1.0;
	}

	public static double allowanceFor(Entity entity) {
		Player player = measuring;
		return player != null && player == entity ? allowance : 1.0;
	}
}

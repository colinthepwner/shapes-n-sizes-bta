package com.shapesnsizes;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;

public final class Ridable {

	public static final float MAX_RIDER = 2.5f;

	private Ridable() {}

	public static boolean fitsAboard(Player player) {
		return PlayerScale.get(player) <= MAX_RIDER;
	}

	public static boolean crush(Player rider, Entity vehicle) {
		if (vehicle == null || vehicle.removed) return false;
		if (rider.world == null || rider.world.isClientSide) return false;
		if (fitsAboard(rider)) return false;
		if (rider.vehicle == vehicle) vehicle.ejectRider();
		vehicle.hurt(rider, CRUSHING_BLOW, DamageType.GENERIC);
		return true;
	}

	private static final int CRUSHING_BLOW = 100;
}

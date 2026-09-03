package com.shapesnsizes;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.MathHelper;

public final class Piggyback {

	public static final float MIN_RATIO = 1.5f;

	public static final float SHOULDER_RATIO = 3.0f;

	private Piggyback() {}

	public static boolean wearingSaddle(Player player) {
		if (player == null) return false;
		ItemStack chest = player.getItemInArmorSlot(HumanArmorShape.CHEST);
		return chest != null && Items.SADDLE != null && chest.itemID == Items.SADDLE.id;
	}

	public static float ratio(Player carrier, Player rider) {
		return PlayerScale.get(carrier) / PlayerScale.get(rider);
	}

	public static boolean canCarry(Player carrier, Player rider) {
		if (carrier == null || rider == null || carrier == rider) return false;
		if (!wearingSaddle(carrier)) return false;
		if (carrier.isPlayerSleeping() || rider.isPlayerSleeping()) return false;
		return ratio(carrier, rider) >= MIN_RATIO;
	}

	public static boolean stillAllowed(Player carrier, Player rider) {
		return canCarry(carrier, rider);
	}

	public static boolean onShoulder(Player carrier, Player rider) {
		return ratio(carrier, rider) >= SHOULDER_RATIO;
	}

	public static void seat(Player carrier, Player rider) {
		float s = PlayerScale.get(carrier);
		float yaw = carrier.yBodyRot * MathHelper.DEG_TO_RAD;

		double behindX = Math.sin(yaw), behindZ = -Math.cos(yaw);
		double rightX = -Math.cos(yaw), rightZ = -Math.sin(yaw);

		double back, side, feet;
		if (onShoulder(carrier, rider)) {

			back = 0.0;
			side = 0.28 * s + rider.bbWidth * 0.5;
			feet = 1.4 * s;
		} else {

			back = 0.22 * s + rider.bbWidth * 0.5;
			side = 0.0;
			feet = Math.max(carrier.bbHeight - rider.bbHeight, 0.9 * s);
		}
		rider.setPos(
			carrier.x + behindX * back + rightX * side,
			carrier.y + feet,
			carrier.z + behindZ * back + rightZ * side);
	}

	public static void ride(Player rider, net.minecraft.core.world.IVehicle mount) {
		if (rider == null) return;
		rider.startRiding(mount);
		if (rider instanceof RideSync) ((RideSync) rider).shapesnsizes$syncRiding();
	}

	public static void dismount(Player rider) {
		ride(rider, null);
	}

	public static final float MOUNT_RATIO = 2.0f;

	public static boolean canMount(Player rider, net.minecraft.core.entity.Mob mount) {
		if (rider == null || mount == null || rider == mount) return false;
		if (!PlayerScale.isSmall(rider)) return false;
		if (rider.bbHeight <= 0.0f || mount.bbHeight <= 0.0f) return false;
		if (mount.bbHeight / rider.bbHeight < MOUNT_RATIO) return false;
		return holdingString(rider);
	}

	public static boolean holdingString(Player player) {
		ItemStack held = player.getHeldItem();
		return held != null && Items.STRING != null && held.itemID == Items.STRING.id;
	}

	public static boolean isCarried(Entity entity) {
		return entity instanceof Player && entity.vehicle instanceof Player;
	}
}

package com.shapesnsizes;

import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public final class Behemoth {

	public static final float THRESHOLD = 6.0f;
	private Behemoth() {}

	public static boolean is(Player player) {
		return PlayerScale.get(player) >= THRESHOLD;
	}

	public static float strideLength(Player player) {
		return 1.67f * Math.max(1.0f, PlayerScale.get(player));
	}

	public static void step(Player player) {
		World world = player.world;
		if (world == null || world.isClientSide || !is(player)) return;
		if (!PlayerScale.stompSounds(world)) return;

		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, player.x, player.bb.minY, player.z,
			"random.explode", 0.6f, 0.7f);
	}

	public static boolean wearingSoftBoots(Player player) {
		ItemStack boots = player.getItemInArmorSlot(HumanArmorShape.BOOTS);
		return boots != null && boots.getItem() == Items.ARMOR_BOOTS_LEATHER;
	}
}

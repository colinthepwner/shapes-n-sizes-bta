package com.shapesnsizes.item;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class ItemBrownie extends ItemFood {

	private static final int HEAL = 2;
	private static final int TICKS_PER_HEAL = 4;

	private final float sizeChange;

	public ItemBrownie(String name, String namespaceId, int id, float sizeChange) {
		super(name, namespaceId, id, HEAL, TICKS_PER_HEAL, false, 16);
		this.sizeChange = sizeChange;
	}

	public float getSizeChange() {
		return this.sizeChange;
	}

	@Override
	public ItemStack onUse(ItemStack selfStack, World world, Player player) {
		if (!selfStack.consumeItem(player)) {
			return selfStack;
		}
		player.eatFood(selfStack);
		if (!world.isClientSide) {
			PlayerScale.addBonus(player, this.sizeChange);
		}

		world.playSoundAtEntity(player, player, "random.bite", 0.5f, 1.1f);
		return selfStack;
	}
}

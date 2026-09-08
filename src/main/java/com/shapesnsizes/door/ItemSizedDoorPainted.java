package com.shapesnsizes.door;

import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.NotNull;

public class ItemSizedDoorPainted extends ItemSizedDoor {
	public ItemSizedDoorPainted(@NotNull String name, @NotNull String namespaceId, int id, @NotNull Block<?>... segments) {
		super(name, namespaceId, id, segments);
	}

	@Override
	protected int extraData(@NotNull ItemStack selfStack) {
		return DyeColor.colorFromItemMeta(selfStack.getMetadata()).blockMeta << 4 & BlockLogicSizedDoorPainted.MASK_COLOR;
	}

	@NotNull
	@Override
	public String getLanguageKey(@NotNull ItemStack selfStack) {
		return super.getKey() + "." + DyeColor.colorFromItemMeta(selfStack.getMetadata()).colorID;
	}
}

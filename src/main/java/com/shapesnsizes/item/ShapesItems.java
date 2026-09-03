package com.shapesnsizes.item;

import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

public final class ShapesItems {

	private static final int FIRST_ID = 20100;

	public static final float STEP = 0.1f;

	public static Item BROWNIE_BIG;
	public static Item BROWNIE_SMALL;

	private ShapesItems() {}

	public static void register() {
		ItemBuilder builder = new ItemBuilder(ShapesNSizes.MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.After(() -> Items.FOOD_APPLE));

		int id = FIRST_ID;
		BROWNIE_BIG = builder.clone().build(
			new ItemBrownie("brownie_big", "shapesnsizes:item/browniebig", id++, STEP));
		BROWNIE_SMALL = builder.clone().build(
			new ItemBrownie("brownie_small", "shapesnsizes:item/browniesmall", id++, -STEP));

		ShapesNSizes.LOGGER.info("Registered 2 brownies, ids {}-{}.", FIRST_ID, id - 1);
	}
}

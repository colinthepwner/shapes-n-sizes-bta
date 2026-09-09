package com.shapesnsizes.item;

import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.item.Item;
import net.minecraft.core.net.entity.NetEntityHandler;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

public final class ShapesItems {

	private static final int FIRST_ID = 20100;

	private static final int CHARRED_FIRST_ID = 20108;

	public static final float STEP = 0.1f;

	public static Item BROWNIE_BIG;
	public static Item BROWNIE_SMALL;

	public static Item BROWNIE_BIG_CHARRED;
	public static Item BROWNIE_SMALL_CHARRED;

	private ShapesItems() {}

	public static void register() {
		ItemBuilder builder = new ItemBuilder(ShapesNSizes.MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.After(() -> Items.FOOD_APPLE));

		int id = FIRST_ID;
		BROWNIE_BIG = builder.clone().build(
			new ItemBrownie("brownie_big", "shapesnsizes:item/browniebig", id++, STEP));
		BROWNIE_SMALL = builder.clone().build(
			new ItemBrownie("brownie_small", "shapesnsizes:item/browniesmall", id++, -STEP));

		int charred = CHARRED_FIRST_ID;
		BROWNIE_BIG_CHARRED = builder.clone().build(new ItemBrownieCharred(
			"brownie_big_charred", "shapesnsizes:item/browniebigcharred", charred++,
			(world, thrower) -> new ProjectileCharredBrownie.Growing(world, thrower)));
		BROWNIE_SMALL_CHARRED = builder.clone().build(new ItemBrownieCharred(
			"brownie_small_charred", "shapesnsizes:item/browniesmallcharred", charred++,
			(world, thrower) -> new ProjectileCharredBrownie.Shrinking(world, thrower)));

		registerProjectiles();
		ShapesNSizes.LOGGER.info("Registered 2 brownies, ids {}-{}, and 2 charred, ids {}-{}.",
			FIRST_ID, id - 1, CHARRED_FIRST_ID, charred - 1);
	}

	private static void registerProjectiles() {
		EntityDispatcher.getInstance().addMapping(ProjectileCharredBrownie.Growing.class,
			NamespaceID.fromPool(ShapesNSizes.MOD_ID, "charred_brownie_growing"),
			ProjectileCharredBrownie.Growing::new);
		EntityDispatcher.getInstance().addMapping(ProjectileCharredBrownie.Shrinking.class,
			NamespaceID.fromPool(ShapesNSizes.MOD_ID, "charred_brownie_shrinking"),
			ProjectileCharredBrownie.Shrinking::new);
		NetEntityHandler.registerNetworkEntry(new NetEntryCharredBrownie.Growing(), NetEntryCharredBrownie.Growing.TYPE);
		NetEntityHandler.registerNetworkEntry(new NetEntryCharredBrownie.Shrinking(), NetEntryCharredBrownie.Shrinking.TYPE);
	}
}

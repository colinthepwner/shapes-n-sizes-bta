package com.shapesnsizes.door;

import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.BlockSounds;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

public final class ShapesDoors {
	private static final int FIRST_BLOCK_ID = 3500;
	private static final int FIRST_ITEM_ID = 20102;
	public static final String MODEL_DIR = "shapesnsizes:block/door/planks";

	public static Block<BlockLogicSizedDoor> SHORT;
	public static Block<BlockLogicSizedDoor>[] TALL;
	public static Block<BlockLogicSizedDoor>[] VERY_TALL;

	public static Item DOOR_SHORT;
	public static Item DOOR_TALL;
	public static Item DOOR_VERY_TALL;

	private ShapesDoors() {}

	public static String segmentName(String door, int index) {
		return door + index;
	}

	@SuppressWarnings("unchecked")
	public static void register() {
		int id = FIRST_BLOCK_ID;
		SHORT = segment("door_short", id++, 1, 0, () -> DOOR_SHORT);
		TALL = new Block[3];
		for (int i = 0; i < 3; ++i) {
			TALL[i] = segment("door_tall_" + i, id++, 3, i, () -> DOOR_TALL);
		}
		VERY_TALL = new Block[4];
		for (int i = 0; i < 4; ++i) {
			VERY_TALL[i] = segment("door_verytall_" + i, id++, 4, i, () -> DOOR_VERY_TALL);
		}

		ItemBuilder items = new ItemBuilder(ShapesNSizes.MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.After(() -> Items.DOOR_OAK));
		DOOR_SHORT = items.build(new ItemSizedDoor("door_short", "shapesnsizes:item/door_short", FIRST_ITEM_ID, SHORT));
		DOOR_TALL = items.build(new ItemSizedDoor("door_tall", "shapesnsizes:item/door_tall", FIRST_ITEM_ID + 1, TALL));
		DOOR_VERY_TALL = items.build(new ItemSizedDoor("door_verytall", "shapesnsizes:item/door_verytall", FIRST_ITEM_ID + 2, VERY_TALL));
		ShapesNSizes.LOGGER.info("Registered 3 doors: blocks {}-{}, items {}-{}.", FIRST_BLOCK_ID, id - 1, FIRST_ITEM_ID, FIRST_ITEM_ID + 2);
	}

	private static Block<BlockLogicSizedDoor> segment(String name, int id, int height, int index, java.util.function.Supplier<Item> drops) {
		return new BlockBuilder(ShapesNSizes.MOD_ID)
			.setHardness(3.0f)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.MINEABLE_BY_AXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.build(name, id, b -> new BlockLogicSizedDoor(b, Materials.WOOD, height, index, drops));
	}

	public static void registerRecipes() {
		String ns = ShapesNSizes.MOD_ID;
		Item trapdoor = Blocks.TRAPDOOR_PLANKS_OAK.asItem();

		RecipeBuilder.Shapeless(ns).addInput(trapdoor).create("door_short_from_trapdoor", new ItemStack(DOOR_SHORT, 1));
		RecipeBuilder.Shapeless(ns).addInput(Items.DOOR_OAK).create("door_short_from_door", new ItemStack(DOOR_SHORT, 2));

		RecipeBuilder.Shaped(ns).setShape("T", "D").addInput('T', trapdoor).addInput('D', Items.DOOR_OAK)
			.create("door_tall_from_trapdoor", new ItemStack(DOOR_TALL, 1));
		RecipeBuilder.Shaped(ns).setShape("S", "D").addInput('S', DOOR_SHORT).addInput('D', Items.DOOR_OAK)
			.create("door_tall_from_short", new ItemStack(DOOR_TALL, 1));

		RecipeBuilder.Shaped(ns).setShape("T", "D").addInput('T', trapdoor).addInput('D', DOOR_TALL)
			.create("door_verytall_from_trapdoor", new ItemStack(DOOR_VERY_TALL, 1));
		RecipeBuilder.Shaped(ns).setShape("S", "D").addInput('S', DOOR_SHORT).addInput('D', DOOR_TALL)
			.create("door_verytall_from_short", new ItemStack(DOOR_VERY_TALL, 1));
		ShapesNSizes.LOGGER.info("Registered 6 door recipes.");
	}
}

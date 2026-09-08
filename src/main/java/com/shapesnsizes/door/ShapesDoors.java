package com.shapesnsizes.door;

import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryDyeing;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.helper.DyeColor;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

	public static Block<BlockLogicSizedDoor>[] SHORT_PAINTED;
	public static Block<BlockLogicSizedDoor>[] TALL_PAINTED;
	public static Block<BlockLogicSizedDoor>[] VERY_TALL_PAINTED;

	public static Item DOOR_SHORT_PAINTED;
	public static Item DOOR_TALL_PAINTED;
	public static Item DOOR_VERY_TALL_PAINTED;

	private ShapesDoors() {}

	public static String segmentName(String door, int index) {
		return door + index;
	}

	@SuppressWarnings("unchecked")
	public static void register() {
		int id = FIRST_BLOCK_ID;

		Object plainShort = new Object();
		Object plainTall = new Object();
		Object plainVeryTall = new Object();
		SHORT = segment("door_short", id++, 1, 0, plainShort, () -> DOOR_SHORT);
		TALL = new Block[3];
		for (int i = 0; i < 3; ++i) {
			TALL[i] = segment("door_tall_" + i, id++, 3, i, plainTall, () -> DOOR_TALL);
		}
		VERY_TALL = new Block[4];
		for (int i = 0; i < 4; ++i) {
			VERY_TALL[i] = segment("door_verytall_" + i, id++, 4, i, plainVeryTall, () -> DOOR_VERY_TALL);
		}

		Object dyedShort = new Object();
		Object dyedTall = new Object();
		Object dyedVeryTall = new Object();
		SHORT_PAINTED = new Block[]{painted("door_short_painted", id++, 1, 0, dyedShort,
			() -> new Block<?>[]{SHORT}, () -> DOOR_SHORT_PAINTED)};
		TALL_PAINTED = new Block[3];
		for (int i = 0; i < 3; ++i) {
			TALL_PAINTED[i] = painted("door_tall_painted_" + i, id++, 3, i, dyedTall, () -> TALL, () -> DOOR_TALL_PAINTED);
		}
		VERY_TALL_PAINTED = new Block[4];
		for (int i = 0; i < 4; ++i) {
			VERY_TALL_PAINTED[i] = painted("door_verytall_painted_" + i, id++, 4, i, dyedVeryTall, () -> VERY_TALL, () -> DOOR_VERY_TALL_PAINTED);
		}

		ItemBuilder items = new ItemBuilder(ShapesNSizes.MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.After(() -> Items.DOOR_OAK));
		DOOR_SHORT = items.build(new ItemSizedDoor("door_short", "shapesnsizes:item/door_short", FIRST_ITEM_ID, SHORT));
		DOOR_TALL = items.build(new ItemSizedDoor("door_tall", "shapesnsizes:item/door_tall", FIRST_ITEM_ID + 1, TALL));
		DOOR_VERY_TALL = items.build(new ItemSizedDoor("door_verytall", "shapesnsizes:item/door_verytall", FIRST_ITEM_ID + 2, VERY_TALL));

		DOOR_SHORT_PAINTED = paintedItem("door_short_painted", FIRST_ITEM_ID + 3, SHORT_PAINTED, () -> DOOR_SHORT_PAINTED);
		DOOR_TALL_PAINTED = paintedItem("door_tall_painted", FIRST_ITEM_ID + 4, TALL_PAINTED, () -> DOOR_TALL_PAINTED);
		DOOR_VERY_TALL_PAINTED = paintedItem("door_verytall_painted", FIRST_ITEM_ID + 5, VERY_TALL_PAINTED, () -> DOOR_VERY_TALL_PAINTED);

		ShapesNSizes.LOGGER.info("Registered 3 doors and 3 dyed doors: blocks {}-{}, items {}-{}.",
			FIRST_BLOCK_ID, id - 1, FIRST_ITEM_ID, FIRST_ITEM_ID + 5);
	}

	private static Block<BlockLogicSizedDoor> segment(String name, int id, int height, int index,
													  Object family, Supplier<Item> drops) {
		return builder().build(name, id, b -> new BlockLogicSizedDoor(b, Materials.WOOD, height, index, family, drops));
	}

	private static Block<BlockLogicSizedDoor> painted(String name, int id, int height, int index, Object family,
													  Supplier<Block<?>[]> plain, Supplier<Item> drops) {
		return builder().build(name, id, b -> new BlockLogicSizedDoorPainted(b, Materials.WOOD, height, index, family, plain, drops));
	}

	private static BlockBuilder builder() {
		return new BlockBuilder(ShapesNSizes.MOD_ID)
			.setHardness(3.0f)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.MINEABLE_BY_AXE, BlockTags.NOT_IN_CREATIVE_MENU);
	}

	private static Item paintedItem(String name, int id, Block<?>[] segments, Supplier<Item> self) {
		CreativeInventoryPlacement placement = new CreativeInventoryPlacement.After(() -> Items.DOOR_OAK_PAINTED);
		placement.setCustomSupplier(() -> {
			List<ItemStack> stacks = new ArrayList<>(DyeColor.COLOR_AMOUNT);
			for (DyeColor color : DyeColor.values()) {
				stacks.add(new ItemStack(self.get(), 1, color.itemMeta));
			}
			return stacks;
		});
		return new ItemBuilder(ShapesNSizes.MOD_ID)
			.setCreativeInventoryPlacement(placement)
			.build(new ItemSizedDoorPainted(name, "shapesnsizes:item/" + name, id, segments));
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

		registerDyedRecipes(ns);
		ShapesNSizes.LOGGER.info("Registered {} door recipes.", 6 + DyeColor.COLOR_AMOUNT * 6 + 3);
	}

	private static void registerDyedRecipes(String ns) {
		Block<?> dyedTrapdoor = Blocks.TRAPDOOR_PLANKS_PAINTED;

		for (DyeColor color : DyeColor.values()) {
			int block = color.blockMeta;
			int item = color.itemMeta;
			String c = color.colorID;

			RecipeBuilder.Shapeless(ns).addInput(dyedTrapdoor, block)
				.create(c + "_door_short_from_trapdoor", new ItemStack(DOOR_SHORT_PAINTED, 1, item));
			RecipeBuilder.Shapeless(ns).addInput(Items.DOOR_OAK_PAINTED, item)
				.create(c + "_door_short_from_door", new ItemStack(DOOR_SHORT_PAINTED, 2, item));

			RecipeBuilder.Shaped(ns).setShape("T", "D")
				.addInput('T', dyedTrapdoor, block).addInput('D', Items.DOOR_OAK_PAINTED, item)
				.create(c + "_door_tall_from_trapdoor", new ItemStack(DOOR_TALL_PAINTED, 1, item));
			RecipeBuilder.Shaped(ns).setShape("S", "D")
				.addInput('S', DOOR_SHORT_PAINTED, item).addInput('D', Items.DOOR_OAK_PAINTED, item)
				.create(c + "_door_tall_from_short", new ItemStack(DOOR_TALL_PAINTED, 1, item));

			RecipeBuilder.Shaped(ns).setShape("T", "D")
				.addInput('T', dyedTrapdoor, block).addInput('D', DOOR_TALL_PAINTED, item)
				.create(c + "_door_verytall_from_trapdoor", new ItemStack(DOOR_VERY_TALL_PAINTED, 1, item));
			RecipeBuilder.Shaped(ns).setShape("S", "D")
				.addInput('S', DOOR_SHORT_PAINTED, item).addInput('D', DOOR_TALL_PAINTED, item)
				.create(c + "_door_verytall_from_short", new ItemStack(DOOR_VERY_TALL_PAINTED, 1, item));
		}

		dyeing(ns, "door_short", DOOR_SHORT, DOOR_SHORT_PAINTED);
		dyeing(ns, "door_tall", DOOR_TALL, DOOR_TALL_PAINTED);
		dyeing(ns, "door_verytall", DOOR_VERY_TALL, DOOR_VERY_TALL_PAINTED);
	}

	private static void dyeing(String ns, String name, Item plain, Item dyed) {
		String key = ns + ":" + name + "_dyeing";
		if (Registries.RECIPES.WORKBENCH.getItem(key) != null) return;
		List<ItemStack> dyeable = new ArrayList<>(DyeColor.COLOR_AMOUNT + 1);
		dyeable.add(new ItemStack(plain));
		for (DyeColor color : DyeColor.values()) {
			dyeable.add(new ItemStack(dyed, 1, color.itemMeta));
		}
		Registries.RECIPES.WORKBENCH.register(key,
			new RecipeEntryDyeing(new RecipeSymbol(dyeable), new ItemStack(dyed), false, true));
	}
}

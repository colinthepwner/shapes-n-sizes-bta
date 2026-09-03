package com.shapesnsizes.item;

import com.shapesnsizes.ShapesNSizes;
import com.shapesnsizes.door.ShapesDoors;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import turniplabs.halplibe.helper.RecipeBuilder;

public final class ShapesRecipes {
	private ShapesRecipes() {}

	public static void register() {
		RecipeBuilder.Shapeless(ShapesNSizes.MOD_ID)
			.addInput(dye(DyeColor.BROWN))
			.addInput(Items.WHEAT)
			.addInput(Items.DUST_SUGAR)
			.addInput(dye(DyeColor.BLUE))
			.create("brownie_big", new ItemStack(ShapesItems.BROWNIE_BIG, 2));

		RecipeBuilder.Shapeless(ShapesNSizes.MOD_ID)
			.addInput(dye(DyeColor.BROWN))
			.addInput(Items.WHEAT)
			.addInput(Items.DUST_SUGAR)
			.addInput(dye(DyeColor.ORANGE))
			.create("brownie_small", new ItemStack(ShapesItems.BROWNIE_SMALL, 2));

		ShapesDoors.registerRecipes();
		Registries.RECIPES.invalidateCaches();
		ShapesNSizes.LOGGER.info("Registered 2 brownie recipes.");
	}

	private static ItemStack dye(DyeColor color) {
		return new ItemStack(Items.DYE, 1, color.itemMeta);
	}
}

package com.shapesnsizes;

import com.shapesnsizes.command.ScalingCommand;
import com.shapesnsizes.item.ShapesItems;
import com.shapesnsizes.door.ShapesDoors;
import com.shapesnsizes.item.ShapesRecipes;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.dependency.Key;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;

public class ShapesNSizes implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("shapesnsizes", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ScalingRules.init();
		ShapesConfig.load();
		CommandManager.registerCommand(new ScalingCommand());

		Key key = Key.of(MOD_ID);
		CommonEvents.BEFORE_GAME_START.listen(key, ShapesItems::register);

		CommonEvents.BEFORE_GAME_START.listen(key, ShapesDoors::register);
		CommonEvents.AFTER_GAME_START.listen(key, this::onRecipesReady);
		CommonEvents.RECIPES_NAMESPACE_INIT.listen(key, this::onRecipeNamespace);
		LOGGER.info("Shapes n Sizes ready -- /scaling set <player> <scale>");
	}

	private boolean recipesRegistered = false;

	private void onRecipesReady() {
		RecipeBuilder.initNameSpace(MOD_ID);
		ShapesRecipes.register();
		this.recipesRegistered = true;
	}

	private void onRecipeNamespace() {
		RecipeBuilder.initNameSpace(MOD_ID);
		if (this.recipesRegistered) {
			ShapesRecipes.register();
		}
	}
}

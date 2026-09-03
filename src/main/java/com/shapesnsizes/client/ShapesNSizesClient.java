package com.shapesnsizes.client;

import com.shapesnsizes.ShapesNSizes;
import com.shapesnsizes.door.BlockLogicSizedDoor;
import com.shapesnsizes.door.ShapesDoors;
import com.shapesnsizes.item.ShapesItems;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.Item;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.OptionBoolean;
import org.lwjgl.input.Keyboard;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

public class ShapesNSizesClient implements ClientModInitializer {
	public static final KeyBinding KEY_CRAWL =
		GameSettings.register(new KeyBinding("key.shapesnsizes.crawl").setDefault(InputDevice.keyboard, Keyboard.KEY_LMENU));
	public static final OptionBoolean CRAWL_TOGGLE =
		GameSettings.register(new OptionBoolean("shapesnsizesCrawlToggle", false));

	@Override
	public void onInitializeClient() {
		Key key = Key.of(ShapesNSizes.MOD_ID);
		CommonEvents.AFTER_GAME_START.listen(key, ShapesNSizesClient::addOptions);
		ClientEvents.ITEM_MODEL_RELOAD.listen(key, ShapesNSizesClient::bindItemModels);
		ClientEvents.BLOCK_MODEL_RELOAD.listen(key, ShapesNSizesClient::bindBlockModels);
	}

	private static void bindItemModels(ItemModelDispatcher dispatcher) {
		int bound = 0;
		for (Item brownie : new Item[]{ShapesItems.BROWNIE_BIG, ShapesItems.BROWNIE_SMALL,
				ShapesDoors.DOOR_SHORT, ShapesDoors.DOOR_TALL, ShapesDoors.DOOR_VERY_TALL}) {
			if (brownie == null) continue;
			dispatcher.addDispatch(new ItemModelStandard(brownie));

			if (dispatcher.hasDispatch(brownie)) {
				++bound;
			} else {
				ShapesNSizes.LOGGER.warn("No item model bound for {}; it will be invisible.", brownie.getKey());
			}
		}
		ShapesNSizes.LOGGER.info("Bound {} of 5 item models (brownies and doors).", bound);
	}

	private static void bindBlockModels(BlockModelDispatcher dispatcher) {
		int bound = 0;
		bound += bindDoor(dispatcher, "short", new Block[]{ShapesDoors.SHORT});
		bound += bindDoor(dispatcher, "tall", ShapesDoors.TALL);
		bound += bindDoor(dispatcher, "verytall", ShapesDoors.VERY_TALL);
		ShapesNSizes.LOGGER.info("Bound {} of 8 door segment models.", bound);
	}

	private static int bindDoor(BlockModelDispatcher dispatcher, String door, Block<?>[] segments) {
		int bound = 0;
		for (int i = 0; i < segments.length; ++i) {
			@SuppressWarnings("unchecked")
			Block<BlockLogicSizedDoor> block = (Block<BlockLogicSizedDoor>) segments[i];
			String segment = ShapesDoors.segmentName(door, i);
			dispatcher.addDispatch(new BlockModelSizedDoor<>(block, ShapesDoors.MODEL_DIR, segment));
			if (dispatcher.hasDispatch(block)) ++bound;
			else ShapesNSizes.LOGGER.warn("No block model bound for {}; it will be invisible.", block.getKey());
		}
		return bound;
	}

	private static void addOptions() {
		try {
			OptionsPages.CONTROLS.withComponent(new OptionsCategory("gui.options.page.controls.category.shapesnsizes")
				.withComponent(new KeyBindingComponent(KEY_CRAWL))
				.withComponent(new BooleanOptionComponent(CRAWL_TOGGLE)));
		} catch (Throwable t) {

			ShapesNSizes.LOGGER.warn("Could not add the crawl controls to the options screen", t);
		}
	}
}

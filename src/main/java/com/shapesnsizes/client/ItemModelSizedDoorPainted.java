package com.shapesnsizes.client;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.NotNull;

public class ItemModelSizedDoorPainted extends ItemModelStandard {
	private final IconCoordinate[] icons = new IconCoordinate[16];

	public ItemModelSizedDoorPainted(Item item, String texturePrefix) {
		super(item, false);
		for (DyeColor color : DyeColor.itemOrderedColors()) {
			this.icons[color.itemMeta] = TextureRegistry.getTexture(texturePrefix + "_" + color.colorID);
		}
	}

	@NotNull
	@Override
	public IconCoordinate getIcon(Entity entity, @NotNull ItemStack itemStack) {
		return this.icons[itemStack.getMetadata() & 15];
	}
}

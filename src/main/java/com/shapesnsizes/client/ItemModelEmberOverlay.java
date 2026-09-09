package com.shapesnsizes.client;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.LightIndexHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemModelEmberOverlay extends ItemModelStandard {
	private final IconCoordinate embers;

	public ItemModelEmberOverlay(@NotNull Item item, @NotNull String overlayTexture) {
		super(item);
		this.embers = TextureRegistry.getTexture(overlayTexture);
	}

	@Override
	protected void renderSingle(@NotNull TessellatorGeneral tessellator, @Nullable Entity holder,
								@NotNull ItemStack itemStack, boolean items3d, byte lightIndex,
								int color, float partialTick, boolean mirrorX) {
		super.renderSingle(tessellator, holder, itemStack, items3d, lightIndex, color, partialTick, mirrorX);

		this.renderCoordinate(tessellator, this.embers, LightIndexHelper.lightIndex2i(15, 15), color, items3d, mirrorX);
	}
}

package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModel.class, remap = false)
public class ItemModelMixin {
	@Inject(
		method = "renderFirstPerson",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/item/model/ItemModel;render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;Ljava/lang/String;ZIBFZ)V"
		)
	)
	private void shapesnsizes$worldSizedInHand(TessellatorGeneral tessellator, ItemRenderer itemRenderer, Player player,
											   ItemStack itemStack, byte lightIndex, float partialTick, CallbackInfo ci) {
		float s = PlayerScale.get(player);
		if (s == PlayerScale.DEFAULT) return;
		float inv = 1.0f / s;

		float shove = Math.min(OVERSIZE_CAP, inv) - 1.0f;
		if (shove > 0.0f) {
			GLRenderer.modelM4f().translate(shove * ASIDE, shove * -DOWN, shove * -AWAY);
		}
		GLRenderer.modelM4f().scale(inv, inv, inv);
	}

	private static final float OVERSIZE_CAP = 5.0f;
	private static final float ASIDE = 0.16f;
	private static final float DOWN = 0.10f;
	private static final float AWAY = 0.07f;
}

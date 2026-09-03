package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.component.HudComponentHealthBar;
import net.minecraft.core.block.BlockLogicEdible;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HudComponentHealthBar.class, remap = false)
public class HudComponentHealthBarMixin {

	@Unique
	private static Player shapesnsizes$viewer() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc == null ? null : mc.thePlayer;
	}

	@Redirect(
		method = "getPotentialHealing",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getHealAmount(Lnet/minecraft/core/item/ItemStack;)I")
	)
	private int shapesnsizes$previewHeldFood(ItemFood food, ItemStack stack) {
		return PlayerScale.nourishment(shapesnsizes$viewer(), food.getHealAmount(stack));
	}

	@Redirect(
		method = "getPotentialHealing",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/block/BlockLogicEdible;getHealAmount(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/pos/TilePosc;)I"
		)
	)
	private int shapesnsizes$previewEdibleBlock(BlockLogicEdible edible, World world, TilePosc tilePos) {
		return PlayerScale.nourishment(shapesnsizes$viewer(), edible.getHealAmount(world, tilePos));
	}
}

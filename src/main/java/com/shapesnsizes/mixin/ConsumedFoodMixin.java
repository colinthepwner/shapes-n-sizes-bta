package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.ConsumedFood;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ConsumedFood.class, remap = false)
public class ConsumedFoodMixin {
	@Shadow @Final private Mob entity;

	@Redirect(
		method = "<init>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getHealAmount(Lnet/minecraft/core/item/ItemStack;)I")
	)
	private int shapesnsizes$firstBite(ItemFood food, ItemStack stack) {
		return PlayerScale.nourishment(this.entity, food.getHealAmount(stack));
	}

	@Redirect(
		method = "addFood",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getHealAmount(Lnet/minecraft/core/item/ItemStack;)I")
	)
	private int shapesnsizes$anotherBite(ItemFood food, ItemStack stack) {
		return PlayerScale.nourishment(this.entity, food.getHealAmount(stack));
	}
}

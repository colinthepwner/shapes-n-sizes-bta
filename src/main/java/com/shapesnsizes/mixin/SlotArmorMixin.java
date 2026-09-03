package com.shapesnsizes.mixin;

import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.slot.SlotArmor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlotArmor.class, remap = false)
public class SlotArmorMixin {
	@Shadow @Final HumanArmorShape armorShape;

	@Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$allowSaddleOnChest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (this.armorShape == HumanArmorShape.CHEST && stack != null && Items.SADDLE != null
			&& stack.itemID == Items.SADDLE.id) {
			cir.setReturnValue(true);
		}
	}
}

package com.shapesnsizes.mixin.client;

import com.shapesnsizes.Cursor;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerController.class, remap = false)
public abstract class PlayerControllerMixin {
	@Unique private boolean shapesnsizes$spreading = false;

	@Inject(method = "useOrPlaceItemStackOnTile", at = @At("RETURN"))
	private void shapesnsizes$placeSquare(Player player, World world, ItemStack itemStack, TilePosc tilePos,
			Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() || this.shapesnsizes$spreading) return;
		if (world == null || world.isClientSide || player == null) return;

		if (itemStack == null || itemStack.stackSize <= 0) return;
		TilePos[] rest = Cursor.around(player, tilePos.x(), tilePos.y(), tilePos.z(), side);
		if (rest == null) return;

		this.shapesnsizes$spreading = true;
		try {
			PlayerController self = (PlayerController) (Object) this;
			for (TilePos pos : rest) {
				ItemStack held = player.getCurrentEquippedItem();
				if (held == null || held.stackSize <= 0) break;
				self.useOrPlaceItemStackOnTile(player, world, held, pos, side, xPlaced, yPlaced);
			}
		} finally {
			this.shapesnsizes$spreading = false;
		}
	}
}

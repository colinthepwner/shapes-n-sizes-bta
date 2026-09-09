package com.shapesnsizes.mixin.server;

import com.shapesnsizes.Cursor;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerController.class, remap = false)
public abstract class ServerPlayerControllerMixin {
	@Shadow public Player player;

	@Unique private boolean shapesnsizes$breaking = false;
	@Unique private boolean shapesnsizes$placing = false;

	@Inject(method = "mineBlock", at = @At("RETURN"))
	private void shapesnsizes$breakSquare(int x, int y, int z, Side side, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() || this.shapesnsizes$breaking) return;
		TilePos[] rest = Cursor.around(this.player, x, y, z, side);
		if (rest == null) return;

		this.shapesnsizes$breaking = true;
		try {
			ServerPlayerController self = (ServerPlayerController) (Object) this;
			for (TilePos pos : rest) {
				self.mineBlock(pos.x, pos.y, pos.z, side);
			}
		} finally {
			this.shapesnsizes$breaking = false;
		}
	}

	@Inject(method = "useOrPlaceItemStackOnTile", at = @At("RETURN"))
	private void shapesnsizes$placeSquare(Player entityplayer, World world, ItemStack itemstack,
			int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced,
			CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() || this.shapesnsizes$placing) return;
		if (entityplayer == null || world == null) return;

		if (itemstack == null || itemstack.stackSize <= 0) return;
		TilePos[] rest = Cursor.around(entityplayer, blockX, blockY, blockZ, side);
		if (rest == null) return;

		this.shapesnsizes$placing = true;
		try {
			ServerPlayerController self = (ServerPlayerController) (Object) this;
			for (TilePos pos : rest) {

				ItemStack held = entityplayer.getCurrentEquippedItem();
				if (held == null || held.stackSize <= 0) break;
				self.useOrPlaceItemStackOnTile(entityplayer, world, held, pos.x, pos.y, pos.z,
					side, xPlaced, yPlaced);
			}
		} finally {
			this.shapesnsizes$placing = false;
		}
	}
}

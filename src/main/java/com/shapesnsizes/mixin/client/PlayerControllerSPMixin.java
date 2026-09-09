package com.shapesnsizes.mixin.client;

import com.shapesnsizes.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerSP.class, remap = false)
public abstract class PlayerControllerSPMixin {
	@Unique private boolean shapesnsizes$spreading = false;

	@Unique
	private static Player shapesnsizes$player() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc == null ? null : mc.thePlayer;
	}

	@Inject(method = "destroyBlock", at = @At("RETURN"))
	private void shapesnsizes$breakSquare(TilePosc tilePos, Side side, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() || this.shapesnsizes$spreading) return;
		Player player = shapesnsizes$player();
		if (player == null) return;
		TilePos[] rest = Cursor.around(player, tilePos.x(), tilePos.y(), tilePos.z(), side);
		if (rest == null) return;

		this.shapesnsizes$spreading = true;
		try {
			PlayerControllerSP self = (PlayerControllerSP) (Object) this;
			for (TilePos pos : rest) {

				self.destroyBlock(pos, side);
			}
		} finally {
			this.shapesnsizes$spreading = false;
		}
	}
}

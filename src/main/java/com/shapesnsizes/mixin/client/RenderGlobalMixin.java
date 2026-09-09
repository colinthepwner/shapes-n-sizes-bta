package com.shapesnsizes.mixin.client;

import com.shapesnsizes.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, remap = false)
public abstract class RenderGlobalMixin {
	@Inject(method = "drawSelectionBox", at = @At("TAIL"))
	private void shapesnsizes$outlineSquare(ICamera camera, HitResult hitResult, float partialTick, CallbackInfo ci) {
		if (!(hitResult instanceof HitResult.Tile)) return;
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null) return;
		Player player = mc.thePlayer;
		World world = mc.currentWorld;
		if (player == null || world == null) return;

		HitResult.Tile tile = (HitResult.Tile) hitResult;
		TilePos[] rest = Cursor.around(player,
			tile.tilePos.x(), tile.tilePos.y(), tile.tilePos.z(), tile.side);
		if (rest == null) return;

		RenderGlobal self = (RenderGlobal) (Object) this;
		for (TilePos pos : rest) {
			Block<?> block = world.getBlockType(pos);
			if (block == null || block == Blocks.AIR) continue;
			self.drawTileSelectionBox(camera, pos, partialTick);
		}
	}
}

package com.shapesnsizes.mixin;

import net.minecraft.core.block.BlockLogicBed;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLogicBed.class, remap = false)
public abstract class BlockLogicBedMixin {

	private static final int SEARCH_RADIUS = 3;

	private static final int SEARCH_HEIGHT = 1;

	@Inject(method = "getNearestEmptyTilePos", at = @At("HEAD"), cancellable = true)
	private static void shapesnsizes$searchWider(World world, TilePos tilePos, int attempts,
			CallbackInfoReturnable<TilePos> cir) {
		TilePos probe = new TilePos(0, 0, 0);
		int remaining = attempts;

		for (int ring = 0; ring <= SEARCH_RADIUS; ring++) {
			for (int dy = 0; dy <= SEARCH_HEIGHT; dy++) {

				for (int sign = 1; sign >= -1; sign -= 2) {
					int y = tilePos.y + dy * sign;
					for (int dx = -ring; dx <= ring; dx++) {
						for (int dz = -ring; dz <= ring; dz++) {

							if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
							int x = tilePos.x + dx;
							int z = tilePos.z + dz;
							if (!world.isBlockNormalCube(probe.set(x, y - 1, z))) continue;
							if (!world.isAirBlock(probe.set(x, y, z))) continue;
							if (!world.isAirBlock(probe.set(x, y + 1, z))) continue;
							if (remaining <= 0) {
								cir.setReturnValue(new TilePos(x, y, z));
								return;
							}
							remaining--;
						}
					}
					if (dy == 0) break;
				}
			}
		}
		cir.setReturnValue(null);
	}
}

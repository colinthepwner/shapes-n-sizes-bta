package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.Trample;
import net.minecraft.core.block.BlockLogicFarmland;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = BlockLogicFarmland.class, remap = false)
public class BlockLogicFarmlandMixin {
	@Inject(method = "onEntityWalkedOn", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$smallFeetDontTrample(World world, TilePosc tilePos, Entity walker, CallbackInfo ci) {

		if (walker instanceof Player && PlayerScale.isSmall((Player) walker)) {
			ci.cancel();
		}
	}

	@Redirect(
		method = "onEntityWalkedOn",
		at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I")
	)
	private int shapesnsizes$bigFeetAlwaysTrample(Random random, int bound, World world, TilePosc tilePos, Entity walker) {

		if (walker instanceof Player && Trample.treadsHeavily((Player) walker)) {
			return 0;
		}
		return random.nextInt(bound);
	}
}

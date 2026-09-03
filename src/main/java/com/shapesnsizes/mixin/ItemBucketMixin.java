package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemBucket.class, remap = false)
public class ItemBucketMixin {
	@Redirect(
		method = "onUse",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F")
	)
	private float shapesnsizes$scaledReach(Gamemode gamemode, ItemStack stack, World world, Player player) {
		return gamemode.getBlockReachDistance() * PlayerScale.abilityFactor(player);
	}
}

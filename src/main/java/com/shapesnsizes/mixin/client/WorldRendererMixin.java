package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Shadow public Minecraft mc;

	@Redirect(
		method = "getMouseOver",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F")
	)
	private float shapesnsizes$blockReach(Gamemode gamemode) {
		return gamemode.getBlockReachDistance() * PlayerScale.abilityFactor(this.mc.thePlayer);
	}

	@Redirect(
		method = "getMouseOver",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getEntityReachDistance()F")
	)
	private float shapesnsizes$entityReach(Gamemode gamemode) {
		return gamemode.getEntityReachDistance() * PlayerScale.abilityFactor(this.mc.thePlayer);
	}

}

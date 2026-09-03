package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.hud.component.HudComponentOxygenBar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HudComponentOxygenBar.class, remap = false)
public class HudComponentOxygenBarMixin {

	@ModifyConstant(method = "render", constant = @Constant(doubleValue = 300.0))
	private double shapesnsizes$bubblesFitTheLungs(double vanillaAir) {
		Player viewer = shapesnsizes$viewer();
		return viewer == null ? vanillaAir : PlayerScale.airCapacity(viewer);
	}

	@Unique
	private static Player shapesnsizes$viewer() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc == null ? null : mc.thePlayer;
	}

	@Redirect(
		method = "isVisible",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;isUnderAcidOrWater()Z")
	)
	private boolean shapesnsizes$rainShowsBubbles(PlayerLocal player) {
		return player.isUnderAcidOrWater() || PlayerScale.drowningInRain(player);
	}
}

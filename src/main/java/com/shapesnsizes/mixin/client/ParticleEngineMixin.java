package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ParticleEngine;
import net.minecraft.client.render.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ParticleEngine.class, remap = false)
public class ParticleEngineMixin {
	private static final float MIN = 0.3f;
	private static final float MAX = 1.5f;

	private float shapesnsizes$chipSize() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null) return 1.0f;
		float scale = PlayerScale.get(mc.thePlayer);
		if (scale == PlayerScale.DEFAULT) return 1.0f;
		return Math.max(MIN, Math.min(MAX, (float) Math.sqrt(scale)));
	}

	@Redirect(
		method = "destroy",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ParticleEngine;add(Lnet/minecraft/client/render/particle/Particle;)V")
	)
	private void shapesnsizes$scaleBurst(ParticleEngine self, Particle particle) {
		float f = shapesnsizes$chipSize();
		self.add(f == 1.0f ? particle : particle.setScale(f));
	}

	@Redirect(
		method = "crack",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ParticleEngine;add(Lnet/minecraft/client/render/particle/Particle;)V")
	)
	private void shapesnsizes$scaleChip(ParticleEngine self, Particle particle) {
		float f = shapesnsizes$chipSize();
		self.add(f == 1.0f ? particle : particle.setScale(f));
	}
}

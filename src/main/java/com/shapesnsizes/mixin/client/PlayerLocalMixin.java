package com.shapesnsizes.mixin.client;

import com.shapesnsizes.Crawl;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.ShapesNSizes;
import com.shapesnsizes.client.CrawlInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.input.PlayerInput;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerLocal.class, remap = false)
public abstract class PlayerLocalMixin extends Player {
	protected PlayerLocalMixin(World world) {
		super(world);
	}

	@Redirect(
		method = "onLivingUpdate",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;checkAndPushInTile(DDD)Z")
	)
	private boolean shapesnsizes$probeInsideBody(PlayerLocal self, double x, double y, double z) {
		double probe = Math.min(0.5, this.bbHeight * 0.5);
		return ((PlayerLocalInvoker) self).shapesnsizes$checkAndPushInTile(x, this.bb.minY + probe, z);
	}

	@Redirect(
		method = "checkAndPushInTile",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;isSneaking()Z")
	)
	private boolean shapesnsizes$shortIfFitsInOneBlock(PlayerLocal self) {
		return self.isSneaking() || this.bbHeight <= 1.0f;
	}

	@Shadow public PlayerInput input;

	@Inject(method = "onLivingUpdate", at = @At("HEAD"))
	private void shapesnsizes$crawl(CallbackInfo ci) {
		if (this.input == null || !(this.input instanceof CrawlInput)) return;
		boolean want = ((CrawlInput) this.input).shapesnsizes$wantsCrawl() && !this.hasNoPhysics() && this.vehicle == null;
		boolean crawling = PlayerScale.isCrawling(this);
		if (want == crawling) return;
		if (!want && !this.boundsClear(this.getBoundsForState(1))) return;
		PlayerScale.setCrawling(this, want);
		if (this.world.isClientSide) {
			Minecraft.getMinecraft().getSendQueue().addToSendQueue(
				new PacketUpdatePlayerState(want ? Crawl.STATE_CRAWL : Crawl.STATE_UNCRAWL));
		}
	}

	@Inject(method = "getFovModifier", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$undoSizeFov(CallbackInfoReturnable<Float> cir) {
		float f = PlayerScale.speedFactor(this);
		if (f == 1.0f || this.baseSpeed == 0.0f) return;
		float scaled = (this.speed / this.baseSpeed + 1.0f) / 2.0f;
		float unscaled = (this.speed / f / this.baseSpeed + 1.0f) / 2.0f;
		if (scaled == 0.0f) return;
		cir.setReturnValue(cir.getReturnValueF() / scaled * unscaled);
	}
}

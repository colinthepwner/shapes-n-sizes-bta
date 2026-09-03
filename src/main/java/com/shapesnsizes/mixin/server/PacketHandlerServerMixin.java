package com.shapesnsizes.mixin.server;

import com.shapesnsizes.Crawl;
import com.shapesnsizes.PlayerScale;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PacketHandlerServer.class, remap = false)
public class PacketHandlerServerMixin {
	@Shadow private PlayerServer playerEntity;

	@Inject(method = "handleUpdatePlayerState", at = @At("HEAD"))
	private void shapesnsizes$crawlState(PacketUpdatePlayerState packet, CallbackInfo ci) {
		if (packet.state == Crawl.STATE_CRAWL) PlayerScale.setCrawling(this.playerEntity, true);
		else if (packet.state == Crawl.STATE_UNCRAWL) PlayerScale.setCrawling(this.playerEntity, false);
	}

	@Redirect(
		method = "handlePlayerAction",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F")
	)
	private float shapesnsizes$digReach(Gamemode gamemode) {
		return gamemode.getBlockReachDistance() * PlayerScale.abilityFactor(this.playerEntity);
	}

	@Redirect(
		method = "handleUseOrPlaceItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F")
	)
	private float shapesnsizes$useReach(Gamemode gamemode) {
		return gamemode.getBlockReachDistance() * PlayerScale.abilityFactor(this.playerEntity);
	}

	@Redirect(
		method = "handleEntityInteract",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getEntityReachDistance()F")
	)
	private float shapesnsizes$entityReach(Gamemode gamemode) {
		return gamemode.getEntityReachDistance() * PlayerScale.abilityFactor(this.playerEntity);
	}
}

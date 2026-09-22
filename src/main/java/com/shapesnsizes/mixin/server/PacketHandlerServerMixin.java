package com.shapesnsizes.mixin.server;

import com.shapesnsizes.Crawl;
import com.shapesnsizes.Cursor;
import com.shapesnsizes.ModVersion;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.ShapesConfig;
import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import org.joml.Vector3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PacketHandlerServer.class, remap = false)
public abstract class PacketHandlerServerMixin {
	@Shadow private PlayerServer playerEntity;

	@Shadow public abstract void kickPlayer(String reason);

	@Shadow public boolean connectionClosed;

	@Unique private int shapesnsizes$sinceJoin = 0;

	@Unique private boolean shapesnsizes$answered = false;

	@Unique private static final int HANDSHAKE_GRACE = 100;

	@Inject(method = "handlePackets", at = @At("TAIL"))
	private void shapesnsizes$awaitVersion(CallbackInfo ci) {
		if (this.shapesnsizes$answered || !ShapesConfig.requiresClientMod()) return;

		if (this.connectionClosed || this.playerEntity == null) return;
		if (++this.shapesnsizes$sinceJoin < HANDSHAKE_GRACE) return;
		this.shapesnsizes$answered = true;
		ShapesNSizes.LOGGER.info("{} joined without Shapes n Sizes; disconnecting them.",
			this.playerEntity == null ? "A client" : this.playerEntity.username);
		this.kickPlayer(ModVersion.KICK_MISSING);
	}

	@Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$checkVersion(PacketCustomPayload packet, CallbackInfo ci) {
		if (!ModVersion.CHANNEL.equals(packet.channel)) return;
		this.shapesnsizes$answered = true;
		String theirs = ModVersion.read(packet);
		String ours = ModVersion.get();
		if (ours.equals(theirs)) return;

		if (this.connectionClosed || this.playerEntity == null) {
			ci.cancel();
			return;
		}
		ci.cancel();
		ShapesNSizes.LOGGER.info("{} is running Shapes n Sizes {}, but this server is on {}; disconnecting them.",
			this.playerEntity == null ? "A client" : this.playerEntity.username,
			theirs.isEmpty() ? "an unknown version" : theirs, ours);
		this.kickPlayer(ModVersion.KICK_MISMATCH + " (client " + (theirs.isEmpty() ? "?" : theirs) + ", server " + ours + ")");
	}

	@Inject(method = "handleUpdatePlayerState", at = @At("HEAD"))
	private void shapesnsizes$crawlState(PacketUpdatePlayerState packet, CallbackInfo ci) {
		if (packet.state == Crawl.STATE_CRAWL) PlayerScale.setCrawling(this.playerEntity, true);
		else if (packet.state == Crawl.STATE_UNCRAWL) PlayerScale.setCrawling(this.playerEntity, false);

		else if (packet.state == Cursor.STATE_ON) PlayerScale.setCursorOn(this.playerEntity, true);
		else if (packet.state == Cursor.STATE_OFF) PlayerScale.setCursorOn(this.playerEntity, false);
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

	@Redirect(
		method = "handlePlayerAction",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/entity/player/PlayerServer;y:D",
			opcode = Opcodes.GETFIELD
		)
	)
	private double shapesnsizes$digFromTheEye(PlayerServer player) {
		return player.y + player.getHeadHeight();
	}

	@Redirect(
		method = "handleUseOrPlaceItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/player/PlayerServer;distanceToSqr(DDD)D")
	)
	private double shapesnsizes$placeFromTheEye(PlayerServer player, double x, double y, double z) {
		double dx = player.x - x;
		double dy = player.y + player.getHeadHeight() - y;
		double dz = player.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	@ModifyConstant(method = "handleEntityInteract", constant = @Constant(doubleValue = 8.0))
	private double shapesnsizes$seeAsFarAsTheyReach(double blocks) {
		return blocks * PlayerScale.abilityFactor(this.playerEntity);
	}

	@Redirect(
		method = "handleEntityInteract",
		at = @At(value = "INVOKE", target = "Lorg/joml/Vector3d;add(DDD)Lorg/joml/Vector3d;")
	)
	private Vector3d shapesnsizes$aimFromTheEye(Vector3d look, double x, double y, double z) {
		Entity player = this.playerEntity;
		return look.add(x, y + player.getHeadHeight(), z);
	}
}

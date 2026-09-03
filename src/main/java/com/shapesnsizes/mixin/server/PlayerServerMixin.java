package com.shapesnsizes.mixin.server;

import com.shapesnsizes.RideSync;
import com.shapesnsizes.SizeTicker;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketSetRiding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerServer.class, remap = false)
public class PlayerServerMixin implements RideSync {
	@Inject(method = "tick", at = @At("TAIL"))
	private void shapesnsizes$sizeTickOnServer(CallbackInfo ci) {
		((SizeTicker) this).shapesnsizes$sizeTick();
	}

	@Override
	public void shapesnsizes$syncRiding() {
		PlayerServer self = (PlayerServer) (Object) this;
		if (self.world == null || self.world.isClientSide) return;
		MinecraftServer server = MinecraftServer.getInstance();
		if (server == null) return;
		Entity mount = self.vehicle instanceof Entity ? (Entity) self.vehicle : null;
		Packet packet = new PacketSetRiding(self, mount);
		server.getEntityTracker(self.world.dimension.id)
			.sendPacketToTrackedPlayersAndTrackedEntity(self, packet);
	}
}

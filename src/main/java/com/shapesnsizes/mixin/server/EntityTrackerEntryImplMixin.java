package com.shapesnsizes.mixin.server;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketSetEntityData;
import net.minecraft.server.entity.EntityTrackerEntryImpl;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(value = EntityTrackerEntryImpl.class, remap = false)
public class EntityTrackerEntryImplMixin {
	@Shadow
	public Entity trackedEntity;

	@Redirect(
		method = "updatePlayerEntity",
		at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z")
	)
	private boolean shapesnsizes$sendSizeOnFirstSight(Set<PlayerServer> watchers, Object watcher) {
		boolean added = watchers.add((PlayerServer) watcher);
		if (added && this.trackedEntity instanceof Player && watcher instanceof PlayerServer) {
			PlayerServer viewer = (PlayerServer) watcher;
			if (viewer.playerNetServerHandler != null) {

				viewer.playerNetServerHandler.sendPacket(
					new PacketSetEntityData(this.trackedEntity.id, this.trackedEntity.getEntityData()));
			}
		}
		return added;
	}

}

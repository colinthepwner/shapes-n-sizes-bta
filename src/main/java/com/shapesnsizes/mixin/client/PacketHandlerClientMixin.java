package com.shapesnsizes.mixin.client;

import com.shapesnsizes.ModVersion;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.packet.PacketLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerClient.class, remap = false)
public class PacketHandlerClientMixin {
	@Inject(method = "handleLogin", at = @At("TAIL"))
	private void shapesnsizes$announceVersion(PacketLogin packetLogin, CallbackInfo ci) {
		((PacketHandlerClient) (Object) this).addToSendQueue(ModVersion.hello());
	}
}

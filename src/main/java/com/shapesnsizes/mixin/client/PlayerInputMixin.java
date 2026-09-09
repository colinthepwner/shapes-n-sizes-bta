package com.shapesnsizes.mixin.client;

import com.shapesnsizes.Cursor;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.client.CrawlInput;
import com.shapesnsizes.client.ShapesNSizesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.PlayerInput;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerInput.class, remap = false)
public class PlayerInputMixin implements CrawlInput {
	@Unique private boolean shapesnsizes$crawl = false;

	@Inject(method = "keyEvent", at = @At("HEAD"))
	private void shapesnsizes$crawlKey(int keyCode, int mouseCode, boolean pressed, CallbackInfo ci) {
		PlayerInput self = (PlayerInput) (Object) this;
		if (self.mc.currentScreen != null) return;
		if (!ShapesNSizesClient.KEY_CRAWL.isKeyOrMouse(keyCode, mouseCode)) return;
		if (ShapesNSizesClient.CRAWL_TOGGLE.isTrue()) {
			if (pressed) this.shapesnsizes$crawl = !this.shapesnsizes$crawl;
		} else {
			this.shapesnsizes$crawl = pressed;
		}
	}

	@Inject(method = "keyEvent", at = @At("HEAD"))
	private void shapesnsizes$cursorKey(int keyCode, int mouseCode, boolean pressed, CallbackInfo ci) {
		if (!pressed) return;
		PlayerInput self = (PlayerInput) (Object) this;
		if (self.mc == null || self.mc.currentScreen != null) return;
		if (!ShapesNSizesClient.KEY_CURSOR.isKeyOrMouse(keyCode, mouseCode)) return;
		Player player = self.mc.thePlayer;
		if (player == null) return;

		boolean want = !PlayerScale.isCursorOn(player);
		PlayerScale.setCursorOn(player, want);
		Minecraft.getMinecraft().getSendQueue().addToSendQueue(
			new PacketUpdatePlayerState(want ? Cursor.STATE_ON : Cursor.STATE_OFF));

		if (!want) {
			player.sendStatusMessage("§7Scaled cursor §foff§7.");
		} else if (!Cursor.allowed(player.world)) {
			player.sendStatusMessage("§7Scaled cursor on, but §fdoSizeScaledCursor§7 is off in this world.");
		} else if (Cursor.edge(player) < 2) {
			player.sendStatusMessage("§7Scaled cursor on. Nothing to do below §f2x§7 size.");
		} else {
			int e = Cursor.edge(player);
			player.sendStatusMessage("§7Scaled cursor §fon§7 — §f" + e + "x" + e + "§7, "
				+ (e * e) + " blocks a swing.");
		}
	}

	@Inject(method = "onGameFocused", at = @At("TAIL"))
	private void shapesnsizes$refocus(CallbackInfo ci) {
		if (!ShapesNSizesClient.CRAWL_TOGGLE.isTrue()) {
			this.shapesnsizes$crawl = ShapesNSizesClient.KEY_CRAWL.isPressed();
		}
	}

	@Override
	public boolean shapesnsizes$wantsCrawl() {
		return this.shapesnsizes$crawl;
	}
}

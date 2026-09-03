package com.shapesnsizes.mixin.client;

import com.shapesnsizes.client.CrawlInput;
import com.shapesnsizes.client.ShapesNSizesClient;
import net.minecraft.client.input.PlayerInput;
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

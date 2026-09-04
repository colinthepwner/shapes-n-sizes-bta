package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.client.PreviewRender;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.BlocksContainer;
import net.minecraft.core.world.pos.TilePos;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.useless.dragonfly.models.entity.BoneTransform;
import org.useless.dragonfly.models.entity.StaticEntityModel;

@Mixin(value = MobRendererPlayer.class, remap = false)
public class MobRendererPlayerMixin {
	@Unique private BlocksContainer shapesnsizes$algaeContainer = null;

	@Inject(
		method = "preRenderTransform(Lnet/minecraft/core/entity/player/Player;DDDFF)V",
		at = @At("TAIL")
	)
	private void shapesnsizes$scaleModel(Player entity, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		float s = PlayerScale.get(entity);

		if (PreviewRender.active) s = PreviewRender.portraitScale(s);
		if (s != PlayerScale.DEFAULT) {
			GLRenderer.modelM4f().scale(s, s, s);
		}
		if (PlayerScale.isCrawling(entity) && !entity.isPlayerSleeping()) {

			GLRenderer.modelM4f().translate(0.0f, 3.0f, 0.0f);
			GLRenderer.modelM4f().rotateX(-CRAWL_TIP);
			GLRenderer.modelM4f().translate(0.0f, -12.0f, 0.0f);
		}
	}

	@ModifyConstant(method = "renderSpecials", constant = @Constant(floatValue = 2.3f))
	private float shapesnsizes$crouchedLabelHeight(float vanilla, TessellatorGeneral t, Player entity, double x, double y, double z) {
		float scale = PlayerScale.get(entity);
		if (scale == PlayerScale.DEFAULT) return vanilla;
		return entity.bbHeight + LABEL_GAP * scale;
	}

	@ModifyConstant(method = "renderSpecials", constant = {
		@Constant(floatValue = 0.026666671f),
		@Constant(floatValue = -0.026666671f)
	})
	private float shapesnsizes$crouchedLabelTextSize(float vanilla, TessellatorGeneral t, Player entity,
			double x, double y, double z) {
		return vanilla * PlayerScale.labelScale(entity);
	}

	@Unique
	private static final float LABEL_GAP = 0.8f;

	@Unique
	private static final float CRAWL_TIP = (float) (Math.PI / 2.0);

	@Inject(
		method = "setupAnimations(Lnet/minecraft/core/entity/player/Player;Lorg/useless/dragonfly/models/entity/StaticEntityModel;FI)Lorg/useless/dragonfly/models/entity/StaticEntityModel;",
		at = @At("RETURN")
	)
	private void shapesnsizes$crawlPose(Player entity, StaticEntityModel model, float partialTick, int layer,
			CallbackInfoReturnable<StaticEntityModel> cir) {
		StaticEntityModel posed = cir.getReturnValue();
		if (posed == null || entity == null) return;
		if (!PlayerScale.isCrawling(entity) || entity.isPlayerSleeping()) return;

		BoneTransform head = posed.getTransform("head");
		if (head != null) head.rotX -= CRAWL_TIP;
		BoneTransform right = posed.getTransform("rightArm");
		if (right != null) right.rotX += Math.PI;
		BoneTransform left = posed.getTransform("leftArm");
		if (left != null) left.rotX += Math.PI;
	}

	@Inject(
		method = "setupAnimations(Lnet/minecraft/core/entity/player/Player;Lorg/useless/dragonfly/models/entity/StaticEntityModel;FI)Lorg/useless/dragonfly/models/entity/StaticEntityModel;",
		at = @At("RETURN")
	)
	private void shapesnsizes$glidePose(Player entity, StaticEntityModel model, float partialTick, int layer,
			CallbackInfoReturnable<StaticEntityModel> cir) {
		StaticEntityModel posed = cir.getReturnValue();
		if (posed == null || entity == null) return;
		if (entity.onGround || entity.isPlayerSleeping() || PlayerScale.isCrawling(entity)) return;
		if (!PlayerScale.isGliding(entity)) return;

		double roll = Math.sin((entity.tickCount + partialTick) * GLIDE_ROLL_RATE) * GLIDE_ROLL;

		BoneTransform right = posed.getTransform("rightArm");
		if (right != null) {
			right.rotX = -GLIDE_ARM_FORWARD;
			right.rotZ = -GLIDE_ARM_SPREAD + roll;
		}
		BoneTransform left = posed.getTransform("leftArm");
		if (left != null) {
			left.rotX = -GLIDE_ARM_FORWARD;
			left.rotZ = GLIDE_ARM_SPREAD + roll;
		}

		BoneTransform rightLeg = posed.getTransform("rightLeg");
		if (rightLeg != null) rightLeg.rotX = GLIDE_LEG_TRAIL;
		BoneTransform leftLeg = posed.getTransform("leftLeg");
		if (leftLeg != null) leftLeg.rotX = GLIDE_LEG_TRAIL;
	}

	@Unique private static final double GLIDE_ARM_SPREAD = Math.toRadians(75.0);

	@Unique private static final double GLIDE_ARM_FORWARD = Math.toRadians(20.0);

	@Unique private static final double GLIDE_LEG_TRAIL = Math.toRadians(12.0);

	@Unique private static final double GLIDE_ROLL = Math.toRadians(7.0);
	@Unique private static final double GLIDE_ROLL_RATE = 0.12;

	@Inject(
		method = "getShadowSize(Lnet/minecraft/core/entity/player/Player;)F",
		at = @At("RETURN"),
		cancellable = true
	)
	private void shapesnsizes$scaleShadow(Player entity, CallbackInfoReturnable<Float> cir) {
		float s = PlayerScale.get(entity);
		if (s != PlayerScale.DEFAULT) {
			cir.setReturnValue(cir.getReturnValueF() * s);
		}
	}

	@Inject(
		method = "renderAdditional(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/player/Player;F)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/MobRendererPlayer;drawHeldObject(Lnet/minecraft/core/entity/player/Player;F)V")
	)
	private void shapesnsizes$worldSizedCarriedBlock(TessellatorGeneral tessellator, Player player, float partialTick, CallbackInfo ci) {
		float s = PlayerScale.get(player);
		if (s != PlayerScale.DEFAULT) {
			float inv = 1.0f / s;
			GLRenderer.modelM4f().scale(inv, inv, inv);
		}
	}

	@Inject(
		method = "renderSpecials(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/player/Player;DDD)V",
		at = @At("HEAD")
	)
	private void shapesnsizes$drawAlgae(TessellatorGeneral tessellator, Player player, double x, double y, double z, CallbackInfo ci) {
		if (!PlayerScale.canWaterWalk(player) || player.world == null) return;
		if (PlayerScale.waterUnderfoot(player) == null) return;
		if (this.shapesnsizes$algaeContainer == null || this.shapesnsizes$algaeContainer.world != player.world) {
			this.shapesnsizes$algaeContainer = new BlocksContainer(player.world);
		}
		BlocksContainer container = this.shapesnsizes$algaeContainer;

		GLRenderer.pushFrame();
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.setLightmapCoord2f(15.0f, 15.0f);
		GLRenderer.modelM4f().translate((float) x, (float) y, (float) z);
		GLRenderer.setShader(Shaders.ITEM);
		TextureRegistry.worldAtlas.bind();
		GL41.glActiveTexture(33986);
		TextureRegistry.worldAtlas.layerTextureMap.get("emissive").bind();
		GL41.glActiveTexture(33987);
		TextureRegistry.worldAtlas.layerTextureMap.get("maskColor").bind();
		GL41.glActiveTexture(33984);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.disableState(State.CULL_FACE);

		TilePos blockPos = new TilePos(player);
		tessellator.startDrawingQuads();

		tessellator.setTranslation((double) (-blockPos.x) - 0.5, (double) (-blockPos.y) - 0.02, (double) (-blockPos.z) - 0.5);
		container.setLightReferenceEntity(player);
		container.setBlock(blockPos.x, blockPos.y, blockPos.z, Blocks.ALGAE.id(), 0, null);
		((BlockModel) BlockModelDispatcher.getInstance().getDispatch(Blocks.ALGAE)).renderNoCulling(tessellator, container, blockPos);
		container.setLightReferenceEntity(null);
		container.clear();
		tessellator.setTranslation(0.0, 0.0, 0.0);
		tessellator.draw();

		GLRenderer.enableState(State.CULL_FACE);
		GLRenderer.popFrame();
	}
}

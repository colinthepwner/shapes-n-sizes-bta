package com.shapesnsizes.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.world.World;
import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin {
	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.25))
	private double shapesnsizes$stuckHorizontal(double drag) {
		return PlayerScale.relieveDrag((Entity) (Object) this, drag);
	}

	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.05, ordinal = 0))
	private double shapesnsizes$cobwebVertical(double drag) {
		return PlayerScale.relieveDrag((Entity) (Object) this, drag);
	}

	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.6))
	private double shapesnsizes$strideLength(double perBlock) {
		Entity self = (Entity) (Object) this;
		if (!(self instanceof Player)) return perBlock;
		float scale = PlayerScale.get((Player) self);
		return scale == PlayerScale.DEFAULT ? perBlock : perBlock / scale;
	}
}

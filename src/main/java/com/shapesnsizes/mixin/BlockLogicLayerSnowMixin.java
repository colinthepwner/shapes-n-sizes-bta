package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLayerSnow;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogicLayerSnow.class, remap = false)
public abstract class BlockLogicLayerSnowMixin extends BlockLogic {
	protected BlockLogicLayerSnowMixin(Block<?> block, Material material) {
		super(block, material);
	}

	private static final double BASE_DRAG = 0.75;

	@Override
	public void onEntityCollision(World world, TilePosc tilePos, Entity entity) {
		super.onEntityCollision(world, tilePos, entity);
		double drag = PlayerScale.thickenDrag(entity, BASE_DRAG);
		if (drag == 1.0) return;
		entity.xd *= drag;
		entity.zd *= drag;
	}
}

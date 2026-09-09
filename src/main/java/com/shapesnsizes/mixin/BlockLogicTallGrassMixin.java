package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicTallGrass;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogicTallGrass.class, remap = false)
public abstract class BlockLogicTallGrassMixin extends BlockLogic {
	protected BlockLogicTallGrassMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onEntityCollision(World world, TilePosc tilePos, Entity entity) {
		super.onEntityCollision(world, tilePos, entity);
		double drag = PlayerScale.undergrowthDrag(entity);
		if (drag == 1.0) return;
		entity.xd *= drag;
		entity.zd *= drag;
	}
}

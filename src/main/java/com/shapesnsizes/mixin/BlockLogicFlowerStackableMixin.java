package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlowerStackable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogicFlowerStackable.class, remap = false)
public abstract class BlockLogicFlowerStackableMixin extends BlockLogic {
	protected BlockLogicFlowerStackableMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onEntityCollision(World world, TilePosc tilePos, Entity entity) {
		super.onEntityCollision(world, tilePos, entity);
		if (world.isClientSide || !(entity instanceof Player)) return;
		if (!PlayerScale.isTiny((Player) entity)) return;
		if (world.getBlockType(tilePos) != Blocks.FLOWER_RED) return;
		entity.hurt(null, 1, DamageType.COMBAT);
	}
}

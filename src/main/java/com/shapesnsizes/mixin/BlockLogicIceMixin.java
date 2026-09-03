package com.shapesnsizes.mixin;

import com.shapesnsizes.Trample;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicIce;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogicIce.class, remap = false)
public abstract class BlockLogicIceMixin extends BlockLogic {
	protected BlockLogicIceMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onEntityWalkedOn(World world, TilePosc tilePos, Entity walker) {
		super.onEntityWalkedOn(world, tilePos, walker);
		if (world.isClientSide || !(walker instanceof Player)) return;
		if (world.getBlockType(tilePos) != Blocks.ICE) return;
		Trample.crackIce((Player) walker);
	}
}

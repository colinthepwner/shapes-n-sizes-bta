package com.shapesnsizes.mixin;

import com.shapesnsizes.Foliage;
import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLayerSnow;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
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

		if (entity instanceof Player && PlayerScale.isSnowWalker((Player) entity)) return;
		double drag = PlayerScale.thickenDrag(entity, BASE_DRAG);
		if (drag == 1.0) return;
		entity.xd *= drag;
		entity.zd *= drag;
	}

	@Override
	public boolean collidesWithEntity(Entity entity, World world, TilePosc tilePos) {
		if (entity instanceof Player && Foliage.wadesThroughLeaves((Player) entity)) {
			Block<?> under = world.getBlockType(
				new TilePos(tilePos.x(), tilePos.y() - 1, tilePos.z()));
			if (under != null && under.getLogic() instanceof BlockLogicLeavesBase) return false;
		}
		return super.collidesWithEntity(entity, world, tilePos);
	}
}

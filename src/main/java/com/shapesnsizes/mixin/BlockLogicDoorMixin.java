package com.shapesnsizes.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLogicDoor.class, remap = false)
public abstract class BlockLogicDoorMixin {

	@Unique private static final int MASK_OPENED = 4;

	@Unique private static final int MASK_HINGE = 8;

	@Unique private static final int MASK_ROTATION = 3;

	@Inject(method = "onInteracted", at = @At("RETURN"))
	private void shapesnsizes$swingPartner(World world, TilePosc tilePos, @Nullable Player player,
			@Nullable Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		BlockLogicDoor self = (BlockLogicDoor) (Object) this;
		if (!cir.getReturnValueZ() || self.isTop || world.isClientSide) return;
		shapesnsizes$matchPartner(world, tilePos);
	}

	@Inject(method = "onPoweredBlockChange", at = @At("RETURN"))
	private void shapesnsizes$powerPartner(World world, TilePosc tilePos, boolean isPowered, CallbackInfo ci) {
		BlockLogicDoor self = (BlockLogicDoor) (Object) this;
		if (self.isTop || world.isClientSide) return;
		shapesnsizes$matchPartner(world, tilePos);
	}

	@Unique
	private static int placementFacing(int data) {
		return (data & MASK_HINGE) != 0 ? (((data & MASK_ROTATION) + 1) & MASK_ROTATION) : (data & MASK_ROTATION);
	}

	@Unique
	private void shapesnsizes$matchPartner(World world, TilePosc tilePos) {
		BlockLogicDoor self = (BlockLogicDoor) (Object) this;
		if (self.getMaterial() == Materials.METAL || self.getMaterial() == Materials.STEEL) return;

		int data = world.getBlockData(tilePos);
		boolean open = BlockLogicDoor.isOpen(data);
		int facing = placementFacing(data);

		boolean alongX = facing == 0 || facing == 2;
		TilePos beside = new TilePos();
		TilePos above = new TilePos();

		for (int step = -1; step <= 1; step += 2) {
			beside.set(tilePos.x() + (alongX ? step : 0), tilePos.y(), tilePos.z() + (alongX ? 0 : step));
			BlockLogicDoor partner = world.getBlockLogic(beside, BlockLogicDoor.class);
			if (partner == null || partner.isTop) continue;

			int partnerData = world.getBlockData(beside);
			if (placementFacing(partnerData) != facing) continue;

			if (((partnerData ^ data) & MASK_HINGE) == 0) continue;

			if (BlockLogicDoor.isOpen(partnerData) == open) return;

			int moved = partnerData ^ MASK_OPENED;
			Block<?> leaf = world.getBlockType(beside);
			above.set(beside.x, beside.y + 1, beside.z);
			Block<?> top = world.getBlockType(above);
			if (top != null && top.getLogic() instanceof BlockLogicDoor) {
				world.setBlockData(above, moved);
				world.markBlockNeedsUpdate(above);
			}
			world.setBlockData(beside, moved);
			world.markBlockNeedsUpdate(beside);
			world.notifyBlocksInCapsuleOfNeighborChange(Direction.UP, beside, leaf);
			world.playBlockEvent(beside, 1003, 0);
			return;
		}
	}
}

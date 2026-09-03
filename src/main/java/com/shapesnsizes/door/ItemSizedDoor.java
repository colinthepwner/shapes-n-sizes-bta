package com.shapesnsizes.door;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.support.PartialSupport;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IPlaceable;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSizedDoor extends Item implements IPlaceable {
	private final Block<?>[] segments;

	public ItemSizedDoor(@NotNull String name, @NotNull String namespaceId, int id, @NotNull Block<?>... segments) {
		super(name, namespaceId, id);
		this.segments = segments;
	}

	@Override
	public boolean onUseOnBlock(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		return this.placeOnBlock(selfStack, world, player, blockPos, side, xHit, yHit);
	}

	@Override
	public boolean shouldShiftOutOf(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		return IPlaceable.shouldShiftBlockOutOf(this.segments[0], world, blockPos);
	}

	@Override
	public boolean canPlaceDirectlyAtPosition(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		for (int i = 0; i < this.segments.length; ++i) {
			if (!world.canPlaceInsideBlock(new TilePos(blockPos.x(), blockPos.y() + i, blockPos.z()))) return false;
		}
		return true;
	}

	@Override
	public boolean placeWithoutShift(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		if (selfStack.stackSize <= 0) return false;
		if (!this.canPlaceDirectlyAtPosition(selfStack, world, player, blockPos, side, xHit, yHit)) return false;

		int meta = this.placementData(world, player, blockPos, side);
		BlockLogicSizedDoor bottom = (BlockLogicSizedDoor) this.segments[0].getLogic();
		if (!bottom.canPlaceAt(world, blockPos, meta)) return false;

		TilePos[] positions = new TilePos[this.segments.length];
		Block<?>[] previous = new Block<?>[this.segments.length];
		int[] previousData = new int[this.segments.length];
		for (int i = 0; i < this.segments.length; ++i) {
			positions[i] = new TilePos(blockPos.x(), blockPos.y() + i, blockPos.z());
			previous[i] = world.getBlockType(positions[i]);
			previousData[i] = world.getBlockData(positions[i]);
			world.setBlockTypeDataRaw(positions[i], this.segments[i], meta);
		}
		for (int i = 0; i < this.segments.length; ++i) {
			world.markBlockNeedsUpdate(positions[i]);
		}
		selfStack.consumeItem(player);
		for (int i = 0; i < this.segments.length; ++i) {
			if (previous[i] != null) previous[i].onRemoved(world, positions[i], previousData[i]);
		}
		for (int i = 0; i < this.segments.length; ++i) {
			if (player == null) {
				this.segments[i].onPlacedOnSide(world, positions[i], side, xHit, yHit);
			} else {
				this.segments[i].onPlacedByMob(world, positions[i], side, player, xHit, yHit);
			}
			this.segments[i].onPlacedByWorld(world, positions[i]);
		}
		return true;
	}

	private int placementData(World world, @Nullable Player player, TilePosc blockPos, Side side) {
		Direction leftDir;
		if (player != null) {
			leftDir = player.getHorizontalPlacementDirection(side).rotateY(1);
		} else {
			Direction dir = side.direction().opposite();
			if (!dir.isHorizontal()) dir = Direction.NORTH;
			leftDir = dir.rotateY(1);
		}
		int solidLeft = 0;
		int solidRight = 0;
		for (int i = 0; i < this.segments.length; ++i) {
			TilePos at = new TilePos(blockPos.x(), blockPos.y() + i, blockPos.z());
			if (this.isSupported(world, at, leftDir, false)) ++solidLeft;
			if (this.isSupported(world, at, leftDir.opposite(), true)) ++solidRight;
		}
		TilePos query = new TilePos();
		boolean doorLeft = this.isOwnDoor(world, blockPos.add(leftDir, query));
		boolean doorRight = this.isOwnDoor(world, blockPos.sub(leftDir, query));

		boolean mirrored;
		if (doorLeft && !doorRight) {
			mirrored = (world.getBlockData(blockPos.add(leftDir, new TilePos())) & BlockLogicSizedDoor.MASK_HINGE) == 0;
		} else if (solidLeft > 0 && side.direction() == leftDir.opposite()) {
			mirrored = false;
		} else {
			mirrored = solidRight > 0 && side.direction() == leftDir || solidRight > solidLeft;
		}
		int meta = leftDir.legacyIndex();
		if (mirrored) {
			meta = (meta - 1) & 3;
			meta |= 0xC;
		}
		return meta;
	}

	private boolean isOwnDoor(World world, TilePosc pos) {
		Block<?> at = world.getBlockType(pos);
		for (Block<?> segment : this.segments) {
			if (at == segment) return true;
		}
		return false;
	}

	private boolean isSupported(World world, TilePosc tilePos, Direction direction, boolean mirrored) {
		return world.getSupport(tilePos.add(direction, new TilePos()), direction.opposite().side())
			.canSupport(mirrored ? PartialSupport.INSTANCE.right() : PartialSupport.INSTANCE.left(), direction.side());
	}
}

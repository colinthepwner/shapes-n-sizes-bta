package com.shapesnsizes.door;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.function.Supplier;

public class BlockLogicSizedDoor extends BlockLogicDoor {

	public final int height;
	public final int index;

	public BlockLogicSizedDoor(@NotNull Block<?> block, @NotNull Material material, int height, int index,
							   @Nullable Supplier<@NotNull Item> droppedItem) {

		super(block, material, index > 0, false, droppedItem);
		this.height = height;
		this.index = index;

		this.setBlockBounds(0.0, -index, 0.0, 1.0, height - index, 1.0);
	}

	private boolean isBottom() {
		return this.index == 0;
	}

	private TilePos bottomOf(TilePosc pos) {
		return new TilePos(pos.x(), pos.y() - this.index, pos.z());
	}

	private TilePos[] segments(TilePosc anyPos) {
		TilePos bottom = this.bottomOf(anyPos);
		TilePos[] out = new TilePos[this.height];
		for (int i = 0; i < this.height; ++i) {
			out[i] = new TilePos(bottom.x, bottom.y + i, bottom.z);
		}
		return out;
	}

	private boolean isSegment(World world, TilePosc pos, int expectedIndex) {
		BlockLogicSizedDoor logic = world.getBlockLogic(pos, BlockLogicSizedDoor.class);
		return logic != null && logic.height == this.height && logic.index == expectedIndex;
	}

	private boolean isWhole(World world, TilePosc anyPos) {
		TilePos[] all = this.segments(anyPos);
		for (int i = 0; i < this.height; ++i) {
			if (!this.isSegment(world, all[i], i)) return false;
		}
		return true;
	}

	private void setDataOnAll(World world, TilePosc anyPos, int data) {
		for (TilePos pos : this.segments(anyPos)) {
			if (world.getBlockLogic(pos, BlockLogicSizedDoor.class) != null) {
				world.setBlockData(pos, data);
				world.markBlockNeedsUpdate(pos);
			}
		}
	}

	private void removeWhole(World world, TilePosc anyPos) {
		for (TilePos pos : this.segments(anyPos)) {
			if (world.getBlockLogic(pos, BlockLogicSizedDoor.class) != null) {
				world.setBlockTypeNotify(pos, Blocks.AIR);
			}
		}
	}

	@Override
	@NotNull
	public AABBdc getBoundsForRotation(int rotation, boolean drawingSelection) {
		float bottom = drawingSelection ? -this.index : 0.0f;
		float top = drawingSelection ? this.height - this.index : 1.0f;
		return switch (rotation) {
			case 0 -> new AABBd(0.0, bottom, 0.0, 1.0, top, 0.1875);
			case 1 -> new AABBd(0.8125, bottom, 0.0, 1.0, top, 1.0);
			case 2 -> new AABBd(0.0, bottom, 0.8125, 1.0, top, 1.0);
			case 3 -> new AABBd(0.0, bottom, 0.0, 0.1875, top, 1.0);
			default -> new AABBd(0.0, bottom, 0.0, 1.0, top, 1.0);
		};
	}

	@Override
	@NotNull
	public AABBdc getBoundsFromState(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		return this.getBoundsForRotation(this.getRotation(source.getBlockData(tilePos)), false);
	}

	@Override
	public void onPlacedOnSide(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, double xHit, double yHit) {

		if (this.isBottom() && world.hasNeighborSignal(tilePos)) {
			this.setDataOnAll(world, tilePos, world.getBlockData(tilePos) | MASK_OPENED);
			world.notifyBlocksInCapsuleOfNeighborChange(Direction.UP, tilePos, this.block);
			this.playDoorSound(world, tilePos, true);
		}
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @Nullable Player player, @Nullable Side side, double xHit, double yHit) {
		if (this.material == Materials.METAL) {
			return false;
		}
		if (!this.isBottom()) {
			TilePos bottom = this.bottomOf(tilePos);
			BlockLogicSizedDoor logic = world.getBlockLogic(bottom, BlockLogicSizedDoor.class);
			return logic != null && logic.onInteracted(world, bottom, player, side, xHit, yHit);
		}
		int data = world.getBlockData(tilePos);
		int toggled = data ^ MASK_OPENED;
		this.setDataOnAll(world, tilePos, toggled);
		this.swingPartner(world, tilePos, data);
		if (!this.isSupported(world, tilePos, data)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, toggled, null, null);
			this.removeWhole(world, tilePos);
		}
		world.notifyBlocksInCapsuleOfNeighborChange(Direction.UP, tilePos, this.block);
		this.playDoorSound(world, tilePos, (toggled & MASK_OPENED) != 0);
		return true;
	}

	private void playDoorSound(World world, TilePosc tilePos, boolean opening) {

		if (world.isClientSide) return;
		float pitch = (float) Math.pow(VANILLA_DOOR_HEIGHT / (double) this.height, PITCH_CURVE);
		pitch *= 0.95f + world.rand.nextFloat() * 0.1f;
		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS,
			tilePos.x() + 0.5, tilePos.y() + 0.5, tilePos.z() + 0.5,
			opening ? "random.door_open" : "random.door_close", 1.0f, pitch);
	}

	private static final double VANILLA_DOOR_HEIGHT = 2.0;

	private static final double PITCH_CURVE = 0.2;

	private void swingPartner(World world, TilePosc tilePos, int data) {

		int rotation = this.getRotation(data);
		boolean open = isOpenFor(data ^ MASK_OPENED);

		boolean alongX = rotation == 0 || rotation == 2;
		for (int step = -1; step <= 1; step += 2) {
			TilePos beside = new TilePos(
				tilePos.x() + (alongX ? step : 0), tilePos.y(), tilePos.z() + (alongX ? 0 : step));
			BlockLogicSizedDoor partner = world.getBlockLogic(beside, BlockLogicSizedDoor.class);
			if (partner == null || partner.height != this.height || partner.index != 0) continue;
			int partnerData = world.getBlockData(beside);
			if (partner.getRotation(partnerData) != rotation) continue;
			if (((partnerData ^ data) & MASK_HINGE) == 0) continue;

			if (isOpenFor(partnerData) == open) return;
			partner.setDataOnAll(world, beside, partnerData ^ MASK_OPENED);
			world.notifyBlocksInCapsuleOfNeighborChange(Direction.UP, beside, partner.block);
			return;
		}
	}

	private static boolean isOpenFor(int data) {
		return ((data & MASK_OPENED) != 0) != ((data & MASK_HINGE) != 0);
	}

	@Override
	public void onPoweredBlockChange(@NotNull World world, @NotNull TilePosc tilePos, boolean isPowered) {
		int data = world.getBlockData(tilePos);
		if ((data & MASK_HINGE) != 0) {
			isPowered = !isPowered;
		}
		if (!this.isBottom()) {
			TilePos bottom = this.bottomOf(tilePos);
			BlockLogicSizedDoor logic = world.getBlockLogic(bottom, BlockLogicSizedDoor.class);
			if (logic != null) logic.onPoweredBlockChange(world, bottom, isPowered);
			return;
		}
		boolean isOpen = (data & MASK_OPENED) != 0;
		if (isOpen == isPowered) return;
		TilePos[] all = this.segments(tilePos);
		for (TilePos pos : all) {
			if (world.getBlockLogic(pos, BlockLogicSizedDoor.class) != null) {
				world.setBlockDataNotify(pos, data ^ MASK_OPENED);
			}
		}
		world.markBlocksDirty(tilePos, all[this.height - 1]);
		this.playDoorSound(world, tilePos, (data & MASK_OPENED) == 0);
	}

	@Override
	public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
		int data = world.getBlockData(tilePos);
		if (!this.isBottom()) {

			TilePos bottom = this.bottomOf(tilePos);
			BlockLogicSizedDoor logic = world.getBlockLogic(bottom, BlockLogicSizedDoor.class);
			if (logic == null || logic.height != this.height) {
				world.setBlockTypeNotify(tilePos, Blocks.AIR);
				return;
			}
			world.getBlockType(bottom).onNeighborChanged(world, bottom, block);
			return;
		}
		boolean gone = false;
		if (!this.isWhole(world, tilePos)) {
			this.removeWhole(world, tilePos);
			gone = true;
		} else if (!this.isSupported(world, tilePos, data)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, data, null, null);
			this.removeWhole(world, tilePos);
			gone = true;
		}
		if (!gone && block.isSignalSource()) {

			boolean signal = false;
			for (TilePos pos : this.segments(tilePos)) {
				if (world.hasNeighborSignal(pos)) {
					signal = true;
					break;
				}
			}
			this.onPoweredBlockChange(world, tilePos, signal);
		}
	}

	@Override
	public void onRemoved(@NotNull World world, @NotNull TilePosc tilePos, int data) {

		for (TilePos pos : this.segments(tilePos)) {
			if (pos.x == tilePos.x() && pos.y == tilePos.y() && pos.z == tilePos.z()) continue;
			if (world.getBlockLogic(pos, BlockLogicSizedDoor.class) != null) {
				world.setBlockTypeNotify(pos, Blocks.AIR);
			}
		}
	}

	@Override
	public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		if (this.droppedItem == null) return null;
		return new ItemStack[]{new ItemStack(this.droppedItem.get())};
	}

	@Override
	public boolean canPlaceAt(@NotNull World world, @NotNull TilePosc tilePos, int data) {
		if (!this.isBottom()) return false;
		for (TilePos pos : this.segments(tilePos)) {
			if (!world.canPlaceInsideBlock(pos)) return false;
		}
		return this.isSupported(world, tilePos, data);
	}

	@Override
	public boolean canBePainted() {
		return false;
	}

	@Override
	public void setColor(@NotNull World world, @NotNull TilePosc tilePos, @NotNull DyeColor color) {

	}
}

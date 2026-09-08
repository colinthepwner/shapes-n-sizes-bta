package com.shapesnsizes.door;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.IPainted;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BlockLogicSizedDoorPainted extends BlockLogicSizedDoor implements IPainted {

	public static final int MASK_COLOR = 240;

	private final Supplier<Block<?>[]> plain;

	public BlockLogicSizedDoorPainted(@NotNull Block<?> block, @NotNull Material material, int height, int index,
									  @NotNull Object family, @NotNull Supplier<Block<?>[]> plain,
									  @Nullable Supplier<@NotNull Item> droppedItem) {
		super(block, material, height, index, family, droppedItem);
		this.plain = plain;
	}

	@NotNull
	@Override
	public DyeColor fromMetadata(int meta) {
		return DyeColor.colorFromBlockMeta(meta >> 4 & 15);
	}

	@Override
	public int toMetadata(@NotNull DyeColor color) {
		return color.blockMeta << 4;
	}

	@Override
	public int stripColorFromMetadata(int meta) {
		return meta & 15;
	}

	@Override
	public boolean canBePainted() {
		return true;
	}

	@Override
	public void setColor(@NotNull World world, @NotNull TilePosc tilePos, @NotNull DyeColor color) {
		int data = world.getBlockData(tilePos);
		this.setDataOnAll(world, tilePos, this.stripColorFromMetadata(data) | this.toMetadata(color));
		world.notifyBlockChange(tilePos, this.block);
	}

	@Override
	public void removeDye(@NotNull World world, @NotNull TilePosc tilePos) {
		Block<?>[] undyed = this.plain.get();
		if (undyed == null || undyed.length != this.height) return;
		TilePos bottom = new TilePos(tilePos.x(), tilePos.y() - this.index, tilePos.z());
		TilePos[] positions = new TilePos[this.height];
		for (int i = 0; i < this.height; ++i) {
			positions[i] = new TilePos(bottom.x, bottom.y + i, bottom.z);
		}
		int[] kept = new int[this.height];
		for (int i = 0; i < this.height; ++i) {
			kept[i] = this.stripColorFromMetadata(world.getBlockData(positions[i]));
		}
		for (int i = 0; i < this.height; ++i) {
			world.setBlockTypeDataRaw(positions[i], undyed[i], kept[i]);
		}
		for (int i = 0; i < this.height; ++i) {
			world.markBlockNeedsUpdate(positions[i]);
		}
		world.notifyBlockChange(bottom, undyed[0]);
	}

	@Override
	public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		if (this.droppedItem == null) return null;
		return new ItemStack[]{new ItemStack(this.droppedItem.get(), 1, this.fromMetadata(data).itemMeta)};
	}
}

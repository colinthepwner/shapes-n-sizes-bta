package com.shapesnsizes.client;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelSizedDoor<T extends BlockLogic> extends BlockModelGeneric<T> {
	private final StaticBlockModel left;
	private final StaticBlockModel leftOpen;
	private final StaticBlockModel right;
	private final StaticBlockModel rightOpen;

	public BlockModelSizedDoor(@NotNull Block<T> block, @NotNull String basekey, @NotNull String segment) {
		super(block, BlockModelDispatcher.loadDataModel(basekey + "/" + segment + "_left"));
		this.left = BlockModelDispatcher.loadDataModel(basekey + "/" + segment + "_left").asModel();
		this.leftOpen = BlockModelDispatcher.loadDataModel(basekey + "/" + segment + "_left_open").asModel();
		this.right = BlockModelDispatcher.loadDataModel(basekey + "/" + segment + "_right").asModel();
		this.rightOpen = BlockModelDispatcher.loadDataModel(basekey + "/" + segment + "_right_open").asModel();
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		int data = worldSource.getBlockData(tilePos);
		int rotation = data & 3;
		boolean isOpen = (data & 4) != 0;

		int turns = isOpen ? (3 - rotation) : (4 - rotation) & 3;
		return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, turns, 0, 0.0, 0.0, 0.0, false, cullFaces, overrideTexture);
	}

	@Override
	@NotNull
	public StaticBlockModel getModelFromData(int data) {
		boolean isLeft = (data & 8) != 0;
		boolean isOpen = (data & 4) != 0;
		if (isLeft) {
			return isOpen ? this.leftOpen : this.left;
		}
		return isOpen ? this.right : this.rightOpen;
	}
}

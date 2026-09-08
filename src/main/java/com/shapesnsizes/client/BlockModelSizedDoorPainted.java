package com.shapesnsizes.client;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelSizedDoorPainted<T extends BlockLogic> extends BlockModelSizedDoor<T> {
	private final StaticBlockModel[] left = new StaticBlockModel[16];
	private final StaticBlockModel[] leftOpen = new StaticBlockModel[16];
	private final StaticBlockModel[] right = new StaticBlockModel[16];
	private final StaticBlockModel[] rightOpen = new StaticBlockModel[16];

	public BlockModelSizedDoorPainted(@NotNull Block<T> block, @NotNull String basekey, @NotNull String segment) {

		super(block, basekey + "/" + DyeColor.WHITE.colorID, segment);
		for (DyeColor color : DyeColor.blockOrderedColors()) {
			String dir = basekey + "/" + color.colorID + "/" + segment;
			this.left[color.blockMeta] = BlockModelDispatcher.loadDataModel(dir + "_left").asModel();
			this.leftOpen[color.blockMeta] = BlockModelDispatcher.loadDataModel(dir + "_left_open").asModel();
			this.right[color.blockMeta] = BlockModelDispatcher.loadDataModel(dir + "_right").asModel();
			this.rightOpen[color.blockMeta] = BlockModelDispatcher.loadDataModel(dir + "_right_open").asModel();
		}
	}

	@NotNull
	@Override
	public StaticBlockModel getModelFromData(int data) {
		int color = data >> 4 & 15;
		boolean isLeft = (data & 8) != 0;
		boolean isOpen = (data & 4) != 0;
		if (isLeft) {
			return isOpen ? this.leftOpen[color] : this.left[color];
		}
		return isOpen ? this.right[color] : this.rightOpen[color];
	}
}

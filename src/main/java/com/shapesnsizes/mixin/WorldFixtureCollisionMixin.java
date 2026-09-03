package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicButton;
import net.minecraft.core.block.BlockLogicAlgae;
import net.minecraft.core.block.BlockLogicFlag;
import net.minecraft.core.block.BlockLogicLever;
import net.minecraft.core.block.BlockLogicTorch;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = World.class, remap = false)
public class WorldFixtureCollisionMixin {

	@Unique
	private static final float FIXTURE_SCALE = 0.25f;

	@Inject(method = "getCubes", at = @At("RETURN"))
	private void shapesnsizes$fixturesAreSolidToTheTiny(Entity entity, AABBdc aabb,
			CallbackInfoReturnable<List<AABBdc>> cir) {
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
		float scale = PlayerScale.get(player);

		if (scale > PlayerScale.SMALL) return;
		boolean fixtures = scale <= FIXTURE_SCALE;
		List<AABBdc> boxes = cir.getReturnValue();
		if (boxes == null) return;

		World world = (World) (Object) this;
		int minX = MathHelper.floor(aabb.minX());
		int maxX = MathHelper.floor(aabb.maxX() + 1.0);
		int minY = MathHelper.floor(aabb.minY());
		int maxY = MathHelper.floor(aabb.maxY() + 1.0);
		int minZ = MathHelper.floor(aabb.minZ());
		int maxZ = MathHelper.floor(aabb.maxZ() + 1.0);

		TilePos pos = new TilePos();
		for (int x = minX - 1; x <= maxX; ++x) {
			for (int y = minY - 1; y <= maxY; ++y) {
				for (int z = minZ - 1; z <= maxZ; ++z) {
					pos.set(x, y, z);
					Block<?> block = world.getBlockType(pos);
					if (block == null || block == Blocks.AIR) continue;
					boolean algae = block.getLogic() instanceof BlockLogicAlgae;
					if (!algae && !(fixtures && shapesnsizes$isFixture(block))) continue;
					AABBdc box = block.getSelectionAABB(world, pos);
					if (box == null || !(aabb instanceof org.joml.primitives.AABBd)) continue;
					if (!box.intersectsAABB((org.joml.primitives.AABBd) aabb)) continue;
					boxes.add(box);
				}
			}
		}
	}

	@Unique
	private static boolean shapesnsizes$isFixture(Block<?> block) {
		Object logic = block.getLogic();
		return logic instanceof BlockLogicButton
			|| logic instanceof BlockLogicLever
			|| logic instanceof BlockLogicTorch
			|| logic instanceof BlockLogicFlag;
	}
}

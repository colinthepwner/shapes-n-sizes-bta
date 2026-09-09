package com.shapesnsizes.item;

import java.util.function.BiFunction;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBrownieCharred extends Item {
	private final BiFunction<World, Player, ProjectileCharredBrownie> projectile;

	public ItemBrownieCharred(String name, String namespaceId, int id,
							  BiFunction<World, Player, ProjectileCharredBrownie> projectile) {
		super(name, namespaceId, id);
		this.projectile = projectile;
		this.maxStackSize = 16;
	}

	@Nullable
	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		selfStack.consumeItem(player);
		world.playSoundAtEntity(player, player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
		if (!world.isClientSide) {
			world.entityJoinedWorld(this.projectile.apply(world, player));
		}
		return selfStack;
	}
}

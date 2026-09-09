package com.shapesnsizes.item;

import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class ProjectileCharredBrownie extends Projectile {
	protected ProjectileCharredBrownie(@NotNull World world) {
		super(world);
	}

	protected ProjectileCharredBrownie(@NotNull World world, @NotNull Mob owner) {
		super(world, owner);
	}

	protected ProjectileCharredBrownie(@NotNull World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@NotNull
	protected abstract Item asItem();

	@Override
	public void initProjectile() {
		super.initProjectile();
		this.damage = 1;
	}

	@Override
	public void onHit(@NotNull HitResult hitResult) {
		if (hitResult instanceof HitResult.Entity hitEntity) {
			hitEntity.entity.hurt(this.owner, this.damage, DamageType.COMBAT);
		}
		if (!this.world.isClientSide) {
			this.world.entityJoinedWorld(new EntityItem(this.world, this.x, this.y, this.z, new ItemStack(this.asItem(), 1)));
		}
		this.remove();
	}

	public static class Growing extends ProjectileCharredBrownie {
		public Growing(@NotNull World world) {
			super(world);
		}

		public Growing(@NotNull World world, @NotNull Mob owner) {
			super(world, owner);
		}

		public Growing(@NotNull World world, double x, double y, double z) {
			super(world, x, y, z);
		}

		@NotNull
		@Override
		protected Item asItem() {
			return ShapesItems.BROWNIE_BIG_CHARRED;
		}
	}

	public static class Shrinking extends ProjectileCharredBrownie {
		public Shrinking(@NotNull World world) {
			super(world);
		}

		public Shrinking(@NotNull World world, @NotNull Mob owner) {
			super(world, owner);
		}

		public Shrinking(@NotNull World world, double x, double y, double z) {
			super(world, x, y, z);
		}

		@NotNull
		@Override
		protected Item asItem() {
			return ShapesItems.BROWNIE_SMALL_CHARRED;
		}
	}
}

package com.shapesnsizes.item;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.item.Item;
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

	protected abstract float sizeChange();

	@NotNull
	protected abstract Item asItem();

	@Override
	public void initProjectile() {
		super.initProjectile();

		this.damage = 0;

		this.modelItem = this.asItem();
	}

	@Override
	public void onHit(@NotNull HitResult hitResult) {
		if (hitResult instanceof HitResult.Entity hitEntity
			&& hitEntity.entity instanceof Player hitPlayer
			&& !this.world.isClientSide) {
			PlayerScale.addBonus(hitPlayer, this.sizeChange());
		}

		if (this.modelItem != null) {
			for (int i = 0; i < 8; ++i) {
				this.world.spawnParticle("item", this.x, this.y, this.z, 0.0, 0.0, 0.0,
					this.modelItem.id, false);
			}
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

		@Override
		protected float sizeChange() {
			return ShapesItems.CHARRED_STEP;
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

		@Override
		protected float sizeChange() {
			return -ShapesItems.CHARRED_STEP;
		}

		@NotNull
		@Override
		protected Item asItem() {
			return ShapesItems.BROWNIE_SMALL_CHARRED;
		}
	}
}

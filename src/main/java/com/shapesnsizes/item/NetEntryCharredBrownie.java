package com.shapesnsizes.item;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NetEntryCharredBrownie<T extends ProjectileCharredBrownie>
		implements IVehicleEntry<T>, ITrackedEntry<T> {

	@Override
	public int getTrackingDistance() {
		return 64;
	}

	@Override
	public int getMovementPacketDelay() {
		return 10;
	}

	@Override
	public boolean sendMotionUpdates() {
		return true;
	}

	@Override
	public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, T trackedObject) {
	}

	@Override
	public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, T trackedObject) {
		return new PacketAddEntity(trackedObject);
	}

	public static final class Growing extends NetEntryCharredBrownie<ProjectileCharredBrownie.Growing> {
		public static final int TYPE = 3550;

		@NotNull
		@Override
		public Class<ProjectileCharredBrownie.Growing> getAppliedClass() {
			return ProjectileCharredBrownie.Growing.class;
		}

		@Override
		public Entity getEntity(World world, double x, double y, double z, int metadata, boolean hasVelocity,
								double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag) {
			return new ProjectileCharredBrownie.Growing(world, x, y, z);
		}
	}

	public static final class Shrinking extends NetEntryCharredBrownie<ProjectileCharredBrownie.Shrinking> {
		public static final int TYPE = 3551;

		@NotNull
		@Override
		public Class<ProjectileCharredBrownie.Shrinking> getAppliedClass() {
			return ProjectileCharredBrownie.Shrinking.class;
		}

		@Override
		public Entity getEntity(World world, double x, double y, double z, int metadata, boolean hasVelocity,
								double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag) {
			return new ProjectileCharredBrownie.Shrinking(world, x, y, z);
		}
	}
}

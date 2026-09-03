package com.shapesnsizes;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFarmland;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.primitives.AABBd;

import java.util.List;

public final class Trample {

	public static final float CRUSH_RATIO = 3.5f;

	public static final int CRUSH_INTERVAL = 5;

	private static final double KICK_SPEED = 0.05;

	private static final double STOMP_SPEED = 0.08;

	private Trample() {}

	private static boolean wearingLeatherBoots(Player player) {
		ItemStack boots = player.getItemInArmorSlot(HumanArmorShape.BOOTS);
		return boots != null && boots.getItem() == Items.ARMOR_BOOTS_LEATHER;
	}

	public static void landOnGrass(Player player, float fallDistance) {
		World world = player.world;
		if (world == null || world.isClientSide) return;
		if (!PlayerScale.isBig(player) || !PlayerScale.sizeGriefing(world)) return;
		if (treadsCarefully(player) || isIntangible(player)) return;
		if (fallDistance <= PlayerScale.jumpHeight(player) * JUMP_MARGIN) return;

		int y = footprintY(player);
		TilePos pos = new TilePos();
		for (int x = MathHelper.floor(player.bb.minX); x <= MathHelper.floor(player.bb.maxX); ++x) {
			for (int z = MathHelper.floor(player.bb.minZ); z <= MathHelper.floor(player.bb.maxZ); ++z) {
				pos.set(x, y, z);
				Block<?> block = world.getBlockType(pos);
				if (block == Blocks.GRASS || block == Blocks.GRASS_RETRO) {
					world.setBlockTypeNotify(pos, Blocks.DIRT);
				} else if (block == Blocks.GRASS_SCORCHED) {
					world.setBlockTypeNotify(pos, Blocks.DIRT_SCORCHED);
				}
			}
		}
	}

	private static final float JUMP_MARGIN = 1.1f;

	private static int footprintY(Player player) {
		return MathHelper.floor(player.bb.minY - 0.2);
	}

	private static final float SOFT_GROUND = 0.6f;

	private static float triggerHeight(float scale) {
		return Math.max(IMPACT_FLOOR, IMPACT_REFERENCE / scale);
	}

	private static final float POUND_MIN_SCALE = 3.0f;

	private static final float IMPACT_REFERENCE = 28.0f;

	private static final float IMPACT_FLOOR = 4.0f;

	private static final double RADIUS_BASE = 1.25;
	private static final double RADIUS_PER_FORCE = 0.75;

	private static final float MAX_FORCE = 3.0f;

	private static final double MAX_IMPACT_RADIUS = 12.0;

	public static boolean landingImpact(Player player, float fallDistance) {
		World world = player.world;
		if (world == null || world.isClientSide) return false;
		if (!PlayerScale.isBig(player) || !PlayerScale.sizeGriefing(world)) return false;
		if (isIntangible(player)) return false;

		if (treadsCarefully(player)) return false;

		float scale = PlayerScale.get(player);

		if (scale < POUND_MIN_SCALE) return false;

		float trigger = triggerHeight(scale);
		if (fallDistance <= trigger) return false;

		float force = Math.min(MAX_FORCE, fallDistance / trigger);

		double halfWidth = (player.bb.maxX - player.bb.minX) * 0.5;
		double radius = Math.min(MAX_IMPACT_RADIUS,
			halfWidth * (RADIUS_BASE + RADIUS_PER_FORCE * (force - 1.0)));
		double centreX = player.x;
		double centreZ = player.z;
		int groundY = footprintY(player);

		int depth = 1 + (int) ((force - 1.0f) * 0.5f);

		TilePos pos = new TilePos();

		int reach = (int) Math.ceil(radius);
		for (int dx = -reach; dx <= reach; ++dx) {
			for (int dz = -reach; dz <= reach; ++dz) {
				double distSq = dx * dx + dz * dz;
				if (distSq > radius * radius) continue;

				int here = Math.max(1, (int) Math.round(depth * (1.0 - Math.sqrt(distSq) / radius)));
				for (int dy = 0; dy < here; ++dy) {
					pos.set(MathHelper.floor(centreX) + dx, groundY - dy, MathHelper.floor(centreZ) + dz);
					Block<?> block = world.getBlockType(pos);
					if (block == null || block == Blocks.AIR) continue;
					float hardness = block.getHardness();

					if (hardness < 0.0f || hardness > SOFT_GROUND) break;
					world.setBlockTypeNotify(pos, Blocks.AIR);
				}
			}
		}

		int damage = blastDamage(scale, force);

		List<Entity> caught = world.getEntitiesWithinAABBExcludingEntity(player,
			MathHelper.aabbGrow(player.bb, radius, radius * 0.5, radius, new AABBd()));
		for (Entity other : caught) {
			if (other == player || other.removed || !(other instanceof Mob)) continue;
			other.hurt(player, damage, DamageType.BLAST);
		}

		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, centreX, player.bb.minY, centreZ,
			"random.explode", 2.0f, 0.75f);
		int puffs = (int) MathHelper.clamp(radius * 6.0, 8, 48);
		for (int i = 0; i < puffs; ++i) {
			double offX = (world.rand.nextDouble() - 0.5) * radius * 2.0;
			double offZ = (world.rand.nextDouble() - 0.5) * radius * 2.0;

			world.spawnParticle("explode", centreX + offX, player.bb.minY, centreZ + offZ,
				0.0, 0.0, 0.0, world.dimension.id, true);
		}
		return true;
	}

	public static boolean treadsHeavily(Player player) {
		return PlayerScale.isBig(player)
			&& !isIntangible(player)
			&& !treadsCarefully(player)
			&& !wearingLeatherBoots(player)
			&& PlayerScale.sizeGriefing(player.world);
	}

	public static boolean isIntangible(Player player) {
		return player.hasNoPhysics() || player.vehicle != null;
	}

	public static boolean treadsCarefully(Player player) {
		return player.isSneaking() || PlayerScale.isCrawling(player);
	}

	private static final double FOOT_WIDTH = 0.5;

	private static final double FOOT_SPREAD = 0.8;

	public static void snowFootprint(Player player, boolean leftFoot) {
		World world = player.world;
		if (world == null || world.isClientSide || !treadsHeavily(player)) return;

		double dx = stepX(player);
		double dz = stepZ(player);
		double len = Math.sqrt(dx * dx + dz * dz);
		if (len < 1.0e-4) {
			dx = -Math.sin(player.yRot * Math.PI / 180.0);
			dz = Math.cos(player.yRot * Math.PI / 180.0);
			len = 1.0;
		}

		double width = player.bb.maxX - player.bb.minX;
		double halfWidth = width * 0.5;
		double offset = halfWidth * FOOT_SPREAD * (leftFoot ? 1.0 : -1.0);

		double centreX = player.x + dz / len * offset;
		double centreZ = player.z - dx / len * offset;

		int span = footBlocks(width);
		int firstX = MathHelper.floor(centreX) - (span - 1) / 2;
		int firstZ = MathHelper.floor(centreZ) - (span - 1) / 2;

		int low = footprintY(player);
		TilePos pos = new TilePos();
		for (int x = firstX; x < firstX + span; ++x) {
			for (int z = firstZ; z < firstZ + span; ++z) {
				for (int y = low; y <= low + 1; ++y) {
					pos.set(x, y, z);
					if (world.getBlockType(pos) == Blocks.LAYER_SNOW) {
						world.setBlockTypeNotify(pos, Blocks.AIR);
					}
				}
			}
		}
	}

	public static int footBlocks(double bodyWidth) {
		return Math.max(1, (int) Math.round(bodyWidth * FOOT_WIDTH));
	}

	public static void crackIce(Player player) {
		World world = player.world;
		if (world == null || world.isClientSide) return;

		if (!treadsHeavily(player) || wearingIceSkates(player)) return;

		int minX = MathHelper.floor(player.bb.minX - ICE_MARGIN);
		int maxX = MathHelper.floor(player.bb.maxX + ICE_MARGIN);
		int minZ = MathHelper.floor(player.bb.minZ - ICE_MARGIN);
		int maxZ = MathHelper.floor(player.bb.maxZ + ICE_MARGIN);
		int minY = footprintY(player);

		int maxY = player.isInWater()
			? Math.min(MathHelper.floor(player.bb.maxY), minY + MAX_ICE_HEIGHT)
			: minY + 1;

		boolean broke = false;
		TilePos pos = new TilePos();
		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					pos.set(x, y, z);

					if (world.getBlockType(pos) != Blocks.ICE) continue;
					world.setBlockTypeNotify(pos, Blocks.FLUID_WATER_FLOWING);
					broke = true;
				}
			}
		}
		if (broke) {
			world.playBlockSoundEffect(null, player.x, player.bb.minY, player.z,
				Blocks.ICE, EnumBlockSoundEffectType.MINE);
		}
	}

	private static final int MAX_ICE_HEIGHT = 32;

	private static final double ICE_MARGIN = 0.1;

	public static boolean wearingIceSkates(Player player) {
		ItemStack boots = player.getItemInArmorSlot(HumanArmorShape.BOOTS);
		return boots != null && Items.ARMOR_BOOTS_ICESKATES != null
			&& boots.getItem() == Items.ARMOR_BOOTS_ICESKATES;
	}

	public static void crushCrops(Player player) {
		World world = player.world;
		if (world == null || world.isClientSide || !treadsHeavily(player)) return;

		int y = footprintY(player);
		TilePos pos = new TilePos();
		for (int x = MathHelper.floor(player.bb.minX); x <= MathHelper.floor(player.bb.maxX); ++x) {
			for (int z = MathHelper.floor(player.bb.minZ); z <= MathHelper.floor(player.bb.maxZ); ++z) {
				pos.set(x, y, z);
				if (world.getBlockType(pos) != Blocks.FARMLAND_DIRT) continue;
				boolean wet = BlockLogicFarmland.isWet(world.getBlockData(pos));
				world.setBlockTypeNotify(pos, wet ? Blocks.MUD : Blocks.DIRT);
			}
		}
	}

	public static void crushCheck(Player self) {
		World world = self.world;
		if (world == null || world.isClientSide || !self.isAlive()) return;
		if (!PlayerScale.sizeGriefing(world) || self.hasNoPhysics()) return;

		List<Entity> nearby = world.getEntitiesWithinAABBExcludingEntity(self,
			MathHelper.aabbGrow(self.bb, 0.2, 0.2, 0.2, new AABBd()));
		for (Entity other : nearby) {
			if (other.removed || !(other instanceof Mob)) continue;
			if (other == self.vehicle || other == self.passenger) continue;
			Mob victimOrCrusher = (Mob) other;
			if (victimOrCrusher.getHealth() <= 0) continue;

			float mine = self.bbHeight;
			float theirs = victimOrCrusher.bbHeight;
			if (mine <= 0.0f || theirs <= 0.0f) continue;

			if (treads(self, victimOrCrusher) && connects(self, victimOrCrusher)) {
				victimOrCrusher.hurt(self, crushDamage(mine / theirs), DamageType.GENERIC);
			} else if (treads(victimOrCrusher, self) && connects(victimOrCrusher, self)) {
				self.hurt(victimOrCrusher, crushDamage(theirs / mine), DamageType.GENERIC);
			}
		}
	}

	private static boolean treads(Entity bigger, Entity smaller) {
		float mine = bigger.bbHeight;
		float theirs = smaller.bbHeight;
		if (mine <= 0.0f || theirs <= 0.0f) return false;
		if (mine / theirs < CRUSH_RATIO) return false;
		return scaleOf(bigger) / scaleOf(smaller) >= CRUSH_RATIO;
	}

	private static float scaleOf(Entity entity) {
		return entity instanceof Player ? PlayerScale.get((Player) entity) : 1.0f;
	}

	private static boolean connects(Entity bigger, Entity smaller) {
		return comingDownOn(bigger, smaller) || kickingInto(bigger, smaller);
	}

	private static boolean comingDownOn(Entity above, Entity below) {
		if (above.yd < -STOMP_SPEED) return true;
		return above.bb.minY >= (below.bb.minY + below.bb.maxY) / 2.0 - 0.1;
	}

	private static boolean kickingInto(Entity mover, Entity target) {

		double vx = stepX(mover);
		double vz = stepZ(mover);
		if (vx * vx + vz * vz < KICK_SPEED * KICK_SPEED) return false;
		return vx * (target.x - mover.x) + vz * (target.z - mover.z) > 0.0;
	}

	private static double stepX(Entity entity) {
		return entity instanceof SizeTicker ? ((SizeTicker) entity).shapesnsizes$stepX() : entity.x - entity.xo;
	}

	private static double stepZ(Entity entity) {
		return entity instanceof SizeTicker ? ((SizeTicker) entity).shapesnsizes$stepZ() : entity.z - entity.zo;
	}

	private static int crushDamage(float ratio) {
		float over = Math.max(0.0f, ratio - CRUSH_RATIO);
		return (int) MathHelper.clamp(Math.round(2.0f + over * 2.0f), 2, 12);
	}

	private static int blastDamage(float scale, float force) {
		return (int) MathHelper.clamp(Math.round(scale * force), 2, 20);
	}
}

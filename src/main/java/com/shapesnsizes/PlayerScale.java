package com.shapesnsizes;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.data.SynchedEntityData;
import net.minecraft.core.world.pos.TilePos;

public final class PlayerScale {
	public static final int DATA_ID = 20;
	public static final int BROKEN_ID = 21;
	public static final int CRAWL_ID = 22;
	public static final int BONUS_ID = 23;
	public static final int EASED_ID = 24;
	public static final String TAG = "ShapesScale";
	public static final String BONUS_TAG = "ShapesBonus";
	public static final float MIN = 0.1f;
	public static final float MAX = 16.0f;
	public static final float DEFAULT = 1.0f;
	public static final float SMALL = 0.5f;
	public static final float BIG = 1.5f;
	private static final int DEFAULT_RAW = 1000;

	private PlayerScale() {}

	public static void define(SynchedEntityData data) {
		data.define(DATA_ID, DEFAULT_RAW, Integer.class);
		data.define(BROKEN_ID, (byte) 0, Byte.class);
		data.define(CRAWL_ID, (byte) 0, Byte.class);
		data.define(BONUS_ID, 0, Integer.class);

		data.define(EASED_ID, -1, Integer.class);
	}

	public static final float CRAWL_HEIGHT = 0.6f;

	public static final float CRAWL_SPEED = 0.35f;

	public static boolean isCrawling(Player player) {
		if (player == null) return false;
		try {
			return player.getEntityData().getByte(CRAWL_ID) != 0;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public static void setCrawling(Player player, boolean crawling) {
		if (player == null) return;
		try {
			player.getEntityData().set(CRAWL_ID, (byte) (crawling ? 1 : 0));
		} catch (RuntimeException ignored) {
		}
	}

	public static float clamp(float scale) {
		if (Float.isNaN(scale)) return DEFAULT;
		return Math.max(MIN, Math.min(MAX, scale));
	}

	public static float get(Player player) {
		if (player == null) return DEFAULT;
		float eased = easedNow(player);

		return eased >= 0.0f ? eased : target(player);
	}

	private static float easedNow(Player player) {
		if (player.world != null && player.world.isClientSide) {
			try {
				return player.getEntityData().getInt(EASED_ID) / 1000.0f;
			} catch (RuntimeException e) {
				return -1.0f;
			}
		}
		return player instanceof ScaledPlayer ? ((ScaledPlayer) player).shapesnsizes$easedScale() : -1.0f;
	}

	public static float target(Player player) {
		if (player == null) return DEFAULT;
		return clamp(getBase(player) + getBonus(player));
	}

	public static void tickEase(Player player) {
		if (!(player instanceof ScaledPlayer)) return;

		if (player.world != null && player.world.isClientSide) return;
		ScaledPlayer scaled = (ScaledPlayer) player;
		float goal = target(player);
		float eased = scaled.shapesnsizes$easedScale();
		if (eased < 0.0f) {
			scaled.shapesnsizes$setEasedScale(goal);
			return;
		}
		if (Math.abs(goal - eased) < SETTLE) {
			if (eased != goal) scaled.shapesnsizes$setEasedScale(goal);
			return;
		}
		scaled.shapesnsizes$setEasedScale(eased + (goal - eased) * EASE_RATE);
	}

	public static void publishEase(Player player) {
		if (player == null || !(player instanceof ScaledPlayer)) return;
		if (player.world != null && player.world.isClientSide) return;
		float eased = ((ScaledPlayer) player).shapesnsizes$easedScale();
		try {
			player.getEntityData().set(EASED_ID, Math.round(eased * 1000.0f));
		} catch (RuntimeException ignored) {
		}
	}

	public static void snapEase(Player player) {
		if (player instanceof ScaledPlayer) {
			float goal = target(player);
			((ScaledPlayer) player).shapesnsizes$setEasedScale(goal);
			publishEase(player);
		}
	}

	private static final float EASE_RATE = 0.09f;
	private static final float SETTLE = 0.002f;

	public static float getBase(Player player) {
		if (player == null) return DEFAULT;
		try {
			return player.getEntityData().getInt(DATA_ID) / 1000.0f;
		} catch (RuntimeException e) {

			return DEFAULT;
		}
	}

	public static float getBonus(Player player) {
		if (player == null) return 0.0f;
		try {
			return player.getEntityData().getInt(BONUS_ID) / 1000.0f;
		} catch (RuntimeException e) {
			return 0.0f;
		}
	}

	public static void set(Player player, float scale) {
		player.getEntityData().set(DATA_ID, Math.round(clamp(scale) * 1000.0f));
	}

	public static void setBonus(Player player, float bonus) {
		if (player == null) return;
		player.getEntityData().set(BONUS_ID, Math.round(bonus * 1000.0f));
	}

	public static void addBonus(Player player, float delta) {
		if (player == null) return;
		float base = getBase(player);
		float total = clamp(base + getBonus(player) + delta);
		setBonus(player, total - base);
	}

	public static float climbFactor(Player player) {
		float scale = get(player);
		return scale >= DEFAULT ? 1.0f : scale;
	}

	private static final double VANILLA_CLIMB_PUSH = 0.25;
	private static final double VANILLA_GRAVITY = 0.08;

	public static double climbPush(Player player) {
		double net = (VANILLA_CLIMB_PUSH - VANILLA_GRAVITY) * climbFactor(player);
		return net + VANILLA_GRAVITY;
	}

	public static boolean tooLightForPlate(Player player, net.minecraft.core.block.material.Material plate) {
		float scale = get(player);
		if (scale > PLATE_STONE_MIN) return false;
		if (scale <= PLATE_ANY_MIN) return true;
		return plate != net.minecraft.core.block.material.Materials.WOOD;
	}

	public static float knockbackBetween(net.minecraft.core.entity.Entity striker, net.minecraft.core.entity.Entity struck) {
		float ratio = knockbackWeight(striker) / knockbackWeight(struck);
		if (ratio == 1.0f) return 1.0f;
		return Math.max(0.3f, Math.min(3.0f, (float) Math.sqrt(ratio)));
	}

	private static float knockbackWeight(net.minecraft.core.entity.Entity entity) {
		return entity instanceof Player ? abilityFactor((Player) entity) : 1.0f;
	}

	public static final float PLATE_ANY_MIN = 0.5f;

	public static final float PLATE_STONE_MIN = 0.75f;

	public static void clearBonus(Player player) {
		if (player != null) setBonus(player, 0.0f);
	}

	public static boolean isScaled(Player player) {
		return get(player) != DEFAULT;
	}

	public static boolean isSmall(Player player) {
		return get(player) <= SMALL;
	}

	public static boolean isBig(Player player) {
		return get(player) >= BIG;
	}

	public static float stepFactor(Player player) {
		float scale = get(player);
		return scale <= DEFAULT ? scale : (float) Math.pow(scale, STEP_EXPONENT);
	}

	private static final double STEP_EXPONENT = 0.75;

	public static final float CACTUS_PROOF = 4.0f;

	public static boolean isCactusProof(Player player) {
		return get(player) >= CACTUS_PROOF;
	}

	public static void forceResync(Player player) {
		if (player == null) return;
		SynchedEntityData data = player.getEntityData();
		int raw = data.getInt(DATA_ID);
		data.set(DATA_ID, raw == DEFAULT_RAW ? DEFAULT_RAW + 1 : DEFAULT_RAW);
		data.set(DATA_ID, raw);
		int bonus = data.getInt(BONUS_ID);
		data.set(BONUS_ID, bonus == 0 ? 1 : 0);
		data.set(BONUS_ID, bonus);

		int eased = data.getInt(EASED_ID);
		data.set(EASED_ID, eased == 0 ? 1 : 0);
		data.set(EASED_ID, eased);
	}

	public static int abilityPercent(Player player) {
		if (player == null || player.world == null) return 100;
		Integer pct = player.world.getGameRuleValue(ScalingRules.ABILITY_SCALING);
		return pct == null ? 100 : pct;
	}

	public static float abilityFactor(Player player) {
		return abilityFor(get(player), abilityPercent(player));
	}

	public static float abilityFor(float scale, int percent) {
		if (scale == DEFAULT) return 1.0f;
		float base = scale >= DEFAULT ? scale : (float) Math.sqrt(scale);
		float f = 1.0f + (base - 1.0f) * percent / 100.0f;
		return Math.max(0.05f, f);
	}

	public static float speedFactor(Player player) {
		return speedFrom(abilityFactor(player));
	}

	public static float speedFrom(float ability) {
		if (ability == 1.0f) return 1.0f;

		if (ability <= 1.0f) return ability;
		return (float) Math.pow(ability, speedExponent(ability));
	}

	private static float speedExponent(float ability) {
		float t = MathHelper.clamp((ability - 1.0f) / (MAX - 1.0f), 0.0f, 1.0f);
		return SPEED_EXPONENT_MIN + (SPEED_EXPONENT_MAX - SPEED_EXPONENT_MIN) * t;
	}

	private static final float SPEED_EXPONENT_MIN = 0.5f;

	private static final float SPEED_EXPONENT_MAX = 0.75f;

	public static float jumpVelocityFactor(Player player) {
		float f = abilityFactor(player);
		float launch = f == 1.0f ? 1.0f : (float) Math.sqrt(jumpFrom(f));

		if (get(player) >= SMALL) {
			float needed = (float) Math.sqrt(MIN_JUMP_BLOCKS / VANILLA_JUMP_BLOCKS);
			if (launch < needed) launch = needed;
		}
		return launch;
	}

	public static float jumpHeight(Player player) {
		float launch = jumpVelocityFactor(player);
		return VANILLA_JUMP_BLOCKS * launch * launch;
	}

	public static float jumpFrom(float ability) {
		if (ability <= 1.0f) return ability;
		return (float) Math.sqrt(ability);
	}

	private static final float VANILLA_JUMP_BLOCKS = 1.1025f;

	private static final float MIN_JUMP_BLOCKS = 1.05f;

	public static float fallSeverity(Player player) {
		float s = get(player);
		if (s == DEFAULT) return 1.0f;
		return s > DEFAULT ? 1.0f / s : s * s;
	}

	public static double relieveDrag(net.minecraft.core.entity.Entity entity, double drag) {
		if (!(entity instanceof Player)) return drag;
		float f = abilityFactor((Player) entity);
		if (f <= 1.0f) return drag;

		return 1.0 - (1.0 - drag) / Math.min(f, MAX_DRAG_RELIEF);
	}

	private static final float MAX_DRAG_RELIEF = 2.5f;

	public static int nourishment(net.minecraft.core.entity.Entity entity, int amount) {
		if (amount <= 0 || !(entity instanceof Player)) return amount;
		float f = abilityFactor((Player) entity);
		if (f == 1.0f) return amount;
		return Math.max(1, Math.round(amount / f));
	}

	public static int incomingDamage(Player player, int damage) {
		if (damage <= 0) return damage;
		float f = abilityFactor(player);
		if (f == 1.0f) return damage;
		return Math.max(1, Math.round(damage / MathHelper.clamp(f, 1.0f / MAX_DAMAGE_RELIEF, MAX_DAMAGE_RELIEF)));
	}

	public static double presence(Player player) {
		float weight = abilityFactor(player);
		if (weight == 1.0f) return 1.0;
		return MathHelper.clamp(Math.sqrt(weight), PRESENCE_FLOOR, PRESENCE_CEILING);
	}

	private static final double PRESENCE_FLOOR = 0.6;

	private static final double PRESENCE_CEILING = 3.0;

	public static float labelScale(Player player) {
		float scale = player == null ? DEFAULT : get(player);
		if (scale == DEFAULT) return 1.0f;
		return MathHelper.clamp((float) Math.pow(scale, LABEL_EXPONENT), LABEL_MIN, LABEL_MAX);
	}

	private static final double LABEL_EXPONENT = 0.4;
	private static final float LABEL_MIN = 0.6f;
	private static final float LABEL_MAX = 2.5f;

	public static float nearPlane(Player player) {
		float scale = player == null ? DEFAULT : get(player);
		return Math.max(MIN_NEAR_PLANE, VANILLA_NEAR_PLANE * Math.min(1.0f, scale));
	}

	private static final float VANILLA_NEAR_PLANE = 0.05f;
	private static final float MIN_NEAR_PLANE = 0.01f;

	private static final float MAX_DAMAGE_RELIEF = 4.0f;

	public static float breathFactor(Player player) {
		float scale = get(player);
		return scale == DEFAULT ? 1.0f : (float) Math.pow(scale, BREATH_EXPONENT);
	}

	public static int airCapacity(Player player) {
		return Math.max(MIN_AIR, Math.round(VANILLA_AIR * breathFactor(player)));
	}

	private static final double BREATH_EXPONENT = 0.75;

	private static final int VANILLA_AIR = 300;

	private static final int MIN_AIR = 60;

	public static double pickupMargin(Player player) {
		return Math.min(MAX_PICKUP_MARGIN, VANILLA_PICKUP_MARGIN * get(player));
	}

	private static final double VANILLA_PICKUP_MARGIN = 1.0;
	private static final double MAX_PICKUP_MARGIN = 6.0;

	public static boolean sizeGriefing(World world) {
		return rule(world, ScalingRules.SIZE_GRIEFING, true);
	}

	public static boolean stompSounds(World world) {
		return rule(world, ScalingRules.STOMP_SOUNDS, false);
	}

	public static boolean waterDisplacement(World world) {
		return rule(world, ScalingRules.WATER_DISPLACEMENT, true);
	}

	private static boolean rule(World world, net.minecraft.core.data.gamerule.GameRuleBoolean key, boolean fallback) {
		if (world == null) return fallback;
		Boolean on = world.getGameRuleValue(key);
		return on == null ? fallback : on;
	}

	public static final float EMBER_PROOF_SMALL = 0.33f;
	public static final float EMBER_PROOF_BIG = 6.0f;

	public static boolean emberProof(Player player) {
		float scale = get(player);
		return scale <= EMBER_PROOF_SMALL || scale >= EMBER_PROOF_BIG;
	}

	public static boolean boulderProof(Player player) {
		return get(player) >= EMBER_PROOF_BIG;
	}

	public static final float TINY = 0.3f;

	public static boolean isTiny(Player player) {
		return get(player) <= TINY;
	}

	public static boolean drowningInRain(Player player) {
		if (player == null || player.world == null || !isTiny(player)) return false;
		if (player.getItemInArmorSlot(HumanArmorShape.HEAD) != null) return false;
		return player.world.isBlockBeingRainedOn(new TilePos(
			MathHelper.floor(player.x),
			MathHelper.floor(player.y + player.getHeadHeight()),
			MathHelper.floor(player.z)));
	}

	public static double thickenDrag(net.minecraft.core.entity.Entity entity, double drag) {
		if (!(entity instanceof Player)) return 1.0;
		Player player = (Player) entity;
		if (!isSmall(player)) return 1.0;
		float f = abilityFactor(player);
		if (f >= 1.0f) return drag;
		return Math.max(0.25, 1.0 - (1.0 - drag) / f);
	}

	private static boolean smallAndHolding(Player player, int itemID) {
		if (player == null || !isSmall(player)) return false;
		ItemStack held = player.getHeldItem();
		return held != null && held.itemID == itemID;
	}

	public static boolean isWaterWalker(Player player) {
		return smallAndHolding(player, Blocks.ALGAE.id());
	}

	public static boolean canClimbWalls(Player player) {
		if (player == null || !isSmall(player)) return false;

		if (player.isSneaking()) return true;
		return Items.SLIMEBALL != null && smallAndHolding(player, Items.SLIMEBALL.id);
	}

	public static boolean isWallClimbing(Player player) {
		if (player == null || player.world == null) return false;
		if (!player.horizontalCollision || !canClimbWalls(player)) return false;
		TilePos pos = new TilePos(
			MathHelper.floor(player.x), MathHelper.floor(player.bb.minY), MathHelper.floor(player.z));
		Block<?> block = player.world.getBlockType(pos);
		return block == null || !block.isClimbable(player.world, pos);
	}

	public static final double GLIDE_FALL = -0.06;

	private static final double GLIDE_DRIFT = 0.014;

	public static boolean isGliding(Player player) {
		return Items.PAPER != null && smallAndHolding(player, Items.PAPER.id) && !player.isSneaking();
	}

	public static double glideDriftX(Player player) {
		return -Math.sin(player.yRot * Math.PI / 180.0) * GLIDE_DRIFT;
	}

	public static double glideDriftZ(Player player) {
		return Math.cos(player.yRot * Math.PI / 180.0) * GLIDE_DRIFT;
	}

	public static boolean isSurfaceBroken(Player player) {
		if (player == null) return true;
		try {
			return player.getEntityData().getByte(BROKEN_ID) != 0;
		} catch (RuntimeException e) {
			return true;
		}
	}

	public static void setSurfaceBroken(Player player, boolean broken) {
		if (player == null) return;
		try {
			player.getEntityData().set(BROKEN_ID, (byte) (broken ? 1 : 0));
		} catch (RuntimeException ignored) {

		}
	}

	public static boolean canWaterWalk(Player player) {
		return isWaterWalker(player) && !player.isSneaking() && !isSurfaceBroken(player);
	}

	public static TilePos waterUnderfoot(Player player) {
		if (player.world == null) return null;
		int bx = MathHelper.floor(player.x);
		int by = MathHelper.floor(player.y - 0.05);
		int bz = MathHelper.floor(player.z);
		TilePos probe = new TilePos(bx, by, bz);
		Block<?> below = player.world.getBlockType(probe);
		if (below == null || !below.hasTag(BlockTags.IS_WATER)) return null;
		Block<?> above = player.world.getBlockType(new TilePos(bx, by + 1, bz));
		if (above != null && above.hasTag(BlockTags.IS_WATER)) return null;
		return probe;
	}

	public static String format(float scale) {
		String s = String.format(java.util.Locale.ROOT, "%.3f", scale);
		s = s.replaceAll("0+$", "");
		if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
		return s;
	}
}

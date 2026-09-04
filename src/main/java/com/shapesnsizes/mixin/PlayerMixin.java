package com.shapesnsizes.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.shapesnsizes.Behemoth;
import com.shapesnsizes.Foliage;
import com.shapesnsizes.Wading;
import com.shapesnsizes.Piggyback;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.PortalSized;
import com.shapesnsizes.PortalSizes;
import com.shapesnsizes.Ridable;
import com.shapesnsizes.ScaledPlayer;
import com.shapesnsizes.SizeTicker;
import com.shapesnsizes.Trample;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin extends Mob implements ScaledPlayer, PortalSized, Wading.Wader, Foliage.Walker, SizeTicker {
	protected PlayerMixin(World world) {
		super(world);
	}

	@Unique
	private boolean shapesnsizes$jumpedSinceSupported = false;

	@Unique
	private long shapesnsizes$lastSizeTick = -1L;

	@Unique private double shapesnsizes$seenX = Double.NaN;
	@Unique private double shapesnsizes$seenZ = Double.NaN;
	@Unique private double shapesnsizes$stepX = 0.0;
	@Unique private double shapesnsizes$stepZ = 0.0;

	@Override
	public double shapesnsizes$stepX() {
		return this.shapesnsizes$stepX;
	}

	@Override
	public double shapesnsizes$stepZ() {
		return this.shapesnsizes$stepZ;
	}

	@Unique
	private int shapesnsizes$crushTimer = 0;

	@Unique
	private boolean shapesnsizes$hasSavedScale = false;

	@Override
	public boolean shapesnsizes$hasSavedScale() {
		return this.shapesnsizes$hasSavedScale;
	}

	@Unique
	private float shapesnsizes$eased = -1.0f;

	@Override
	public float shapesnsizes$easedScale() {
		return this.shapesnsizes$eased;
	}

	@Override
	public void shapesnsizes$setEasedScale(float scale) {
		this.shapesnsizes$eased = scale;
	}

	@Unique
	private double shapesnsizes$sinceFootfall = 0.0;

	@Unique
	private boolean shapesnsizes$leftFoot = false;

	@Unique
	private float shapesnsizes$boxScale = -1.0f;

	@Unique
	private final Foliage.State shapesnsizes$foliage = new Foliage.State();

	@Override
	public Foliage.State shapesnsizes$foliageState() {
		return this.shapesnsizes$foliage;
	}

	@Unique
	private final Wading.State shapesnsizes$wading = new Wading.State();

	@Override
	public Wading.State shapesnsizes$wadingState() {
		return this.shapesnsizes$wading;
	}

	@Unique
	private PortalSizes.Shape shapesnsizes$lastPortal = null;

	@Override
	public PortalSizes.Shape shapesnsizes$lastPortalShape() {
		return this.shapesnsizes$lastPortal;
	}

	@Override
	public void shapesnsizes$setLastPortalShape(PortalSizes.Shape shape) {
		this.shapesnsizes$lastPortal = shape;
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void shapesnsizes$defineScale(CallbackInfo ci) {
		PlayerScale.define(this.entityData);
	}

	@Inject(
		method = "setupScale",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;setBounds()V")
	)
	private void shapesnsizes$scaleSize(CallbackInfo ci) {
		Player self = (Player) (Object) this;
		float s = PlayerScale.get(self);
		this.shapesnsizes$boxScale = s;
		if (s != PlayerScale.DEFAULT) {
			this.setSize(this.bbWidth * s, this.bbHeight * s);
		}

		if (PlayerScale.isCrawling(self) && !self.isPlayerSleeping()) {
			this.setSize(this.bbWidth, PlayerScale.CRAWL_HEIGHT * s);
		}

		this.footSize = 0.5f * PlayerScale.stepFactor(self);
	}

	@Inject(method = "isSneaking", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$crawlBeatsCrouch(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ() && PlayerScale.isCrawling((Player) (Object) this)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "getHeadHeight", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$scaleEyeHeight(CallbackInfoReturnable<Float> cir) {
		float s = PlayerScale.get((Player) (Object) this);
		if (s != PlayerScale.DEFAULT) {
			cir.setReturnValue(this.bbHeight - 0.18f * s);
		}
	}

	@Inject(method = "getBoundsForState", at = @At("RETURN"))
	private void shapesnsizes$scaleStateBounds(int state, CallbackInfoReturnable<AABBdc> cir) {
		float s = PlayerScale.get((Player) (Object) this);
		AABBdc bb = cir.getReturnValue();
		if (s == PlayerScale.DEFAULT || !(bb instanceof AABBd)) return;

		double height = bb.maxY() - bb.minY() + 0.025;
		((AABBd) bb).maxY = bb.minY() + height * s - 0.025;
	}

	@Inject(method = "onLivingUpdate", at = @At("TAIL"))
	private void shapesnsizes$scaleSpeed(CallbackInfo ci) {
		this.shapesnsizes$sizeTick();
	}

	@Override
	public void shapesnsizes$sizeTick() {
		Player self = (Player) (Object) this;

		long now = this.world == null ? 0L : this.world.getTotalWorldTime();
		if (now == this.shapesnsizes$lastSizeTick) return;
		this.shapesnsizes$lastSizeTick = now;

		if (Double.isNaN(this.shapesnsizes$seenX)) {
			this.shapesnsizes$stepX = 0.0;
			this.shapesnsizes$stepZ = 0.0;
		} else {
			this.shapesnsizes$stepX = this.x - this.shapesnsizes$seenX;
			this.shapesnsizes$stepZ = this.z - this.shapesnsizes$seenZ;
		}
		this.shapesnsizes$seenX = this.x;
		this.shapesnsizes$seenZ = this.z;

		PlayerScale.tickEase(self);
		shapesnsizes$holdGrowthInsideBlocks(self);

		PlayerScale.publishEase(self);
		shapesnsizes$footfalls(self);
		float f = PlayerScale.speedFactor(self);
		if (f != 1.0f) {
			this.speed *= f;
			this.flySpeed *= f;
		}
		if (PlayerScale.isCrawling(self)) {
			this.speed *= PlayerScale.CRAWL_SPEED;
		}
		Wading.tick(self, this.shapesnsizes$wading);
		Foliage.tick(self);
		shapesnsizes$fluidDrag(self);
		shapesnsizes$glide(self);
		shapesnsizes$walkOnWater(self);
		shapesnsizes$checkRider(self);
		shapesnsizes$checkVehicleHolds(self);

		this.airMaxSupply = PlayerScale.airCapacity(self);
		if (this.shapesnsizes$impactCooldown > 0) --this.shapesnsizes$impactCooldown;
		if (++this.shapesnsizes$crushTimer >= Trample.CRUSH_INTERVAL) {
			this.shapesnsizes$crushTimer = 0;
			Trample.crushCheck(self);
		}

		Trample.crackIce(self);
	}

	@Unique
	private void shapesnsizes$holdGrowthInsideBlocks(Player self) {
		if (!(self instanceof ScaledPlayer) || self.world == null) return;
		if (this.hasNoPhysics() || this.vehicle != null) return;

		float built = this.shapesnsizes$boxScale;
		float wanted = PlayerScale.get(self);

		if (built <= 0.0f || wanted <= built) return;

		float fits = built;
		float reach = wanted;
		for (int attempt = 0; attempt < GROWTH_ATTEMPTS; ++attempt) {
			if (shapesnsizes$boxIsClear(self, reach / built)) {
				fits = reach;
				break;
			}
			reach = (fits + reach) * 0.5f;
		}
		if (fits != wanted) {
			((ScaledPlayer) self).shapesnsizes$setEasedScale(fits);
		}
	}

	@Unique
	private static final int GROWTH_ATTEMPTS = 3;

	@Unique
	private boolean shapesnsizes$boxIsClear(Player self, float ratio) {
		AABBdc bb = self.bb;
		double halfX = (bb.maxX() - bb.minX()) * 0.5 * ratio;
		double halfZ = (bb.maxZ() - bb.minZ()) * 0.5 * ratio;
		double height = (bb.maxY() - bb.minY()) * ratio;
		double midX = (bb.minX() + bb.maxX()) * 0.5;
		double midZ = (bb.minZ() + bb.maxZ()) * 0.5;
		AABBd grown = new AABBd(
			midX - halfX + GROWTH_SKIN, bb.minY() + GROWTH_SKIN, midZ - halfZ + GROWTH_SKIN,
			midX + halfX - GROWTH_SKIN, bb.minY() + height - GROWTH_SKIN, midZ + halfZ - GROWTH_SKIN);
		return self.world.getCubes(self, grown).isEmpty();
	}

	@Unique
	private static final double GROWTH_SKIN = 0.002;

	@Unique
	private void shapesnsizes$glide(Player self) {
		if (this.onGround || this.hasNoPhysics() || this.vehicle != null) return;
		if (this.isInWater() || this.isInLava()) return;
		if (!PlayerScale.isGliding(self)) return;
		if (this.yd < PlayerScale.GLIDE_FALL) {
			this.yd = PlayerScale.GLIDE_FALL;
		}
		this.xd += PlayerScale.glideDriftX(self);
		this.zd += PlayerScale.glideDriftZ(self);
		this.fallDistance = 0.0f;
	}

	@Override
	public boolean canClimb() {
		if (this.horizontalCollision && PlayerScale.canClimbWalls((Player) (Object) this)) {
			return true;
		}
		return super.canClimb();
	}

	@Unique
	private void shapesnsizes$footfalls(Player self) {

		if (!PlayerScale.isBig(self)) {
			this.shapesnsizes$sinceFootfall = 0.0;
			return;
		}
		if (!this.onGround) return;
		double travelled = this.shapesnsizes$stepLength();
		if (travelled > 0.0) {

			Trample.crushCrops(self);
		}
		this.shapesnsizes$sinceFootfall += travelled;
		float stride = Behemoth.strideLength(self);
		if (this.shapesnsizes$sinceFootfall >= stride) {
			this.shapesnsizes$sinceFootfall = 0.0;
			Behemoth.step(self);

			Trample.snowFootprint(self, this.shapesnsizes$leftFoot);
			this.shapesnsizes$leftFoot = !this.shapesnsizes$leftFoot;
		}
	}

	@Unique
	private void shapesnsizes$fluidDrag(Player self) {
		if (this.hasNoPhysics()) return;
		double vanilla;
		if (this.isInWater()) vanilla = 0.8;
		else if (this.isInLava() || this.isInAcid()) vanilla = 0.5;
		else return;
		double correction = PlayerScale.relieveDrag(self, vanilla) / vanilla;
		if (correction == 1.0) return;
		this.xd *= correction;
		this.yd *= correction;
		this.zd *= correction;
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$climbOn(Player clicker, CallbackInfoReturnable<Boolean> cir) {
		Player rider = (Player) (Object) this;
		if (clicker == null || !clicker.isSneaking()) return;
		if (!Piggyback.canCarry(clicker, rider)) return;
		if (clicker.passenger != null || rider.vehicle != null || rider.passenger != null) return;
		if (this.world.isClientSide) {
			cir.setReturnValue(true);
			return;
		}
		Piggyback.ride(rider, clicker);
		cir.setReturnValue(true);
	}

	@Unique
	private void shapesnsizes$checkRider(Player self) {
		if (!(this.passenger instanceof Player)) return;
		if (this.world.isClientSide) return;
		if (!Piggyback.stillAllowed(self, (Player) this.passenger)) {

			Piggyback.dismount((Player) this.passenger);
		}
	}

	@Unique
	private void shapesnsizes$checkVehicleHolds(Player self) {
		if (this.vehicle == null || this.vehicle instanceof Player) return;
		if (this.world.isClientSide || Ridable.fitsAboard(self)) return;
		if (this.vehicle instanceof Entity && Ridable.crush(self, (Entity) this.vehicle)) {
			self.sendStatusMessage("§7It gives under you.");
		}
	}

	@Redirect(
		method = "onLivingUpdate",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/util/helper/MathHelper;aabbGrow(Lorg/joml/primitives/AABBdc;DDDLorg/joml/primitives/AABBd;)Lorg/joml/primitives/AABBd;")
	)
	private AABBd shapesnsizes$sizedReachForPickups(AABBdc box, double dx, double dy, double dz, AABBd out) {
		double margin = PlayerScale.pickupMargin((Player) (Object) this);
		return MathHelper.aabbGrow(box, margin, margin * 0.5, margin, out);
	}

	@Override
	public void positionRider() {
		if (this.passenger instanceof Player) {
			Piggyback.seat((Player) (Object) this, (Player) this.passenger);
			return;
		}
		super.positionRider();
	}

	@Override
	public boolean passesThroughRiderTargeting(ItemStack heldItem) {
		if (this.passenger instanceof Player) return true;
		return super.passesThroughRiderTargeting(heldItem);
	}

	@Override
	public float getYRotDelta() {
		if (this.passenger instanceof Player) return 0.0f;
		return super.getYRotDelta();
	}

	@Unique
	private void shapesnsizes$walkOnWater(Player self) {
		TilePos water = PlayerScale.waterUnderfoot(self);

		if (this.onGround && water == null && !this.isInWater()) {
			this.shapesnsizes$jumpedSinceSupported = false;
			if (PlayerScale.isSurfaceBroken(self)) PlayerScale.setSurfaceBroken(self, false);
		}

		if (!PlayerScale.isWaterWalker(self)) return;
		if (PlayerScale.isSurfaceBroken(self)) return;
		if (this.isSneaking()) {
			PlayerScale.setSurfaceBroken(self, true);
			return;
		}
		if (this.hasNoPhysics() || this.vehicle != null || water == null) return;
		if (this.shapesnsizes$jumpedSinceSupported) {
			PlayerScale.setSurfaceBroken(self, true);
			this.shapesnsizes$jumpedSinceSupported = false;
			return;
		}
		if (this.isMultiplayerEntity) return;

		double surface = water.y + 1.0;
		if (this.y < surface) {
			this.y = surface;
			this.setBounds();
		}
		if (this.yd < 0.0) this.yd = 0.0;
		this.onGround = true;
		this.fallDistance = 0.0f;
	}

	@Inject(method = "jump", at = @At("TAIL"))
	private void shapesnsizes$scaleJump(CallbackInfo ci) {
		Player self = (Player) (Object) this;

		this.shapesnsizes$jumpedSinceSupported = true;
		if (this.hasNoPhysics()) return;
		float f = PlayerScale.jumpVelocityFactor(self);
		if (f != 1.0f) {
			this.yd *= f;
		}
	}

	@Inject(method = "hurt", at = @At("RETURN"))
	private void shapesnsizes$breakOnDamage(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		Player self = (Player) (Object) this;
		if (cir.getReturnValueZ() && PlayerScale.isWaterWalker(self)) {
			PlayerScale.setSurfaceBroken(self, true);
		}
	}

	@Inject(method = "getCurrentPlayerStrVsBlock", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$scaleBreakSpeed(CallbackInfoReturnable<Float> cir) {
		float str = cir.getReturnValueF();
		if (str <= 0.0f) return;
		float f = PlayerScale.abilityFactor((Player) (Object) this);
		if (f != 1.0f) {
			cir.setReturnValue(str * (float) Math.sqrt(f));
		}
	}

	@Unique
	private int shapesnsizes$impactCooldown = 0;

	private static final int IMPACT_COOLDOWN = 10;

	@Override
	public boolean collidesWithBlock(Block<?> block, int metadata) {
		if (block != null && block.getMaterial() == Materials.LEAVES
				&& Foliage.wadesThroughLeaves((Player) (Object) this)) {
			return false;
		}
		return super.collidesWithBlock(block, metadata);
	}

	@Inject(method = "getRidingHeight", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$scaleSeat(CallbackInfoReturnable<Double> cir) {
		Player self = (Player) (Object) this;
		float scale = PlayerScale.get(self);

		if (scale == PlayerScale.DEFAULT || cir.getReturnValueD() >= this.heightOffset) return;
		double sink = RIDE_SINK * scale;

		if (this.vehicle instanceof Entity) {
			Entity mount = (Entity) this.vehicle;
			sink = Math.min(sink, Math.max(RIDE_SINK, mount.bbHeight));
			if (mount instanceof Mob) {

				double hipsOnBack = mount.getRideHeight() + LEG_FRACTION * self.bbHeight - mount.bbHeight;
				sink = Math.min(sink, hipsOnBack);
			}
		}
		cir.setReturnValue(this.heightOffset - sink);
	}

	@Unique
	private static final double RIDE_SINK = 0.5;

	@Unique
	private static final double LEG_FRACTION = 0.4;

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true)
	private float shapesnsizes$scaleFall(float distance) {
		Player self = (Player) (Object) this;

		Trample.landOnGrass(self, distance);
		if (this.shapesnsizes$impactCooldown <= 0 && Trample.landingImpact(self, distance)) {
			this.shapesnsizes$impactCooldown = IMPACT_COOLDOWN;
		}

		float severity = PlayerScale.fallSeverity(self);
		return severity == 1.0f ? distance : distance * severity;
	}

	@Inject(method = "setPlayerSleeping", at = @At("TAIL"))
	private void shapesnsizes$stretchOutInBed(int x, int y, int z, CallbackInfo ci) {
		float s = PlayerScale.get((Player) (Object) this);
		if (s == PlayerScale.DEFAULT) return;
		this.sleepOffX *= s;
		this.sleepOffZ *= s;
	}

	@Shadow public float sleepOffX;
	@Shadow public float sleepOffZ;

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void shapesnsizes$save(CompoundTag tag, CallbackInfo ci) {
		Player self = (Player) (Object) this;

		tag.putFloat(PlayerScale.TAG, PlayerScale.getBase(self));
		tag.putFloat(PlayerScale.BONUS_TAG, PlayerScale.getBonus(self));
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void shapesnsizes$load(CompoundTag tag, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		if (tag.containsKey(PlayerScale.TAG)) {
			PlayerScale.set(self, tag.getFloat(PlayerScale.TAG));
			this.shapesnsizes$hasSavedScale = true;
		}

		PlayerScale.setBonus(self, tag.containsKey(PlayerScale.BONUS_TAG)
			? tag.getFloat(PlayerScale.BONUS_TAG)
			: 0.0f);

		PlayerScale.snapEase(self);
	}

	@ModifyVariable(method = "damageEntity", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int shapesnsizes$scaleIncomingDamage(int value, int damage, DamageType damageType) {
		if (damageType == DamageType.FALL) return value;
		return PlayerScale.incomingDamage((Player) (Object) this, value);
	}
}

package com.raishxn.ufo.entity.custom;

import com.raishxn.ufo.util.EntityDamageHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public final class ApocalypseTypeAEntity extends Monster implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation BASIC_ATTACK = RawAnimation.begin().thenPlay("basic_attack");
    private static final RawAnimation BASIC_ATTACK_2 = RawAnimation.begin().thenPlay("basic_attack2");
    private static final RawAnimation COMBO = RawAnimation.begin().thenPlay("combo");
    private static final RawAnimation BEAM = RawAnimation.begin().thenPlay("beam");
    private static final RawAnimation TELEPORT = RawAnimation.begin().thenPlay("teleport");

    private static final int BASIC_ATTACK_DURATION = 30;
    private static final int BASIC_ATTACK_HIT_TICK = 19;
    private static final int BASIC_ATTACK_2_DURATION = 36;
    private static final int BASIC_ATTACK_2_HIT_TICK = 24;
    private static final int COMBO_DURATION = 63;
    private static final int[] COMBO_HIT_TICKS = {12, 25, 40, 54};
    private static final int BEAM_DURATION = 109;
    private static final int BEAM_WINDUP_TICK = 18;
    private static final int BEAM_END_TICK = 96;
    private static final int TELEPORT_DURATION = 27;
    private static final int TELEPORT_STEP_TICK = 14;
    private static final double AGGRO_RANGE = 48.0D;
    private static final double BEAM_RANGE = AGGRO_RANGE;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent;

    private AttackState attackState = AttackState.NONE;
    private int attackTicks;
    private int basicAttackCooldown;
    private int comboCooldown;
    private int beamCooldown;
    private int teleportCooldown;
    private boolean attackConnected;
    private int comboHitIndex;

    public ApocalypseTypeAEntity(final EntityType<? extends Monster> entityType, final Level level) {
        super(entityType, level);
        this.bossEvent = new ServerBossEvent(this.getUUID(), Component.literal("APOCALYPSE TYPE-A"),
                BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
        this.xpReward = 250;
        this.bossEvent.setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ApocalypseCombatGoal());
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement", 0, state ->
                state.setAndContinue(state.isMoving() ? WALK : IDLE)));

        controllers.add(new AnimationController<>("actions", 0, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(DEATH);
            }

            return PlayState.STOP;
        }).triggerableAnim("basic_attack", BASIC_ATTACK)
                .triggerableAnim("basic_attack2", BASIC_ATTACK_2)
                .triggerableAnim("combo", COMBO)
                .triggerableAnim("beam", BEAM)
                .triggerableAnim("teleport", TELEPORT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void customServerAiStep(final ServerLevel level) {
        super.customServerAiStep(level);
        this.updateTargeting();
        this.tickCooldowns();
        this.tickActiveAttack();
        this.setAggressive(this.getTarget() != null);
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(final ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(final ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void setCustomName(final Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("entity.ufo.apocalypse_type_a");
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(final double distanceToClosestPlayer) {
        return false;
    }

    public boolean isPerformingAttack() {
        return this.attackState != AttackState.NONE;
    }

    private void tickCooldowns() {
        if (this.basicAttackCooldown > 0) {
            this.basicAttackCooldown--;
        }

        if (this.comboCooldown > 0) {
            this.comboCooldown--;
        }

        if (this.beamCooldown > 0) {
            this.beamCooldown--;
        }

        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
    }

    private void tickActiveAttack() {
        if (this.attackState == AttackState.NONE) {
            return;
        }

        final LivingEntity target = this.getTarget();
        this.attackTicks++;
        this.getNavigation().stop();

        if (target != null) {
            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        switch (this.attackState) {
            case BASIC_ATTACK -> this.tickBasicAttack(target);
            case BASIC_ATTACK_2 -> this.tickBasicAttack2(target);
            case COMBO -> this.tickCombo(target);
            case BEAM -> this.tickBeam(target);
            case TELEPORT -> this.tickTeleport(target);
            case NONE -> {
            }
        }
    }

    private void tickBasicAttack(final LivingEntity target) {
        if (!this.attackConnected && this.attackTicks >= BASIC_ATTACK_HIT_TICK) {
            this.attackConnected = true;
            this.tryAnimationHit(target, 4.8D);
        }

        if (this.attackTicks >= BASIC_ATTACK_DURATION) {
            this.finishAttack(22);
        }
    }

    private void tickBasicAttack2(final LivingEntity target) {
        if (!this.attackConnected && this.attackTicks >= BASIC_ATTACK_2_HIT_TICK) {
            this.attackConnected = true;
            this.tryAnimationHit(target, 5.4D);
        }

        if (this.attackTicks >= BASIC_ATTACK_2_DURATION) {
            this.finishAttack(30);
        }
    }

    private void tickTeleport(final LivingEntity target) {
        if (target != null && this.attackTicks == TELEPORT_STEP_TICK) {
            this.teleportNearTarget(target);
        }

        if (this.attackTicks >= TELEPORT_DURATION) {
            this.finishAttack(0);
            this.teleportCooldown = 120;
        }
    }

    private void tickCombo(final LivingEntity target) {
        if (target == null) {
            this.finishAttack(0);
            return;
        }

        while (this.comboHitIndex < COMBO_HIT_TICKS.length && this.attackTicks >= COMBO_HIT_TICKS[this.comboHitIndex]) {
            this.comboHitIndex++;
            this.tryComboHit(target);
        }

        if (this.attackTicks >= COMBO_DURATION) {
            this.finishAttack(16);
            this.comboCooldown = 80;
        }
    }

    private void tickBeam(final LivingEntity target) {
        if (target == null) {
            this.finishAttack(0);
            return;
        }

        if (this.attackTicks == BEAM_WINDUP_TICK && this.level() instanceof final ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.0F, 0.7F);
        }

        if (this.attackTicks >= BEAM_WINDUP_TICK && this.attackTicks <= BEAM_END_TICK && this.attackTicks % 10 == 0) {
            this.fireBeamAt(target);
        }

        if (this.attackTicks >= BEAM_DURATION) {
            this.finishAttack(0);
            this.beamCooldown = 140;
        }
    }

    private void tryAnimationHit(final LivingEntity target, final double reach) {
        if (target == null || !target.isAlive()) {
            return;
        }

        final boolean inRange = this.distanceToSqr(target) <= reach * reach;
        final boolean inAttackArc = this.isTargetInsideAttackArc(target, reach, 1.5D, 1.0D);
        if (!inRange && !inAttackArc) {
            return;
        }

        this.applyDirectHit(target, 1.0F, false);
    }

    private void tryComboHit(final LivingEntity target) {
        final boolean inRange = this.distanceToSqr(target) <= 36.0D;
        final boolean inAttackArc = this.isTargetInsideAttackArc(target, 6.0D, 2.0D, 1.2D);
        if (!inRange && !inAttackArc) {
            return;
        }

        if (this.applyDirectHit(target, 0.85F, true)) {
            final Vec3 knockback = target.position().subtract(this.position()).normalize().scale(0.65D);
            target.push(knockback.x, 0.22D, knockback.z);
        }
    }

    private void fireBeamAt(final LivingEntity target) {
        if (!target.isAlive() || !this.hasLineOfSight(target)) {
            return;
        }

        final double distance = this.distanceToSqr(target);
        if (distance > BEAM_RANGE * BEAM_RANGE) {
            return;
        }

        final float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75F;
        target.invulnerableTime = 0;
        EntityDamageHelper.hurt(target, this.damageSources().mobAttack(this), damage);

        final Vec3 push = target.position().subtract(this.position()).normalize().scale(0.35D);
        target.push(push.x, 0.08D, push.z);
    }

    private boolean applyDirectHit(final LivingEntity target, final float damageMultiplier, final boolean bypassIFrames) {
        if (!target.isAlive()) {
            return false;
        }

        if (bypassIFrames) {
            target.invulnerableTime = 0;
        }

        final float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
        EntityDamageHelper.hurt(target, this.damageSources().mobAttack(this), damage);
        target.setLastHurtByMob(this);
        return true;
    }

    private boolean isTargetInsideAttackArc(final LivingEntity target, final double forwardReach, final double sidePadding, final double verticalPadding) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        final AABB attackBox = this.getBoundingBox()
                .inflate(sidePadding, verticalPadding, sidePadding)
                .expandTowards(this.getLookAngle().normalize().scale(forwardReach));

        return attackBox.intersects(target.getBoundingBox());
    }

    private void teleportNearTarget(final LivingEntity target) {
        if (!(this.level() instanceof final ServerLevel serverLevel)) {
            return;
        }

        final double angle = target.getYRot() * (Math.PI / 180.0D);
        final double offsetX = -Math.sin(angle) * 2.5D;
        final double offsetZ = Math.cos(angle) * 2.5D;
        final double x = target.getX() - offsetX;
        final double y = target.getY();
        final double z = target.getZ() - offsetZ;

        if (this.randomTeleport(x, y, z, true)) {
            serverLevel.broadcastEntityEvent(this, (byte) 46);
        }
    }

    private void startAttack(final AttackState state, final String animation) {
        this.attackState = state;
        this.attackTicks = 0;
        this.attackConnected = false;
        this.comboHitIndex = 0;
        this.getNavigation().stop();
        this.triggerAnim("actions", animation);
    }

    private void finishAttack(final int cooldown) {
        this.attackState = AttackState.NONE;
        this.attackTicks = 0;
        this.attackConnected = false;
        this.basicAttackCooldown = Math.max(this.basicAttackCooldown, cooldown);
    }

    private void updateTargeting() {
        final LivingEntity currentTarget = this.getTarget();
        if (currentTarget instanceof final Player player && isValidTarget(player)) {
            return;
        }

        final Player nearestPlayer = this.level().getNearestPlayer(
                this.getX(),
                this.getY(),
                this.getZ(),
                AGGRO_RANGE,
                entity -> entity instanceof final Player player && isValidTarget(player)
        );

        if (nearestPlayer != null) {
            this.setTarget(nearestPlayer);
        } else if (currentTarget != null && !currentTarget.isAlive()) {
            this.setTarget(null);
        }
    }

    private static boolean isValidTarget(final Player player) {
        return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
    }

    private enum AttackState {
        NONE,
        BASIC_ATTACK,
        BASIC_ATTACK_2,
        COMBO,
        BEAM,
        TELEPORT
    }

    private final class ApocalypseCombatGoal extends Goal {
        private ApocalypseCombatGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            final LivingEntity target = ApocalypseTypeAEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            ApocalypseTypeAEntity.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            final LivingEntity target = ApocalypseTypeAEntity.this.getTarget();
            if (target == null) {
                return;
            }

            ApocalypseTypeAEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (ApocalypseTypeAEntity.this.isPerformingAttack()) {
                return;
            }

            final double distanceSqr = ApocalypseTypeAEntity.this.distanceToSqr(target);
            if (distanceSqr > AGGRO_RANGE * AGGRO_RANGE) {
                ApocalypseTypeAEntity.this.setTarget(null);
                ApocalypseTypeAEntity.this.getNavigation().stop();
                return;
            }

            if (distanceSqr > 100.0D && ApocalypseTypeAEntity.this.beamCooldown <= 0 && ApocalypseTypeAEntity.this.hasLineOfSight(target)) {
                ApocalypseTypeAEntity.this.startAttack(AttackState.BEAM, "beam");
                return;
            }

            if (distanceSqr > 49.0D && ApocalypseTypeAEntity.this.teleportCooldown <= 0 && ApocalypseTypeAEntity.this.hasLineOfSight(target)) {
                ApocalypseTypeAEntity.this.startAttack(AttackState.TELEPORT, "teleport");
                return;
            }

            if (distanceSqr <= 49.0D && ApocalypseTypeAEntity.this.comboCooldown <= 0 && ApocalypseTypeAEntity.this.getRandom().nextFloat() < 0.18F) {
                ApocalypseTypeAEntity.this.startAttack(AttackState.COMBO, "combo");
                return;
            }

            if (distanceSqr <= 30.25D && ApocalypseTypeAEntity.this.basicAttackCooldown <= 0) {
                if (ApocalypseTypeAEntity.this.getRandom().nextBoolean()) {
                    ApocalypseTypeAEntity.this.startAttack(AttackState.BASIC_ATTACK, "basic_attack");
                } else {
                    ApocalypseTypeAEntity.this.startAttack(AttackState.BASIC_ATTACK_2, "basic_attack2");
                }
                return;
            }

            ApocalypseTypeAEntity.this.getNavigation().moveTo(target, 1.0D);
        }
    }
}

package com.echoingsilence;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
public class ObserverEntity extends PathAwareEntity {
  public boolean armed; boolean warned;
  public ObserverEntity(EntityType<? extends ObserverEntity> t, World w) { super(t, w); setInvulnerable(true); setSilent(true); }
  @Override public boolean isPushable() { return false; }
  public void vanish(ServerWorld w) {
    w.spawnParticles(ParticleTypes.CLOUD, getX(), getY() + 1.5, getZ(), 12, .3, 1, .3, .02);
    w.playSound(null, getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.AMBIENT, .6f, 1.6f);
    discard();
  }
  @Override public void tick() {
    super.tick();
    if (getWorld().isClient) return;
    ServerWorld w = (ServerWorld) getWorld();
    if (age > 600) { discard(); return; }
    if (!(w.getClosestPlayer(this, 32) instanceof ServerPlayerEntity p)) return;
    lookAtEntity(p, 60, 60);
    Vec3d to = getEyePos().subtract(p.getEyePos()).normalize();
    if (p.getRotationVec(1f).dotProduct(to) > 0.8 && p.canSee(this)) { vanish(w); return; }
    double d = distanceTo(p);
    if (d < 8 && !warned) { warned = true; p.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 40)); }
    if (armed) {
      if (SanityManager.nearSalt(getBlockPos(), w.getTime())) { vanish(w); return; }
      if (age % 10 == 0) getNavigation().startMovingTo(p, 1.0);
      if (d < 2.5) {
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
        float dmg = Math.min(4 + random.nextInt(3), p.getHealth() - 2); // mai sotto 1 cuore
        for (net.minecraft.item.ItemStack it : p.getInventory().main) if (it.isOf(EchoingSilence.AMULET)) { it.decrement(1); dmg = Math.min(dmg, 2f); break; }
        if (dmg > 0) p.damage(getDamageSources().magic(), dmg);
        SanityState.get(p.server).add(p.getUuid(), -5);
        vanish(w);
      }
    }
  }
}

package com.echoingsilence;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.*;
import net.minecraft.server.network.ServerPlayerEntity;
public class CalmEffect extends StatusEffect {
  public CalmEffect() { super(StatusEffectCategory.BENEFICIAL, 0xA0D8A0); }
  @Override public boolean canApplyUpdateEffect(int d, int a) { return d % 20 == 0; }
  @Override public void applyUpdateEffect(LivingEntity e, int a) { if (e instanceof ServerPlayerEntity p) SanityState.get(p.server).add(p.getUuid(), 0.5f); }
}

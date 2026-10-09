package com.echoingsilence;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
public class WhistleItem extends Item {
  public WhistleItem(Settings s) { super(s); }
  @Override public TypedActionResult<ItemStack> use(World w, PlayerEntity p, Hand h) {
    if (!w.isClient) {
      w.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.PLAYERS, 1, 2f);
      for (ObserverEntity o : w.getEntitiesByClass(ObserverEntity.class, p.getBoundingBox().expand(32), e -> true)) o.vanish((ServerWorld) w);
      p.getItemCooldownManager().set(this, 600);
    }
    return TypedActionResult.success(p.getStackInHand(h));
  }
}

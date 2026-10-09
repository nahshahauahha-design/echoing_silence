package com.echoingsilence;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class SanityFood extends Item {
  final float gain; final boolean fire, drink;
  public SanityFood(Settings s, float gain, boolean fire, boolean drink) { super(s); this.gain = gain; this.fire = fire; this.drink = drink; }
  @Override public UseAction getUseAction(ItemStack s) { return drink ? UseAction.DRINK : UseAction.EAT; }
  @Override public ItemStack finishUsing(ItemStack st, World w, LivingEntity u) {
    if (!w.isClient && u instanceof ServerPlayerEntity p) {
      float g = gain;
      if (fire) for (BlockPos q : BlockPos.iterate(p.getBlockPos().add(-4, -2, -4), p.getBlockPos().add(4, 2, 4))) {
        BlockState bs = w.getBlockState(q);
        if (bs.contains(Properties.LIT) && bs.get(Properties.LIT) && (bs.isIn(BlockTags.CAMPFIRES) || bs.isOf(Blocks.FURNACE))) { g = 8; break; }
      }
      SanityState.get(p.server).add(p.getUuid(), g);
    }
    return super.finishUsing(st, w, u);
  }
}

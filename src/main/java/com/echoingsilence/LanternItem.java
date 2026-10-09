package com.echoingsilence;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
public class LanternItem extends Item {
  public final boolean infinite;
  public LanternItem(Settings s, boolean infinite) { super(s); this.infinite = infinite; }
  public static boolean lit(ItemStack s) { return s.getItem() instanceof LanternItem l && (l.infinite || s.getDamage() < s.getMaxDamage() - 1); }
  public static boolean shield(ItemStack s) { return lit(s) && !((LanternItem) s.getItem()).infinite; }
  @Override public TypedActionResult<ItemStack> use(World w, PlayerEntity p, Hand h) {
    ItemStack s = p.getStackInHand(h), o = p.getStackInHand(h == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
    if (!infinite && o.isOf(EchoingSilence.TALLOW) && s.getDamage() > 0) {
      if (!w.isClient) { s.setDamage(Math.max(0, s.getDamage() - 600)); o.decrement(1); w.playSound(null, p.getBlockPos(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1, 1); }
      return TypedActionResult.success(s);
    }
    return TypedActionResult.pass(s);
  }
}

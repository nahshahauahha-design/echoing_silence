package com.echoingsilence;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
public class SaltItem extends Item {
  public SaltItem(Settings s) { super(s); }
  @Override public ActionResult useOnBlock(ItemUsageContext c) {
    if (c.getWorld() instanceof ServerWorld w) {
      Direction side = c.getHorizontalPlayerFacing().rotateYClockwise();
      BlockPos base = c.getBlockPos().offset(c.getSide());
      for (int i = -2; i <= 2; i++) { BlockPos q = base.offset(side, i);
        SanityManager.SALT.add(new long[]{q.getX(), q.getY(), q.getZ(), w.getTime() + 1200});
        w.spawnParticles(ParticleTypes.WHITE_ASH, q.getX() + .5, q.getY() + .2, q.getZ() + .5, 6, .2, .1, .2, 0); }
      c.getStack().decrement(1);
    }
    return ActionResult.success(c.getWorld().isClient);
  }
}

package com.echoingsilence;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class EchoDoor extends Block {
  public EchoDoor(Settings s) { super(s); }
  @Override public ActionResult onUse(BlockState st, World w, BlockPos pos, PlayerEntity p, Hand h, BlockHitResult hit) {
    if (w instanceof ServerWorld sw) { BlockPos t = Chamber.destination(sw); p.requestTeleport(t.getX() + .5, t.getY(), t.getZ() + .5); sw.playSound(null, t, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, .6f, .5f); }
    return ActionResult.success(w.isClient);
  }
}

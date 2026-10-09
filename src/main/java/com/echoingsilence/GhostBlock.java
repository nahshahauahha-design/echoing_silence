package com.echoingsilence;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
public class GhostBlock extends Block {
  public GhostBlock(Settings s) { super(s); }
  @Override public void onBreak(World w, BlockPos p, BlockState st, PlayerEntity pl) {
    if (!w.isClient && pl instanceof ServerPlayerEntity sp) SanityState.get(sp.server).add(sp.getUuid(), -3);
    super.onBreak(w, p, st, pl);
  }
  @Environment(EnvType.CLIENT) @Override public void randomDisplayTick(BlockState s, World w, BlockPos p, Random r) {
    if (r.nextInt(30) == 0) w.playSound(p.getX() + .5, p.getY() + .5, p.getZ() + .5, SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.BLOCKS, .6f, .6f, false);
  }
}

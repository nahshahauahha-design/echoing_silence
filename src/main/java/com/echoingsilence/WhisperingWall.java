package com.echoingsilence;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.sound.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
public class WhisperingWall extends Block {
  public WhisperingWall(Settings s) { super(s); }
  @Environment(EnvType.CLIENT) @Override public void randomDisplayTick(BlockState st, World w, BlockPos p, Random r) {
    if (r.nextInt(25) == 0) w.playSound(p.getX() + .5, p.getY() + .5, p.getZ() + .5, SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.BLOCKS, .5f, .35f + ClientSanity.value / 250f, false);
  }
}

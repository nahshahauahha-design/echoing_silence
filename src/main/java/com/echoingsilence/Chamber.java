package com.echoingsilence;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
public class Chamber {
  static final int[][] OFF = {{0, 0}, {30, 0}, {0, 30}, {-30, 0}};
  public static BlockPos origin(ServerWorld w) { BlockPos s = w.getSpawnPos(); return new BlockPos(s.getX() + 400, -40, s.getZ() + 400); }
  public static BlockPos destination(ServerWorld w) {
    BlockPos o = origin(w); Random r = w.random;
    if (r.nextInt(5) == 0) return o.add(0, 1, -30);
    int[] f = OFF[r.nextInt(OFF.length)]; return o.add(f[0], 1, f[1]);
  }
  static void room(ServerWorld w, BlockPos c, int hx, int hz, int h) {
    Random r = w.random;
    for (int x = -hx - 1; x <= hx + 1; x++) for (int z = -hz - 1; z <= hz + 1; z++) for (int y = 0; y <= h; y++) {
      boolean wall = Math.abs(x) > hx || Math.abs(z) > hz || y == 0 || y == h;
      BlockState s = !wall ? Blocks.AIR.getDefaultState() : y == 0 ? Blocks.POLISHED_DEEPSLATE.getDefaultState()
        : (r.nextInt(8) == 0 ? EchoingSilence.WHISPERING_WALL : Blocks.DEEPSLATE_BRICKS).getDefaultState();
      w.setBlockState(c.add(x, y, z), s, 3);
    }
    w.setBlockState(c.add(0, 1, hz), EchoingSilence.ECHO_DOOR.getDefaultState(), 3);
    w.setBlockState(c.add(0, 2, hz), EchoingSilence.ECHO_DOOR.getDefaultState(), 3);
  }
  public static void build(ServerWorld w) {
    BlockPos o = origin(w);
    for (int[] f : OFF) { boolean entry = f[0] == 0 && f[1] == 0; room(w, o.add(f[0], 0, f[1]), entry ? 5 : 2, entry ? 5 : 6, 4); }
    BlockPos v = o.add(0, 0, -30); room(w, v, 2, 2, 4);
    BlockPos chest = v.add(0, 1, -1);
    w.setBlockState(chest, Blocks.CHEST.getDefaultState(), 3);
    if (w.getBlockEntity(chest) instanceof ChestBlockEntity ch) { ch.setStack(0, new ItemStack(EchoingSilence.ECHO_FRAGMENT, 3 + w.random.nextInt(2))); ch.setStack(1, new ItemStack(EchoingSilence.TALLOW, 4)); }
  }
}

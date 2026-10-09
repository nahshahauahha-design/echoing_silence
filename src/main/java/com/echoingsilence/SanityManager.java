package com.echoingsilence;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import java.util.*;
public class SanityManager {
  public static final List<long[]> SALT = new ArrayList<>();
  public static boolean nearSalt(BlockPos q, long now) {
    SALT.removeIf(a -> a[3] < now);
    for (long[] a : SALT) if (Math.abs(a[0] - q.getX()) <= 3 && Math.abs(a[1] - q.getY()) <= 3 && Math.abs(a[2] - q.getZ()) <= 3) return true;
    return false;
  }
  public static int phase(float s) { return s > 75 ? 1 : s > 50 ? 2 : s > 25 ? 3 : 4; }
  static void snd(ServerPlayerEntity p, SoundEvent e, double x, double y, double z, float v, float pt) {
    p.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(e), SoundCategory.AMBIENT, x, y, z, v, pt, p.getRandom().nextLong()));
  }
  static void sync(ServerPlayerEntity p, float s) { PacketByteBuf b = PacketByteBufs.create(); b.writeFloat(s); ServerPlayNetworking.send(p, EchoingSilence.SYNC, b); }
  public static void tick(ServerPlayerEntity p) {
    SanityState st = SanityState.get(p.server); UUID id = p.getUuid();
    ServerWorld w = p.getServerWorld();
    if (p.isCreative() || p.isSpectator() || w.getRegistryKey() != World.OVERWORLD) { sync(p, 100); return; }
    Random r = p.getRandom(); BlockPos pos = p.getBlockPos();
    boolean crown = p.getEquippedStack(EquipmentSlot.HEAD).isOf(EchoingSilence.CROWN), cape = p.getEquippedStack(EquipmentSlot.CHEST).isOf(EchoingSilence.CAPE);
    ItemStack a = p.getMainHandStack(), b = p.getOffHandStack();
    boolean lamp = LanternItem.lit(a) || LanternItem.lit(b), shield = LanternItem.shield(a) || LanternItem.shield(b), echo = lamp && !shield;
    if (crown) st.set(id, 20); // la sanità non scende più, il mondo resta in Fase 4
    else {
      int bl = w.getLightLevel(LightType.BLOCK, pos);
      boolean under = !w.isSkyVisible(pos) && w.getLightLevel(LightType.SKY, pos) < 8;
      boolean company = !w.getEntitiesByClass(LivingEntity.class, p.getBoundingBox().expand(8),
        e -> e != p && (e instanceof VillagerEntity || e instanceof AnimalEntity || e instanceof PlayerEntity)).isEmpty();
      float d = 0;
      if (bl >= 8 || lamp) d += .25f; else if (w.getLightLevel(pos) <= 4) d -= under ? .15f : .04f;
      if (under && !company && bl < 8 && !lamp) d -= .05f;
      if (company) d += .1f;
      if (!under && w.isDay()) d += .1f;
      st.add(id, d);
    }
    for (Hand h : Hand.values()) { ItemStack s = p.getStackInHand(h);
      if (LanternItem.shield(s)) { s.setDamage(s.getDamage() + (cape ? 2 : 1)); if (!LanternItem.lit(s)) snd(p, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, p.getX(), p.getY(), p.getZ(), 1.5f, 1.4f); } }
    float s = st.get(id); int ph = phase(s); sync(p, s);
    if (s < 100 && r.nextInt(ph == 1 ? 70 : 40) == 0) ambient(p, ph);
    int div = echo ? 2 : 1;
    if (ph >= 2 && !shield && p.getHealth() > 6 && (!cape || r.nextBoolean()) && r.nextInt(Math.max(1, (ph == 2 ? 100 : ph == 3 ? 50 : 25) / div)) == 0
        && w.getEntitiesByClass(ObserverEntity.class, p.getBoundingBox().expand(40), e -> true).isEmpty()) spawnObserver(p, w, ph, s);
    if (ph >= 3 && !shield && r.nextInt(8) == 0) ghost(p, w);
  }
  static void ambient(ServerPlayerEntity p, int ph) {
    Random r = p.getRandom(); Vec3d b = p.getRotationVector().multiply(-1, 0, -1).normalize();
    double x = p.getX() + b.x * 3, z = p.getZ() + b.z * 3;
    switch (ph) {
      case 1 -> snd(p, SoundEvents.BLOCK_CHEST_CLOSE, p.getX() + r.nextInt(30) - 15, p.getY(), p.getZ() + r.nextInt(30) - 15, .6f, .5f);
      case 2 -> { if (r.nextBoolean()) snd(p, SoundEvents.ENTITY_CREEPER_PRIMED, x, p.getY(), z, 1, 1); else snd(p, SoundEvents.BLOCK_GRAVEL_STEP, x, p.getY(), z, 1, .7f); }
      case 3 -> snd(p, r.nextInt(3) == 0 ? EchoingSilence.WHISPER : SoundEvents.ENTITY_WARDEN_HEARTBEAT, p.getX(), p.getY(), p.getZ(), 1, .9f);
      default -> { int k = r.nextInt(3); snd(p, k == 0 ? EchoingSilence.WHISPER : k == 1 ? EchoingSilence.DRONE : SoundEvents.ENTITY_WARDEN_AMBIENT, p.getX(), p.getY(), p.getZ(), 1, .8f); }
    }
  }
  static void spawnObserver(ServerPlayerEntity p, ServerWorld w, int ph, float s) {
    Random r = p.getRandom();
    double ang = Math.toRadians(p.getYaw() + (r.nextBoolean() ? 1 : -1) * (60 + r.nextInt(30)));
    double dist = ph == 2 ? 22 : ph == 3 ? 15 : 9;
    double x = p.getX() - Math.sin(ang) * dist, z = p.getZ() + Math.cos(ang) * dist;
    BlockPos f = null;
    for (int dy = 3; dy >= -4; dy--) { BlockPos q = BlockPos.ofFloored(x, p.getY() + dy, z);
      if (w.getBlockState(q).isAir() && w.getBlockState(q.up()).isAir() && w.getBlockState(q.up(2)).isAir() && w.getBlockState(q.down()).isSideSolidFullSquare(w, q.down(), Direction.UP)) { f = q; break; } }
    if (f == null) return;
    ObserverEntity o = EchoingSilence.OBSERVER.create(w);
    o.refreshPositionAndAngles(f.getX() + .5, f.getY(), f.getZ() + .5, 0, 0);
    o.armed = ph == 4 && s < 15 && r.nextInt(3) == 0;
    w.spawnEntity(o);
  }
  static void ghost(ServerPlayerEntity p, ServerWorld w) {
    Random r = p.getRandom();
    BlockPos q = p.getBlockPos().add(r.nextBetween(-6, 6), r.nextBetween(-3, 3), r.nextBetween(-6, 6));
    if (q.getSquaredDistance(p.getBlockPos()) > 6 && w.getBlockState(q).isIn(BlockTags.BASE_STONE_OVERWORLD)) w.setBlockState(q, EchoingSilence.GHOST_BLOCK.getDefaultState());
  }
}

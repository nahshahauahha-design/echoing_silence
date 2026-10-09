package com.echoingsilence;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import java.util.*;
public class SanityState extends PersistentState {
  public final Map<UUID, Float> map = new HashMap<>();
  public final Map<UUID, Integer> pages = new HashMap<>();
  public boolean chamber;
  public static SanityState get(MinecraftServer s) { return s.getOverworld().getPersistentStateManager().getOrCreate(SanityState::fromNbt, SanityState::new, "echo_sanity"); }
  public static SanityState fromNbt(NbtCompound n) {
    SanityState s = new SanityState(); NbtCompound c = n.getCompound("m"), p = n.getCompound("p");
    for (String k : c.getKeys()) s.map.put(UUID.fromString(k), c.getFloat(k));
    for (String k : p.getKeys()) s.pages.put(UUID.fromString(k), p.getInt(k));
    s.chamber = n.getBoolean("c"); return s;
  }
  @Override public NbtCompound writeNbt(NbtCompound n) {
    NbtCompound c = new NbtCompound(), p = new NbtCompound();
    map.forEach((k, v) -> c.putFloat(k.toString(), v)); pages.forEach((k, v) -> p.putInt(k.toString(), v));
    n.put("m", c); n.put("p", p); n.putBoolean("c", chamber); return n;
  }
  public float get(UUID u) { return map.getOrDefault(u, 100f); }
  public void set(UUID u, float v) { map.put(u, MathHelper.clamp(v, 0, 100)); markDirty(); }
  public void add(UUID u, float d) { set(u, get(u) + d); }
}

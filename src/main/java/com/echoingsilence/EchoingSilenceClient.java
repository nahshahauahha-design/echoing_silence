package com.echoingsilence;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
public class EchoingSilenceClient implements ClientModInitializer {
  public static class ObserverRenderer extends MobEntityRenderer<ObserverEntity, EndermanEntityModel<ObserverEntity>> {
    static final Identifier TEX = new Identifier("textures/entity/enderman/enderman.png");
    public ObserverRenderer(EntityRendererFactory.Context c) { super(c, new EndermanEntityModel<>(c.getPart(EntityModelLayers.ENDERMAN)), 0.5f); }
    @Override public Identifier getTexture(ObserverEntity e) { return TEX; }
  }
  static SoundInstance cur; static int chordDelay;
  @Override public void onInitializeClient() {
    EntityRendererRegistry.register(EchoingSilence.OBSERVER, ObserverRenderer::new);
    ClientPlayNetworking.registerGlobalReceiver(EchoingSilence.SYNC, (mc, h, buf, snd) -> { float v = buf.readFloat(); mc.execute(() -> ClientSanity.target = v); });
    ClientTickEvents.END_CLIENT_TICK.register(mc -> {
      if (mc.player == null) return;
      ClientSanity.tick();
      int ph = SanityManager.phase(ClientSanity.value); var r = mc.player.getRandom();
      if (ph >= 2 && r.nextInt(1500) == 0) { mc.getSoundManager().stopSounds(null, SoundCategory.MUSIC); if (ph == 4) chordDelay = 100; } // silenzio improvviso, poi un solo accordo
      if (chordDelay > 0 && --chordDelay == 0) mc.getSoundManager().play(PositionedSoundInstance.master(EchoingSilence.CHORD, 1f));
      if (ph >= 3 && (cur == null || !mc.getSoundManager().isPlaying(cur)) && r.nextInt(2400) == 0) {
        mc.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        cur = PositionedSoundInstance.music(ph == 3 ? EchoingSilence.MUSIC_WARPED : EchoingSilence.MUSIC_REVERSE); mc.getSoundManager().play(cur);
      }
    });
    HudRenderCallback.EVENT.register((ctx, td) -> {
      MinecraftClient mc = MinecraftClient.getInstance(); if (mc.player == null || mc.world == null) return;
      float s = ClientSanity.value; int W = ctx.getScaledWindowWidth();
      if (s < 75) ctx.fill(0, 0, W, ctx.getScaledWindowHeight(), ((int) ((75 - s) / 75f * 120) << 24) | 0x46604F); // viraggio grigio-verde
      ItemStack off = mc.player.getOffHandStack(); boolean full = off.isOf(EchoingSilence.FULL_MIRROR);
      if (off.isOf(EchoingSilence.MIRROR) || full) {
        int x = W - 52, y = 10; ctx.fill(x - 2, y - 2, x + 42, y + 58, 0xFF8A7BB0); ctx.fill(x, y, x + 40, y + 56, 0xFF1A1E24);
        boolean show = full ? !mc.world.getEntitiesByClass(ObserverEntity.class, mc.player.getBoundingBox().expand(40), e -> true).isEmpty() : (s < 80 && (mc.player.age / 80) % 4 == 0);
        if (show) { ctx.fill(x + 16, y + 14, x + 24, y + 22, 0xFF050505); ctx.fill(x + 13, y + 22, x + 27, y + 52, 0xFF050505); }
      }
    });
  }
}

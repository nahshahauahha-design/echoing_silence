package com.echoingsilence.mixin;
import com.echoingsilence.ClientSanity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(BackgroundRenderer.class)
public class FogMixin {
  @Inject(method = "applyFog", at = @At("TAIL"))
  private static void echo$fog(Camera cam, BackgroundRenderer.FogType type, float view, boolean thick, float td, CallbackInfo ci) {
    float s = ClientSanity.value;
    if (s >= 75 || type != BackgroundRenderer.FogType.FOG_TERRAIN) return;
    float k = (75 - s) / 75f, end = Math.max(12f, view * (1f - 0.8f * k));
    RenderSystem.setShaderFogStart(end * 0.15f * (1f - k));
    RenderSystem.setShaderFogEnd(Math.min(RenderSystem.getShaderFogEnd(), end));
    float[] c = RenderSystem.getShaderFogColor();
    RenderSystem.setShaderFogColor(c[0] + (0.42f - c[0]) * k, c[1] + (0.50f - c[1]) * k, c[2] + (0.45f - c[2]) * k);
  }
}

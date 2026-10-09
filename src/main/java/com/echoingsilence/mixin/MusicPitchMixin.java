package com.echoingsilence.mixin;
import com.echoingsilence.ClientSanity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(SoundSystem.class)
public class MusicPitchMixin {
  @Inject(method = "getAdjustedPitch", at = @At("RETURN"), cancellable = true)
  private void echo$pitch(SoundInstance s, CallbackInfoReturnable<Float> cir) {
    if (s.getCategory() == SoundCategory.MUSIC && ClientSanity.value < 100) cir.setReturnValue(Math.max(0.5f, cir.getReturnValue() * (0.5f + 0.5f * ClientSanity.value / 100f)));
  }
}

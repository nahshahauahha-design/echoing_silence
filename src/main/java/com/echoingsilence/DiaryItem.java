package com.echoingsilence;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class DiaryItem extends Item {
  static final String[] PAGES = {
    "Giorno 3. Le torce hanno iniziato a sfarfallare. Ho smesso di sentire gli uccelli.",
    "Giorno 5. Qualcuno mi segue nei tunnel. Quando mi giro non c'è nessuno.",
    "Giorno 8. La luce lo tiene lontano. Il fuoco, il pane caldo, le voci dei villager.",
    "Giorno 11. Ho scavato un muro che respirava. Non ho più dormito.",
    "Giorno 14. L'Osservatore non vuole farmi del male. Vuole solo che io guardi altrove.",
    "Giorno 17. Il corridoio non è mai lo stesso. Ho trovato una porta che si apre ogni volta su un posto diverso.",
    "Giorno 20. La Camera dell'Eco è sotto di me. Sento il mio nome ripetuto, in ritardo.",
    "Ultima pagina. Se leggi questo, non scendere al buio da solo. Porta la luce con te." };
  public DiaryItem(Settings s) { super(s); }
  @Override public TypedActionResult<ItemStack> use(World w, PlayerEntity p, Hand h) {
    ItemStack s = p.getStackInHand(h);
    if (w instanceof ServerWorld sw && p instanceof ServerPlayerEntity sp) {
      SanityState st = SanityState.get(sp.server); int n = st.pages.getOrDefault(sp.getUuid(), 0); String msg = PAGES[Math.min(n, 7)];
      if (n < 7) { st.pages.put(sp.getUuid(), n + 1); st.markDirty(); }
      else {
        if (!st.chamber) { Chamber.build(sw); st.chamber = true; st.markDirty(); }
        BlockPos o = Chamber.origin(sw); msg += " Camera dell'Eco: " + o.getX() + " " + o.getY() + " " + o.getZ();
      }
      sp.sendMessage(Text.literal("§7« " + msg + " »"), false);
      w.playSound(null, p.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1, .7f);
      st.add(sp.getUuid(), -6); s.decrement(1);
    }
    return TypedActionResult.success(s);
  }
}

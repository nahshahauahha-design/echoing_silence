package com.echoingsilence;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.*;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.LightType;
public class EchoingSilence implements ModInitializer {
  public static final String ID = "echoing_silence";
  public static Identifier id(String p) { return new Identifier(ID, p); }
  static FabricItemSettings s() { return new FabricItemSettings(); }
  static Item item(String n, Item i) { return Registry.register(Registries.ITEM, id(n), i); }
  static Block block(String n, Block b) { Registry.register(Registries.BLOCK, id(n), b); Registry.register(Registries.ITEM, id(n), new BlockItem(b, s())); return b; }
  static SoundEvent sound(String n) { return Registry.register(Registries.SOUND_EVENT, id(n), SoundEvent.of(id(n))); }

  public static final Identifier SYNC = id("sync");
  public static final SoundEvent WHISPER = sound("whisper"), DRONE = sound("drone"), CHORD = sound("chord"), MUSIC_WARPED = sound("music_warped"), MUSIC_REVERSE = sound("music_reverse");
  public static final StatusEffect CALM = Registry.register(Registries.STATUS_EFFECT, id("calm"), new CalmEffect());
  public static final Block GHOST_BLOCK = block("ghost_block", new GhostBlock(FabricBlockSettings.copyOf(Blocks.STONE).dropsNothing()));
  public static final Block WHISPERING_WALL = block("whispering_wall", new WhisperingWall(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS)));
  public static final Block ECHO_DOOR = block("echo_door", new EchoDoor(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).strength(-1f, 3600000f).dropsNothing()));
  public static final Item LANTERN = item("brass_lantern", new LanternItem(s().maxCount(1).maxDamage(1800), false));
  public static final Item ECHO_LANTERN = item("echo_lantern", new LanternItem(s().maxCount(1), true));
  public static final Item TALLOW = item("tallow", new Item(s()));
  public static final Item DIARY = item("predecessor_diary", new DiaryItem(s().maxCount(1)));
  public static final Item WHISTLE = item("bone_whistle", new WhistleItem(s().maxCount(1)));
  public static final Item SALT = item("consecrated_salt", new SaltItem(s()));
  public static final Item AMULET = item("silence_amulet", new Item(s().maxCount(4)));
  public static final Item MIRROR = item("selenite_mirror", new Item(s().maxCount(1)));
  public static final Item FULL_MIRROR = item("full_mirror", new Item(s().maxCount(1)));
  public static final Item ECHO_FRAGMENT = item("echo_fragment", new Item(s()));
  public static final Item CAPE = item("shadow_cape", new ArmorItem(ShadowMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, s()));
  public static final Item CROWN = item("silence_crown", new ArmorItem(ShadowMaterial.INSTANCE, ArmorItem.Type.HELMET, s().maxCount(1)));
  public static final Item TEA = item("calm_tea", new SanityFood(s().maxCount(16).food(new FoodComponent.Builder().hunger(1).saturationModifier(.2f)
      .statusEffect(new StatusEffectInstance(CALM, 1200), 1f).alwaysEdible().build()), 0, false, true));
  public static final Item SOUP = item("warm_soup", new SanityFood(s().maxCount(16).food(new FoodComponent.Builder().hunger(6).saturationModifier(.6f).build()), 8, false, false));
  public static final Item BREAD = item("hearth_bread", new SanityFood(s().food(new FoodComponent.Builder().hunger(5).saturationModifier(.6f).build()), 4, true, false));
  public static final EntityType<ObserverEntity> OBSERVER = Registry.register(Registries.ENTITY_TYPE, id("observer"),
      FabricEntityTypeBuilder.create(SpawnGroup.MISC, ObserverEntity::new).dimensions(EntityDimensions.fixed(.6f, 2.9f)).build());
  public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, id("main"), FabricItemGroup.builder()
      .icon(() -> new ItemStack(LANTERN)).displayName(Text.translatable("itemGroup.echoing_silence"))
      .entries((c, e) -> { for (Item i : new Item[]{LANTERN, ECHO_LANTERN, TALLOW, DIARY, WHISTLE, SALT, AMULET, MIRROR, FULL_MIRROR, ECHO_FRAGMENT, CAPE, CROWN, TEA, SOUP, BREAD,
          GHOST_BLOCK.asItem(), WHISPERING_WALL.asItem(), ECHO_DOOR.asItem()}) e.add(i); }).build());

  @Override public void onInitialize() {
    FabricDefaultAttributeRegistry.register(OBSERVER, ObserverEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.12));
    ServerTickEvents.END_SERVER_TICK.register(sv -> { if (sv.getTicks() % 20 == 0) for (ServerPlayerEntity p : sv.getPlayerManager().getPlayerList()) SanityManager.tick(p); });
    EntitySleepEvents.STOP_SLEEPING.register((e, pos) -> {
      if (e instanceof ServerPlayerEntity sp && sp.getServerWorld().getLightLevel(LightType.BLOCK, pos) >= 8) SanityState.get(sp.server).add(sp.getUuid(), 30);
    });
    Identifier mine = new Identifier("minecraft", "chests/abandoned_mineshaft");
    LootTableEvents.MODIFY.register((rm, lm, lid, tb, src) -> { if (src.isBuiltin() && mine.equals(lid)) tb.pool(LootPool.builder().conditionally(RandomChanceLootCondition.builder(0.4f)).with(ItemEntry.builder(DIARY)).build()); });
    CommandRegistrationCallback.EVENT.register((d, ra, env) -> d.register(CommandManager.literal("sanity").requires(x -> x.hasPermissionLevel(2))
      .then(CommandManager.argument("v", FloatArgumentType.floatArg(0, 100)).executes(c -> {
        ServerPlayerEntity p = c.getSource().getPlayerOrThrow(); SanityState.get(p.server).set(p.getUuid(), FloatArgumentType.getFloat(c, "v")); return 1; }))
      .executes(c -> { ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
        float v = SanityState.get(p.server).get(p.getUuid()); c.getSource().sendFeedback(() -> Text.literal("Sanità: " + Math.round(v) + "% (fase " + SanityManager.phase(v) + ")"), false); return 1; })));
  }
}

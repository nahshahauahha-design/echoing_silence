package com.echoingsilence;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.*;
public class ShadowMaterial implements ArmorMaterial {
  public static final ShadowMaterial INSTANCE = new ShadowMaterial();
  public int getDurability(ArmorItem.Type t) { return 150; }
  public int getProtection(ArmorItem.Type t) { return 2; }
  public int getEnchantability() { return 10; }
  public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER; }
  public Ingredient getRepairIngredient() { return Ingredient.ofItems(Items.LEATHER); }
  public String getName() { return "echoing_silence:shadow"; }
  public float getToughness() { return 0; }
  public float getKnockbackResistance() { return 0; }
}

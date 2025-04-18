package me.matl114.matlib.utils.language.lan.i18n;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static me.matl114.matlib.utils.language.lan.TranslateKeyUtils.*;

@MultiDescriptive(targetDefault = "?")
public interface ZhCNLocalizationHelper extends RegistryLocalizationHelper, TargetDescriptor {
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper")
    default String getItemName(ItemStack itemStack) {
        return getMaterialName(Objects.requireNonNull(itemStack).getType());
    }

    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.MaterialHelper")
    default String getMaterialName(Material material) {
        return getMaterialTranslateDefault(material);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.attribute.AttributeHelper")
    default String getAttributeName(Attribute attribute) {
        return getAttributeTranslationDefault(attribute);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.block.BiomeHelper")
    default String getBiomeName(Biome biome) {
        return getKeyedTranslationDefault("biome.minecraft",biome);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.enchantments.EnchantmentHelper")
    default String getEnchantmentName(Enchantment ench) {
        return getEnchantmentTranslationDefault(ench);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.entity.EntityTypeHelper")

    default String getEntityTypeName(EntityType entity) {
        return getEntityTranslationDefault(entity);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.potion.PotionEffectTypeHelper")
    default String getPotionEffectTypeName(PotionEffectType type) {
        return getPotionTranslationDefault(type);
    }
    @MethodTarget(isStatic = true)
    @RedirectName("getName")
    @RedirectClass("net.guizhanss.guizhanlib.minecraft.helper.DyeColorHelper")
    default String getDyeColorName(DyeColor color) {
        return getDyeColorTranslationDefault(color);
    }
}

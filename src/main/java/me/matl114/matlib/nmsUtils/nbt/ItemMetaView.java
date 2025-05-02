package me.matl114.matlib.nmsUtils.nbt;

import com.destroystokyo.paper.Namespaced;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemMetaView implements ItemMeta {
    @Override
    public boolean hasDisplayName() {
        return false;
    }

    @Override
    public @Nullable Component displayName() {
        return null;
    }

    @Override
    public void displayName(@Nullable Component component) {

    }

    @Override
    public @NotNull String getDisplayName() {
        return "";
    }

    @Override
    public @NotNull BaseComponent[] getDisplayNameComponent() {
        return new BaseComponent[0];
    }

    @Override
    public void setDisplayName(@Nullable String s) {

    }

    @Override
    public void setDisplayNameComponent(@Nullable BaseComponent[] baseComponents) {

    }

    @Override
    public boolean hasItemName() {
        return false;
    }

    @Override
    public @NotNull Component itemName() {
        return null;
    }

    @Override
    public void itemName(@Nullable Component component) {

    }

    @Override
    public @NotNull String getItemName() {
        return "";
    }

    @Override
    public void setItemName(@Nullable String s) {

    }

    @Override
    public boolean hasLocalizedName() {
        return false;
    }

    @Override
    public @NotNull String getLocalizedName() {
        return "";
    }

    @Override
    public void setLocalizedName(@Nullable String s) {

    }

    @Override
    public boolean hasLore() {
        return false;
    }

    @Override
    public @Nullable List<Component> lore() {
        return List.of();
    }

    @Override
    public void lore(@Nullable List<? extends Component> list) {

    }

    @Override
    public @Nullable List<String> getLore() {
        return List.of();
    }

    @Override
    public @Nullable List<BaseComponent[]> getLoreComponents() {
        return List.of();
    }

    @Override
    public void setLore(@Nullable List<String> list) {

    }

    @Override
    public void setLoreComponents(@Nullable List<BaseComponent[]> list) {

    }

    @Override
    public boolean hasCustomModelData() {
        return false;
    }

    @Override
    public int getCustomModelData() {
        return 0;
    }

    @Override
    public void setCustomModelData(@Nullable Integer integer) {

    }

    @Override
    public boolean hasEnchantable() {
        return false;
    }

    @Override
    public int getEnchantable() {
        return 0;
    }

    @Override
    public void setEnchantable(@Nullable Integer integer) {

    }

    @Override
    public boolean hasEnchants() {
        return false;
    }

    @Override
    public boolean hasEnchant(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public int getEnchantLevel(@NotNull Enchantment enchantment) {
        return 0;
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchants() {
        return Map.of();
    }

    @Override
    public boolean addEnchant(@NotNull Enchantment enchantment, int i, boolean b) {
        return false;
    }

    @Override
    public boolean removeEnchant(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public void removeEnchantments() {

    }

    @Override
    public boolean hasConflictingEnchant(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {

    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {

    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        return Set.of();
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        return false;
    }

    @Override
    public boolean isHideTooltip() {
        return false;
    }

    @Override
    public void setHideTooltip(boolean b) {

    }

    @Override
    public boolean hasTooltipStyle() {
        return false;
    }

    @Override
    public @Nullable NamespacedKey getTooltipStyle() {
        return null;
    }

    @Override
    public void setTooltipStyle(@Nullable NamespacedKey namespacedKey) {

    }

    @Override
    public boolean hasItemModel() {
        return false;
    }

    @Override
    public @Nullable NamespacedKey getItemModel() {
        return null;
    }

    @Override
    public void setItemModel(@Nullable NamespacedKey namespacedKey) {

    }

    @Override
    public boolean isUnbreakable() {
        return false;
    }

    @Override
    public void setUnbreakable(boolean b) {

    }

    @Override
    public boolean hasEnchantmentGlintOverride() {
        return false;
    }

    @Override
    public @NotNull Boolean getEnchantmentGlintOverride() {
        return null;
    }

    @Override
    public void setEnchantmentGlintOverride(@Nullable Boolean aBoolean) {

    }

    @Override
    public boolean isGlider() {
        return false;
    }

    @Override
    public void setGlider(boolean b) {

    }

    @Override
    public boolean isFireResistant() {
        return false;
    }

    @Override
    public void setFireResistant(boolean b) {

    }

    @Override
    public boolean hasDamageResistant() {
        return false;
    }

    @Override
    public @Nullable Tag<DamageType> getDamageResistant() {
        return null;
    }

    @Override
    public void setDamageResistant(@Nullable Tag<DamageType> tag) {

    }

    @Override
    public boolean hasMaxStackSize() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(@Nullable Integer integer) {

    }

    @Override
    public boolean hasRarity() {
        return false;
    }

    @Override
    public @NotNull ItemRarity getRarity() {
        return null;
    }

    @Override
    public void setRarity(@Nullable ItemRarity itemRarity) {

    }

    @Override
    public boolean hasUseRemainder() {
        return false;
    }

    @Override
    public @Nullable ItemStack getUseRemainder() {
        return null;
    }

    @Override
    public void setUseRemainder(@Nullable ItemStack itemStack) {

    }

    @Override
    public boolean hasUseCooldown() {
        return false;
    }

    @Override
    public @NotNull UseCooldownComponent getUseCooldown() {
        return null;
    }

    @Override
    public void setUseCooldown(@Nullable UseCooldownComponent useCooldownComponent) {

    }

    @Override
    public boolean hasFood() {
        return false;
    }

    @Override
    public @NotNull FoodComponent getFood() {
        return null;
    }

    @Override
    public void setFood(@Nullable FoodComponent foodComponent) {

    }

    @Override
    public boolean hasTool() {
        return false;
    }

    @Override
    public @NotNull ToolComponent getTool() {
        return null;
    }

    @Override
    public void setTool(@Nullable ToolComponent toolComponent) {

    }

    @Override
    public boolean hasEquippable() {
        return false;
    }

    @Override
    public @NotNull EquippableComponent getEquippable() {
        return null;
    }

    @Override
    public void setEquippable(@Nullable EquippableComponent equippableComponent) {

    }

    @Override
    public boolean hasJukeboxPlayable() {
        return false;
    }

    @Override
    public @NotNull JukeboxPlayableComponent getJukeboxPlayable() {
        return null;
    }

    @Override
    public void setJukeboxPlayable(@Nullable JukeboxPlayableComponent jukeboxPlayableComponent) {

    }

    @Override
    public boolean hasAttributeModifiers() {
        return false;
    }

    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        return null;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public @Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Attribute attribute) {
        return List.of();
    }

    @Override
    public boolean addAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier attributeModifier) {
        return false;
    }

    @Override
    public void setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> multimap) {

    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute) {
        return false;
    }

    @Override
    public boolean removeAttributeModifier(@NotNull EquipmentSlot equipmentSlot) {
        return false;
    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier attributeModifier) {
        return false;
    }

    @Override
    public @NotNull String getAsString() {
        return "";
    }

    @Override
    public @NotNull String getAsComponentString() {
        return "";
    }

    @Override
    public @NotNull CustomItemTagContainer getCustomTagContainer() {
        return null;
    }

    @Override
    public void setVersion(int i) {

    }

    @Override
    public @NotNull ItemMeta clone() {
        return null;
    }

    @Override
    public Set<Material> getCanDestroy() {
        return Set.of();
    }

    @Override
    public void setCanDestroy(Set<Material> set) {

    }

    @Override
    public Set<Material> getCanPlaceOn() {
        return Set.of();
    }

    @Override
    public void setCanPlaceOn(Set<Material> set) {

    }

    @Override
    public @NotNull Set<Namespaced> getDestroyableKeys() {
        return Set.of();
    }

    @Override
    public void setDestroyableKeys(@NotNull Collection<Namespaced> collection) {

    }

    @Override
    public @NotNull Set<Namespaced> getPlaceableKeys() {
        return Set.of();
    }

    @Override
    public void setPlaceableKeys(@NotNull Collection<Namespaced> collection) {

    }

    @Override
    public boolean hasPlaceableKeys() {
        return false;
    }

    @Override
    public boolean hasDestroyableKeys() {
        return false;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of();
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return null;
    }
}

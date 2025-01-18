package me.matl114.matlib.Utils.Version;

import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.UUID;

public interface VersionedFeature {
    public Version getVersion();
    public Enchantment getEnchantment(String name);
    public Material getMaterial(String name);
    public boolean comparePotionMeta(PotionMeta meta1,PotionMeta meta2);
    public boolean copyBlockStateTo(BlockState state1, Block block);
    public boolean differentSpecialMeta(ItemMeta meta1, ItemMeta meta2);
    public EntityType getEntityType(String name);
    public AttributeModifier createAttributeModifier(UUID uuid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot);
    public String getAttributeModifierName(AttributeModifier modifier);
    public boolean setAttributeModifierValue(AttributeModifier modifier, double value);
    public UUID getAttributeModifierUid(AttributeModifier modifier);
    public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier);
    boolean matchBlockStateMeta(BlockStateMeta meta1,BlockStateMeta meta2);
}

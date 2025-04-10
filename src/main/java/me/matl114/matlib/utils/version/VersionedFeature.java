package me.matl114.matlib.utils.version;

import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.InitializeProvider;
import me.matl114.matlib.utils.version.versionedFeatures.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.function.Consumer;

public interface VersionedFeature {

    static VersionedFeature feature = new InitializeProvider<>(()->{
        Version version = Version.getVersionInstance();
        return switch (version){
            case v1_20_R1 -> new VersionedFeature_1_20_R1_Impl();
            case v1_20_R2 -> new VersionedFeature_1_20_R2_Impl();
            case v1_20_R3 -> new VersionedFeature_1_20_R3_Impl();
            case v1_20_R4 -> new VersionedFeature_1_20_R4_Impl();
            case v1_21_R1 -> new VersionedFeature_1_21_R1_Impl();
            case v1_21_R2 -> new VersionedFeature_1_21_R2_Impl();
            default -> new DefaultVersionedFeatureImpl();
        };
    }).v();
    static VersionedFeature getFeature(){
        return feature;
    }
    public Version getVersion();
    public Enchantment getEnchantment(String name);
    public Material getMaterial(String name);
    public boolean comparePotionMeta(PotionMeta meta1,PotionMeta meta2);
    public BlockState copyBlockStateTo(BlockState state1, Block block);
    public boolean differentSpecialMeta(ItemMeta meta1, ItemMeta meta2);
    public EntityType getEntityType(String name);
    public AttributeModifier createAttributeModifier(UUID uuid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot);
    public String getAttributeModifierName(AttributeModifier modifier);
    public boolean setAttributeModifierValue(AttributeModifier modifier, double value);
    public UUID getAttributeModifierUid(AttributeModifier modifier);
    public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier);
    public boolean matchBlockStateMeta(BlockStateMeta meta1,BlockStateMeta meta2);
    public PotionEffectType getPotionEffectType(String key);
    public <T extends Entity> T spawnEntity(Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason);
}

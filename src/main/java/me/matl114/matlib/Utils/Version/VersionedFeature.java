package me.matl114.matlib.Utils.Version;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public interface VersionedFeature {
    public Version getVersion();
    public Enchantment getEnchantment(String name);
    public Material getMaterial(String name);
    public boolean comparePotionMeta(PotionMeta meta1,PotionMeta meta2);
    public boolean copyBlockStateTo(BlockState state1, Block block);
    public boolean differentSpecialMeta(ItemMeta meta1, ItemMeta meta2);
    public EntityType getEntityType(String name);
}

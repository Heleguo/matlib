package me.matl114.matlib.Utils.Version;

import lombok.Getter;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeSafeProvider;
import me.matl114.matlib.Utils.CraftUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffectType;

import java.lang.invoke.VarHandle;
import java.util.*;

public class DefaultVersionedFeatureImpl implements VersionedFeature{
    @Getter
    protected Version version;
    public DefaultVersionedFeatureImpl() {

    }
    protected final HashMap<String,String> remappingEnchantId=new HashMap<>();
    protected final EnumSet<Material> blockItemWithDifferentId=EnumSet.noneOf(Material.class);

    @Override
    public Enchantment getEnchantment(String name) {
        name=convertLegacy(name);
        return Enchantment.getByName(remappingEnchantId.getOrDefault(name,name));
    }
    public static String convertLegacy(String from) {
        if (from == null) {
            return null;
        } else {
            switch (from.toLowerCase()) {
                case "damage_all":
                    return "sharpness";
                case "arrow_fire":
                    return "flame";
                case "protection_explosions":
                    return "blast_protection";
                case "sweeping_edge":
                    return "sweeping";
                case "oxygen":
                    return "respiration";
                case "protection_projectile":
                    return "projectile_protection";
                case "loot_bonus_blocks":
                    return "fortune";
                case "water_worker":
                    return "aqua_affinity";
                case "arrow_damage":
                    return "power";
                case "luck":
                    return "luck_of_the_sea";
                case "damage_arthropods":
                    return "bane_of_arthropods";
                case "damage_undead":
                    return "smite";
                case "durability":
                    return "unbreaking";
                case "arrow_knockback":
                    return "punch";
                case "arrow_infinite":
                    return "infinity";
                case "loot_bonus_mobs":
                    return "looting";
                case "protection_environmental":
                    return "protection";
                case "dig_speed":
                    return "efficiency";
                case "protection_fall":
                    return "feather_falling";
                case "protection_fire":
                    return "fire_protection";
            }

            return from;
        }
    }


    protected final HashMap<String,String> remappingMaterialId=new HashMap<>();
    public Material getMaterial(String name) {
        return Material.getMaterial(remappingMaterialId.getOrDefault(name,name));
    }

    public BlockState copyBlockStateTo(BlockState state1, Block target){
        return WorldUtils.copyBlockState(state1,target);
    }
    protected final HashMap<String,String> remappingEntityId=new HashMap<>();
    public EntityType getEntityType(String name){
        name=name.toLowerCase(Locale.ROOT);
        return EntityType.fromName(remappingEntityId.getOrDefault(name,name));
    }

    public boolean comparePotionType(PotionMeta instanceOne, PotionMeta instanceTwo){
        return Objects.equals(instanceOne.getBasePotionData(), instanceTwo.getBasePotionData());
    }
    public boolean comparePotionMeta(PotionMeta instanceOne, PotionMeta instanceTwo){
        if(!comparePotionType(instanceOne, instanceTwo)){
            return false;
        }
        if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
            return false;
        }
        if (instanceOne.hasColor() != instanceTwo.hasColor()) {
            return false;
        }
        if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
            return false;
        }
        if (!instanceOne.getCustomEffects().equals(instanceTwo.getCustomEffects())) {
            return false;
        }
        return true;
    }
    public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo){
        // Axolotl
        if (metaOne instanceof AxolotlBucketMeta instanceOne && metaTwo instanceof AxolotlBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }

            if (!instanceOne.hasVariant() || !instanceTwo.hasVariant()) {
                return true;
            }

            if (instanceOne.getVariant() != instanceTwo.getVariant()) {
                return true;
            }
        }
        // Banner
        if (metaOne instanceof BannerMeta instanceOne && metaTwo instanceof BannerMeta instanceTwo) {
            if (instanceOne.numberOfPatterns() != instanceTwo.numberOfPatterns()) {
                return true;
            }

            if (!instanceOne.getPatterns().equals(instanceTwo.getPatterns())) {
                return true;
            }
        }
        // BlockData
        if (metaOne instanceof BlockDataMeta instanceOne && metaTwo instanceof BlockDataMeta instanceTwo) {
            if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                return true;
            }
        }
        // BlockState
        if (metaOne instanceof BlockStateMeta instanceOne && metaTwo instanceof BlockStateMeta instanceTwo) {
            if (instanceOne.hasBlockState() != instanceTwo.hasBlockState()) {
                return true;
            }



            if (!CraftUtils.matchBlockStateMetaField(instanceOne,instanceTwo)) {
                return true;
            }
        }
        // Books
        if (metaOne instanceof BookMeta instanceOne && metaTwo instanceof BookMeta instanceTwo) {
            if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getAuthor(), instanceTwo.getAuthor())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getTitle(), instanceTwo.getTitle())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getGeneration(), instanceTwo.getGeneration())) {
                return true;
            }
        }

        // Bundle
        if (metaOne instanceof BundleMeta instanceOne && metaTwo instanceof BundleMeta instanceTwo) {
            if (instanceOne.hasItems() != instanceTwo.hasItems()) {
                return true;
            }
            if (!instanceOne.getItems().equals(instanceTwo.getItems())) {
                return true;
            }
        }

        // Compass
        if (metaOne instanceof CompassMeta instanceOne && metaTwo instanceof CompassMeta instanceTwo) {
            if (instanceOne.isLodestoneTracked() != instanceTwo.isLodestoneTracked()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLodestone(), instanceTwo.getLodestone())) {
                return true;
            }
        }
        // Crossbow
        if (metaOne instanceof CrossbowMeta instanceOne && metaTwo instanceof CrossbowMeta instanceTwo) {
            if (instanceOne.hasChargedProjectiles() != instanceTwo.hasChargedProjectiles()) {
                return true;
            }
            if (!instanceOne.getChargedProjectiles().equals(instanceTwo.getChargedProjectiles())) {
                return true;
            }
        }
        // Enchantment Storage
        if (metaOne instanceof EnchantmentStorageMeta instanceOne && metaTwo instanceof EnchantmentStorageMeta instanceTwo) {
            if (instanceOne.hasStoredEnchants() != instanceTwo.hasStoredEnchants()) {
                return true;
            }
            if (!instanceOne.getStoredEnchants().equals(instanceTwo.getStoredEnchants())) {
                return true;
            }
        }
        // Firework Star
        if (metaOne instanceof FireworkEffectMeta instanceOne && metaTwo instanceof FireworkEffectMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getEffect(), instanceTwo.getEffect())) {
                return true;
            }
        }
        // Firework
        if (metaOne instanceof FireworkMeta instanceOne && metaTwo instanceof FireworkMeta instanceTwo) {
            if (instanceOne.getPower() != instanceTwo.getPower()) {
                return true;
            }
            if (!instanceOne.getEffects().equals(instanceTwo.getEffects())) {
                return true;
            }
        }
        // Leather Armor
        if (metaOne instanceof LeatherArmorMeta instanceOne && metaTwo instanceof LeatherArmorMeta instanceTwo) {
            if (!instanceOne.getColor().equals(instanceTwo.getColor())) {
                return true;
            }
        }
        // Maps
        if (metaOne instanceof MapMeta instanceOne && metaTwo instanceof MapMeta instanceTwo) {
            if (instanceOne.hasMapView() != instanceTwo.hasMapView()) {
                return true;
            }
            if (instanceOne.hasLocationName() != instanceTwo.hasLocationName()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getMapView(), instanceTwo.getMapView())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLocationName(), instanceTwo.getLocationName())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
            }
        }
        // Potion
        if (metaOne instanceof PotionMeta instanceOne && metaTwo instanceof PotionMeta instanceTwo) {
            if(!comparePotionMeta(instanceOne,instanceTwo)){
                return true;
            }
        }
        if (metaOne instanceof SuspiciousStewMeta instanceOne && metaTwo instanceof SuspiciousStewMeta instanceTwo) {
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getCustomEffects(), instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Fish Bucket
        if (metaOne instanceof TropicalFishBucketMeta instanceOne && metaTwo instanceof TropicalFishBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }
            if (!instanceOne.getPattern().equals(instanceTwo.getPattern())) {
                return true;
            }
            if (!instanceOne.getBodyColor().equals(instanceTwo.getBodyColor())) {
                return true;
            }
            if (!instanceOne.getPatternColor().equals(instanceTwo.getPatternColor())) {
                return true;
            }
        }

        // Knowledge Book
        if (metaOne instanceof KnowledgeBookMeta instanceOne && metaTwo instanceof KnowledgeBookMeta instanceTwo) {
            if (instanceOne.hasRecipes() != instanceTwo.hasRecipes()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getRecipes(), instanceTwo.getRecipes())) {
                return true;
            }
        }

        // Music Instrument
        if (metaOne instanceof MusicInstrumentMeta instanceOne && metaTwo instanceof MusicInstrumentMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getInstrument(), instanceTwo.getInstrument())) {
                return true;
            }
        }

        // Armor
        if (metaOne instanceof ArmorMeta instanceOne && metaTwo instanceof ArmorMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getTrim(), instanceTwo.getTrim())) {
                return true;
            }
        }

        return false;
    }
    public AttributeModifier createAttributeModifier(UUID uid,String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot){
        return new AttributeModifier(uid,name,amount,operation,slot);
    }
    public String getAttributeModifierName(AttributeModifier modifier){
        return modifier.getName();
    }
    FieldAccess access=FieldAccess.ofName(AttributeModifier.class,"amount");
    public boolean setAttributeModifierValue(AttributeModifier modifier, double value){
        return access.ofAccess(modifier).set(value);

    }
    public UUID getAttributeModifierUid(AttributeModifier modifier){
        return modifier.getUniqueId();
    }
    public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier){
        return modifier.getSlot();
    }
    @Getter
    private static final FieldAccess blockEntityTagAccess=FieldAccess.ofName("blockEntityTag");
    private static final VarHandle handle = new InitializeSafeProvider<>(VarHandle.class,()->{
        ItemMeta meta = new ItemStack(Material.SPAWNER).getItemMeta();
        BlockStateMeta blockState = (BlockStateMeta)meta;
        return blockEntityTagAccess.finalizeHandleOrDefault(blockState,()->null);
    }).runNonnullAndNoError(()-> Debug.logger("Successfully initialize CraftMetaBlockState.blockEntityTag VarHandle")).v();
    public boolean matchBlockStateMeta(BlockStateMeta meta1,BlockStateMeta meta2){
        return blockEntityTagAccess.compareFieldOrDefault(meta1,meta2,()->meta1.equals(meta2));
//        .ofAccess(meta1).computeIf((b)->{
//            return Objects.equals(b, blockEntityTagAccess.ofAccess(meta2).getRawOrDefault(()->null));
//        },()->meta1.equals(meta2));
    }
    protected final HashMap<NamespacedKey,NamespacedKey> remappingPotionEffect = new HashMap<>();
    protected PotionEffectType getPotionEffectByNSK(NamespacedKey key){
        return PotionEffectType.getByKey(remappingPotionEffect.getOrDefault(key,key));
    }
    public PotionEffectType getPotionEffectType(String key){
        String[] splited=key.split(":");
        if(splited.length==2){
            return getPotionEffectByNSK(new NamespacedKey(splited[0],splited[1]));
        }else{
            return getPotionEffectByNSK(NamespacedKey.minecraft(key));
        }

    }
}

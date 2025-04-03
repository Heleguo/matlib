package me.matl114.matlib.utils.version.versionedFeatures;

import com.google.common.base.Preconditions;
import me.matl114.matlib.algorithms.dataStructures.frames.InitializeSafeProvider;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.*;

import java.util.Objects;
import java.util.UUID;

public class VersionedFeature_1_21_R1_Impl extends VersionedFeature_1_20_R4_Impl{
    public VersionedFeature_1_21_R1_Impl() {
        this.version= Version.v1_21_R1;
    }
    private static final boolean hasShieldMetaInterface = new InitializeSafeProvider<>(()->{
        Class<?> testClass = ShieldMeta.class;
        Preconditions.checkArgument(testClass.isInterface());
        return true;
    },false).v();
    @Override
    public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo) {
        if (metaOne.hasJukeboxPlayable() && metaTwo.hasJukeboxPlayable()) {
            if (!Objects.equals(metaOne.getJukeboxPlayable(),metaTwo.getJukeboxPlayable())) {
                return true;
            }
        } else if (metaOne.hasJukeboxPlayable() != metaTwo.hasJukeboxPlayable()) {
            return true;
        }
        if(super.differentSpecialMeta(metaOne, metaTwo)){
            return true;
        }
        if (metaOne instanceof OminousBottleMeta instanceOne && metaTwo instanceof OminousBottleMeta instanceTwo) {
            if (instanceOne.hasAmplifier() != instanceTwo.hasAmplifier()) {
                return true;
            }

            if (instanceOne.getAmplifier() != instanceTwo.getAmplifier()) {
                return true;
            }
        }
        // Shield
        if (hasShieldMetaInterface && metaOne instanceof ShieldMeta instanceOne && metaTwo instanceof ShieldMeta instanceTwo) {
            if (Objects.equals(instanceOne.getBaseColor(), instanceTwo.getBaseColor())) {
                return true;
            }
        }
        return false;
    }
     public boolean matchBlockStateMeta(BlockStateMeta meta1, BlockStateMeta meta2){
        if(meta1.getClass()!=meta2.getClass()){
            return false;
        }
        if(hasShieldMetaInterface && (meta1 instanceof ShieldMeta meta11 && meta2 instanceof ShieldMeta meta22)){
            return true;
            //just let then gooooooooooooooooooooo fuck
           // return ;
        }else{
            return super.matchBlockStateMeta(meta1, meta2);
        }
     }
    public AttributeModifier createAttributeModifier(UUID uid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot){
        return new AttributeModifier(new NamespacedKey("minecraft",name),amount,operation,slot == null ? EquipmentSlotGroup.ANY : slot.getGroup());
    }
    public String getAttributeModifierName(AttributeModifier modifier){
        return modifier.getName();
    }
    public UUID getAttributeModifierUid(AttributeModifier modifier){
        return null;
    }
    //this is shit

//    public boolean isSpecial(ItemType itemType){
//
//        if (itemType == ItemType.WRITTEN_BOOK) {
//            return true;
//        }
//        if (itemType == ItemType.WRITABLE_BOOK) {
//            return true;
//        }
//        if (itemType == ItemType.CREEPER_HEAD || itemType == ItemType.DRAGON_HEAD
//                || itemType == ItemType.PIGLIN_HEAD || itemType == ItemType.PLAYER_HEAD
//                || itemType == ItemType.SKELETON_SKULL || itemType == ItemType.WITHER_SKELETON_SKULL
//                || itemType == ItemType.ZOMBIE_HEAD) {
//            return true;
//        }
//        if (itemType == ItemType.CHAINMAIL_HELMET || itemType == ItemType.CHAINMAIL_CHESTPLATE
//                || itemType == ItemType.CHAINMAIL_LEGGINGS || itemType == ItemType.CHAINMAIL_BOOTS
//                || itemType == ItemType.DIAMOND_HELMET || itemType == ItemType.DIAMOND_CHESTPLATE
//                || itemType == ItemType.DIAMOND_LEGGINGS || itemType == ItemType.DIAMOND_BOOTS
//                || itemType == ItemType.GOLDEN_HELMET || itemType == ItemType.GOLDEN_CHESTPLATE
//                || itemType == ItemType.GOLDEN_LEGGINGS || itemType == ItemType.GOLDEN_BOOTS
//                || itemType == ItemType.IRON_HELMET || itemType == ItemType.IRON_CHESTPLATE
//                || itemType == ItemType.IRON_LEGGINGS || itemType == ItemType.IRON_BOOTS
//                || itemType == ItemType.NETHERITE_HELMET || itemType == ItemType.NETHERITE_CHESTPLATE
//                || itemType == ItemType.NETHERITE_LEGGINGS || itemType == ItemType.NETHERITE_BOOTS
//                || itemType == ItemType.TURTLE_HELMET) {
//            return true;
//        }
//        if (itemType == ItemType.LEATHER_HELMET || itemType == ItemType.LEATHER_CHESTPLATE
//                || itemType == ItemType.LEATHER_LEGGINGS || itemType == ItemType.LEATHER_BOOTS
//                || itemType == ItemType.WOLF_ARMOR) {
//            return true;
//        }
//        if (itemType == ItemType.LEATHER_HORSE_ARMOR) {
//            return true;
//        }
//        if (itemType == ItemType.POTION || itemType == ItemType.SPLASH_POTION
//                || itemType == ItemType.LINGERING_POTION || itemType == ItemType.TIPPED_ARROW) {
//            return true;
//        }
//        if (itemType == ItemType.FILLED_MAP) {
//            return true;
//        }
//        if (itemType == ItemType.FIREWORK_ROCKET) {
//            return true;
//        }
//        if (itemType == ItemType.FIREWORK_STAR) {
//            return true;
//        }
//        if (itemType == ItemType.ENCHANTED_BOOK) {
//            return true;
//        }
//        if (itemHandle instanceof ItemBanner) {
//            return true;
//        }
//        if (itemHandle instanceof ItemMonsterEgg) {
//            return true;
//        }
//        if (itemType == ItemType.ARMOR_STAND) {
//            return true;
//        }
//        if (itemType == ItemType.KNOWLEDGE_BOOK) {
//            return true;
//        }
//        if (itemType == ItemType.FURNACE || itemType == ItemType.CHEST
//                || itemType == ItemType.TRAPPED_CHEST || itemType == ItemType.JUKEBOX
//                || itemType == ItemType.DISPENSER || itemType == ItemType.DROPPER
//                || itemHandle instanceof ItemSign || itemType == ItemType.SPAWNER
//                || itemType == ItemType.BREWING_STAND || itemType == ItemType.ENCHANTING_TABLE
//                || itemType == ItemType.COMMAND_BLOCK || itemType == ItemType.REPEATING_COMMAND_BLOCK
//                || itemType == ItemType.CHAIN_COMMAND_BLOCK || itemType == ItemType.BEACON
//                || itemType == ItemType.DAYLIGHT_DETECTOR || itemType == ItemType.HOPPER
//                || itemType == ItemType.COMPARATOR || itemType == ItemType.STRUCTURE_BLOCK
//                || blockHandle instanceof BlockShulkerBox
//                || itemType == ItemType.ENDER_CHEST || itemType == ItemType.BARREL
//                || itemType == ItemType.BELL || itemType == ItemType.BLAST_FURNACE
//                || itemType == ItemType.CAMPFIRE || itemType == ItemType.SOUL_CAMPFIRE
//                || itemType == ItemType.JIGSAW || itemType == ItemType.LECTERN
//                || itemType == ItemType.SMOKER || itemType == ItemType.BEEHIVE
//                || itemType == ItemType.BEE_NEST || itemType == ItemType.SCULK_CATALYST
//                || itemType == ItemType.SCULK_SHRIEKER || itemType == ItemType.SCULK_SENSOR
//                || itemType == ItemType.CALIBRATED_SCULK_SENSOR || itemType == ItemType.CHISELED_BOOKSHELF
//                || itemType == ItemType.DECORATED_POT || itemType == ItemType.SUSPICIOUS_SAND
//                || itemType == ItemType.SUSPICIOUS_GRAVEL || itemType == ItemType.CRAFTER
//                || itemType == ItemType.TRIAL_SPAWNER || itemType == ItemType.VAULT
//                || itemType == ItemType.CREAKING_HEART) {
//            return true;
//        }
//        if (itemType == ItemType.SHIELD) {
//            return true;
//        }
//        if (itemType == ItemType.TROPICAL_FISH_BUCKET) {
//            return true;
//        }
//        if (itemType == ItemType.AXOLOTL_BUCKET) {
//            return true;
//        }
//        if (itemType == ItemType.CROSSBOW) {
//            return true;
//        }
//        if (itemType == ItemType.SUSPICIOUS_STEW) {
//            return true;
//        }
//        if (itemType == ItemType.COD_BUCKET || itemType == ItemType.PUFFERFISH_BUCKET
//                || itemType == ItemType.SALMON_BUCKET || itemType == ItemType.ITEM_FRAME
//                || itemType == ItemType.GLOW_ITEM_FRAME || itemType == ItemType.PAINTING) {
//            return true;
//        }
//        if (itemType == ItemType.COMPASS) {
//            return true;
//        }
//        if (itemHandle instanceof BundleItem) {
//            return true;
//        }
//        if (itemType == ItemType.GOAT_HORN) {
//            return true;
//        }
//
//        if (itemType == ItemType.OMINOUS_BOTTLE) {
//            return true;
//        }
//    }
}

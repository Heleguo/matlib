package me.matl114.matlib.Utils.Version.VersionedFeatures;

import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Version.Version;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.WritableBookMeta;

import java.util.Objects;

public class VersionedFeature_1_20_R4_Impl extends VersionedFeature_1_20_R3_Impl{
    public VersionedFeature_1_20_R4_Impl() {
        this.version= Version.v1_20_R4;
    }
    {
        remappingMaterialId.put("SCUTE","TURTLE_SCUTE");
        remappingEntityId.put("mushroom_cow","mooshroom");
        remappingEntityId.put("snowman","snow_golem");
    }
    public boolean comparePotionType(PotionMeta instanceOne, PotionMeta instanceTwo){
        return instanceOne.getBasePotionType() == instanceTwo.getBasePotionType();
    }
    public BlockState copyBlockStateTo(BlockState state1, Block target){
        try{
            BlockState newBlockStae = state1.copy(target.getLocation());
            newBlockStae.update(true,false);
            return newBlockStae;
        }catch(Throwable e){
            return super.copyBlockStateTo( state1, target );
        }
    }

    @Override
    public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo) {
        if (metaOne.isFireResistant() != metaTwo.isFireResistant()) {
            return true;
        }

        // Check if unbreakable
        if (metaOne.isUnbreakable() != metaTwo.isUnbreakable()) {
            return true;
        }

        // Check if hide tooltip
        if (metaOne.isHideTooltip() != metaTwo.isHideTooltip()) {
            return true;
        }

        // Check rarity
        final boolean hasRarityOne = metaOne.hasRarity();
        final boolean hasRarityTwo = metaTwo.hasRarity();
        if (hasRarityOne) {
            if (!hasRarityTwo || metaOne.getRarity() != metaTwo.getRarity()) {
                return true;
            }
        } else if (hasRarityTwo) {
            return true;
        }

        // Check food components
        if (metaOne.hasFood() && metaTwo.hasFood()) {
            if (!Objects.equals(metaOne.getFood(), metaTwo.getFood())) {
                return true;
            }
        } else if (metaOne.hasFood() != metaTwo.hasFood()) {
            return true;
        }

        // Check tool components
        if (metaOne.hasTool() && metaTwo.hasTool()) {
            if (!Objects.equals(metaOne.getTool(), metaTwo.getTool())) {
                return true;
            }
        } else if (metaOne.hasTool() != metaTwo.hasTool()) {
            return true;
        }



        if( super.differentSpecialMeta(metaOne, metaTwo)){
            return true;
        }
        if (metaOne instanceof WritableBookMeta instanceOne && metaTwo instanceof WritableBookMeta instanceTwo) {
            if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getPages(), instanceTwo.getPages())) {
                return true;
            }
        }
        return false;
    }
    public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier){
        var slotGrop=modifier.getSlotGroup();
        return slotGrop == EquipmentSlotGroup.ANY ? null : slotGrop.getExample();
    }
}

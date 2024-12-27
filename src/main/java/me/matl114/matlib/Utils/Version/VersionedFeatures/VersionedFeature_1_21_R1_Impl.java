package me.matl114.matlib.Utils.Version.VersionedFeatures;

import me.matl114.matlib.Utils.Version.Version;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.ShieldMeta;

import java.util.Objects;

public class VersionedFeature_1_21_R1_Impl extends VersionedFeature_1_20_R4_Impl{
    public VersionedFeature_1_21_R1_Impl() {
        this.version= Version.v1_21_R1;
    }
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
        if (metaOne instanceof ShieldMeta instanceOne && metaTwo instanceof ShieldMeta instanceTwo) {
            if (Objects.equals(instanceOne.getBaseColor(), instanceTwo.getBaseColor())) {
                return true;
            }
        }
        return false;
    }
}

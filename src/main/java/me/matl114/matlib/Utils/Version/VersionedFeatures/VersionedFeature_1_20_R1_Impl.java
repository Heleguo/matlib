package me.matl114.matlib.Utils.Version.VersionedFeatures;

import me.matl114.matlib.Utils.Version.DefaultVersionedFeatureImpl;
import me.matl114.matlib.Utils.Version.Version;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nonnull;
import java.util.Locale;

public class VersionedFeature_1_20_R1_Impl extends DefaultVersionedFeatureImpl {
    public VersionedFeature_1_20_R1_Impl() {
        this.version= Version.v1_20_R1;
    }
    private static String convertToLegacy(String from) {
        if (from == null) {
            return null;
        } else {
            switch (from.toLowerCase()) {
                case "sharpness":
                    return "damage_all";
                case "flame":
                    return "arrow_fire";
                case "blast_protection":
                    return "protection_explosions";
                case "sweeping":
                    return "sweeping_edge";
                case "respiration":
                    return "oxygen";
                case "projectile_protection":
                    return "protection_projectile";
                case "fortune":
                    return "loot_bonus_blocks";
                case "aqua_affinity":
                    return "water_worker";
                case "power":
                    return "arrow_damage";
                case "luck_of_the_sea":
                    return "luck";
                case "bane_of_arthropods":
                    return "damage_arthropods";
                case "smite":
                    return "damage_undead";
                case "unbreaking":
                    return "durability";
                case "punch":
                    return "arrow_knockback";
                case "infinity":
                    return "arrow_infinite";
                case "looting":
                    return "loot_bonus_mobs";
                case "protection":
                    return "protection_environmental";
                case "efficiency":
                    return "dig_speed";
                case "feather_falling":
                    return "protection_fall";
                case "fire_protection":
                    return "protection_fire";
            }
            return from;
        }
    }
    private static String convertToBelow1_20_R1(@Nonnull String name){
        return convertToLegacy(name).toUpperCase(Locale.ROOT);
    }
    @Override
    public Enchantment getEnchantment(String name) {
        name=convertToBelow1_20_R1(name);
        return Enchantment.getByName(remappingEnchantId.getOrDefault(name,name));
    }

}

package me.matl114.matlib.utils.version.versionedFeatures;

import me.matl114.matlib.utils.version.Version;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.function.Consumer;


public class VersionedFeature_1_20_R3_Impl extends VersionedFeature_1_20_R2_Impl{
    public VersionedFeature_1_20_R3_Impl() {
        this.version= Version.v1_20_R3;
    }
    {
        remappingMaterialId.put("grass","short_grass");
    }

    public Enchantment getEnchantment(String name) {
        name=convertLegacy(name);
        return Enchantment.getByName(remappingEnchantId.getOrDefault(name,name));
    }
    public <T extends Entity> T spawnEntity(Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason) {
        return  location.getChunk().getWorld().spawn(location,clazz,consumer,reason);
    }
}

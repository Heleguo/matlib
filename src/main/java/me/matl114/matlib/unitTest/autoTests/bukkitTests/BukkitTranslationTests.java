package me.matl114.matlib.unitTest.autoTests.bukkitTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.language.lan.TranslateKeyUtils;
import me.matl114.matlib.utils.language.lan.i18n.RegistryLocalizationHelper;
import me.matl114.matlib.utils.language.lan.i18n.ZhCNLocalizationHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.version.VersionedAttribute;
import me.matl114.matlib.utils.version.VersionedRegistry;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static me.matl114.matlib.utils.language.lan.TranslateKeyUtils.*;

public class BukkitTranslationTests implements TestCase {
    @OnlineTest(name = "ENUS translation test")
    public void test_translation()throws Throwable{
        Debug.logger(getEnchantmentTranslationDefault(Enchantment.AQUA_AFFINITY));
        Debug.logger(getEntityTranslationDefault(EntityType.SPAWNER_MINECART));
        Debug.logger(getPotionTranslationDefault(PotionEffectType.FIRE_RESISTANCE));
        Debug.logger(getDyeColorTranslationDefault(DyeColor.BROWN));
        Debug.logger(getMaterialTranslateDefault(Material.CHERRY_BOAT));
        Debug.logger(getMaterialTranslateDefault(Material.PLAYER_HEAD));
        Debug.logger(getMaterialTranslateDefault(Material.CALIBRATED_SCULK_SENSOR));
        Debug.logger(getAttributeTranslationDefault(VersionedRegistry.getInstance().getAttribute("armor")));

    }


}

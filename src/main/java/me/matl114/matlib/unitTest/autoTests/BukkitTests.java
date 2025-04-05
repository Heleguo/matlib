package me.matl114.matlib.unitTest.autoTests;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class BukkitTests implements TestCase {
    @OnlineTest(name = "Unsafe value tests")
    public void test_unsafe() throws Throwable{
        ItemStack item1 = CleanItemStack.ofBukkitClean(SlimefunItems.ELECTRIC_ORE_GRINDER_3);
        Debug.logger(item1);
        byte[] bytes = Bukkit.getUnsafe().serializeItem(item1);
        List<Byte> bytes1 = new ArrayList<>();
        for (byte var : bytes){
            bytes1.add(var);
        }
      //  Debug.logger(bytes1);
       // Debug.logger(new String(bytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] var4;
        try {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);

            try {
                bukkitObjectOutputStream.writeObject(item1);
                var4 = outputStream.toByteArray();
            } catch (Throwable var9) {
                try {
                    bukkitObjectOutputStream.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            bukkitObjectOutputStream.close();
        } catch (Throwable var10) {
            try {
                outputStream.close();
            } catch (Throwable var7) {
                var10.addSuppressed(var7);
            }

            throw var10;
        }

        outputStream.close();
        List<Byte> bytes2 = new ArrayList<>();
        for (byte var : var4){
            bytes2.add(var);
        }
     //   Debug.logger(bytes2);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Debug.logger(Bukkit.getUnsafe().getProtocolVersion());
        Debug.logger(Bukkit.getUnsafe().getMainLevelName());
        Debug.logger(Bukkit.getUnsafe().getItemTranslationKey(Material.CHEST));

    }
}

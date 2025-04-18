package me.matl114.matlib.unitTest.autoTests.nmsTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.COMPOUND_TAG;

public class InventoryTests implements TestCase {
    BooleanConsumer bh ;
    @OnlineTest(name = "ItemStackHelper Test")
    public void test_ItemStackHelper(){
        CompoundTagHelper compHelper = NMSCore.COMPOUND_TAG;
        ResourceLocationHelper keyHelper = NMSCore.NAMESPACE_KEY;
        RegistriesHelper regHelper = NMSCore.REGISTRIES;
        ItemStackHelper itemHelper = NMSItem.ITEMSTACK;
        Object itm = regHelper.getRegistryByKey(BuiltInRegistryEnum.ITEM, keyHelper.newNSKey("minecraft","diamond_pickaxe"));
        Debug.logger(itm);
        Object newItemStack = itemHelper.newItemStack(itm, 3);
        Object nbt = itemHelper.getOrCreateCustomTag(newItemStack);
        ItemStack item = itemHelper.getBukkitStack(newItemStack);
        Assert(item.getAmount() == 3);
        Debug.logger(nbt);
        ItemStack itemStack = SlimefunItems.GOLD_18K;
        ItemStack cis = ItemUtils.copyStack(itemStack);
        Object handle = ItemUtils.getHandle(cis);
        Object nbt1 = itemHelper.getCustomTag(handle);
        Debug.logger(nbt1);
        Object pdc = compHelper.getCompound(nbt1, "PublicBukkitValues");
        Debug.logger(pdc);
        String id = compHelper.getString(pdc, Slimefun.getItemDataService().getKey().toString());
        Debug.logger(id);
        AssertEq(id, SlimefunItems.GOLD_18K.getItemId());
        Object emptyNbt = compHelper.newComp();
        Debug.logger( itemHelper.save(handle, emptyNbt));
        Debug.logger(emptyNbt);
        cis.lore(List.of(Component.text("你好")));
        //test eq
        var itemStack2 = cis.clone();
        var itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(List.of("c","t","m","d"));
        itemStack2.setItemMeta(itemMeta);
        var itemStack3 = cis.clone();
        itemMeta.setDisplayName("nmd");
        itemStack3.setItemMeta(itemMeta);
        //"clone" a exact same itemStack to avoid copy-on-write mechanism, because most of our comparison happens between unrelated ItemStack, not cloned or sth
        var itemStack4 = ItemUtils.cleanStack(CleanItemStack.ofBukkitClean(itemStack3));
        Debug.logger(cis);
        Debug.logger(itemStack2);
        Debug.logger(itemStack3);
        Assert(itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack2), false, true));
        Assert(!itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), true, true));
        Assert(itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), false, false));

        Assert(!itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), false, true));
        ;
        Assert(itemHelper.matchItem(ItemUtils.getHandle(itemStack2), ItemUtils.getHandle(itemStack3), true, false));
        Assert(!itemHelper.matchItem(ItemUtils.getHandle(itemStack2), ItemUtils.getHandle(itemStack3), true, true));
        Assert(itemHelper.matchItem(ItemUtils.getHandle(itemStack3), ItemUtils.getHandle(itemStack4), true, true));
//        Object handle1 =  ItemUtils.getHandle(itemStack3);
//        Object handle2 = ItemUtils.getHandle(itemStack4);
//        Assert(handle1 != handle2);
        bh =(b)->{};
        long a= System.nanoTime();
        for (int i=0;i<1000;++i){
            bh.accept(ItemUtils.matchItemStack(itemStack3, itemStack4, false, true));
        }
        long b= System.nanoTime();
        Debug.logger("check nbt time",b-a);

        a = System.nanoTime();
        ItemMeta meta1 = itemStack3.getItemMeta();
        ItemMeta meta2 = itemStack4.getItemMeta();
        for (int i=0;i<1000;++i){
//             itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
           bh.accept(meta1.equals(meta2));
//            ItemUtils.matchItemMeta(meta1, meta2, true);
        }
        b = System.nanoTime();
        Debug.logger("meta match time",b-a);
        a = System.nanoTime();
        for (int i=0;i<1000;++i){
            bh.accept(itemStack3.isSimilar(itemStack4));
        }
        b = System.nanoTime();
        Debug.logger("itemSimilar time",b-a);
        a = System.nanoTime();
        ItemMeta meta3 = itemStack3.getItemMeta();
        ItemMeta meta4 = itemStack4.getItemMeta();
        for (int i=0;i<1000;++i){
//             itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
//            meta1.equals(meta2);
            bh.accept(CraftUtils.matchItemMeta(meta3, meta4, true));
        }
        b = System.nanoTime();
        Debug.logger("utils match time",b-a);
        a = System.nanoTime();
        for (int i=0; i< 1000; ++i){
            itemStack3.getItemMeta();
        }
        b = System.nanoTime();
        Debug.logger("getItemMeta time",b-a);
        Debug.logger("Test shulker box item");
        ItemStack shulker = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
        InventoryHolder holder = (InventoryHolder) meta.getBlockState();
        Inventory inventory = holder.getInventory();
        inventory.setItem(0,new ItemStack(Material.DIAMOND));
        inventory.setItem(1, new ItemStack(Material.BOOK));
        meta.setBlockState((BlockState) holder);
        shulker.setItemMeta(meta);
        var nmsShulker = CraftBukkit.ITEMSTACK.unwrapToNMS(shulker);
        var nbtShulker = NMSItem.ITEMSTACK.save(nmsShulker, COMPOUND_TAG.newComp());
        Debug.logger(nbtShulker);
    }

}

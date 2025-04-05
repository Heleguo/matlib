package me.matl114.matlib.unitTest.autoTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.matl114.matlib.implement.bukkit.ScheduleManager;
import me.matl114.matlib.nmsMirror.impl.*;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.level.v1_20_R4.BlockEntityHelper_1_20_R4;
import me.matl114.matlib.nmsMirror.nbt.ComponentTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.nmsUtils.ServerUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.WorldUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.matl114.matlib.nmsMirror.impl.NMSLevel.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

public class NMSTests implements TestCase {
    @OnlineTest(name = "ItemStackHelper Test")
    public void test_ItemStackHelper(){
        ComponentTagHelper compHelper = NMSCore.COMPONENT_TAG;
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
        ItemStack cis = CraftUtils.getCraftCopy(itemStack);
        Object handle = CraftUtils.getHandled(cis);
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
        //test eq
        var itemStack2 = cis.clone();
        var itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(List.of("c","t","m","d"));
        itemStack2.setItemMeta(itemMeta);
        var itemStack3 = cis.clone();
        itemMeta.setDisplayName("nmd");
        itemStack3.setItemMeta(itemMeta);
        var itemStack4 = itemStack3.clone();
        Debug.logger(cis);
        Debug.logger(itemStack2);
        Debug.logger(itemStack3);
        Assert(itemHelper.matchItem(CraftUtils.getHandled(cis), CraftUtils.getHandled(itemStack2), false, true));
        Assert(!itemHelper.matchItem(CraftUtils.getHandled(cis), CraftUtils.getHandled(itemStack3), true, true));
        Assert(itemHelper.matchItem(CraftUtils.getHandled(cis), CraftUtils.getHandled(itemStack3), false, false));

        Assert(!itemHelper.matchItem(CraftUtils.getHandled(cis), CraftUtils.getHandled(itemStack3), false, true));
        ;
        Assert(itemHelper.matchItem(CraftUtils.getHandled(itemStack2), CraftUtils.getHandled(itemStack3), true, false));
        Assert(!itemHelper.matchItem(CraftUtils.getHandled(itemStack2), CraftUtils.getHandled(itemStack3), true, true));
        Assert(itemHelper.matchItem(CraftUtils.getHandled(itemStack3), CraftUtils.getHandled(itemStack4), true, true));

        for (int i=0; i< 30000; ++i){
            itemHelper.matchItem(CraftUtils.getHandled(cis), CraftUtils.getHandled(itemStack2), false, true);
            cis.getItemMeta().equals(itemStack2.getItemMeta());
        }
        long a= System.nanoTime();
        Object handle1 =  CraftUtils.getHandled(itemStack3);
        Object handle2 = CraftUtils.getHandled(itemStack4);
        Assert(handle1 != handle2);
        for (int i=0;i<1_000_000;++i){
            itemHelper.matchItem(handle1, handle2, true, true);
        }
        long b= System.nanoTime();
        Debug.logger("check nbt time",b-a);

        a = System.nanoTime();
        ItemMeta meta1 = itemStack3.getItemMeta();
        ItemMeta meta2 = itemStack4.getItemMeta();
        for (int i=0;i<1_000_000;++i){
            //itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
            //meta1.equals(meta2);
            CraftUtils.matchItemMeta(meta1, meta2, true);
        }
        b = System.nanoTime();
        Debug.logger("meta match time",b-a);
        a = System.nanoTime();
        for (int i=0;i<1_000_000;++i){
            itemStack3.isSimilar(itemStack4);
        }
        b = System.nanoTime();
        Debug.logger("itemSimilar time",b-a);
        Debug.logger("Test shulker box item");
        ItemStack shulker = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
        InventoryHolder holder = (InventoryHolder) meta.getBlockState();
        Inventory inventory = holder.getInventory();
        inventory.setItem(0,new ItemStack(Material.DIAMOND));
        inventory.setItem(1, new ItemStack(Material.BOOK));
        meta.setBlockState((BlockState) holder);
        shulker.setItemMeta(meta);
        var nmsShulker = CraftBukkit.CRAFT_ITEMSTACK.unwrapToNMS(shulker);
        var nbtShulker = NMSItem.ITEMSTACK.save(nmsShulker, COMPONENT_TAG.newComp());
        Debug.logger(nbtShulker);
    }

    @OnlineTest(name = "Env and Enum tests")
    public void test_env() throws Throwable{
        Assert(CraftBukkit.MAGIC_NUMBERS.getBlock(Material.AIR) == EmptyEnum.BLOCK_AIR);
        Assert(CraftBukkit.MAGIC_NUMBERS.getItem(Material.AIR) == EmptyEnum.ITEM_AIR);
        var itemA  = CraftBukkit.CRAFT_ITEMSTACK.unwrapToNMS(new CleanItemStack(Material.BOOK, 336));
        Debug.logger(itemA);
        Assert(NMSItem.ITEMSTACK.getCount(itemA) == 336 );
        var cisA = CraftBukkit.CRAFT_ITEMSTACK.createCraftItemStack(Material.BOOK, 23,null);
        Debug.logger(cisA);
        Assert(CraftUtils.isCraftItemStack(cisA));

    }

    @OnlineTest(name = "World and blockEntity tests")
    public void test_worldAndBlockEntity() throws Throwable {
        int x =2;
        int y = 36;
        int z = 36;
        Block block = testWorld().getBlockAt(2,36,36);
        ServerUtils.executeFuture(()->{
            Assert(Bukkit.isPrimaryThread());
            block.setType(Material.CHEST);
            Assert(block.getState(false) instanceof TileState);
            ((TileState)block.getState(false)).getPersistentDataContainer().set(new NamespacedKey("matlibtest","test"), PersistentDataType.STRING,"testvalue");
            ((InventoryHolder)block.getState(false)).getInventory().setItem(10,new ItemStack(Material.DIAMOND));
            return null;
        }).get();
        int cx = x >> 4;
        int cz = z >> 4;
        block.getChunk();
        var serverLevel = WorldUtils.getHandledWorld(testWorld());
        var chunk1 = NMSLevel.LEVEL.getChunkIfLoadedImmediately(serverLevel, cx, cz);
        AssertNN(chunk1);
        Debug.logger(chunk1);
        var entity1 = NMSLevel.LEVEL_CHUNK.getBlockEntity(chunk1, NMSCore.BLOCKPOS.ofVec(x,y,z));
        AssertNN(entity1);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));
        Debug.logger(BLOCK_ENTITY.saveWithId(entity1));
        Object pdc =  BLOCK_ENTITY.getPersistentDataCompound(entity1, true);
        COMPONENT_TAG.clear(pdc);
        COMPONENT_TAG.putBoolean(pdc, "testBoolean",true);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));

    }
}

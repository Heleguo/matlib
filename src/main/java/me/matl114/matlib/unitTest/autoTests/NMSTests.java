package me.matl114.matlib.unitTest.autoTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.matl114.matlib.algorithms.algorithm.ThreadUtils;
import me.matl114.matlib.nmsMirror.impl.*;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.nmsUtils.LevelUtils;
import me.matl114.matlib.nmsUtils.ServerUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.WorldUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import static me.matl114.matlib.nmsMirror.impl.NMSLevel.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

public class NMSTests implements TestCase {
    static TestReflection TEST = DescriptorImplBuilder.createMultiHelper(TestReflection.class);
    @OnlineTest(name = "ItemStackHelper Test")
    public void test_ItemStackHelper(){
        CompoundTagHelper compHelper = NMSCore.COMPONENT_TAG;
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
            handle1 =  CraftUtils.getHandled(itemStack3);
            handle2 = CraftUtils.getHandled(itemStack4);
            itemHelper.matchItem(handle1, handle2, true, true);
        }
        long b= System.nanoTime();
        Debug.logger("check nbt time",b-a);

        a = System.nanoTime();
        ItemMeta meta1 = itemStack3.getItemMeta();
        ItemMeta meta2 = itemStack4.getItemMeta();
        for (int i=0;i<1_000_000;++i){
           // itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
            meta1.equals(meta2);
//            CraftUtils.matchItemMeta(meta1, meta2, true);
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
        var nmsShulker = CraftBukkit.ITEMSTACK.unwrapToNMS(shulker);
        var nbtShulker = NMSItem.ITEMSTACK.save(nmsShulker, COMPONENT_TAG.newComp());
        Debug.logger(nbtShulker);
    }

   // @OnlineTest(name = "Env and Enum tests")
    public void test_env() throws Throwable{
        Assert(CraftBukkit.MAGIC_NUMBERS.getBlock(Material.AIR) == EmptyEnum.BLOCK_AIR);
        Assert(CraftBukkit.MAGIC_NUMBERS.getItem(Material.AIR) == EmptyEnum.ITEM_AIR);
        var itemA  = CraftBukkit.ITEMSTACK.unwrapToNMS(new CleanItemStack(Material.BOOK, 336));
        Debug.logger(itemA);
        Assert(NMSItem.ITEMSTACK.getCount(itemA) == 336 );
        var cisA = CraftBukkit.ITEMSTACK.createCraftItemStack(Material.BOOK, 23,null);
        Debug.logger(cisA);
        Assert(CraftUtils.isCraftItemStack(cisA));

    }

   // @OnlineTest(name = "World and blockEntity tests")
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
        var serverLevel = WorldUtils.getHandledWorld(testWorld());
        testWorld().getChunkAt(cx, cz);
        var chunk1 = LEVEL.getChunkCustom(serverLevel, cx, cz, true);
        AssertNN(chunk1);
        Debug.logger(chunk1);
        var entity1 = LevelUtils.getBlockEntityAsync(block, false);
        AssertNN(entity1);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));
        Debug.logger(BLOCK_ENTITY.saveWithId(entity1));
        Object pdc =  BLOCK_ENTITY.getPersistentDataCompound(entity1, true);
        COMPONENT_TAG.clear(pdc);
        COMPONENT_TAG.putBoolean(pdc, "testBoolean",true);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));

        Debug.logger("test block getter");
        Debug.logger(block.getType());
        long a = System.nanoTime();
        for (int i=0; i< 99_999; ++i){
           block.getType();
        }
        long b = System.nanoTime();
        Debug.logger("using time ",b-a);
        Debug.logger(LevelUtils.getBlockTypeAsync(block,true));
        a = System.nanoTime();
        for (int i=0; i< 99_999; ++i){
            //LEVEL.getBlockStateCustom(serverLevel, block.getX(), block.getY(), block.getZ(),true);
            LevelUtils.getBlockTypeAsync(block,true);
        }
        b = System.nanoTime();
        Debug.logger("using time ",b-a);
    }

    //@OnlineTest(name = "minecraft schedular test")
    public void test_schedular() throws Throwable {
        World world =testWorld();
        for (int i=0;i<10;++i){
            long a = System.nanoTime();
            FutureTask<Void> task = ThreadUtils.getFutureTask(()->{
                long b = System.nanoTime();
                Debug.logger("Main Executor Response Time", b-a);
                Assert(Bukkit.isPrimaryThread());
            });
            ServerUtils.executeSync(task);
            task.get();
        }
        for (int i=0;i<10;++i){
            long c = System.nanoTime();
            FutureTask<Void> task = ThreadUtils.getFutureTask(()->{
                long b = System.nanoTime();
                Debug.logger("World Main Executor Response Time", b-c);
                Assert(Bukkit.isPrimaryThread());
            });
            ServerUtils.executeAsChunkTask(world, task);
            task.get();
        }
        for (int i=0;i<10;++i){
            long c = System.nanoTime();
            FutureTask<Void> task = ThreadUtils.getFutureTask(()->{
                long b = System.nanoTime();
                Debug.logger("Bukkit Schedular Response Time", b-c);
                Assert(Bukkit.isPrimaryThread());
            });
            ThreadUtils.executeSync(task);
            task.get();
        }
        ServerUtils.executeSync(()->{
            Debug.logger("throw a fucking exception on main ");
            throw  new RuntimeException();
        });
        ThreadUtils.sleep(50);

    }
    @OnlineTest(name = "special test ",automatic = false)
    public void test_test() throws Throwable{
        if(REGISTRIES.containsKey(BuiltInRegistryEnum.ITEM, NAMESPACE_KEY.newNSKey("minecraft","myitem"))){
            Debug.logger("already registered");
            return;
        }
        TEST.frozenSetter( BuiltInRegistryEnum.ITEM, false);
        TEST.unregisteredIntrusiveHoldersSetter(BuiltInRegistryEnum.ITEM, new IdentityHashMap<>());
        Object newItem = TEST.newItem(TEST.newProperties());
        Object item =  TEST.registerItem("myitem", newItem);
        TEST.freeze( BuiltInRegistryEnum.ITEM);
        Debug.logger(item);
        CraftBukkit.MAGIC_NUMBERS.ITEM_MATERIALGetter().put(item, Material.END_GATEWAY);
        CraftBukkit.MAGIC_NUMBERS.MATERIAL_ITEMGetter().put(Material.END_GATEWAY, item);
    }

    //@OnlineTest(name = "package private access test")
    public void test_access()throws  Throwable{
        Debug.logger(NMSItem.CONTAINER.newCustomContainer(null, 1, "byd").getClass());
    }
    @MultiDescriptive(targetDefault = "wtf")
    public interface TestReflection extends TargetDescriptor{
        @FieldTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void frozenSetter(Object obj, boolean f);
        @FieldTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void unregisteredIntrusiveHoldersSetter(Object obj, Map<?,?> map);

        @MethodTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void freeze(Object reg);

        @MethodTarget(isStatic = true)
        @RedirectClass("net.minecraft.world.item.Items")
        Object registerItem(String id,@RedirectType("Lnet/minecraft/world/item/Item;") Object item);

        @ConstructorTarget
        @RedirectClass("net.minecraft.world.item.Item")
        Object newItem(@RedirectType("Lnet/minecraft/world/item/Item$Properties;")Object properties);


        @ConstructorTarget
        @RedirectClass("net.minecraft.world.item.Item$Properties")
        Object newProperties();
    }
}

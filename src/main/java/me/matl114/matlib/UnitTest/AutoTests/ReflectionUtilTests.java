package me.matl114.matlib.UnitTest.AutoTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.CraftUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Inventory.ItemStacks.CleanItemStack;
import me.matl114.matlib.Utils.Reflect.ByteCodeUtils;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.ProxyUtils;
import me.matl114.matlib.Utils.Version.DefaultVersionedFeatureImpl;
import me.matl114.matlib.Utils.WorldUtils;
import me.matl114.matlibAdaptor.Algorithms.DataStructures.LockFactory;
import me.matl114.matlibAdaptor.Algorithms.Interfaces.Initialization;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.Map;

public class ReflectionUtilTests implements TestCase {
    @OnlineTest(name = "CraftUtils VarHandle test")
    public void testDisplayVarHandle(){
        ItemStack item = new CleanItemStack(Material.BOOK,"这是&a一个&c书","这&e是一本&r书","这并&6不是两&3本书");
        AddUtils.addGlow(item);
        ItemMeta meta = item.getItemMeta();
        Debug.logger(CraftUtils.getDisplayNameHandle().get(meta));
        Debug.logger(CraftUtils.getLoreHandle().get(meta));
        item = CraftUtils.getCraftCopy(item,true);
        Debug.logger(item);
        Assert(CraftUtils.isCraftItemStack(item));
        Debug.logger(CraftUtils.getHandled(item));
        ItemMeta meta2 = item.getItemMeta();
        Assert(CraftUtils.matchDisplayNameField(meta,meta2));
        Assert(CraftUtils.matchLoreField(meta,meta2));
        Assert(CraftUtils.matchEnchantmentsFields(meta,meta2));
        Debug.logger(CraftUtils.getEnchantmentsHandle().get(meta2));
        Assert( ((Map)CraftUtils.getEnchantmentsHandle().get(meta)).size()==1);
        Assert(CraftUtils.matchItemStack(item,item,true));
        ItemStack blockStateItem = new CleanItemStack(Material.SPAWNER);
        ItemMeta blockStateMeta = blockStateItem.getItemMeta();
        Assert(blockStateMeta instanceof BlockStateMeta );
        BlockStateMeta blockState = (BlockStateMeta) blockStateMeta;
        BlockState blockStateThis = blockState.getBlockState();
        Assert(blockStateThis instanceof CreatureSpawner);
        CreatureSpawner spawner = (CreatureSpawner) blockStateThis;
        spawner.setSpawnedType(EntityType.ZOMBIE);
        spawner.setSpawnRange(114);
        blockState.setBlockState(spawner);
        Assert(CraftUtils.matchBlockStateMetaField(blockState,blockState));
        var reflectAsm = DefaultVersionedFeatureImpl.getBlockEntityTagAccess().getReflectAsm();
        Assert( reflectAsm );
        Debug.logger(reflectAsm.getA(),reflectAsm.getA().getClass(),reflectAsm.getB());
        try{
            reflectAsm.getA().get(blockState,reflectAsm.getB());
        }catch (Throwable protect){
        }
        Debug.logger(blockState.getClass(),blockState.getClass().getClassLoader(), ClassLoader.getSystemClassLoader());
    }


    @OnlineTest(name = "CraftUtils Invoker test")
    public void testCraftInvoker(){
        ItemStack item = new CleanItemStack(Material.BOOK);
        ItemStack citem = CraftUtils.getCraftCopy(item);
        Debug.logger(citem);
        Debug.logger(citem.getClass());
//        watch me.matl114.matlib.UnitTest.AutoTests.ReflectionUtilTests testCraftInvoker '{params,returnObj,throwExp}'  -n 5  -x 3
//        watch net.minecraft.server.network.PlayerConnection a '{params}' -n 5
        Assert(CraftUtils.isCraftItemStack(citem));
        Object nmsItem = CraftUtils.getNMSCopy(item);
        Debug.logger(nmsItem);
        Debug.logger(nmsItem.getClass());
        Assert(CraftUtils.isNMSItemStack(nmsItem));
        Debug.logger("Test Success");
    }

    @OnlineTest(name = "Paper Obf utils test")
    public void test_paperobf() throws Throwable{
        Class obfClass = Class.forName("io.papermc.paper.util.ObfHelper");
        var instance =  Enum.valueOf(obfClass, "INSTANCE");
        Map<String,?> re = (Map<String, ?>) FieldAccess.ofName(obfClass,"mappingsByObfName").ofAccess(instance).getRaw();
        //Debug.logger(re);
        Debug.logger(re.size());
        Object classMapper = re.get( WorldUtils.getTileEntityClass().getName());
        //Debug.logger( classMapper);
        String des = ByteCodeUtils.getMethodDescriptor( WorldUtils.getTileEntitySetChangeAccess().getMethodOrDefault(()->null));
        Debug.logger(des);
        FieldAccess methodObf = FieldAccess.ofName(classMapper.getClass(),"methodsByObf");
        Debug.logger( ((Map<String,?>)(methodObf.getValue(classMapper))).get(des));
    }
    @OnlineTest(name = "MatlibAdaptor Test")
    public void testAPI() throws Throwable {
        Class logiTech = Class.forName("me.matl114.logitech.MyAddon");
        Debug.logger(logiTech.getName());
        FieldAccess initAccess = FieldAccess.ofName(logiTech,"matlibInstance");
        Object instance= initAccess.initWithNull().getValue(null);
        Debug.logger(instance.getClass().getName());
        Debug.logger(instance instanceof Initialization);
        Initialization init = ProxyUtils.buildAdaptorOf(Initialization.class, instance);
        Debug.logger(init);
        Debug.logger(init.getClass());
        Debug.logger(init.getClass().getSimpleName());
        Debug.logger(init.getDisplayName());
        Debug.logger(init.getLogger());
        long start = System.nanoTime();
        String value = null;
        for (int i=0;i<1_000_000;++i){
            value = init.getDisplayName();
        }
        long end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 invocation",end-start,value);
        Method method = instance.getClass().getMethod("getDisplayName");
        start = System.nanoTime();
        value = null;
        for (int i=0;i<1_000_000;++i){
            method.invoke(instance);
        }
        end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 reflection",end-start,value);

        //DO NOT CALL METHOD WITH OUR CLASS RETURN VALUE ,OTHERWISE CLASS CAST EXCEPTION WILL OCCURS
        Debug.logger(init.isTestMode());
        Object access = me.matl114.matlib.Utils.Reflect.MethodAccess.ofName(Slimefun.class,"getCargoLockFactory")
            .noSnapShot()
            .initWithNull()
            .invoke(null);
        Debug.logger(access);
        LockFactory<Location> locationLockFactory = ProxyUtils.buildAdaptorOf(LockFactory.class, access);
        Debug.logger(locationLockFactory);
        Debug.logger(locationLockFactory.getClass().getSimpleName());
        Debug.logger(locationLockFactory.checkThreadStatus(new Location(testWorld(),0,0,0)));
    }
}

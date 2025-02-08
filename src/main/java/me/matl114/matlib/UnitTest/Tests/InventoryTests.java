package me.matl114.matlib.UnitTest.Tests;

import me.matl114.matlib.Implements.Managers.ScheduleManager;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryTests implements TestCase {

    @OnlineTest(name = "Inventory validation test")
    public void test_Inventory(){
        World thisWorld = Bukkit.getWorlds().get(0);
        Location testLocation = new Location(thisWorld,0,0,0);
        Block testBlock = testLocation.getBlock();
        Debug.logger("Running test in world: " + thisWorld.getName());
        Debug.logger("Launch scheduled sync task");
        FutureTask<Void> createChest =  ScheduleManager.getManager().getScheduledFuture(()->{
            testBlock.setType(Material.CHEST);
            Debug.logger("Create a chest");
            return null;
        },40,true);
        AtomicReference<BlockState> blockStateHere = new AtomicReference<>(null);
        AtomicReference<Inventory> inventoryHere = new AtomicReference<>(null);
        FutureTask<Void> getChestBlockState = ScheduleManager.getManager().getScheduledFuture(()->{
            blockStateHere.set(WorldUtils.getBlockStateNoSnapShot(testBlock));
            Assert(blockStateHere.get() instanceof InventoryHolder);
            inventoryHere.set(((InventoryHolder) blockStateHere.get()).getInventory());
            Assert(inventoryHere.get());
            Debug.logger("get chest block state: " + blockStateHere.get());
            return null;
        },80,true);
        try{
            createChest.get();
            getChestBlockState.get();
        }catch (Throwable e){
            Debug.logger("Error :");
            Debug.logger(e);
        }
        Debug.logger("Back to Async Thread");
        Debug.logger("check inventory here ");
        Inventory inventory = inventoryHere.get();
        Assert(inventory);
        Debug.logger("check inventory slot ");
        inventory.setItem(0,new ItemStack(Material.CHEST));
        Assert(inventory.getItem(0).getType()==Material.CHEST);
        Debug.logger("check async blockState function");
        TileState tileState = (TileState)blockStateHere.get();
        Assert(tileState.getPersistentDataContainer());
        Assert(tileState.isPlaced());
        Assert(WorldUtils.getCraftBlockEntityStateClass().isInstance(tileState));
        Object tileEntity =WorldUtils.getTileEntityHandle().get(tileState);
        Debug.logger("check tileEntity ");
        Assert(WorldUtils.getTileEntityClass().isInstance(tileEntity));
        Debug.logger("check tile Entity Removal");
        Assert(!((boolean) WorldUtils.getTileEntityRemovalHandle().get(tileEntity)));
        AtomicReference<BlockState> newState = new AtomicReference<>(null);
        FutureTask<Void> deleteChest = ScheduleManager.getManager().getScheduledFuture(()->{
            testBlock.setType(Material.AIR);
            newState.set(WorldUtils.getBlockStateNoSnapShot(testBlock));
            Debug.logger("Delete a chest");
            return null;
        },40,true);
        try{
            deleteChest.get();
        }catch (Throwable e){}
        Debug.logger("check new BlockState");
        Assert(!WorldUtils.getCraftBlockEntityStateClass().isInstance(newState.get()));
        Debug.logger("check inventory status after delete");
        Assert(inventory.getItem(0).getType()==Material.CHEST);
        inventory.setItem(0,new ItemStack( Material.DIAMOND));
        Debug.logger("check inventory update after delete");
        Assert(inventory.getItem(0).getType()==Material.DIAMOND);
        Debug.logger("check tile Entity Removal");
        Assert(WorldUtils.getTileEntityRemovalHandle().get(tileEntity));
        long start = System.nanoTime();
        for (int i=0;i<1_000_000;++i){
            //tileEntityRemovalHandle.get(tileEntityHandle.get(tileState));
            WorldUtils.isTileEntityStillValid(tileState);
            //24112100
        }
        long end = System.nanoTime();
        Debug.logger("Test Accessing tileState removal flag: cost",end-start);
        Debug.logger("check safety of Async inventory operation");
        final AtomicReference<Inventory> inv = new AtomicReference<>(null);
        var iteration=WorldUtils.getInventoryHolderTypes();
        while(iteration.hasNext()){
            var mat = iteration.next();
            FutureTask<Inventory> materialInventory = ScheduleManager.getManager().getScheduledFuture(()->{
                testBlock.setType(Material.AIR);
                testBlock.setType(mat);
                BlockState state=WorldUtils.getBlockStateNoSnapShot(testBlock);
                Assert(state instanceof InventoryHolder);
                return ((InventoryHolder) state).getInventory();
            },0,true);
            try{
                doAsyncInventoryTest(materialInventory.get(),mat);
            }catch (Throwable e){}
        }
    }
    public void doAsyncInventoryTest(Inventory testInventory,Material material){
        try{
            if(testInventory.getSize()>0){
                //check set
                testInventory.setItem(0,new ItemStack( Material.DIAMOND));
                Assert(testInventory.getItem(0).getType()==Material.DIAMOND);
                //check update
                testInventory.setItem(0,new ItemStack( Material.CHEST));
                Assert(testInventory.getItem(0).getType()==Material.CHEST);
            }else{
                Debug.logger("Zero size Inventory: ",testInventory);
            }
        }catch (Throwable e){
            Debug.logger("Validate unsafe inventory type:",testInventory.getType());
            Assert(!WorldUtils.isInventoryTypeAsyncSafe(testInventory.getType()));
        }
    }
}

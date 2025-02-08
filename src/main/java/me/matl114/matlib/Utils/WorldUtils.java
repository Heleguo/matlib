package me.matl114.matlib.Utils;

import lombok.Getter;
import me.matl114.matlib.UnitTest.Tests.InventoryTests;
import me.matl114.matlib.Utils.Algorithm.InitializeProvider;
import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;

public class WorldUtils {

    private static final BlockState sampleBlockState = new InitializeSafeProvider<>(BlockState.class,()->{
        try{
            return Material.STONE.createBlockData().createBlockState();
        }catch (Throwable versionTooEarlyError){
            World sampleWorld= Bukkit.getWorlds().get(0);
            return sampleWorld.getBlockAt(0, 0, 0).getState();
        }
    }).v();
    private static final Class craftBlockStateClass = new InitializeSafeProvider<>(Class.class,()->{
        var result= ReflectUtils.getFieldsRecursively(sampleBlockState.getClass(),"data");
        return result.getB();
    }).v();
    private static final FieldAccess iBlockDataFieldAccess = new InitializeSafeProvider<>(()->{
        var result= ReflectUtils.getFieldsRecursively(craftBlockStateClass,"data");
        var IBlockDataField=result.getA();
        IBlockDataField.setAccessible(true);
        return FieldAccess.of(IBlockDataField);
    },FieldAccess.ofFailure()).v();
    private static final FieldAccess blockPositionFieldAccess = new InitializeSafeProvider<>(()->{
        var BlockPositionField=ReflectUtils.getFieldsRecursively(craftBlockStateClass,"position").getA();
        BlockPositionField.setAccessible(true);
        return FieldAccess.of(BlockPositionField);
    },FieldAccess.ofFailure()).v();
    private static final FieldAccess worldFieldAccess = new InitializeSafeProvider<>(()->{
        var WorldField=ReflectUtils.getFieldsRecursively(craftBlockStateClass,"world").getA();
        WorldField.setAccessible(true);
        return FieldAccess.of(WorldField);
    },FieldAccess.ofFailure()).v();
    private static final FieldAccess weakWorldFieldAccess = new InitializeSafeProvider<>(()->{
        var WeakWorldField=ReflectUtils.getFieldsRecursively(craftBlockStateClass,"weakWorld").getA();
        WeakWorldField.setAccessible(true);
        return FieldAccess.of(WeakWorldField);
    },FieldAccess.ofFailure()).v();
    private static final boolean invokeBlockStateSuccess = blockPositionFieldAccess!=FieldAccess.ofFailure()&&worldFieldAccess!=FieldAccess.ofFailure()&&weakWorldFieldAccess!=FieldAccess.ofFailure();

    private static final VarHandle positionHandle = new InitializeSafeProvider<>(VarHandle.class,()->{
       return blockPositionFieldAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize Blockstate.position VarHandle")).v();
    private static final VarHandle worldHandle = new InitializeSafeProvider<>(VarHandle.class,()->{
        return worldFieldAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize Blockstate.world VarHandle")).v();
    private static  final VarHandle weakWorldHandle = new InitializeSafeProvider<>(VarHandle.class,()->{
        return weakWorldFieldAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize Blockstate.weakWorld VarHandle")).v();
    private static final boolean handleBlockStateSuccess = positionHandle!=null&&worldHandle!=null&&weakWorldHandle!=null;

    public static BlockState copyBlockState(BlockState state, Block block2){
        if(invokeBlockStateSuccess){
            BlockState state2=block2.getState();
            if(craftBlockStateClass.isInstance(state2)&&craftBlockStateClass.isInstance(state)){
                if(handleBlockStateSuccess){
                    try{
                        positionHandle.set(state,positionHandle.get(state2));
                        worldHandle.set(state,worldHandle.get(state2));
                        weakWorldHandle.set(state,weakWorldHandle.get(state2));
                        state.update(true,false);
                        return state;
                    }catch (Throwable unexpected){}
                }
                try{
                    blockPositionFieldAccess.ofAccess(state).set(blockPositionFieldAccess.getValue(state2));
                    worldFieldAccess.ofAccess(state).set(worldFieldAccess.getValue(state2));
                    worldFieldAccess.ofAccess(state).set(worldFieldAccess.getValue(state2));
                    weakWorldFieldAccess.ofAccess(state).set(weakWorldFieldAccess.getValue(state2));
                    state.update(true,false);
                    return state;
                }catch (Throwable e){
                    return null;
                }
            }else return null;
        }else return null;
    }
    private static final boolean hasPaperLib = new InitializeProvider<>(()->{
        try{
            return io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib.isPaper();
        }catch(Throwable unexpected){
            return false;
        }
    }).v();
    private static final MethodAccess<?> getStateNoSnapshotAccess = new InitializeSafeProvider<>(MethodAccess.class,()->{
        try {
            return MethodAccess.of( Block.class.getDeclaredMethod("getState",boolean.class) );
        } catch (NoSuchMethodException e) {
            return MethodAccess.ofFailure();
        }
    }).v();
    //paper environment only;
    private static final MethodHandle getStateNoSnapshotHandle = new InitializeSafeProvider<>(MethodHandle.class,()->{
        return getStateNoSnapshotAccess.getMethodHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize Blockstate.getState MethodHandle")).v();

    public static BlockState getBlockStateNoSnapShot(Block block){
        if(hasPaperLib){
            return io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib.getBlockState(block,false).getState();
        }
        if(getStateNoSnapshotHandle!=null){
            try{
                return (BlockState)getStateNoSnapshotHandle.invokeExact(block,false);
            }catch (Throwable ignored){}
        }
        return block.getState();
    }
    @Getter
    private static final Class<?> craftBlockEntityStateClass = new InitializeSafeProvider<>(Class.class,()->{
        try{
            ItemStack spawner = new ItemStack(Material.CHEST);
            ItemMeta meta = spawner.getItemMeta();
            BlockStateMeta blockMeta = (BlockStateMeta)meta;
            BlockState state = blockMeta.getBlockState();
            Class<?> type = state.getClass();
            Class<?> oldType ;
            do{
                oldType = type;
                type = type.getSuperclass();
            }while(TileState.class.isAssignableFrom(type));
            return oldType;
        }catch (Throwable e){
            return null;
        }
    }).v();
    private static final FieldAccess tileEntityAccess = FieldAccess.ofName(craftBlockEntityStateClass,"tileEntity").printError(true);
    @Getter
    private static final Class<?> tileEntityClass = new InitializeSafeProvider<>(Class.class,()->{
        Field tileEntityField = tileEntityAccess.getFieldOrDefault(()->null);
        return tileEntityField.getType();
    }).v();
    private static final FieldAccess tileEntityRemovalAccess = new FieldAccess((ignored)->{
        return ReflectUtils.getFirstFitField(tileEntityClass,boolean.class,false);
    }).initWithNull();
    @Getter
    private static final VarHandle tileEntityHandle = new InitializeSafeProvider<>(VarHandle.class,()->{
        return tileEntityAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize BlockEntityState.tileEntity VarHandle")).v();
    @Getter
    private static final VarHandle tileEntityRemovalHandle = new InitializeSafeProvider<>(VarHandle.class,()->{
        return tileEntityRemovalAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize TileEntity.remove VarHandle")).v();

    public static boolean isTileEntityStillValid(TileState tile){
        if(craftBlockEntityStateClass.isInstance(tile)){
            Object tileEntity = tileEntityHandle.get(tile);
            return tileEntity != null && !((boolean) tileEntityRemovalHandle.get(tileEntity));
        }else {
            //they may get a wrong state ,so we suppose that the origin state is removed
            return false;
        }
    }

    private static final EnumSet<Material> TILE_ENTITIES_MATERIAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> INVENTORYHOLDER_MATERIAL = EnumSet.noneOf(Material.class);
    static {
        for(Material material : Material.values()){
            if(material.isBlock()){
                try{
                    BlockState sampleBlockState =  material.createBlockData().createBlockState();
                    if(sampleBlockState instanceof TileState){
                        TILE_ENTITIES_MATERIAL. add(material);
                    }
                    if(sampleBlockState instanceof InventoryHolder){
                        INVENTORYHOLDER_MATERIAL.add(material);
                    }
                }catch (Throwable e){
                }
            }
        }
    }
    private static boolean isInventoryTypeCommon(InventoryType inventoryType){
        return inventoryType!=InventoryType.CHISELED_BOOKSHELF && inventoryType!=InventoryType.JUKEBOX;
    }
    public static boolean isInventoryTypeAsyncSafe(InventoryType inventoryType){
        return inventoryType!=InventoryType.LECTERN && isInventoryTypeCommon(inventoryType);
    }
    public static boolean isTileEntity(Material material){
        return TILE_ENTITIES_MATERIAL.contains(material);
    }
    public static Iterator<Material> getTileEntityTypes(){
        return TILE_ENTITIES_MATERIAL.iterator();
    }
    public static boolean isInventoryHolder(Material material){
        return INVENTORYHOLDER_MATERIAL.contains(material);
    }
    public static Iterator<Material> getInventoryHolderTypes(){
        return INVENTORYHOLDER_MATERIAL.iterator();
    }

}

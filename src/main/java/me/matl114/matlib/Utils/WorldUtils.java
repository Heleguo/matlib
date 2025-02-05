package me.matl114.matlib.Utils;

import com.google.errorprone.annotations.Var;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
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
import org.bukkit.loot.LootTable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class WorldUtils {
    //fixme
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
            return PaperLib.isPaper();
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
            return PaperLib.getBlockState(block,false).getState();
        }
        if(getStateNoSnapshotHandle!=null){
            try{
                return (BlockState)getStateNoSnapshotHandle.invokeExact(block,false);
            }catch (Throwable ignored){}
        }
        return block.getState();
    }

}

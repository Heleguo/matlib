package me.matl114.matlib.Utils;

import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.loot.LootTable;

public class WorldUtils {
    protected static Class CraftBlockStateClass;
    protected static boolean invokeBlockStateSuccess=false;
    protected static FieldAccess iBlockDataFieldAccess;
    protected static FieldAccess blockPositionFieldAccess;
    protected static FieldAccess worldFieldAccess;
    protected static FieldAccess weakWorldFieldAccess;
    static {
        try{
            World sampleWorld= Bukkit.getWorlds().get(0);
            BlockState blockstate=sampleWorld.getBlockAt(0, 0, 0).getState();
            var result= ReflectUtils.getFieldsRecursively(blockstate.getClass(),"data");
            var IBlockDataField=result.getFirstValue();
            IBlockDataField.setAccessible(true);
            iBlockDataFieldAccess=FieldAccess.of(IBlockDataField);
            CraftBlockStateClass=result.getSecondValue();
            var BlockPositionField=ReflectUtils.getFieldsRecursively(CraftBlockStateClass,"position").getFirstValue();
            BlockPositionField.setAccessible(true);
            blockPositionFieldAccess=FieldAccess.of(BlockPositionField);
            var WorldField=ReflectUtils.getFieldsRecursively(CraftBlockStateClass,"world").getFirstValue();
            WorldField.setAccessible(true);
            worldFieldAccess=FieldAccess.of(WorldField);
            var WeakWorldField=ReflectUtils.getFieldsRecursively(CraftBlockStateClass,"weakWorld").getFirstValue();
            WeakWorldField.setAccessible(true);
            weakWorldFieldAccess=FieldAccess.of(WeakWorldField);
            invokeBlockStateSuccess=true;
        }catch (Throwable e){
            Debug.logger(e);
        }
    }
    public static boolean copyBlockState(BlockState state, Block block2){
        if(invokeBlockStateSuccess){
            BlockState state2=block2.getState();
            if(CraftBlockStateClass.isInstance(state2)&&CraftBlockStateClass.isInstance(state)){
                try{
                    blockPositionFieldAccess.ofAccess(state).set(blockPositionFieldAccess.getValue(state2));
                    worldFieldAccess.ofAccess(state).set(worldFieldAccess.getValue(state2));
                    worldFieldAccess.ofAccess(state).set(worldFieldAccess.getValue(state2));
                    weakWorldFieldAccess.ofAccess(state).set(weakWorldFieldAccess.getValue(state2));
                    state.update(true,false);
                    return true;
                }catch (Throwable e){
                    return false;
                }
            }else return false;
        }else return false;
    }
}

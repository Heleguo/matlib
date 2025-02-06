package me.matl114.matlib.Utils.ItemCache;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.NotImplementedException;
import me.matl114.matlib.Utils.Algorithm.InitializingTasks;
import me.matl114.matlib.Utils.CraftUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Flags;
import org.bukkit.inventory.ItemStack;

import javax.naming.InitialContext;
import java.util.List;
import java.util.function.Supplier;

public interface MultiItemStack extends AbstractItemStack {
    public List<ItemStack> getItemStacks();
    default ItemStack clone()  {
        throw new NotImplementedException(".clone() method not implemented");
    }
    public int getTypeNum();
    public List<Double> getWeight(Double percent);

    public boolean matchItem(ItemStack item,boolean strictCheck);
    static void getVoid(){

    }
    default boolean canStackWithMatch(){
        return true;
    }
    InitializingTasks task=new InitializingTasks(()->{
        CraftUtils.registerCustomMatcher((stack1,stack2,strictCheck)->{
            if(stack1 instanceof MultiItemStack) {
                return ((MultiItemStack) stack1).matchItem(stack2,strictCheck)? Flags.ACCEPT:Flags.REJECT;
            }else if (stack2 instanceof MultiItemStack) {
                return ((MultiItemStack) stack2).matchItem(stack1,strictCheck)? Flags.ACCEPT:Flags.REJECT;
            }
            return Flags.IGNORED;
        });
//        CraftUtils.registerCustomMatcher((stack1,stack2,strictCheck)->{
//            if(stack1 instanceof MultiItemStack) {
//                return ((MultiItemStack) stack1).matchItem(stack2,strictCheck)? Flags.ACCEPT:Flags.REJECT;
//            }else if (stack2 instanceof MultiItemStack) {
//                return ((MultiItemStack) stack2).matchItem(stack1,strictCheck)? Flags.ACCEPT:Flags.REJECT;
//            }
//            return Flags.IGNORED;
//        });
    });
}

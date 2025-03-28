package me.matl114.matlib.UnitTest;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Utils.Debug;
import org.bukkit.Bukkit;
import org.bukkit.World;

public interface TestCase {
    default void Assert(boolean expression){
        Preconditions.checkArgument(expression,"Assertion failed!");
    }
    default void Assert(Object expression){
        Preconditions.checkNotNull(expression,"Assertion failed!");
    }
    default World testWorld(){
        return Bukkit.getWorlds().get(0);
    }
}

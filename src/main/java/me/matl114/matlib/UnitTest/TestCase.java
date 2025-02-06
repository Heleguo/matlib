package me.matl114.matlib.UnitTest;

import com.google.common.base.Preconditions;

public interface TestCase {
    default void Assert(boolean expression){
        Preconditions.checkArgument(expression,"Assertion failed!");
    }
}

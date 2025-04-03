package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.utils.Debug;

public interface DemoTargetInterface {
    static int i = -114;
    default void f(){
        Debug.logger("Interface.f called");
    }
}

package me.matl114.matlib.unitTest.demo;

public class DemoLoad {
    public static Class<?> initDemo(){
        DemoTargetClass.c();
        return DemoTargetClass.class;
    }
}

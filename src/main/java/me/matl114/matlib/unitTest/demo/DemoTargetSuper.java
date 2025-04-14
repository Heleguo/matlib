package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.utils.Debug;

public abstract class DemoTargetSuper {
    public DemoTargetSuper(){
        Debug.logger("Super.init called");
    }
    private final String g = "www";
    private static final String h = "http";
    public void g(){
        Debug.logger("Super.g called");
    }
    public void abs(){
        Debug.logger("Super.abs called");
    }
}

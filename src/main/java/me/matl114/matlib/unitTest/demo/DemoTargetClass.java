package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.utils.Debug;

class DemoTargetClass extends DemoTargetSuper implements DemoTargetInterface{
    public DemoTargetClass(){
        super();
        Debug.logger("Instance create");
    }
    DemoTargetClass(int a){
        super();
        Debug.logger("Private Instance create:",a);
    }
    private int a =114;
    public int b;
    public long c =514;
    public static double d;
    public Object e;
    public String f;
    public void a(){
        Debug.logger("Target.a called");
    }
    private void b(){
        Debug.logger("Target.b called");
    }
    public static void c(){
        Debug.logger("Target.c called");
    }
    public Object d(){
        Debug.logger("Target.d called");
        return new byte[0];
    }
    public Object e() throws Throwable {
        return null;
    }
    static{
        Debug.logger("Demo Target Class Init");
    }
//    public void f(){
//        Debug.logger("Target.f called");
//    }
}

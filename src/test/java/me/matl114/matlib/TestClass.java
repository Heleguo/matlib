package me.matl114.matlib;

import lombok.Getter;

public class TestClass {
    public Object a;
    public Object b;
    public static Object c;
    @Getter
    private Object d;
    @Getter
    private static Object e;
    public final static Object f = new Object();
    @Getter
    public final static Object s=1;
}

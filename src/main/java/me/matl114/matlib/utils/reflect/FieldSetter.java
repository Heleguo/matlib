package me.matl114.matlib.utils.reflect;

public interface FieldSetter {
    void consume(Object base,Object fieldValue) throws Throwable;
}

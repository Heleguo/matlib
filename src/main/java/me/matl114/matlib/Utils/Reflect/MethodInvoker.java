package me.matl114.matlib.Utils.Reflect;

import me.matl114.matlib.Common.Lang.Annotations.Note;

import java.util.function.BiFunction;

public interface MethodInvoker<T> {
    @Note("this method shouldn't be called when invoke target method")
    public T invokeInternal(Object obj, Object... args) throws Throwable;
    default T invoke(Object obj, Object... args)  {
        try{
            return invokeInternal(obj, args);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    public static <T> MethodInvoker<T> ofSafe(BiFunction<Object, Object[], T> con){
        return new MethodInvoker<T>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public T invoke(Object obj, Object... args) {
                return con.apply(obj, args);
            }
        };
    }
}

package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.Protected;
import me.matl114.matlib.utils.reflect.reflectasm.MethodAccess;

import java.util.function.BiFunction;

public interface MethodInvoker<T> {
    @Note("this method shouldn't be called when invoke target method")
    @Protected
    public T invokeInternal(Object obj, Object... args) throws Throwable;
    default T invoke(Object obj, Object... args)  {
        try{
            return invokeInternal(obj, args);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    static final Object[] NO_ARGUMENT = new Object[0];
    default T invokeNoArg(Object obj){
        return invoke(obj, NO_ARGUMENT);
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
    public static <T> MethodInvoker<T> ofASM(Pair<MethodAccess, Integer> val){
        MethodAccess a = val.getA();
        int b = val.getB();
        return new MethodInvoker<>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }
            @Override
            public T invoke(Object obj, Object... args) {
                return (T) a.invoke(obj, b);
            }
        };
    }
}

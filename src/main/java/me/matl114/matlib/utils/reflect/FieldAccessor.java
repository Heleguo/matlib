package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.reflect.reflectasm.FieldAccess;

public interface FieldAccessor<T extends Object> {
    public void set(Object obj, T value);
    public T get(Object obj);
    public static FieldAccessor ofASM(Pair<me.matl114.matlib.utils.reflect.reflectasm.FieldAccess,Integer> asm){
        final int a = asm.getB();
        FieldAccess access = asm.getA();
        return new FieldAccessor() {

            @Override
            public void set(Object obj, Object value) {
                access.set(obj,a,value);
            }

            @Override
            public Object get(Object obj) {
                return access.get(obj,a);
            }
        };
    }
    public static <T> FieldAccessor<T> fromGetterMethod(MethodInvoker<T> getter){
        return new FieldAccessor<T>() {
            @Override
            public void set(Object obj, T value) {
                throw new RuntimeException("Setting field is not allowed in this FieldAccessor");
            }

            @Override
            public T get(Object obj) {
                return getter.invokeNoArg(obj);
            }
        };
    }
    default <W extends Object> FieldAccessor<W> safeCast(){
        return (FieldAccessor<W>)this;
    }

}

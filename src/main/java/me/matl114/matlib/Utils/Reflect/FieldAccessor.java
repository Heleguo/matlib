package me.matl114.matlib.Utils.Reflect;

import com.esotericsoftware.reflectasm.FieldAccess;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Pair;

public interface FieldAccessor {
    public void set(Object obj, Object value);
    public Object get(Object obj);
    public static FieldAccessor ofASM(Pair<com.esotericsoftware.reflectasm.FieldAccess,Integer> asm){
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
}

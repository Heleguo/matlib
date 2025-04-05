package me.matl114.matlib.nmsMirror;

import com.google.common.base.Preconditions;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ObfManager;

import java.lang.reflect.Field;
import java.util.List;

public class Utils {
    public static Object matchName(List<Field> fields, String name){
        Preconditions.checkArgument(!fields.isEmpty());
        try{
            return fields.stream()
                .filter( f -> ObfManager.getManager().deobfField(f).equals(name))
                .peek(f->f.setAccessible(true))
                .findFirst()
                .orElseThrow()
                .get(null);
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting "+ fields.getFirst().getDeclaringClass().getSimpleName()+":");
            return null;
        }

    }
    public static Object reflect(Field field, Object tar){
        try{
            return field.get(tar);
        }catch (Throwable e){
            throw  new RuntimeException(e);
        }
    }
    public static Object deobfStatic(Class<?> clazz, String name){
        return reflect( ObfManager.getManager().lookupFieldInClass(clazz, name),null);
    }
}

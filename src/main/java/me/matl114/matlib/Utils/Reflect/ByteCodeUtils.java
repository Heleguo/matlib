package me.matl114.matlib.Utils.Reflect;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

public class ByteCodeUtils {
    public static String toJvmType(Class<?> clazz) {
        if (clazz.isArray()) {
            //return "["+toJvmType( clazz.getComponentType());
            //getName of this will return  [[L... or [[<primitive>
            return clazz.getName().replace('.', '/');
        }
        if (clazz.isPrimitive()) {
            if (clazz == void.class) return "V";
            if (clazz == int.class) return "I";
            if (clazz == boolean.class) return "Z";
            if (clazz == byte.class) return "B";
            if (clazz == char.class) return "C";
            if (clazz == short.class) return "S";
            if (clazz == double.class) return "D";
            if (clazz == float.class) return "F";
            if (clazz == long.class) return "J";
        }
        return "L" + clazz.getName().replace('.', '/') + ";";
    }
    public static String getMethodDescriptor(Method method) {
        var builder = new StringBuilder();
        builder.append(method.getName());
        builder.append("(");
        for (var arg : method.getParameterTypes()) {
            builder.append(toJvmType(arg));
        }
        builder.append(")");
        builder.append(toJvmType(method.getReturnType()));
        return builder.toString();
    }
    public static String getMethodDescriptor(MethodSignature signature,Class<?> returnType) {
        var builder = new StringBuilder();
        builder.append(signature.methodName());
        builder.append("(");
        for (var arg : signature.parameterTypes()) {
            builder.append(toJvmType(arg));
        }
        builder.append(")");
        builder.append(toJvmType(returnType));
        return builder.toString();
    }


}

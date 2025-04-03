package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.dataStructures.struct.Triplet;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ByteCodeUtils {
    public static String toJvmType(Class<?> clazz) {
        return Type.getDescriptor(clazz);
//        if (clazz.isArray()) {
//            //return "["+toJvmType( clazz.getComponentType());
//            //getName of this will return  [[L... or [[<primitive>
//            return clazz.getName().replace('.', '/');
//        }
//        if (clazz.isPrimitive()) {
//            if (clazz == void.class) return "V";
//            if (clazz == int.class) return "I";
//            if (clazz == boolean.class) return "Z";
//            if (clazz == byte.class) return "B";
//            if (clazz == char.class) return "C";
//            if (clazz == short.class) return "S";
//            if (clazz == double.class) return "D";
//            if (clazz == float.class) return "F";
//            if (clazz == long.class) return "J";
//        }
//        return "L" + clazz.getName().replace('.', '/') + ";";
    }
    public static String toJvmType(String clazzName){
        if(clazzName.charAt(0) == '['){
            //is array
            return clazzName.replace('.','/');
        }
        return switch (clazzName) {
            case "void" -> "V";
            case "int" -> "I";
            case "boolean" -> "Z";
            case "byte" -> "B";
            case "char" -> "C";
            case "short" -> "S";
            case "double" -> "D";
            case "float" -> "F";
            case "long" -> "J";
            default -> "L" + clazzName.replace(".", "/") + ";";
        };
    }
//    public static String fromJvmType(String val){
//
//    }
    public static Pair<String, String> getComponentType(Class<?> clazz){
        if(clazz.isArray()){
            //is array
            StringBuilder builder = new StringBuilder();
            while(clazz.isArray()){
                builder.append('[');
                clazz = clazz.getComponentType();
            }
            return Pair.of(builder.toString(), clazz.getName());
        }else {
            return Pair.of("",clazz.getName());
        }
    }
    public static String getPrimitiveType(char descriptor) {
        return switch (descriptor) {
            case 'Z' -> "boolean";
            case 'B' -> "byte";
            case 'C' -> "char";
            case 'S' -> "short";
            case 'I' -> "int";
            case 'J' -> "long";
            case 'F' -> "float";
            case 'D' -> "double";
            case 'V' -> "void";
            default ->
                //not a primitive
                // throw new RuntimeException("Not a primitive descriptor:"+descriptor);
                null;
        };
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

    public static String parseMethodNameFromDescriptor(String descriptor){
        return descriptor.substring(0, descriptor.indexOf('('));
    }

    public static Triplet<String,String[],String> parseMethodDescriptor(String descriptor){
        int index = descriptor.indexOf('(');
        String name = descriptor.substring(0, index);
        int index2 = descriptor.indexOf(')');
        String params = descriptor.substring(index+1, index2);
        String retType = descriptor.substring(index2 +1);
        List<String> paramsList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int len = params.length();
        boolean recordingPath =false;
        for (int i=0; i< len; ++i){
            char c = params.charAt(i);
            if(recordingPath){
                builder.append(c);
                if (c == ';') {
                    recordingPath = false;
                    paramsList.add(builder.toString());
                }
            }else {
                if('L' == c || '[' == c){
                    recordingPath = true;
                    builder = new StringBuilder();
                    builder.append(c);
                }else {
                    paramsList.add(String.valueOf(c));
                }
            }
        }
        return Triplet.of(name, paramsList.toArray(String[]::new), retType);
    }
    public static String parseFieldNameFromDescriptor(String descriptor){
        if(descriptor.charAt(descriptor.length()-1) == ';'){
            int isArray = descriptor.indexOf('[');
            if(isArray > 0 ){
                //class array
                return descriptor.substring(0, isArray);
            }else {
                //class type
                String firstLevel = descriptor.substring(0, descriptor.indexOf('/'));
                return firstLevel.substring(0, firstLevel.lastIndexOf('L'));
            }
        }else {
            int isArray = descriptor.indexOf('[');
            if(isArray > 0 ){
                //primitive type array
                return descriptor.substring(0, isArray);
            }
            //primitive type
            return descriptor.substring(0, descriptor.length()-1);
        }
    }
    public static String getFieldDescriptor(String fieldName, Class<?> fieldType){
        return fieldName + toJvmType(fieldType);
    }


}

package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.Flags;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectUtils {
    public static  Object invokeGetRecursively(Object target, Flags mod, String declared){
        return invokeGetRecursively(target,target.getClass(),mod,declared);
    }
    public static  Object invokeGetRecursively(Object target, Class clazz, Flags mod, String decleared){
//        if(Debug.debug){
//            Debug.debug("try invoke ",clazz);
//        }
        try{
            switch (mod){
                case FIELD:
//                    if(clazz.getName().endsWith("AbstractMachineBlock")){
//                        Debug.debug("try print this");
//                        Field[] fields=clazz.getDeclaredFields();
//                        for(Field f:fields){
//                            Debug.debug(f.getName());
//                        }
//                    }
                    //Debug.debug("start find field ",decleared);
                    Field _hasType=clazz.getDeclaredField(decleared);
                    // Debug.debug("find field");
                    _hasType.setAccessible(true);
                    //  Debug.debug("Access true");
                    return  _hasType.get(target);
                case METHOD:
                    Method _hasMethod=clazz.getDeclaredMethod(decleared);

                    _hasMethod.setAccessible(true);
                    return _hasMethod.invoke(target);
            }
        }catch (Throwable e){
        }
        clazz=clazz.getSuperclass();
        if(clazz==null){
            return null;
        }else {
            return invokeGetRecursively(target,clazz,mod,decleared);
        }
    }
    public static boolean setFieldRecursively(Object target,  String declared,Object value){
        return setFieldRecursively(target,target.getClass(),declared,value);
    }
    public static boolean setFieldRecursively(Object target, Class clazz, String decleared, Object value){
        try{
            Field _hasType=clazz.getDeclaredField(decleared);
            _hasType.setAccessible(true);
            _hasType.set(target,value);
            return true;
        }catch (Throwable e){
        }
        clazz=clazz.getSuperclass();
        if(clazz==null){
            return false;
        }else {
            return setFieldRecursively(target,clazz,decleared,value);
        }
    }
    public static Pair<Field,Class> getFieldsRecursively(Class clazz, String fieldName){
        try{
            Field field=clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return Pair.of(field,clazz);
        }catch (Throwable e){
            clazz=clazz.getSuperclass();
            if(clazz==null){
                return null;
            }else{
                return getFieldsRecursively(clazz,fieldName);
            }
        }
    }
    public static List<Field> getAllFieldsRecursively(Class clazz){
        List<Field> fieldList=new ArrayList<>();
        if(clazz==null){
            return fieldList;
        }
        Field[] fields=clazz.getDeclaredFields();
        for(Field f:fields){
            fieldList.add(f);
            try{
                f.setAccessible(true);
            }catch (Throwable e){
                continue;
            }
        }
        for (Class<?> classes :clazz.getInterfaces()){
            fieldList.addAll(getAllFieldsRecursively(classes));
        }
        fieldList.addAll(getAllFieldsRecursively(clazz.getSuperclass()));
        return fieldList;
    }
    public static List<Method> getAllMethodsRecursively(Class clazz){
        List<Method> fieldList=new ArrayList<>();
        if(clazz==null){
            return fieldList;
        }
        Method[] fields=clazz.getDeclaredMethods();
        for(Method f:fields){
            fieldList.add(f);
            try{
                f.setAccessible(true);
            }catch (Throwable e){
                continue;
            }
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            //should include abstract methods as well ,
            fieldList.addAll(getAllMethodsRecursively(iface));
        }
        fieldList.addAll(getAllMethodsRecursively(clazz.getSuperclass()));
        return fieldList;
    }
    public static List<Method> getAllDefaultMethodRecursively(Class iface){
        List<Method> methodList = new ArrayList<>();
        for (Method method : iface.getMethods()) {
            int mod = method.getModifiers();
            if (method.isDefault() || Modifier.isPrivate(mod) ||Modifier.isStatic(mod) ) { // 只添加 在当前类已经实现的 方法
                try {
                    methodList.add(method);
                } catch (Throwable ignored) {
                }
            }
        }
        for (var iiface :iface.getInterfaces()){
            methodList.addAll(getAllDefaultMethodRecursively(iiface));
        }
        return methodList;
    }

    /**
     * return the directly implemented interface, the superInterface of interface is not listed in the return List
     * @param clazz
     * @return
     */
    public static List<Class> getAllInterfacesRecursively(Class clazz){
        List<Class> fieldList=new ArrayList<>();
        if(clazz==null){
            return fieldList;
        }
        Class[] fields=clazz.getInterfaces();
        for(Class f:fields){
            fieldList.add(f);
        }
        fieldList.addAll(getAllInterfacesRecursively(clazz.getSuperclass()));
        return fieldList;
    }
    public static List<Class> getAllAssignableInterface(Class clazz){
        Set<Class> fieldList=new HashSet<>();
        if(clazz==null){
            return fieldList.stream().toList();
        }
        Class[] fields=clazz.getInterfaces();
        for(Class f:fields){
            fieldList.addAll(getAllAssignableInterface(f));
        }
        fieldList.addAll(getAllAssignableInterface(clazz.getSuperclass()));
        return fieldList.stream().toList();
    }
    public static List<Class> getAllSuperClassRecursively(Class clazz){
        List<Class> fieldList=new ArrayList<>();
        while (clazz!=null){
            fieldList.add(clazz);
            clazz=clazz.getSuperclass();

        };
        return fieldList;
    }
    public static Pair<Method,Class> getMethodsRecursively(Class clazz, String fieldName, Class[] parameterTypes){
        try{
            Method field=clazz.getDeclaredMethod(fieldName,parameterTypes);
            field.setAccessible(true);
            return Pair.of(field,clazz);
        }catch (Throwable e){
            clazz=clazz.getSuperclass();
            if(clazz==null){
                return null;
            }else{
                return getMethodsRecursively(clazz,fieldName,parameterTypes);
            }
        }
    }
    public static Pair<Method,Class> getMethodsByName(Class clazz, String fieldName){

        Method[] field=clazz.getDeclaredMethods();
        for(Method m:field){
            try{
                if(m.getName().equals(fieldName)){
                    m.setAccessible(true);
                    return Pair.of(m,clazz);
                }
            }catch (Throwable e){
            }
        }


        clazz=clazz.getSuperclass();
        if(clazz==null){
            return null;
        }else{
            return getMethodsByName(clazz,fieldName);
        }
    }
    public static boolean isPrimitiveType(String val){
        return switch (val) {
            case "int", "void", "boolean", "long", "double", "float", "short", "byte", "char" -> true;
            default -> false;
        };
    }
    public static boolean isBoxedPrimitive(String className) {
        return switch (className) {
            case "java/lang/Integer",
                 "java/lang/Boolean",
                 "java/lang/Long",
                 "java/lang/Double",
                 "java/lang/Float",
                 "java/lang/Short",
                 "java/lang/Byte",
                 "java/lang/Character",
                 "java/lang/Void"-> true;
            default -> false;
        };
    }
    public static String getUnboxedClass(String boxedClassName) {
        return switch (boxedClassName) {
            case "java/lang/Integer"   -> "int";
            case "java/lang/Boolean"   -> "boolean";
            case "java/lang/Long"      -> "long";
            case "java/lang/Double"    -> "double";
            case "java/lang/Float"     -> "float";
            case "java/lang/Short"     -> "short";
            case "java/lang/Byte"      -> "byte";
            case "java/lang/Character" -> "char";
            case "java/lang/Void"      -> "void";
            default -> throw new IllegalArgumentException("Not a boxed primitive class: " + boxedClassName);
        };
    }
    public static String getBoxedClass(String primitive) {
        switch (primitive) {
            case "int":
                return "java/lang/Integer";
            case "boolean":
                return "java/lang/Boolean";
            case "long":
                return "java/lang/Long";
            case "double":
                return "java/lang/Double";
            case "float":
                return "java/lang/Float";
            case "short":
                return "java/lang/Short";
            case "byte":
                return "java/lang/Byte";
            case "char":
                return "java/lang/Character";
            case "void":
                return "java/lang/Void";  // 注意：void 也有对应的包装类 Void
            default:
                throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
        }
    }
    public static Pair< Method,Class> getMethodByParams(Class clazz, String methodName, Class[] parameterTypes){
        try{
            Method[] methods=clazz.getDeclaredMethods();
            for(Method m:methods){
                Class[] params=m.getParameterTypes();
                if(!methodName.equals(m.getName())){
                    continue;
                }
                boolean match=true;
                if(params.length==parameterTypes.length){
                    int len=params.length;
                    for(int i=0;i<len;i++){
                        if(params[i]==parameterTypes[i]||params[i].isAssignableFrom(parameterTypes[i])){
                            continue;
                        }else{
                            match=false;
                        }
                    }
                }else {
                    match=false;
                }
                if(match){
                    m.setAccessible(true);

                    return Pair.of(m,clazz);
                }
            }
        }catch (Throwable e){}
        clazz=clazz.getSuperclass();
        if(clazz==null){return null;}
        return getMethodByParams(clazz,methodName,parameterTypes);
    }
    public static Constructor getConstructorByParams(Class clazz, Class... parameterTypes){
        Constructor[] constructors=clazz.getDeclaredConstructors();
        for(Constructor c:constructors){
            Class[] params=c.getParameterTypes();
            boolean match=true;
            if(params.length==parameterTypes.length){
                int len=params.length;
                for(int i=0;i<len;i++){
                    if(params[i]==parameterTypes[i]||params[i].isAssignableFrom(parameterTypes[i])){

                    }else
                        match=false;
                }
            }else {
                match=false;
            }
            if(match){
                c.setAccessible(true);
                return c;
            }
        }
        return null;
    }
    public static boolean isExtendedFrom(Class clazz,String s){
        if(clazz==null){
            return false;
        }else {
            if(clazz.getName().endsWith(s)){
                return true;
            }else {
                return isExtendedFrom(clazz.getSuperclass(),s);
            }
        }
    }
    public static Field getFirstFitField(Class<?> clazz,Class<?> fieldType) {
        try{
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(fieldType.isAssignableFrom(field.getType())){
                    field.setAccessible(true);
                    return field;
                }
            }
        }catch(Exception e){}
        if(clazz.getSuperclass().getSuperclass()!=null){
            return getFirstFitField(clazz.getSuperclass(),fieldType);
        }
        return null;
    }
    public static Field getFirstFitField(Class<?> clazz,Class<?> fieldType,boolean isStatic) {
        try{
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if((Modifier.isStatic(field.getModifiers())==isStatic)&& fieldType.isAssignableFrom(field.getType())){
                    field.setAccessible(true);
                    return field;
                }
            }
        }catch(Exception e){}
        if(clazz.getSuperclass().getSuperclass()!=null){
            return getFirstFitField(clazz.getSuperclass(),fieldType);
        }
        return null;
    }
    public static Object getFieldValue(Object object,Class<?> clazz,Class<?> fieldType){
        try{
            if(object!=null&&!clazz.isInstance(object)){
                return false;
            }
            Field field=getFirstFitField(clazz,fieldType);
            field.setAccessible(true);
            return field.get(object);
        }catch (Throwable e){
            e.printStackTrace();
            return null;
        }
    }
    public static Field[] getAllFitFields(Class<?> clazz,Class<?> fieldType){
        if(clazz==null){
            return new Field[0];
        }
        List<Field> fields = new ArrayList<>();
        for(Field field:clazz.getDeclaredFields()){
            if(fieldType.isAssignableFrom(field.getType())){
                field.setAccessible(true);
                fields.add(field);
            }
        }
        fields.addAll(Arrays.stream(getAllFitFields(clazz.getSuperclass(),fieldType)).toList());
        return fields.toArray(Field[]::new);
    }
    public static Object getFieldValue(Object object,Class<?> clazz,Class<?> fieldType,boolean isStatic){
        try{
            if(object!=null&&!clazz.isInstance(object)){
                return false;
            }
            Field field=getFirstFitField(clazz,fieldType,isStatic);
            field.setAccessible(true);
            return field.get(object);
        }catch (Throwable e){
            e.printStackTrace();
            return null;
        }
    }
    public static boolean setFirstFitField(Object object,Object tar,Class<?> clazz,Class<?> fieldType){
        try{
            if(object!=null&&!clazz.isInstance(object)){
                return false;
            }
            Field field=getFirstFitField(clazz,fieldType);
            field.setAccessible(true);
            field.set(object,tar);
            return true;
        }catch (Throwable e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean setFirstFitField(Object object,Object tar,Class<?> clazz,Class<?> fieldType,boolean isStatic){
        try{
            if(object!=null&&!clazz.isInstance(object)){
                return false;
            }
            Field field=getFirstFitField(clazz,fieldType,isStatic);
            field.setAccessible(true);
            field.set(object,tar);
            return true;
        }catch (Throwable e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean copyFirstField(Object to,Object from,Class<?> clazz,Class<?> fieldType){
        return setFirstFitField(to,getFieldValue(from,clazz,fieldType),clazz,fieldType);
    }
    public interface UnsafeSetter{
        public void set(Unsafe unsafe,Object fieldBase, long fieldOffset, Field field);
    }
    private static FieldAccess.AccessWithObject<Unsafe> staticUnsafeAccess=FieldAccess.ofName(Unsafe.class,"theUnsafe").printError(false).ofAccess(null);
    public static void getUnsafeSetter(Field accessibleField,UnsafeSetter setter){
        staticUnsafeAccess.get((unsafe)->{
            boolean isStatic=Modifier.isStatic(accessibleField.getModifiers());
            long fieldOffset = isStatic?unsafe.staticFieldOffset(accessibleField): unsafe.objectFieldOffset(accessibleField);
            Object staticFieldBase =isStatic? unsafe.staticFieldBase(accessibleField):null;
            setter.set(unsafe,staticFieldBase,fieldOffset,accessibleField);
        });
    }
    public static final Map<String,Integer> objectInvocationIndex = new HashMap<>(){{
        put("getClass",-1);
        put("hashCode",-2);
        put("equals",-3);
        put("clone",-4);
        put("toString",-5);
        put("notify",-6);
        put("notifyAll",-7);
        put("wait",-8);
        put("wait0",-9);
        put("finalize",-10);
    }};
    public static boolean isBaseMethod(Method method){
        return objectInvocationIndex.containsKey(method.getName()); //Object.class.equals(method.getDeclaringClass());
    }
    public static int getBaseMethodIndex(Method method){
        String methodName = method.getName();
        if(objectInvocationIndex.containsKey(methodName)){
            return objectInvocationIndex.get(methodName);
        }else {
            throw new IllegalArgumentException("Not a base Method!");
        }
    }
    public static Object invokeBaseMethod(Object target,int index,Object[] args){
        switch (index){
            case -1:return target.getClass();
            case -2:return target.hashCode();
            case -3:return target.equals(args[0]);
            case -4:throw new IllegalStateException("clone method not supported");
            case -5:return target.toString();
            case -6:target.notify();return null;
            case -7:target.notifyAll();return null;
            case -8:try{ switch (args.length){
                case 0:target.wait();return null;
                case 1:target.wait((Long) args[0]);return null;
                case 2:target.wait();return null;
                default:throw new IllegalArgumentException("Wrong argument count for wait!");
            }}catch (InterruptedException e){
            }
                return null;
            case -9:throw new IllegalArgumentException("wait0 method not supported");
            case -10:throw new IllegalArgumentException("finalize method not supported");
        }
        return null;
    }
}

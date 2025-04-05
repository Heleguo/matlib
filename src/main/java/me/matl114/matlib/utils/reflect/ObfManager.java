package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.algorithms.dataStructures.frames.InitializeProvider;
import me.matl114.matlib.utils.Debug;

import me.matl114.matlib.utils.reflect.descriptor.internel.ObfManagerImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public interface ObfManager  {
    static ObfManager getManager(){
        return manager;
    }
    static ObfManager manager = new InitializeProvider<>(()->{
        try{
            var cons = ObfManagerImpl.class.getDeclaredConstructor();
            cons.setAccessible(true);
            return (ObfManager) cons.newInstance();
        }catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)  {
            var e1 = e.getCause();
            Debug.logger(e1 != null? e1: e, "Error while creating ObfManagerImpl:");
            Debug.logger("Using Default Impl");
            return new DefaultImpl();
        }
    }).v();

    /**
     * this method should return the deobf class name of a optional-obf class, you should check whether the class is in the mapping
     * @param obfName
     * @return
     */
    public String deobfClassName(String obfName);
    //fixme fix craftbukkit relocation ,
    /**
     * used for deobf clazz types in params and returnType where there exists fucking  primitive type and fucking arrays
     * @param clazz
     * @return
     */
    default String deobfToJvm(Class<?> clazz){
        var re = ByteCodeUtils.getComponentType(clazz);
        String deobfOrigin = deobfClassName(re.getB());
        String jvmType = ByteCodeUtils.toJvmType(deobfOrigin);
        return re.getA() + jvmType;
    }

//    default String deobfNameToJvm(String clazz){
//
//    }
    /**
     * this method should return the reobf class name of a runtime class, you should check whether the class is in the mapping
     * @param mojangName
     * @return
     */
    public String reobfClassName(String mojangName);

    default Class<?> reobfClass(String mojangName) throws Throwable{
        String obfName = reobfClassName(mojangName);
        try{
            //try check obf
            return Class.forName(obfName);
        }catch (NoClassDefFoundError error){
            if(Objects.equals(mojangName, obfName)){
                //nmd 没有obf,就是不对
                throw error;
            }
            //no obf present
            return Class.forName(mojangName);
        }
    }
    /**
     * this method return the mojang methodName of a obf method descriptor, you should check mapping and return deobf value or self
     * make sure that argument and return value are also deobf
     * @param reobfClassName
     * @param methodDescriptor ,should be in jvm method format, you can use ByteCodeUtils to generate these string
     * @return
     */
    public String deobfMethodInClass(String mojangClassName, String obfMethodDescriptor);

    default String deobfMethod(Method method0){
        return deobfMethodInClass(deobfClassName(method0.getDeclaringClass().getName()), ByteCodeUtils.getMethodDescriptor(method0));
    }

    default boolean isMethodNameMatchAfterDeobf(String reobfClassName, String targetDescriptor, String methodName){
        return Objects.equals(deobfMethodInClass(reobfClassName, targetDescriptor), methodName);
    }

    public String deobfFieldInClass(String mojangClassName, String obfFieldDescriptor);
    default String deobfField(Field field0){
        return deobfFieldInClass(deobfClassName(field0.getDeclaringClass().getName()), ByteCodeUtils.getFieldDescriptor(field0.getName(), field0.getType()));
    }
    default boolean isFieldSameAfterDeobf(String mojangName, String targetDescriptor, String fieldName){
        return Objects.equals(deobfFieldInClass(mojangName, targetDescriptor), fieldName);
    }

    default Field matchFieldOrThrow(List<Field> fields, String name){
        if(fields.isEmpty()){
            return null;
        }
        try{
            return fields.stream()
                .filter( f -> deobfField(f).equals(name))
                .peek(f-> f.setAccessible(true))
                .findFirst()
                .orElseThrow();
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting field",name,"in",fields.get(0).getDeclaringClass().getSimpleName()+":");
            return null;
        }

    }
    default Field lookupFieldInClass(Class<?> clazz, String name){
        Field[] fields = clazz.getDeclaredFields();
        if(fields.length == 0)return null;

        String mojangName = deobfClassName(clazz.getName());

        try{
            return Arrays.stream(fields)
                .filter( f -> {
                    return deobfFieldInClass(mojangName, ByteCodeUtils.getFieldDescriptor(f.getName(), f.getType())).equals(name);
                })
                .peek(f-> f.setAccessible(true))
                .findFirst()
                .orElseThrow(null);
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting field",name,"in" ,fields[0].getDeclaringClass().getSimpleName()+":");
            return null;
        }
    }
//    default String deobfFieldInClass(String reobf){
//        throw new  NotImplementedYet();
//    }
    static class DefaultImpl implements ObfManager{

        @Override
        public String deobfClassName(String currentName) {
            return currentName;
        }

        @Override
        public String reobfClassName(String mojangName) {
            return mojangName;
        }

        @Override
        public String deobfMethodInClass(String reobfClassName, String methodDescriptor) {
            return ByteCodeUtils.parseMethodNameFromDescriptor(methodDescriptor);
        }

        @Override
        public String deobfFieldInClass(String mojangClassName, String obfMethodDescriptor) {
            return ByteCodeUtils.parseFieldNameFromDescriptor(obfMethodDescriptor);
        }
    }

}

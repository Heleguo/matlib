package me.matl114.matlib.utils.reflect.descriptor.internel;

import com.esotericsoftware.reflectasm.MethodAccess;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ASMUtils;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.ObfManager;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ObfManagerImpl implements ObfManager {
    final Class<?> obfClass ;
    final ClassMapperHelper classMapperHelper ;
    final Map<String, ?> mappingsByObfName;
    final Map<String, ?> mappingsByMojangName;
    MethodAccess access;
    int optionalFieldMappingAccess;
    Map<String, ?> mappingsFieldByObf;
    Map<String, ?> mappingsFieldByMojang;
    ObfManagerImpl(){
        try{
            obfClass = Class.forName("io.papermc.paper.util.ObfHelper");
            classMapperHelper = new ClassMapperHelperImpl();
            Object obfIns = obfClass.getEnumConstants()[0];
            var f1 = obfClass.getDeclaredField("mappingsByObfName");
            f1.setAccessible(true);
            mappingsByObfName = (Map<String, ?>) f1.get(obfIns);
            var f2 = obfClass.getDeclaredField("mappingsByMojangName");
            f2.setAccessible(true);
            mappingsByMojangName = (Map<String, ?>) f2.get(obfIns);
            ClassMapperHelperImpl classMapperHelper1 = (ClassMapperHelperImpl) classMapperHelper;
            try{
                //higher version with no obf
                access = classMapperHelper1.getClassMappingAccess();
                optionalFieldMappingAccess = access.getIndex("fieldsByObf",0);
            }catch (IllegalArgumentException noSuchMethod){
                optionalFieldMappingAccess = -1;
                try{
                    Set val = a();
                    this.mappingsFieldByObf = (Map<String, ?>) val.stream().collect(Collectors.toUnmodifiableMap(classMapperHelper::obfNameGetter, map -> map));
                    this.mappingsFieldByMojang = (Map<String, ?>) val.stream().collect(Collectors.toUnmodifiableMap(classMapperHelper::mojangNameGetter, map -> map));
                }catch (Throwable e){
                    Debug.logger(e,"Error while reading field reobf data, disabling field reobf:");
                    this.mappingsFieldByObf = null;
                    this.mappingsFieldByMojang = null;
                }
            }
            Debug.logger("ObfManagerImpl successfully init");
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }


    private Set a() throws Throwable{
        Class clazz = obfClass;
        String classResourcePath = clazz.getName().replace('.', '/') + ".class";
        ClassReader reader = new ClassReader(clazz.getClassLoader().getResourceAsStream(classResourcePath));
        //starting our class
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        String generatedName = ObfManagerImpl.class.getName().replace("ObfManagerImpl","Generated.InvokeLoadMappingsIfPresent");
        String generatedPath = generatedName.replace(".","/");
        cw.visit(
            reader.readShort(6),
            Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN |Opcodes.ACC_SUPER,
            generatedPath,
            null, //泛型签名
            "java/lang/Object",//父类
            null //接口
        );
        ASMUtils.generateEmptyInit(cw, null);

        ClassVisitor writer = new EnhanceLoadMappingMethodVisitor(589824, cw );
        reader.accept(writer, 0);
        cw.visitEnd();
        byte[] bytecode = cw.toByteArray();
        var re =  CustomClassLoader.getInstance().defineAccessClass(generatedName, bytecode);
        Object inst = re.getConstructor().newInstance();
        Method newDefined = re.getDeclaredMethod("loadMappingsIfPresent");
        newDefined.setAccessible(true);
        return (Set) newDefined.invoke(inst);
    }

    @Override
    public String deobfClassName(String currentName) {
        if (this.mappingsByObfName == null) {
            return currentName;
        }

        final Object map = this.mappingsByObfName.get(currentName);
        if (map == null) {
            return currentName;
        }

        return classMapperHelper.mojangNameGetter(map);
    }

    @Override
    public String reobfClassName(String mojangName) {
        if (this.mappingsByMojangName == null) {
            return mojangName;
        }

        final Object map = this.mappingsByMojangName.get(mojangName);
        if (map == null) {
            return mojangName;
        }

        return classMapperHelper.obfNameGetter(map);
    }

    @Override
    public String deobfMethodInClass(String reobfClassName, String methodDescriptor) {
        String methodName =ByteCodeUtils.parseMethodNameFromDescriptor(methodDescriptor);
        if (this.mappingsByMojangName == null) {
            return methodName;
        }
        final Object map = this.mappingsByMojangName.get(reobfClassName);
        if(map == null){
            //no obf,
            return methodName;
        }
        final Map<String,String> methodMapping = classMapperHelper.methodsByObf(map);
        return methodMapping.getOrDefault(methodDescriptor, methodName);
    }

    @Override
    public String deobfFieldInClass(String mojangClassName, String obfFieldDescriptor) {
        String fieldName = ByteCodeUtils.parseFieldNameFromDescriptor(obfFieldDescriptor);
        if(this.mappingsFieldByMojang == null){
            if(optionalFieldMappingAccess < 0){
                return fieldName;
            }
            final Object map = this.mappingsByMojangName.get(mojangClassName);
            if(map == null){
                return fieldName;
            }
            Map<String,String> fieldMapper = (Map<String, String>) access.invoke(map, optionalFieldMappingAccess);
            return fieldMapper.getOrDefault(obfFieldDescriptor, fieldName);
        }
        final Object map = this.mappingsFieldByMojang.get(mojangClassName);
        if(map == null){
            return fieldName;
        }
        final Map<String, String> fieldMapper = classMapperHelper.methodsByObf(map);
        return fieldMapper.getOrDefault(obfFieldDescriptor, fieldName);
    }

    /**
     * this method tries to generate a enhanced loadMappingsIfPresent method,which reads field Mappings from reobf.tiny file
     */
    static class EnhanceLoadMappingMethodVisitor extends ClassVisitor {
        final ClassWriter target;
        final String targetMethod;

        public EnhanceLoadMappingMethodVisitor(int api, ClassWriter writer) {
            super(api);
            this.target =writer;
            this.targetMethod = "loadMappingsIfPresent";
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            //Debug.logger("visiting method ", access, name, descriptor, signature);
            if(!targetMethod.equals(name)){
                //return super.visitMethod(access, name, descriptor, signature, exceptions);
                //ignore other method
                return null;
            }
            //create a method writer for target ClassWriter
            // changing access from private static to public and not static
            var re = target.visitMethod((access &(~Opcodes.ACC_STATIC) &(~Opcodes.ACC_PRIVATE))|Opcodes.ACC_PUBLIC, name, descriptor, signature, exceptions);
            //do injections using default constructor of method Visitor
            return new MethodVisitor(Opcodes.ASM9, re) {
                public String replaceClassPath(String classPath){
                    if("io/papermc/paper/util/ObfHelper$StringPool".equals(classPath)){
                        classPath = MockStringPool.class.getName().replace(".","/");
                    }else if("Lio/papermc/paper/util/ObfHelper$StringPool".equals(classPath)) {
                        classPath = "L"+ MockStringPool.class.getName().replace(".","/");
                    }else if("net/fabricmc/mappingio/tree/MappingTree$MethodMapping".equals(classPath)){
                        classPath = "net/fabricmc/mappingio/tree/MappingTree$FieldMapping";
                    }else if("Lnet/fabricmc/mappingio/tree/MappingTree$MethodMapping".equals(classPath)){
                        classPath = "Lnet/fabricmc/mappingio/tree/MappingTree$FieldMapping";
                    }
                    return classPath;
                }
                public void replaceClassPath(Object[] vars){
                    for (int i=0;i<vars.length;++i){
                        if(vars[i] instanceof String cls){
                            vars[i] = replaceClassPath(cls);
                        }
                    }
                }

                public void visitTypeInsn(int code, String classPath){
                    classPath = replaceClassPath(classPath);
                    super.visitTypeInsn(code, classPath);
                    //
                }
                String originClass;
                String targetClass;
                public void visitMethodInsn(int code, String a, String b, String c, boolean fa){
                    a = replaceClassPath(a);
                    if(b.equals("getMethods")){
                        b  ="getFields";
                    }
                    super.visitMethodInsn(code, a, b, c, fa);
                }
                public void visitFrame(int code, int a, Object[] b, int c, Object[] d){
                    replaceClassPath(b);
                    replaceClassPath(d);
                    super.visitFrame(code, a, b, c, d);
                }
                public void visitVarInsn(int opcode, int index){
                    //留出index = 0从static转非static的，
                    super.visitVarInsn(opcode, index+1);
                }
                @Override
                public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                    //留出index= 0 因为是非static方法，需要给this留地方
                    super.visitLocalVariable(name, replaceClassPath(descriptor), signature, start, end, index +1);
                }
            };
        }
    }
    public static final class MockStringPool {
        private final Map<String, String> pool = new HashMap<>();

        public String string(final String string) {
            return this.pool.computeIfAbsent(string, Function.identity());
        }
    }
}

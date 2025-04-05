package me.matl114.matlib.utils.reflect.descriptor;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.core.EnvironmentManager;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.*;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorBuildException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.*;
import java.util.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

@SuppressWarnings("all")
public class DescriptorImplBuilder {
    private static final Map<Pair<Class<?>, Class<? extends TargetDescriptor>>, TargetDescriptor> CACHE = new HashMap<>();
    private static final Random rand = new Random();
    /**
     * create a impl for descriptive interface at targetClass
     * @param targetClass
     * @param descriptiveInterface
     * @return
     * @param <T>
     */
    public static <T extends TargetDescriptor> T createHelperImplAt(Class<?> targetClass, Class<T> descriptiveInterface){
        synchronized (CACHE){
            return (T)CACHE.computeIfAbsent(Pair.of(targetClass, descriptiveInterface), k-> {
                try {
                    return createInternel(k.getA(), k.getB());
                } catch (DescriptorBuildException e){
                    throw e;
                } catch (Throwable e) {
                    throw new DescriptorBuildException(e);
                }
            });
        }
    }
    public static <T extends TargetDescriptor> T createHelperImpl(Class<T> descriptiveInterface) {
        //using @Descriptive target to target class
        var re = descriptiveInterface.getAnnotation(Descriptive.class);
        Preconditions.checkNotNull(re, "No descriptor annotation found!");
        String val = re.target();
        try{
            Class<?> clazz = ObfManager.getManager().reobfClass(val);
            return createHelperImplAt(clazz, descriptiveInterface);
        }catch (Throwable e){
            throw DescriptorBuildException.warp(e);
        }
    }

    private static  <T extends TargetDescriptor> T createInternel(Class<?> targetClass, Class<T> descriptiveInterface) throws Throwable{
        Preconditions.checkArgument(descriptiveInterface.isInterface(),"Descriptor should be a interface!");
        Preconditions.checkNotNull(descriptiveInterface.getAnnotation(Descriptive.class), "No descriptor annotation found!");
        List<Method> fieldTarget = new ArrayList<>();
        Map<Method, Field> fieldGetDescrip = new LinkedHashMap<>();
        List<Method> methodTarget = new ArrayList<>();
        Map<Method, Field> fieldSetDescrip = new LinkedHashMap<>();
        Map<Method, Method> methodDescrip = new LinkedHashMap<>();
        List<Method> constructorTarget = new ArrayList<>();
        Map<Method, Constructor<?>> constructorDescrip = new LinkedHashMap<>();
        List<Method> uncompletedMethod = new ArrayList<>();
       // Map<String, Method> cdToOrigin = new HashMap<>();
        //collect targets
        Arrays.stream(descriptiveInterface.getMethods())
            .filter(m -> !(m.getName().equals( "getTargetClass") && m.getParameterCount() == 0 && m.getReturnType() == Class.class))
            .filter(m -> {
                var mod = m.getModifiers();
                //only complete abstract methods
                return !Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && Modifier.isAbstract(mod);
            })
           // .filter(m -> )
            .forEach(m->{
                var a1 =  m.getAnnotation(FieldTarget.class);
                if(a1 != null){
                    fieldTarget.add(m);
                    return;
                }
                var a2 = m.getAnnotation(MethodTarget.class);
                if(a2 != null){
                    methodTarget.add(m);
                    return;
                }
                var a3 = m.getAnnotation(ConstructorTarget.class);
                if(a3 != null){
                    constructorTarget.add(m);
                    return;
                }
                if(!m.isSynthetic() && !m.isBridge()){
                    uncompletedMethod.add(m);
                    return;
                }
            });
        //resolve target
        //collect fields first
        List<Field> fields = ReflectUtils.getAllFieldsRecursively(targetClass);
        LinkedHashMap<Pair<String, String>,Field> deobfCache = new LinkedHashMap<>();
        LinkedHashMap<Pair<String, String>,Field> staticDeobfCache = new LinkedHashMap<>();
        for (var test: fields){
            String deobfName = ObfManager.getManager().deobfField(test);
            String typeName = ObfManager.getManager().deobfToJvm(test.getType());
            //get the first field seen, avoid overriding
            if(Modifier.isStatic(test.getModifiers())){
                staticDeobfCache.putIfAbsent(Pair.of(deobfName, typeName), test);
            }else {
                deobfCache.putIfAbsent(Pair.of(deobfName, typeName), test);
            }
        }
        //resolve fields
        for (Method fieldAccess: fieldTarget){
            String targetName;
            boolean isGetter;
            var redirect1 = fieldAccess.getAnnotation(RedirectName.class);
            String name1 = fieldAccess.getName();
            if(name1.endsWith("Getter")){
                isGetter = true;
            }else if (name1.endsWith("Setter")){
                isGetter = false;
            }else {
                throw new DescriptorBuildException("Illegal field target name "+ name1 +", can not resolve Getter or Setter");
            }
            if(redirect1 != null){
                targetName = redirect1.value();
            }else {
                targetName = name1.substring(0, name1.length() - "Netter".length());
            }
            var static1 =fieldAccess.getAnnotation(FieldTarget.class).isStatic();
            var type = fieldAccess.getAnnotation(RedirectType.class);
            Field tar;
            var cache = static1? staticDeobfCache:deobfCache;
            if(type != null){
                tar = cache.get(Pair.of(targetName, type.value()));
            }else {
                tar = null;
                for (var re : cache.entrySet()){
                    if(re.getKey().getA().equals(targetName)){
                        tar  = re.getValue();
                        break;
                    }
                }
            }
            //get tar here
            if(tar == null){
                uncompletedMethod.add(fieldAccess);
            }else {
                if(isGetter){
                    fieldGetDescrip.put(fieldAccess, tar);
                }else {
                    fieldSetDescrip.put(fieldAccess, tar);
                }
            }

        }
        List<Method> methods = ReflectUtils.getAllMethodsRecursively(targetClass).stream().filter(m-> !m.isBridge() && !m.isSynthetic()).toList();
        for (Method methodAccess : methodTarget){
            String targetName ;
            var redirect1 = methodAccess.getAnnotation(RedirectName.class);
            if(redirect1 != null){
                targetName = redirect1.value();
            }else {
                targetName = methodAccess.getName();
            }
            var static1 = methodAccess.getAnnotation(MethodTarget.class).isStatic();
            //what returns does not matter
//            String returnType;
//            if(redirect2 != null){
//                returnType = ObfManager.getManager().reobfClassName( redirect2.value() );
//            }else {
//                returnType = ByteCodeUtils.toJvmType( methodAccess.getReturnType() );
//            }
            var arguCount = static1? methodAccess.getParameterCount(): methodAccess.getParameterCount()-1;
            var startArgument = static1?0:1;
            Method tar = null;
            String[] paramI = new String[arguCount];
            Parameter[] params = methodAccess.getParameters();
            Class[] paramsCls = methodAccess.getParameterTypes();
            for (int i= 0; i< arguCount; ++i){
                var redirect3 = params[i + startArgument].getAnnotation(RedirectType.class);
                if(redirect3 != null){
                    paramI[i] = redirect3.value();
                }else {
                    paramI[i] = ObfManager.getManager().deobfToJvm(paramsCls[i + startArgument]);
                }
            }
            List<Method> filter1 = methods.stream()
                .filter(m -> Modifier.isStatic(m.getModifiers()) == static1)
                .filter(m->m.getParameterCount() == arguCount)
                .filter(test->ObfManager.getManager().deobfMethod(test).equals(targetName))
                .filter(test ->{
                    //match every type after deobf
                    var paramTypes = test.getParameterTypes();
                    for (int i=0; i< arguCount ;++i ){

                        if(!ObfManager.getManager().deobfToJvm(paramTypes[i]).equals(paramI[i])){
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
            if(filter1.isEmpty()){
                uncompletedMethod.add(methodAccess);
            }else {
                methodDescrip.put(methodAccess, filter1.getFirst());
            }
        }
        //resolve methods
        List<Constructor<?>> constructors = Arrays.asList(targetClass.getDeclaredConstructors());
        for (Method constructorAccess : constructorTarget){
            var arguCount = constructorAccess.getParameterCount();
            Method tar = null;
            String[] paramI = new String[arguCount];
            Parameter[] params = constructorAccess.getParameters();
            Class[] paramsCls = constructorAccess.getParameterTypes();
            for (int i= 0; i< arguCount; ++i){
                var redirect3 = params[i].getAnnotation(RedirectType.class);
                if(redirect3 != null){
                    paramI[i] = redirect3.value();
                }else {
                    paramI[i] = ObfManager.getManager().deobfToJvm(paramsCls[i]);
                }
            }
            List<Constructor<?>> constructors1 = Arrays.stream(targetClass.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == arguCount)
                .filter(c -> {
                    var paramsTypes = c.getParameterTypes();
                    for (int i=0; i< arguCount ;++i){
                        if(!ObfManager.getManager().deobfToJvm(paramsTypes[i]).equals(paramI[i])){
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
            if(constructors1.isEmpty()){
                uncompletedMethod.add(constructorAccess);
            }else {
                constructorDescrip.put(constructorAccess, constructors1.getFirst());
            }

        }
        //resolve constructors
        uncompletedMethod.forEach(m ->{
            if(!ignoreFailure(m))
                Debug.warn("Target absent for method", m);
        });
        //start creating clazz
        T result = null;
        synchronized (CustomClassLoader.getInstance()){
            try{
                var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
                String implName = descriptiveInterface.getName().replace("$",".") + "Impl" + rand.nextInt(1000);
                //path.to.your.descriptorImpl114
                String implPath = implName.replace('.','/');
                String interfacePath = getInternalName(descriptiveInterface);
                cw.visit(
                    V21,
                    ACC_PUBLIC|ACC_FINAL|ACC_SUPER,
                    implPath,
                    null,
                    "java/lang/Object",
                    new String[]{interfacePath}
                );
                cw.visitSource(null, null);
                //create 内部类
                int index = 0 ;
                String fieldAccessorPath = getInternalName(FieldAccessor.class);
                Object2IntArrayMap<Field> handledField = new Object2IntArrayMap<>();
                Object2IntArrayMap<Method> handledMethod = new Object2IntArrayMap<>();
                Object2IntArrayMap<Constructor<?>> handledConstructor = new Object2IntArrayMap<>();
                HashSet<Field> relatedFields = new HashSet<>();
                relatedFields.addAll(fieldGetDescrip.values());
                relatedFields.addAll(fieldSetDescrip.values());
                FieldVisitor fv ;
                fv = cw.visitField(
                    ACC_FINAL | ACC_STATIC,
                        "delegate",
                    "Ljava/lang/Class;",
                    null,
                    null
                );
                fv.visitEnd();
                for (var entry: relatedFields){
                    int mod = entry.getModifiers();
                    if(Modifier.isPublic(mod)){
                        continue;
                    }else {
                        //catch need-handle fields
                        handledField.put(entry, index);
                        String fieldName = "handle"+index;
                        {
                            fv = cw.visitField(
                                ACC_FINAL| ACC_STATIC,
                                fieldName,
                                "Ljava/lang/invoke/VarHandle;",
                                null,
                                null
                            );
                            fv.visitEnd();
                        }
                        index ++;

                    }
                }
                for (var entry: methodDescrip.entrySet()){
                    int mod = entry.getValue().getModifiers();
                    if(Modifier.isPublic(mod)){
                        continue;
                    }else {
                        //catch need-handle methods
                        handledMethod.put(entry.getValue(), index);
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                ACC_FINAL| ACC_STATIC,
                                fieldName,
                                "Ljava/lang/invoke/MethodHandle;",
                                null,
                                null
                            );
                            fv.visitEnd();
                        }
                        index ++;

                    }
                }
                for (var entry: constructorDescrip.entrySet()){
                    int mod = entry.getValue().getModifiers();
                    if(Modifier.isPublic(mod)){
                        continue;
                    }else {
                        handledConstructor.put(entry.getValue(), index);
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                ACC_FINAL| ACC_STATIC,
                                fieldName,
                                "Ljava/lang/invoke/MethodHandle;",
                                null,
                                null
                            );
                            fv.visitEnd();
                        }
                        index ++;
                    }
                }
                //create need-handle fields;
                MethodVisitor mv ;
                {
                    ASMUtils.generateEmptyInit(cw, null);
                }
                {
                    mv = cw.visitMethod(
                        ACC_PUBLIC,
                        "getTargetClass",
                        "()Ljava/lang/Class;",
                        null,
                        null
                    );
                    mv.visitCode();
                    mv.visitFieldInsn(
                        GETSTATIC,
                        implPath,
                        "delegate",
                        "Ljava/lang/Class;"
                    );
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                for (var entry: fieldGetDescrip.entrySet()){
                    Method itfMethod = entry.getKey();
                    Field tarField = entry.getValue();
                    int mod = tarField.getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    if(Modifier.isPublic(mod)){
                        //create bytecode access directly
                        if(Modifier.isStatic(mod)){
                            mv.visitCode();
                            mv.visitFieldInsn(
                                GETSTATIC,
                                getInternalName(tarField.getDeclaringClass()),
                                tarField.getName(),
                                ByteCodeUtils.toJvmType(tarField.getType())
                            );
                            if(tarField.getType() != itfMethod.getReturnType()){
                                //cast
                                ASMUtils.castType(mv, getInternalName(tarField.getType()), getInternalName(itfMethod.getReturnType()));
                            }
                            ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                            mv.visitMaxs(0,0);
                            mv.visitEnd();

                        }else{

                            mv.visitCode();
                            if(itfMethod.getParameterCount() == 0){
                                throw new DescriptorBuildException("Illegal parameter detected at "+itfMethod.toString() +", getter of a non-static field, There should be more than one parameter");
                            }
                            //访问第0个参数
                            mv.visitVarInsn(ALOAD, 1);
                            Class<?> instanecType = itfMethod.getParameterTypes()[0];
                            if(!tarField.getDeclaringClass().isAssignableFrom(instanecType)){
                                //instance 不能直接赋值给tarField的
                                //cast
                                ASMUtils.castType(mv, getInternalName(instanecType), getInternalName(tarField.getDeclaringClass()));
                            }
                            mv.visitFieldInsn(
                                GETFIELD,
                                getInternalName(tarField.getDeclaringClass()),
                                tarField.getName(),
                                ByteCodeUtils.toJvmType(tarField.getType())
                            );
                            if(tarField.getType() != itfMethod.getReturnType()){
                                ASMUtils.castType(mv, getInternalName(tarField.getType()), getInternalName(itfMethod.getReturnType()));
                            }
                            ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                            mv.visitMaxs(0,0);
                            mv.visitEnd();

                        }
                    }else {
                        //to be continued
                        //using handlei
                        index = handledField.getInt(tarField);
                        String fieldName = "handle"+index;
                        mv.visitCode();
                        if(!Modifier.isStatic(mod) &&  itfMethod.getParameterCount() == 0){
                            throw new DescriptorBuildException("Illegal parameter detected at "+itfMethod.toString() +", getter of a non-static field, There should be more than one parameter");
                        }
                        mv.visitFieldInsn(
                            GETSTATIC,
                            implPath,
                            fieldName,
                            "Ljava/lang/invoke/VarHandle;"
                        );
                        if(!Modifier.isStatic(mod)){
                           mv.visitVarInsn(ALOAD, 1);
                           mv.visitMethodInsn(
                               INVOKEVIRTUAL,
                               "java/lang/invoke/VarHandle",
                               "get",
                               "(Ljava/lang/Object;)"+ByteCodeUtils.toJvmType(itfMethod.getReturnType()),
                               false);

                        }else {
                            mv.visitMethodInsn(
                                INVOKEVIRTUAL,
                                "java/lang/invoke/VarHandle",
                                "get",
                                "()"+ByteCodeUtils.toJvmType(itfMethod.getReturnType()),
                                false
                                );
                        }
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                        mv.visitMaxs(0,0);
                        mv.visitEnd();

                    }
                }
                for (var entry: fieldSetDescrip.entrySet()){
                    Method itfMethod = entry.getKey();
                    Field tarField = entry.getValue();
                    int mod = tarField.getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    mv.visitCode();
                    if(itfMethod.getParameterCount() + (Modifier.isStatic(mod)? 1:0)<2){
                        throw new DescriptorBuildException("Illegal parameter detected at "+itfMethod.toString() +", setter should have more parameters");
                    }
                    if(Modifier.isPublic(mod)){
                        if(Modifier.isStatic(mod)){
                            //如果参数类型不能直接赋值给tarField
                            Class<?> valType = itfMethod.getParameterTypes()[0];
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 1);
                            if( !tarField.getType().isAssignableFrom(valType) ){
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitFieldInsn(
                                PUTSTATIC,
                                getInternalName(tarField.getDeclaringClass()),
                                tarField.getName(),
                                ByteCodeUtils.toJvmType(tarField.getType())
                            );
                        }else {
                            Class<?> inst = itfMethod.getParameterTypes()[0];
                            Class<?> valType = itfMethod.getParameterTypes()[1];
                            mv.visitVarInsn(ALOAD,1);
                            if(!tarField.getDeclaringClass().isAssignableFrom( inst )){
                                ASMUtils.castType(mv, getInternalName(inst), getInternalName(tarField.getDeclaringClass()));
                            }
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 2);
                            if(!tarField.getType().isAssignableFrom( valType )){
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitFieldInsn(
                                PUTFIELD,
                                getInternalName(tarField.getDeclaringClass()),
                                tarField.getName(),
                                ByteCodeUtils.toJvmType(tarField.getType())
                            );
                        }
                    }else {
                        index = handledField.getInt(tarField);
                        String fieldName = "handle"+index;
                        mv.visitCode();
                        mv.visitFieldInsn(
                            GETSTATIC,
                            implPath,
                            fieldName,
                            "Ljava/lang/invoke/VarHandle;"
                        );
                        if(!Modifier.isStatic(mod)){
                            Class<?> valType = itfMethod.getParameterTypes()[1];
                            mv.visitVarInsn(ALOAD, 1);
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 2);
                            mv.visitMethodInsn(
                                INVOKEVIRTUAL,
                                "java/lang/invoke/VarHandle",
                                "set",
                                "(Ljava/lang/Object;"+ ByteCodeUtils.toJvmType(valType) +")V",
                                false
                            );
                        }else {
                            Class<?> valType = itfMethod.getParameterTypes()[0];
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 1);
                            mv.visitMethodInsn(
                                INVOKEVIRTUAL,
                                "java/lang/invoke/VarHandle",
                                "set",
                                "("+ ByteCodeUtils.toJvmType(valType) +")V",
                                false
                            );
                        }
                    }
                    ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfMethod.getReturnType()));
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                for (var entry : methodDescrip.entrySet()){
                    Method itfMethod = entry.getKey();
                    Method tarMethod = entry.getValue();
                    int mod = tarMethod .getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    int count = itfMethod.getParameterCount();
                    Preconditions.checkArgument(count == tarMethod.getParameterCount() +(Modifier.isStatic(mod)? 0:1),"Parameter count not match at method "+ itfMethod +" with target "+tarMethod);
                    Class<?>[] itfType = itfMethod.getParameterTypes();
                    Class<?>[] tarType = tarMethod.getParameterTypes();

                    Class<?> returnType = tarMethod.getReturnType();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    //itf has int return value, but target return void
                    boolean castReturn = returnType!=void.class && itfReturnType != void.class;
                    //load all invoke arguments
                    //if not public, load MethodHandle here, also load fucking try-catch block
                    boolean useHandle = !Modifier.isPublic(mod);
                    Label label0 = null;
                    Label label1 = null;
                    Label label2 = null;
                    if(useHandle){
                        label0 = new Label();
                        label1 = new Label();
                        label2 = new Label();
                        mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
                        mv.visitLabel(label0);
                        index = handledMethod.getInt(tarMethod);
                        mv.visitFieldInsn(GETSTATIC, implPath, "handle"+index, "Ljava/lang/invoke/MethodHandle;");
                    }
                    //load
                    if(Modifier.isStatic(mod)){
                        for (int i=0;i<count ;++i){
                            ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), i+1);
                            if(!tarType[i].isAssignableFrom(itfType[i])){
                                ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i]));
                            }
                        }
                    }else {
                        mv.visitVarInsn(ALOAD, 1);
                        if(!tarMethod.getDeclaringClass().isAssignableFrom(itfType[0])){
                            ASMUtils.castType(mv, getInternalName(itfType[0]), getInternalName(tarMethod.getDeclaringClass()));
                        }
                        for (int i=1;i<count ;++i){
                            ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), i+1);
                            if(!tarType[i-1].isAssignableFrom(itfType[i])){
                                ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i-1]));
                            }
                        }
                    }
                    //execute invoke
                    if(Modifier.isPublic(mod)){
                        //use pure bytecode
                        if(Modifier.isStatic(mod)){
                            //invokestatic
                            //load parameters
                            mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(tarMethod.getDeclaringClass()),
                                tarMethod.getName(),
                                getMethodDescriptor(tarMethod),
                                tarMethod.getDeclaringClass().isInterface()
                            );
                        }else {
                            //load instance
                            mv.visitMethodInsn(
                               tarMethod.getDeclaringClass().isInterface()?INVOKEINTERFACE: INVOKEVIRTUAL,
                                getInternalName(tarMethod.getDeclaringClass()),
                                tarMethod.getName(),
                                getMethodDescriptor(tarMethod),
                                tarMethod.getDeclaringClass().isInterface()
                            );
                        }
                        if(castReturn){
                            if(!itfReturnType.isAssignableFrom(returnType)){
                                ASMUtils.castType(mv, getInternalName(returnType), getInternalName(itfReturnType));
                            }
                        }
                    }else{
                        //use MethodHandle.invokeExact
                        StringBuilder builder = new StringBuilder();
                        builder.append('(');
                        if(!Modifier.isStatic(mod)){
                            builder.append(ByteCodeUtils.toJvmType(tarMethod.getDeclaringClass()));
                        }
                        for (Class<?> arg: tarMethod.getParameterTypes()){
                            builder.append(ByteCodeUtils.toJvmType(arg));
                        }
                        builder.append(')');
                        builder.append(ByteCodeUtils.toJvmType(tarMethod.getReturnType()));
                        mv.visitMethodInsn(
                            INVOKEVIRTUAL,
                            "java/lang/invoke/MethodHandle",
                            "invokeExact",
                            builder.toString(),
                            false
                        );
                    }
                    if(!castReturn && returnType != void.class){
                        mv.visitInsn(POP);
                    }
                    if(useHandle){
                        mv.visitLabel(label1);
                    }

                    if(!castReturn){
                        //ignore return value, to make stack size correct
                        ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfReturnType));
                    }else {
                        //if return void, the itf also return void
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    }
                    if(useHandle){
                        mv.visitLabel(label2);
                        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
                        //catch block 直接消耗掉throwable
                        mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorException.class),
                            "dump",
                            "(Ljava/lang/Throwable;)"+ ByteCodeUtils.toJvmType(DescriptorException.class),
                            false
                        );
                        mv.visitInsn(Opcodes.ATHROW);
                    }
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                for(var entry: constructorDescrip.entrySet()){
                    Method itfMethod = entry.getKey();
                    Constructor<?> cons = entry.getValue();
                    int mod = cons.getModifiers();
                    int count = itfMethod.getParameterCount();
                    Class<?>[] itfType = itfMethod.getParameterTypes();
                    Class<?>[] tarType = cons.getParameterTypes();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    boolean castReturn =  itfReturnType != void.class;
                    Preconditions.checkArgument(count == cons.getParameterCount(),"Parameters not match for method "+itfMethod+" with constructor "+cons);
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    boolean useHandle = !Modifier.isPublic(mod);
                    Label label0 = null;
                    Label label1 = null;
                    Label label2 = null;
                    if(useHandle){
                        label0 = new Label();
                        label1 = new Label();
                        label2 = new Label();
                        mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
                        mv.visitLabel(label0);
                        index = handledConstructor.getInt(cons);
                        mv.visitFieldInsn(GETSTATIC, implPath, "handle"+index, "Ljava/lang/invoke/MethodHandle;");
                    }
                    String tarClass = getInternalName(cons.getDeclaringClass());
                    if(Modifier.isPublic(mod)){
                        mv.visitTypeInsn(NEW, tarClass);
                        mv.visitInsn(DUP);
                    }
                    //load and cast types
                    for (int i=0;i<count ;++i){
                        ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), i+1);
                        if(!tarType[i].isAssignableFrom(itfType[i])){
                            ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i]));
                        }
                    }
                    if(Modifier.isPublic(mod)){
                        //load instance
                        mv.visitMethodInsn(
                            INVOKESPECIAL,
                            tarClass,
                            "<init>",
                            getConstructorDescriptor(cons),
                            false
                        );

                        if(castReturn){
                            if(!itfReturnType.isAssignableFrom(cons.getDeclaringClass())){
                                ASMUtils.castType(mv, tarClass, getInternalName(itfReturnType));
                            }
                        }
                    }else {
                        //not implemented yet
                        StringBuilder builder = new StringBuilder();
                        builder.append('(');
                        for (Class<?> arg: cons.getParameterTypes()){
                            builder.append(ByteCodeUtils.toJvmType(arg));
                        }
                        builder.append(')');
                        builder.append(ByteCodeUtils.toJvmType(cons.getDeclaringClass()));
                        mv.visitMethodInsn(
                            INVOKEVIRTUAL,
                            "java/lang/invoke/MethodHandle",
                            "invokeExact",
                            builder.toString(),
                            false
                        );
                    }
                    if(!castReturn){
                        mv.visitInsn(POP);
                    }
                    if(useHandle){
                        mv.visitLabel(label1);
                    }
                    if(!castReturn){
                        //ignore return value, to make stack size correct
                        ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfReturnType));
                    }else {
                        //if return void, the itf also return void
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    }
                    if(useHandle){
                        mv.visitLabel(label2);
                        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
                        //catch block 直接消耗掉throwable
                        mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorException.class),
                            "dump",
                            "(Ljava/lang/Throwable;)"+ ByteCodeUtils.toJvmType(DescriptorException.class),
                            false
                        );
                        mv.visitInsn(Opcodes.ATHROW);
                    }
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                for(Method tar: uncompletedMethod){
                    mv = ASMUtils.createOverrideMethodImpl(cw, tar);
                    mv.visitCode();
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        getInternalName(DescriptorException.class),
                        "notImpl",
                        "()"+ ByteCodeUtils.toJvmType(DescriptorException.class),
                        false
                    );
                    mv.visitInsn(ATHROW);
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                //now all methods are created successfully?
                //we should complete <clinit>
                {
                    mv = cw.visitMethod(
                        ACC_STATIC,
                        "<clinit>",
                        "()V",
                        null,
                        null
                    );
                    mv.visitCode();

                    for (Field entry: handledField.keySet()){
                        index = handledField.getInt(entry);
                        String fieldName = "handle"+index;
                        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                            entry.getDeclaringClass(),
                            MethodHandles.lookup()
                        );
                        VarHandle handle = lookup.unreflectVarHandle(entry);
                        String randId = randStr();
                        values0.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorImplBuilder.class),
                            "initVarHandle",
                            "(ILjava/lang/String;)Ljava/lang/invoke/VarHandle;",
                            false
                        );
                        mv.visitFieldInsn(
                            PUTSTATIC,
                            implPath,
                            fieldName,
                            "Ljava/lang/invoke/VarHandle;"
                        );
                    }
                    for (Method method : handledMethod.keySet()){
                        index = handledMethod.getInt(method);
                        String fieldName = "handle"+index;
                        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                            method.getDeclaringClass(),
                            MethodHandles.lookup()
                        );
                        MethodHandle handle = lookup.unreflect(method);
                        String randId = randStr();
                        values1.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorImplBuilder.class),
                            "initMethodHandle",
                            "(ILjava/lang/String;)Ljava/lang/invoke/MethodHandle;",
                            false
                        );
                        mv.visitFieldInsn(
                            PUTSTATIC,
                            implPath,
                            fieldName,
                            "Ljava/lang/invoke/MethodHandle;"
                        );
                    }
                    for (Constructor cons : handledConstructor.keySet()){
                        index = handledConstructor.getInt(cons);
                        String fieldName = "handle"+index;
                        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                            cons.getDeclaringClass(),
                            MethodHandles.lookup()
                        );
                        MethodHandle handle = lookup.unreflectConstructor(cons);
                        String randId = randStr();
                        values1.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorImplBuilder.class),
                            "initMethodHandle",
                            "(ILjava/lang/String;)Ljava/lang/invoke/MethodHandle;",
                            false
                        );
                        mv.visitFieldInsn(
                            PUTSTATIC,
                            implPath,
                            fieldName,
                            "Ljava/lang/invoke/MethodHandle;"
                        );
                    }
                    String randId = randStr();
                    values2.put(randId, targetClass);
                    mv.visitLdcInsn(randCode);
                    mv.visitLdcInsn(randId);
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        getInternalName(DescriptorImplBuilder.class),
                        "initDelegate",
                        "(ILjava/lang/String;)Ljava/lang/Class;",
                        false
                    );
                    mv.visitFieldInsn(
                        PUTSTATIC,
                        implPath,
                        "delegate",
                        "Ljava/lang/Class;"
                    );
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(0,0);
                    mv.visitEnd();
                }
                cw.visitEnd();
                byte[] code = cw.toByteArray();
                CustomClassLoader.getInstance().defineAccessClass(implName, code);
                Class<T> clazz = CustomClassLoader.getInstance().loadAccessClass(implName);
                T val = clazz.getConstructor().newInstance();
                result = val;
            }finally {
                reset0(randCode);
            }
        }

        return result;
    }

    private static boolean ignoreFailure(Method method){
        var re = method.getAnnotation(IgnoreFailure.class);
        if(re != null){
            var version = re.thresholdInclude();
            boolean below = re.below();
            if(EnvironmentManager.getManager().getVersion().isAtLeast(version) != below){
                return true;
            }
        }
        return false;
    }
    private static String randStr(){
        String val;
        do{
            val = UUID.randomUUID().toString();
        }while (stringPool.contains(val));
        stringPool.add(val);
        return val;
    }
    private static void reset0(int rand1){
        Preconditions.checkArgument(rand1 == randCode, "IllegalAccess!");
        values0.clear();
        values1.clear();
        randCode = rand.nextInt(1145141919);
    }
    private static int randCode;
    private static final Set<String> stringPool = new HashSet<>();
    private static final Map<String, VarHandle> values0 = new HashMap<>();
    private static final Map<String, MethodHandle> values1 = new HashMap<>();
    private static final Map<String, Class<?>> values2 = new HashMap<>();
    public static VarHandle initVarHandle(int code, String val){
        Preconditions.checkArgument(code == randCode,"IllegalAccess!");
        return Objects.requireNonNull(values0.remove(val));
    }
    public static MethodHandle initMethodHandle(int code, String value){
        Preconditions.checkArgument(code == randCode,"IllegalAccess!");
        return Objects.requireNonNull(values1.remove(value));
    }
    public static Class initDelegate(int code, String value){
        Preconditions.checkArgument(code == randCode,"IllegalAccess!");
        return Objects.requireNonNull(values2.remove(value));
    }
    static{
        reset0(randCode);
    }
}

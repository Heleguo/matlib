package me.matl114.matlib.utils.reflect;

import com.google.common.base.Preconditions;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LambdaUtils {
    private static final ConcurrentHashMap<Pair<Class<?>, Method>, CallSite> lambdaCache = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<Field, FieldGetter> lambdaFieldGetter = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<Field, FieldSetter> lambdaFieldSetter = new ConcurrentHashMap<>();
    public static <T> T createLambdaForStaticMethod(Class<T> functionalInterface, Method method) throws Throwable{
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method), (p)->{
            try {
                return createLambdaForMethodInternal(p.getA(), p.getB(), true, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        try{
            return (T)(callSite.getTarget().invoke());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    public static <T,W> Function<W,T> createLambdaBinding(Class<T> functionalInterface, Method method) throws Throwable{
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method), (p)->{
            try {
                return createLambdaForMethodInternal(p.getA(), p.getB(), false, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        return (obj)->{
            try{
                return (T)(callSite.getTarget().invoke(obj));
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> T createLambdaForMethod(Class<T> functionalInterface, Method method) throws Throwable{
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method),(p)->{
            try{
                return createLambdaForMethodInternal(p.getA(), p.getB(), false, true);
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        });
        try{
            return (T)(callSite.getTarget().invoke());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    private static <T> CallSite createLambdaForMethodInternal(Class<T> functionalInterface, Method method, boolean sta, boolean dynamicBind){

        Method functionalMethod = Arrays.stream(functionalInterface.getMethods())
            .filter(m-> Modifier.isAbstract(m.getModifiers()))
            .findAny()
            .orElseThrow(()->new IllegalArgumentException("Illegal Argument for functional Interface! Abstract method not found in class"+functionalInterface));
        Class<?> targetClass = method.getDeclaringClass();
        try{
            MethodHandles.Lookup lookup  ;
            //private lookup seems to not having a MODULE access which is required by lambda
            MethodHandle handle ;
            if(Modifier.isPublic(method.getModifiers())){
                lookup = MethodHandles.lookup();
            }else {
                var publicLoopup = MethodHandles.lookup();
                Preconditions.checkArgument(publicLoopup.lookupClass().getModule() == targetClass.getModule(),"Can not create lambda expression for package-private methods in another module");
                lookup = MethodHandles.privateLookupIn(targetClass, publicLoopup);
            }
            handle = lookup.unreflect(method);

            return LambdaMetafactory.metafactory(
                lookup,
                functionalMethod.getName(),
                sta || dynamicBind? MethodType.methodType(functionalInterface):MethodType.methodType(functionalInterface,targetClass),
                getMethodType(functionalMethod),
                handle,
                getMethodType(method, dynamicBind)
                );
        }catch (Throwable e){
            throw new RuntimeException("Error while creating lambda expression!", e);
        }

    }
//    private static <T> CallSite createLambdaForFieldInternal(Class<T> functionalInterface, Field field, boolean getter){
//        Method functionalMethod = Arrays.stream(functionalInterface.getMethods())
//            .filter(m-> Modifier.isAbstract(m.getModifiers()))
//            .findAny()
//            .orElseThrow(()->new IllegalArgumentException("Illegal Argument for functional Interface! Abstract method not found in class"+functionalInterface));
//        Class<?> targetClass = field.getDeclaringClass();
//        boolean sta = Modifier.isStatic(field.getModifiers());
//        try{
//            MethodHandles.Lookup privatelookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
//            return LambdaMetafactory.metafactory(
//                privatelookup,
//                functionalMethod.getName(),
//                MethodType.methodType(functionalInterface),
//                getMethodType(functionalMethod),
//                getter? privatelookup.unreflectGetter(field): privatelookup.unreflectSetter(field),
//                getter?
//                    ( sta ? MethodType.methodType(field.getType()) :MethodType.methodType(field.getType(), targetClass))
//                    :(sta ? MethodType.methodType(void.class, field.getType()): MethodType.methodType(void.class, targetClass, field.getType()))
//            );
//        }catch (Throwable e){
//            throw new RuntimeException("Error while creating lambda expression!", e);
//        }
//    }
//
//    public static FieldGetter<?,?> createFieldGetterLambda(Field field) throws Throwable{
//        return lambdaFieldGetter.computeIfAbsent(field, (f)->{
//            boolean isStatic = Modifier.isStatic(f.getModifiers());
//            if(isStatic){
//                CallSite callSite = createLambdaForFieldInternal(FieldGetter.StaticFieldGetter.class, f, true);
//                try{
//                    return ((FieldGetter.StaticFieldGetter<?>)callSite.getTarget().invokeExact()).toCommon();
//                }catch (Throwable e){
//                    throw  new RuntimeException(e);
//                }
//            }else {
//                CallSite callSite =createLambdaForFieldInternal(FieldGetter.class, f, true);
//                try{
//                    return (FieldGetter<?, ?>) callSite.getTarget().invokeExact();
//                }catch (Throwable e){
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
//
//    public static FieldSetter<?,?> createFieldSetterLambda(Field field) throws Throwable{
//        return lambdaFieldSetter.computeIfAbsent(field, (f)->{
//            boolean isStatic = Modifier.isStatic(f.getModifiers());
//            if(isStatic){
//                CallSite callSite = createLambdaForFieldInternal(FieldSetter.StaticFieldSetter.class, f, true);
//                try{
//                    return ((FieldSetter.StaticFieldSetter<?>)callSite.getTarget().invokeExact()).toCommon();
//                }catch (Throwable e){
//                    throw  new RuntimeException(e);
//                }
//            }else {
//                CallSite callSite =createLambdaForFieldInternal(FieldSetter.class, f, true);
//                try{
//                    return (FieldSetter<?, ?>) callSite.getTarget().invokeExact();
//                }catch (Throwable e){
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }

    public static MethodType getMethodType(Method method){
        return MethodType.methodType(method.getReturnType(), method.getParameterTypes());
    }
    public static MethodType getMethodTypeWithSelf(Method method){
        Class[] param = method.getParameterTypes();
        Class[] newParam = new Class[param.length+1];
        System.arraycopy(param, 0, newParam, 1,param.length);
        newParam[0] = method.getDeclaringClass();
        return MethodType.methodType(method.getReturnType(), newParam);
    }
    public static MethodType getMethodType(Method method, boolean dynamic){
        return dynamic? getMethodTypeWithSelf(method): getMethodType(method);
    }


}

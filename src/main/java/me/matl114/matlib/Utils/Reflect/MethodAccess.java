package me.matl114.matlib.Utils.Reflect;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

public class MethodAccess {
    private boolean printError = false;
    private boolean failInitialization=false;
    private Function<Object, Method> lazilyInitializationFunction;
    private Method field;
    public static MethodAccess ofName(Class clazz ,String fieldName,Class... parameterTypes){
        return new MethodAccess((obj)->{
            var result=ReflectUtils.getMethodsRecursively(clazz,fieldName,parameterTypes);
            return result==null?null:result.getFirstValue();
        }).printError(true);
    }
    public static MethodAccess ofName(String fieldName,Class... parameterTypes){
        return new MethodAccess((obj)->{
            var result=ReflectUtils.getMethodsRecursively(obj.getClass(),fieldName,parameterTypes);
            return result==null?null:result.getFirstValue();
        }).printError(true);
    }
    public static MethodAccess of(Method field){
        return new MethodAccess((obj)->field).printError(true);
    }

    public MethodAccess(Function<Object, Method> initFunction) {
        this.lazilyInitializationFunction =initFunction;
    }
    public MethodAccess printError(boolean printError) {
        this.printError = printError;
        return this;
    }
    private Method getFieldInternal(Object obj) throws Throwable {
        Method field=lazilyInitializationFunction.apply(obj);
        Preconditions.checkArgument(field!=null,"MethodAccess init field failed: method is null! using argument: "+(obj==null?"null":obj.toString()));
        field.setAccessible(true);
        return field;
    }
    private MethodAccess init(Object obj) {
        if(this.field==null&&!failInitialization){
            try{
                field=getFieldInternal(obj);
            }catch (Throwable e){
                failInitialization=true;
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
    private Class getMethodReturnType(){
        Preconditions.checkArgument(!failInitialization,"MehodAccess initialization failed!");
        Preconditions.checkArgument(field!=null,"MehodAccess method not initialized!");
        return field.getReturnType();
    }
    private Class getDeclareClass(){
        Preconditions.checkArgument(!failInitialization,"MehodAccess initialization failed!");
        Preconditions.checkArgument(field!=null,"MehodAccess method not initialized!");
        return field.getDeclaringClass();
    }
    public MethodAccess initWithNull(){
        init(null);
        return this;
    }
    public Object invoke(Object tar,Object... obj) throws Throwable{
        init(obj);
        return field.invoke(tar,obj);
    }
    public Object invokeIgnoreFailure(Object tar,Object... obj) throws Throwable{
        if(field==null){
            try{
                field=getFieldInternal(tar);
            }catch (Throwable e){
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return field.invoke(tar,obj);
    }
    public MethodAccess invokeCallback(Consumer<Object> callback,Runnable failedCallback, Object tar, Object... obj) {
        if(this.failInitialization){
            failedCallback.run();
            return this;
        }
        try{
            Object result=invoke(tar,obj);
            callback.accept(result);
        }catch (Throwable e){
            failedCallback.run();
        }return this;
    }
    public MethodAccess invokeCallback(Consumer<Object> callback, Object tar, Object... obj) {
       return invokeCallback(callback, ()->{},tar,obj);
    }

}

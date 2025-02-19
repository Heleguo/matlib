package me.matl114.matlib.Utils.Reflect;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeProvider;
import me.matl114.matlib.Utils.Debug;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
@SuppressWarnings("unchecked")
public class MethodAccess<T extends Object> {
    private static HashMap<Class, com.esotericsoftware.reflectasm.MethodAccess> cachedAccess = new HashMap<>();
    public static com.esotericsoftware.reflectasm.MethodAccess getOrCreateAccess(Class<?> targetClass){
        return cachedAccess.computeIfAbsent(targetClass, (clz)->Debug.interceptAllOutputs(()-> com.esotericsoftware.reflectasm.MethodAccess.get(clz),(output)->{
            if (output !=null && !output.isEmpty()){
                Debug.warn("Console output is intercepted:",output);
                Debug.warn("It is not a BUG and you can ignore it ");
            }
        }))    ;
    }
    private boolean printError = false;
    @Getter
    private boolean failInitialization=false;
    private Function<Object, Method> lazilyInitializationFunction;
    private Method field;
    private MethodHandle handle;
    private boolean failHandle = false;
    private boolean isStatic;
    private int argLen;
    private boolean isVoidReturn;
    private static final boolean useHandle=false;
    private boolean createSnapshot = true;
    public MethodAccess<T> noSnapShot(){
        this.createSnapshot = false;
        return this;
    }
    public static MethodAccess ofName(Class clazz ,String fieldName){
        return new MethodAccess((obj)->{
            var result=ReflectUtils.getMethodsByName(clazz,fieldName);
            return result==null?null:result.getA();
        });
    }
    public static MethodAccess ofName(Class clazz ,String fieldName,Class... parameterTypes){
        return new MethodAccess((obj)->{
            var result=ReflectUtils.getMethodsRecursively(clazz,fieldName,parameterTypes);
            return result==null?null:result.getA();
        });
    }
    public static MethodAccess ofName(String fieldName,Class... parameterTypes){
        return new MethodAccess((obj)->{
            var result=ReflectUtils.getMethodsRecursively(obj.getClass(),fieldName,parameterTypes);
            return result==null?null:result.getA();
        });
    }
    public static MethodAccess of(Method field){
        return new MethodAccess((obj)->field);
    }
    private static final MethodAccess FAIL = new InitializeProvider<>(()->{
        var access= new MethodAccess<>(null);
        access.failInitialization=true;
        return access;
    }) .v();
    public static MethodAccess ofFailure(){
        return FAIL;
    }
    public <W extends Object> MethodAccess<W> forceCast(Class<W> clazz){
        if(this.field!=null)
            Preconditions.checkArgument(clazz.isAssignableFrom(this.field.getReturnType()),"cast failed,return type does not match :%s ,expect super of %s",clazz,this.field.getReturnType());
        return (MethodAccess<W>)this;
    }

    public MethodAccess(Function<Object, Method> initFunction) {
        this.lazilyInitializationFunction =initFunction;
    }
    public MethodAccess<T> printError(boolean printError) {
        this.printError = printError;
        return this;
    }

    public MethodAccess<T> init(Object obj) {
        if(this.field==null&&!failInitialization){
            try{
                initInternal(obj);
            }catch (Throwable e){
                failInitialization=true;
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
    private void initInternal(Object obj) throws Throwable{
        Method field=lazilyInitializationFunction.apply(obj);
        Preconditions.checkArgument(field!=null,"MethodAccess init method failed: method is null! using argument: "+(obj==null?"null":obj.getClass()));
        field.setAccessible(true);
        this.field= field;
        this.argLen=this.field.getParameterCount();
        if(this.createSnapshot){
            this.isStatic= Modifier.isStatic(this.field.getModifiers());
            this.isVoidReturn =this.field.getReturnType()==void.class;
            try{
                this.handle= MethodHandles.privateLookupIn(this.field.getDeclaringClass(),MethodHandles.lookup()).unreflect(this.field);
            }catch (Throwable handleFailed){
                this.failHandle=true;
                if(printError){
                    Debug.logger("Failed to create method handle for method :",field);
                    handleFailed.printStackTrace();
                }
            }
        }

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
    public MethodAccess<T> initWithNull(){
        init(null);
        return this;
    }
    public MethodHandle getMethodHandleOrDefault(Supplier<MethodHandle> defa){
        init(null);
        return failHandle?defa.get():handle;
    }
    public MethodHandle finalizeHandleOrDefault(Object initializeObject,Supplier<MethodHandle> defa){
        init(initializeObject);
        return failHandle?defa.get():handle;
    }

    private Object invokeUsingHandle(Object tar ,Object... obj) throws Throwable{
        if(!useHandle){
            throw new RuntimeException("Method Invoke using MethodHandle is not supported anyMore");
        }
        if(isStatic){
            if(this.argLen==0){
                if(isVoidReturn){

                }
            }else{
                return handle.invokeWithArguments(obj);
            }
        }else {
            if(this.argLen==0){
                return handle.invoke(tar);
            }else {
                Object[] arg=new Object[obj.length+1];
                arg[0]=tar;
                System.arraycopy(obj,0,arg,1,obj.length);
                return handle.invokeWithArguments(arg);
            }
        }
        return null;
    }
    private T invokeInternal(Object tar,Object... obj) throws Throwable {
        Preconditions. checkArgument(obj.length==this.argLen,"wrong number of arguments: %s expected: %s",obj.length,this.argLen);
        if(!useHandle|| this.failHandle){
            return (T)field.invoke(tar,obj);
        }else {
            return (T)invokeUsingHandle(tar,obj);
        }
    }
    public T invoke(Object tar,Object... obj) throws Throwable{
        init(tar);
        return invokeInternal(tar,obj);
    }
    public T invokeIgnoreFailure(Object tar,Object... obj) throws Throwable{
        if(field==null){
            try{
                initInternal(tar);
            }catch (Throwable e){
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return invokeInternal(tar,obj);
    }

    public MethodAccess<T> invokeCallback(Consumer<T> callback,Runnable failedCallback, Object tar, Object... obj) {
        if(this.failInitialization){
            failedCallback.run();
            return this;
        }
        try{
            T result=invoke(tar,obj);
            callback.accept(result);
        }catch (Throwable e){
            failedCallback.run();
        }return this;
    }

    public MethodAccess<T> invokeCallback(Consumer<T> callback, Object tar, Object... obj) {
       return invokeCallback(callback, ()->{},tar,obj);
    }
    public Supplier<T> getInvokeTask(Object tar, Object... obj){
        if(this.field==null){
            init(tar);
        }
        if(this.failInitialization){
            return null;
        }else {
            //return this::invokeInternal();
            return ()->{
                try{
                    return invokeInternal(tar,obj);
                }catch (Throwable e){
                    throw new RuntimeException(e);
                }
            };
        }
    }

}

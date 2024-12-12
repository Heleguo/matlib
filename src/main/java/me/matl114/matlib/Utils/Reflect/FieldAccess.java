package me.matl114.matlib.Utils.Reflect;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldAccess {
    private boolean printError = false;
    private boolean failInitialization=false;
    private Function<Object, Field> lazilyInitializationFunction;
    private Field field;

    public static FieldAccess ofName(String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(obj.getClass(),fieldName);
            return result==null?null:result.getFirstValue();
        }).printError(true);
    }
    public static FieldAccess of(Field field){
        return new FieldAccess((obj)->field).printError(true);
    }
    public static FieldAccess ofName(Class<?> clazz,String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(clazz,fieldName);
            return result==null?null:result.getFirstValue();
        }).printError(true);
    }
    public FieldAccess(Function<Object, Field> initFunction) {
        this.lazilyInitializationFunction =initFunction;
    }
    public FieldAccess printError(boolean printError) {
        this.printError = printError;
        return this;
    }
    private FieldAccess init(Object obj){
        try{
            field=lazilyInitializationFunction.apply(obj);
            Preconditions.checkArgument(field!=null,"FieldAccess init field failed: field is null! using argument: "+(obj==null?"null":obj.toString()));
            field.setAccessible(true);
        }catch (Throwable e){
            failInitialization=true;
            if(printError){
                e.printStackTrace();
            }
        }
        return this;
    }
    private Class getFieldType(){
        Preconditions.checkArgument(!failInitialization,"FieldAccess initialization failed!");
        Preconditions.checkArgument(field!=null,"FieldAccess field not initialized!");
        return field.getType();
    }
    private Class getDeclareClass(){
        Preconditions.checkArgument(!failInitialization,"FieldAccess initialization failed!");
        Preconditions.checkArgument(field!=null,"FieldAccess field not initialized!");
        return field.getDeclaringClass();
    }
    public FieldAccess initWithNull(){
        init(null);
        return this;
    }
    private Object getValue(Object obj) throws Throwable{
        init(obj);
        return field.get(obj);
    }
    public AccessWithObject ofAccess(Object obj){
        AccessWithObject ob=new AccessWithObject();
        ob.value=obj;
        if(FieldAccess.this.failInitialization){
            ob.failed=true;
        }
        return ob;
    }
    public class AccessWithObject<T>{
        private boolean failed=false;
        private boolean hasTried=false;
        private Object value;
        private T re;
        public AccessWithObject<T> get(Consumer<T> callback){
            if(!hasTried){
                hasTried=true;
                try{
                    re=(T)FieldAccess.this.getValue(value);
                }catch(Throwable e){
                    failed=true;
                    return this;
                }
            }
            if(failed){
                re=null;
                return this;
            }
            callback.accept(re);
            return this;
        }
        public T getRaw(){
            if(!hasTried){
                get((e)->{});
            }
            return re;
        }
        public T getRawOrDefault(Supplier<T> supplier){
            if(!hasTried){
                get((e)->{});
            }
            if(failed){
                return supplier.get();
            }
            return re;
        }
        public AccessWithObject<T> ifFailed(Consumer<Object> callback){
            if(failed){
                callback.accept(value);
            }
            return this;
        }
        public boolean set(T value1){
            if(!failed){
                try{
                    FieldAccess.this.field.set(re, value1);
                    return true;
                }catch (Throwable e){
                    AtomicBoolean result=new AtomicBoolean(false);
                    ReflectUtils.getUnsafeSetter(FieldAccess.this.field,((unsafe, fieldOffset, field1) -> {
                        unsafe.putObject(value, fieldOffset,value1);
                        result.set(true);
                    }));
                    return result.get();
                }

            }
            return false;

        }
    }


}

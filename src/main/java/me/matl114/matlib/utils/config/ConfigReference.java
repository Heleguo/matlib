package me.matl114.matlib.utils.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public final class ConfigReference<T extends  Object> {
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Class<T> identifier;
    private T value;
    private Consumer<T> makeDirty;
    //private String[] path;
    public static  <W extends Object> ConfigReference<W> of(W object, Consumer<W> makeDirty){
        return new ConfigReference<>(object, object==null?(Class<W>)Object.class: (Class<W>) object.getClass(),makeDirty);
    }
    private ConfigReference(T object,Class<T> id,Consumer<T> makeDirty) {
        this.identifier=id;
        this.value=object;
        this.makeDirty=makeDirty;
    }
    public T get(){
        return value;
    }
    //return if pass identifier check
    public boolean set(T value){
        if(this.identifier.isInstance(value)){
            this.value=value;
            this.makeDirty();
            return true;
        }else {
            return false;
        }
    }
    public void makeDirty(){
        if(this.makeDirty!=null){
            this.makeDirty.accept(value);
        }
    }
}

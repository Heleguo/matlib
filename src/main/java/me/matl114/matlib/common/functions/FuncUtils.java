package me.matl114.matlib.common.functions;

import java.util.function.Function;
import java.util.function.Supplier;

public class FuncUtils {
    public static <W> Supplier<W> nullSupplier(){
        return ()->null;
    }
    public static <W> Supplier<W> nullTyped(Class<W> clz){
        return ()->null;
    }

    public static <W,R> Function<W,R> cast(){
        return (w)->(R)w;
    }

    public static <W,R> Function<W,R> ifPresent(Function<W,R> func){
        return (w)->{
            if(w != null){
                return func.apply(w);
            }else{
                return null;
            }
        };
    }
    public static <W> Supplier<W> value(W val){
        return ()->val;
    }

}

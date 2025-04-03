package me.matl114.matlib.utils.language;

import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.utils.language.componentCompiler.Parameter;
import net.kyori.adventure.text.format.Style;

import java.util.function.Function;

public interface PlaceholderProvider {
    /**
     * try resolve dynamic placeholder, return null if not dynamic
     * @param key
     * @return
     * @param <T>
     */
    public <T extends Object> Function<Parameter,T> getDynamic(String key);
    public String getStringOrDefault(String key);
    public Style getStyleOrDefault(String key);
    public <T extends Object> T getValueOrDefault(String key, T defaultValue);
    static int getPotentialIndex(String key){
        if(MathUtils.isInteger(key)){
            try{
                return Integer.parseInt(key);
            }catch (Throwable e){}
        }
        return Integer.MIN_VALUE;
    }
}

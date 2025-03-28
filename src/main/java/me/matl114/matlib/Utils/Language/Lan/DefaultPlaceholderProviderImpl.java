package me.matl114.matlib.Utils.Language.Lan;

import me.matl114.matlib.Utils.Language.ComponentCompiler.Parameter;
import me.matl114.matlib.Utils.Language.PlaceholderProvider;
import net.kyori.adventure.text.format.Style;

import java.util.function.Function;

public class DefaultPlaceholderProviderImpl implements PlaceholderProvider {

    @Override
    public <T> Function<Parameter, T> getDynamic(String key) {
        int i = PlaceholderProvider.getPotentialIndex(key);
        if( i == Integer.MIN_VALUE){
            return null;
        }else {
            return (parameter -> parameter.getI(i));
        }
    }

    @Override
    public String getStringOrDefault(String key) {
        return key;
    }

    @Override
    public Style getStyleOrDefault(String key) {
        return Style.empty();
    }

    @Override
    public <T> T getValueOrDefault(String key, T defaultValue) {
        return defaultValue;
    }
}

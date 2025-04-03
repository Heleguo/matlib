package me.matl114.matlib.utils.language.componentCompiler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.common.lang.exceptions.NoLongerSupport;
import me.matl114.matlib.utils.language.PlaceholderProvider;
import net.kyori.adventure.text.format.Style;

@AllArgsConstructor
public class BaseTypeAST<T extends Object> implements VariableProvider<T> {
    @Getter
    BaseType type;
    String rawData;

    @Setter
    @Getter
    boolean placeholder = false;
    public final T get(PlaceholderProvider placeholder, Object[] arguments){
        throw new NoLongerSupport();
    }

    @Override
    public String getRaw() {
        return rawData;
    }

    public static BaseTypeAST<String> ofRawString(String raw) {
        return new BaseTypeAST<>(BaseType.STRING,raw,false);
    }
    public static BaseTypeAST<String> ofPlaceholderString(String string) {
        return new BaseTypeAST<>(BaseType.STRING,string,true);
    }
    public static BaseTypeAST<Style> ofRawFormat(String raw) {
        return new BaseTypeAST<>(BaseType.STYLE,raw,false);
    }
    public static BaseTypeAST ofPlaceholderFormat(String string) {
        return new BaseTypeAST<>(BaseType.STYLE,string,true);
    }
    public static enum BaseType{
        STRING,
        STYLE,
        UNKNOWN
    }
}

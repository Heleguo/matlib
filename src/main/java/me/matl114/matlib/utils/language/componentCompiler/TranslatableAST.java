package me.matl114.matlib.utils.language.componentCompiler;

import me.matl114.matlib.utils.language.PlaceholderProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TranslatableAST extends ComponentLikeAST implements ComponentAST<Component> {
    //we decided to do something like "key$val"

    @Override
    public Component buildStaticString(BuildContent content, String raw) {
        Style currentStyle = content.getStyle().build();
      //  Debug.logger("builder build at translate ",currentStyle.color());
        return buildInternal(currentStyle, raw);
    }
    public Component buildInternal(Style val, String raw) {
        String[] value = raw.split("[$]");
        return value.length>1? Component.translatable(value[0],value[1],val): Component.translatable(value[0],val);
    }

    @Override
    public BiConsumer<TextComponent.Builder, Parameter> buildDynamicPlaceholder(BuildContent content, Function<Parameter, ?> dynamic) {
        Style currentStyle = content.getStyle().build();
       // Debug.logger("builder build at translate",currentStyle.color());
        return (textComponent, parameter) -> {
            textComponent.append( buildInternal(currentStyle,(String) dynamic.apply(parameter)));
        };
    }
    @Override
    public Component buildStaticPlaceholder(BuildContent content, PlaceholderProvider provider, String raw) {
        Style currentStyle = content.getStyle().build();
      //  Debug.logger("builder build at translate",currentStyle);
        return buildInternal(currentStyle, provider.getStringOrDefault(raw));

    }
}

package me.matl114.matlib.utils.language.componentCompiler;

import me.matl114.matlib.utils.language.PlaceholderProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TextAST extends ComponentLikeAST implements ComponentAST<Component> {

    @Override
    public Component buildStaticString(BuildContent content, String raw) {
        Style currentStyle = content.getStyle().build();
     //   Debug.logger("builder build at text",currentStyle.color());
        return Component.text(raw, currentStyle);
    }

    @Override
    public BiConsumer<TextComponent.Builder, Parameter> buildDynamicPlaceholder(BuildContent content, Function<Parameter, ?> dynamic) {
        Style currentStyle = content.getStyle().build();
     //   Debug.logger("builder build at text",currentStyle.color());
        return (p,param)->{
            p.append(Component.text((String) dynamic.apply(param), currentStyle));
        };
    }

    @Override
    public Component buildStaticPlaceholder(BuildContent content,PlaceholderProvider provider, String raw) {
        String val = provider.getStringOrDefault(raw);
        Style currentStyle = content.getStyle().build();
     //   Debug.logger("builder build at text",currentStyle.color());
        return Component.text(val,currentStyle);
    }
}

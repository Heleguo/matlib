package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Common.Lang.Exceptions.CompileError;
import me.matl114.matlib.Utils.Chat.EnumFormat;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Language.PlaceholderProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleSetter;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

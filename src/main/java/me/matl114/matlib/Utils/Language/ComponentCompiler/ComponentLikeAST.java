package me.matl114.matlib.Utils.Language.ComponentCompiler;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.matl114.matlib.Common.Lang.Exceptions.CompileError;
import me.matl114.matlib.Utils.Chat.EnumFormat;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Language.PlaceholderProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ComponentLikeAST implements ComponentAST<Component> {
    List<EventAST<?>> eventList ;
    List<BaseTypeAST> baseElements = new ArrayList<>();
    static ComponentLikeAST resolveSpecial(String type,List<BaseTypeAST> baseContent,List<EventAST<?>> eventList) {
        //todo: default solution,left as not done
        ComponentLikeAST re= switch (type){
            case "translatable:" -> new TranslatableAST();
            default -> new TextAST();
        };
        re.eventList = eventList;
        re.baseElements.addAll(baseContent);
        return re;
    }
    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append(this.getClass().getSimpleName());
        outputStream.append("{");
        outputStream.append("base = raw[");
        for(BaseTypeAST element: baseElements) {
            outputStream.append(element.getRaw());
            outputStream.append(", ");
        }
        outputStream.append("]");
        if(eventList != null){
            outputStream.append(", events = [");
            for(EventAST element: eventList) {
                outputStream.append(element.toString());
            }
            outputStream.append("]");
        }
        outputStream.append(" }");
    }
    @Override
    public ParameteredLanBuilder<Component> build(BuildContent content) {
        //todo if builder.size = 1, try use
        //todo rebuild with List<Function<param,component>?>, and use textofChildren or self if single
        List<BiConsumer<TextComponent.Builder,Parameter>> builder = new ArrayList<>();
        boolean containsDynamic = false;
      //  boolean firstComponent = true;
        final Style defaultStyle = content.getStyle().build();
        for (BaseTypeAST ast:baseElements){
            if(ast.isPlaceholder()){
                PlaceholderProvider provider = content.getPlaceholderProvider();
                if (ast.getType() == BaseTypeAST.BaseType.STRING){
                    Function<Parameter,?> re = provider.getDynamic(ast.getRaw());
                    if(re != null){
                        containsDynamic = true;
                        content.markDynamic();
                        builder.add(buildDynamicPlaceholder(content,re));
                    }else {
                        Component val = buildStaticPlaceholder(content,provider,ast.getRaw());
                      //  Debug.logger("build const val",val);
                        builder.add((textComponent, parameter) -> {
                            textComponent.append(val);
                        });

                    }
                }else if(ast.getType() == BaseTypeAST.BaseType.STYLE) {
                    //STYLE should always be static ,not dynamic
                    // Function<Parameter,Style> re = provider.getDynamic(ast.getRaw());
                    Style.Builder val = provider.getStyleOrDefault(ast.getRaw()).toBuilder();

                    content.getStyle().merge(val.build(), Style.Merge.Strategy.ALWAYS);
//                    if(firstComponent){
//                        defaultStyle = content.getStyle().build();
//                    }
                  //  Debug.logger("builder change ",content.getStyle());
                }
            }else {
                if(ast.getType() == BaseTypeAST.BaseType.STRING){
                    Component val = buildStaticString(content,ast.getRaw());
                    content.markDynamic();
                 //   Debug.logger("build const val",val);
                    builder.add((textComponent, parameter) -> {
                        textComponent.append(val);
                    });
                }else if(ast.getType() == BaseTypeAST.BaseType.STYLE){
                    applyStyleBuilder(ast.getRaw()).accept(content.getStyle());
//                    if(firstComponent){
//                        defaultStyle = content.getStyle().build();
//                    }
                   // Debug.logger("builder change ",content.getStyle());
                }
            }
          //  firstComponent = false;
        }
        ParameteredLanBuilder<Function<Component, Component>> builderEvent = buildEvent(content);
        if(containsDynamic){
            //dynamic builder
            return (parametered)->{
                TextComponent.Builder builders = Component.text().style(defaultStyle);
                for (var re: builder){
                    re.accept(builders,parametered);
                }
                return builderEvent==null ? builders.build(): builderEvent.build(parametered).apply(builders.build());
            };
        }else {
            TextComponent.Builder builders = Component.text().style(defaultStyle);
            for (var re: builder){
                re.accept(builders,null);
            }
            //static builder
            //calculate static
            Component constVal = builders.build();
            if(builderEvent == null){
                return (parametered -> constVal);
            }else{
                return (parametered -> {
                    return builderEvent.build(parametered).apply(constVal);
                });
            }
        }

    }
    public abstract Component buildStaticString(BuildContent content,String raw);
    public abstract BiConsumer<TextComponent.Builder,Parameter> buildDynamicPlaceholder(BuildContent content,Function<Parameter,?> dynamic);
    public abstract Component buildStaticPlaceholder(BuildContent content,PlaceholderProvider provider,String raw);
    public static Consumer<Style.Builder> applyStyleBuilder(String value){
        int len = value.length();
        if(len == 2){
            EnumFormat format = EnumFormat.getFormat(value.charAt(1));
            if (format.isFormat() && format != EnumFormat.RESET) {
                switch (format) {
                    case BOLD:
                        return (builder) -> {
                            builder.decoration(TextDecoration.BOLD,true);
                        };
                    case ITALIC:
                        return (builder) -> {
                            builder.decoration(TextDecoration.ITALIC,true);
                        };

                    case STRIKETHROUGH:
                        return (builder) -> {
                            builder.decoration(TextDecoration.STRIKETHROUGH,true);
                        };

                    case UNDERLINE:
                        return (builder) -> {
                            builder.decoration(TextDecoration.UNDERLINED,true);
                        };

                    case OBFUSCATED:
                        return (builder) -> {
                            builder.decoration(TextDecoration.OBFUSCATED,true);
                        };

                    default:
                        throw new CompileError(CompileError.CompilePeriod.IR_BUILDING,-1,"Unexpected message format name: "+value);
                }
            } else { // Color resets formatting
                return (builder) ->{
                    builder.merge( Style.empty().color(format.toAdventure()), Style.Merge.Strategy.ALWAYS);
                };
            }
        }else {
            String hex = value.replaceAll("[&§#x]","");
            if(hex.length() == 6){
                try{
                    int i = Integer.parseInt(hex, 16);
                    return (builder) -> {
                        builder.color(TextColor.color(i));
                    };
                }catch (Throwable e){
                    throw new CompileError(CompileError.CompilePeriod.IR_BUILDING,-1,"Unexpected color value: "+value+", to hex: "+hex);
                }
            }else {
                throw new CompileError(CompileError.CompilePeriod.IR_BUILDING,-1,"Unexpected color format: "+value);
            }
        }
    }

    /**
     * get component with Event ,must not change origin component, may create new component
     * @param content
     * @return
     */
    @Nullable
    public final ParameteredLanBuilder<Function<Component,Component>> buildEvent(BuildContent content){
        //solve
        if(eventList==null||eventList.isEmpty()){
            return null;
        }

        return (param)->(component)->{
            return component;
        };
    }
}

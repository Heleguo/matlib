package me.matl114.matlib.nmsMirror.chat;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

import javax.annotation.Nullable;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Style")
public interface FormatHelper extends TargetDescriptor {
    @MethodTarget
    Object withColor(Object val, int rgb);

    @MethodTarget
    Object withColor(Object val, @RedirectType("Lnet/minecraft/ChatFormatting;")Object chat);

    @MethodTarget
    Object withBold(Object val, Boolean bold);

    @MethodTarget
    Object withItalic(Object val, Boolean italic);

    @MethodTarget
    Object withUnderlined(Object val, Boolean underlined);

    @MethodTarget
    Object withStrikethrough(Object val, Boolean strikethrough);

    @MethodTarget
    Object withObfuscated(Object val, Boolean obfuscated);

    // some thing related to clickEvent
    @MethodTarget
    Object withInsertion(Object val, String insertion);

    @MethodTarget
    Object withFont(Object val, @RedirectType(ResourceLocation)Object font);

    @MethodTarget
    Object applyTo(Object val, @RedirectType(Style)Object parent);

    @MethodTarget
    boolean isBold(Object val);

    @MethodTarget
    boolean isItalic(Object val);

    @MethodTarget
    boolean isStrikethrough(Object val);

    @MethodTarget
    boolean isUnderlined(Object val);

    @MethodTarget
    boolean isObfuscated(Object val);
    @MethodTarget
    boolean isEmpty(Object val);

    @MethodTarget
    Object getClickEvent(Object val);

    @MethodTarget
    Object getHoverEvent(Object val);

    @MethodTarget
    String getInsertion(Object val);

    @MethodTarget
    Object getFont(Object val);

    @MethodTarget
    Object getColor(Object val);

    @FieldTarget
    @RedirectClass(TextColor)
    @RedirectName("formatGetter")
    Enum<?> textColor$formatGetter(Object val);

    @MethodTarget
    @RedirectClass(TextColor)
    @RedirectName("getValue")
    int textColor$getValue(Object val);

    @MethodTarget(isStatic = true)
    @RedirectClass(TextColor)
    @RedirectName("fromRgb")
    Object textcolorFromRgb(int rgb);

    @MethodTarget(isStatic = true)
    @RedirectClass(TextColor)
    @RedirectName("fromLegacyFormat")
    Object textcolorFromChatFormat(@RedirectType(ChatFormatting) Object chatFormat);

    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3)
    default Object newStyle(@RedirectType(TextColor)Object color, @Nullable Boolean bold,
                    @Nullable Boolean italic,
                    @Nullable Boolean underlined,
                    @Nullable Boolean strikethrough,
                    @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(ResourceLocation)Object resourceLocation){
        return newStyle0(color, null, bold, italic, underlined, strikethrough, obfuscated, clickevent, hoverEvent, insertion, resourceLocation );
    }
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3, below = true)
    @Internal
    public Object newStyle0(@RedirectType(TextColor)Object color, Integer shadowColor, @Nullable Boolean bold,
                            @Nullable Boolean italic,
                            @Nullable Boolean underlined,
                            @Nullable Boolean strikethrough,
                            @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(ResourceLocation)Object resourceLocation);

    default Object copy(Object style0){
        return applyTo(style0, ChatEnum.STYLE_EMPTY_COPY);
    }

    @ConstructorTarget
    @RedirectClass(ClickEvent)
    Object newClickEvent(@RedirectType(ClickEventAction)Object action, String value);

    @ConstructorTarget
    @RedirectClass(HoverEvent)
    Object newHoverEvent(@RedirectType(HoverEventAction)Object action, Object content);

    @ConstructorTarget
    @RedirectClass(HoverEventItemStackInfo)
    Object newHoverEventItemInfo(@RedirectType(ItemStack)Object itemStack);
}

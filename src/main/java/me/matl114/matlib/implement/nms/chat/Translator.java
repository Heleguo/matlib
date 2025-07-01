package me.matl114.matlib.implement.nms.chat;

import me.matl114.matlib.nmsUtils.network.PacketFlow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface Translator {
    TranslateType type();
    PacketFlow flow();
    int priority() default 0;
}

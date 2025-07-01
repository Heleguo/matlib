package me.matl114.matlib.algorithms.algorithm;

import me.matl114.matlib.implement.nms.network.PacketEvent;
import me.matl114.matlib.implement.nms.network.PacketHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ListenerUtils {
    public static List<Method> collectPublicListenerMethods(Class<?> listenerClass, Class<?> argumentType, Class annotationClass){
        return Arrays.stream(listenerClass.getMethods())
            .filter(m-> !m.isSynthetic() && !m.isBridge())
            .filter(m-> m .getParameterCount() == 1)
            .filter(m-> argumentType.isAssignableFrom(m.getParameterTypes()[0]))
            .filter(m->m.getAnnotation(annotationClass) != null)
            .toList();
    }


}

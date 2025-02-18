package me.matl114.matlibAdaptor.Proxy.Utils;

import com.google.common.base.Preconditions;
import me.matl114.matlibAdaptor.Proxy.Annotations.AdaptorInterface;
import me.matl114.matlibAdaptor.Proxy.Annotations.InternalMethod;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AnnotationUtils {
    public static String ADAPTOR_ANNOTATION_IDENTIFIER = AdaptorInterface.class.getName();
    public static String INTERNEL_ANNOTATION_IDENTIFIER = InternalMethod.class.getName();
    @Nonnull
    public static Optional<Annotation> getAdaptorInstance(Class<?> interfaceClass){
        Preconditions.checkArgument(interfaceClass.isInterface(),"Argument is not an interface");
        Annotation[] annotations = interfaceClass.getAnnotations();
        for(Annotation annotation : annotations){
            if(ADAPTOR_ANNOTATION_IDENTIFIER.equals(annotation.annotationType().getName())){
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }
    @Nonnull
    public static List<Method> getAdaptedMethods(Class<?> interfaceClass){
        Preconditions.checkArgument(interfaceClass.isInterface(),"Argument is not an interface");
        List<Method> methods = new ArrayList<>();
        collectMethods0(interfaceClass, methods);
        //remove all internal method
        methods.removeIf(method -> Arrays.stream(method.getAnnotations()).anyMatch(a->INTERNEL_ANNOTATION_IDENTIFIER.equals(a.annotationType().getName())));
        return methods;
    }
    private static void collectMethods0(Class<?> clazz,List<Method> methods){
        Arrays.stream(clazz.getInterfaces()).forEach(i->collectMethods0(i,methods));
        //only collect public and default method
        methods.addAll(Arrays.asList(clazz.getMethods()));
    }
}

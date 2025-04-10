package me.matl114.matlib.utils.reflect.mixImpl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.matl114.matlib.utils.reflect.ObfManager;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.MixImpl;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixBase;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixImplBuildException;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixImplException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class MixImplBuilder {
    private static final Map<MixImplArgument, Class<? extends MixBase>> CACHE = new HashMap<>();

    public static <T extends R, R> Class<T> createMixinImplDefault(Class<? extends MixBase> mixBases, String fullname) {
        MixImpl defaultArgument = mixBases.getAnnotation(MixImpl.class);
        Preconditions.checkNotNull(defaultArgument, "Invalid Argument passed mixBases: @MixImpl absent for target class");
        Class<?> mixSuperClass ;
        List<Class<?>> extraItf = new ArrayList<>();
        try{
            mixSuperClass = ObfManager.getManager().reobfClass(defaultArgument.subClass());
            for (String name : defaultArgument.interfaces()){
                extraItf.add(ObfManager.getManager().reobfClass(name));
            }

        }catch (Throwable e){
            throw new MixImplBuildException(e);
        }
        return createMixinImplCustom(mixBases, mixSuperClass, extraItf, fullname);
    }
    public static <T extends R, R> Class<T> createMixinImplCustom(Class<? extends MixBase> mixBases, Class<?> mixSuperClass, List<Class<?>> extraItf, String fullname){
        return createMultiMixinImplCustom(List.of(mixBases), mixSuperClass, extraItf, fullname);
    }

    public static <T extends R, R> Class<T> createMultiMixinImplCustom(List<Class<? extends MixBase>> mixBases, Class<?> mixSuperClass, List<Class<?>> extraItf, String fullname){
        return createMixImplAt(mixBases, (Class<? super T>) mixSuperClass, extraItf, fullname);
    }
    /**
     * use mixInterface as main body to create a class <ClassName> overriding mixSuperClass with extra interfaces <extraItf>
     * @param mixBases
     * @param mixSuperClass
     * @param extraItf
     * @param className
     * @return
     * @param <T>
     * @param <R>
     * @param <W>
     */
    private static <T extends R,R> Class<T> createMixImplAt(List<Class<? extends MixBase>> mixBases, Class<R> mixSuperClass, List<Class<?>> extraItf, String className){
        MixImplArgument argument = new MixImplArgument(mixBases, mixSuperClass, extraItf, className);
        return (Class<T>) CACHE.computeIfAbsent(argument, (arg)->{
            try{
                return createMixImplInternel(arg.mixBase, arg.superClass, arg.extraItf, arg.name);
            }catch (Throwable e){
                throw new MixImplBuildException(e);
            }
        });
    }


    private static synchronized  <R, T extends R>  Class<T> createMixImplInternel(Set<Class<? extends MixBase>> mixInterface, Class<R> mixSuperClass, Set<Class<?>> extraItf, String fullname ) throws  Throwable{
        Preconditions.checkArgument(!CustomClassLoader.getInstance().isClassPresent(fullname),"Invalid new ClassPath :"+fullname+" ,because class with this path is present !");
        //todo maybe we will support mix methods /fields conflict priority system
        return null;
    }

    @Data
    static class MixImplArgument{
        public MixImplArgument(List<Class<? extends MixBase>> mixins, Class<?> superClass, List<Class<?>> extraItf, String name){
            this.mixBase = mixins
                .stream()
                .distinct()
                .collect(Collectors.toUnmodifiableSet());
            ;
            this.superClass = superClass;
            this.extraItf = extraItf
                .stream()
                .distinct()
                .filter(
                    //考虑itf是否被mixBase囊括了,如果囊括了,就drop it
                    t ->!mixBase.stream()
                        .filter(mb->t.isAssignableFrom(mb))
                        .findAny()
                        .isPresent()
                )
                .collect(Collectors.toUnmodifiableSet());
            this.name = name;
        }
        Set<Class<? extends MixBase>> mixBase;
        Class<?> superClass;
        Set<Class<?>> extraItf;
        String name;
    }
}

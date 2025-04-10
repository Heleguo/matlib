package me.matl114.matlib.utils.reflect.classBuild.annotation;

import me.matl114.matlib.utils.version.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface IgnoreFailure {
    Version thresholdInclude();
    boolean below() default false;
}

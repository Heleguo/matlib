package me.matl114.matlib.utils.reflect.descriptor.annotations;

import me.matl114.matlib.utils.version.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreFailure {
    Version thresholdInclude() default Version.v1_20_R4;
    boolean below() default false;
}

package me.matl114.matlib.Common.Lang.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Experimental {
    /**
     * mark as experimental,need test
     */
}

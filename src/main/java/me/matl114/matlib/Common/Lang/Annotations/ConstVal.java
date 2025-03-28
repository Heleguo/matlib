package me.matl114.matlib.Common.Lang.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
/**
 * this marks that method /field/class instance should present a const value and should not be changed
 */
public @interface ConstVal {
}

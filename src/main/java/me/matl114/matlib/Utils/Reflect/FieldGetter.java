package me.matl114.matlib.Utils.Reflect;

import java.util.Objects;
import java.util.function.Function;

public interface FieldGetter {
    public Object apply(Object o) throws Throwable;
}

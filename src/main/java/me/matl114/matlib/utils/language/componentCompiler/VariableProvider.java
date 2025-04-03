package me.matl114.matlib.utils.language.componentCompiler;

import me.matl114.matlib.utils.language.PlaceholderProvider;

public interface VariableProvider<T extends Object> {
    public T get(PlaceholderProvider placeholder, Object[] arguments);
    public String getRaw();
}

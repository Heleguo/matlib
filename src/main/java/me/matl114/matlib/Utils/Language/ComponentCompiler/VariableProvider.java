package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Utils.Language.PlaceholderProvider;

public interface VariableProvider<T extends Object> {
    public T get(PlaceholderProvider placeholder, Object[] arguments);
    public String getRaw();
}

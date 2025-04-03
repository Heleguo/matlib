package me.matl114.matlib.utils.language.componentCompiler;

import me.matl114.matlib.utils.language.PlaceholderProvider;

public interface StyleProvider {
    public String get(PlaceholderProvider placeholder, Object[] arguments);
}

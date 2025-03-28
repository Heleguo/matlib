package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Utils.Language.PlaceholderProvider;

public interface StyleProvider {
    public String get(PlaceholderProvider placeholder, Object[] arguments);
}

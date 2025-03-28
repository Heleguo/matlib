package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Utils.Language.PlaceholderProvider;
import net.kyori.adventure.text.Component;

public interface ComponentAST<T extends Object> {
    public abstract ParameteredLanBuilder<T> build(BuildContent content);
    public abstract void walk(StringBuilder outputStream);
}

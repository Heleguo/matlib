package me.matl114.matlib.utils.language.componentCompiler;

public interface ComponentAST<T extends Object> {
    public abstract ParameteredLanBuilder<T> build(BuildContent content);
    public abstract void walk(StringBuilder outputStream);
}

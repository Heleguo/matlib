package me.matl114.matlib.Utils.Language.ComponentCompiler;

import lombok.AllArgsConstructor;
import me.matl114.matlib.Utils.Language.PlaceholderProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface ParameteredLanBuilder<T extends Object> {
    public T build(Parameter parametered);
    @AllArgsConstructor
    public static class LinkedBuilder<W extends Object,P extends Object> implements ParameteredLanBuilder<P> {
        List<ParameteredLanBuilder<W>> builders ;
        Function<List<W>,P> parent ;
        @Override
        public P build(Parameter parametered) {
            return  parent.apply( (builders.stream().map(i->i.build(parametered)).toList()));
        }
    }
    public static <W extends Object,P extends Object> ParameteredLanBuilder<P> linkOf(Function<List<W>,P> linker,ParameteredLanBuilder<W>... child){
        return new LinkedBuilder<>(Arrays.stream(child).toList(),linker);
    }
}

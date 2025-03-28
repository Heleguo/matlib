package me.matl114.matlib.Utils.Language.ComponentCompiler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.Style;

public interface Parameter {
    public <T extends Object> T getI(int index);
    static Parameter wrap(Object... vargs){
        return new ParameterArrayWrapper(vargs);
    }

    @AllArgsConstructor
    static class ParameterArrayWrapper implements Parameter{
        private Object[] vargs;

        @Override
        public <T> T getI(int index) {
            return (T) vargs[index];
        }
    }
}

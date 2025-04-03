package me.matl114.matlib.utils.language.componentCompiler;

import lombok.AllArgsConstructor;

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

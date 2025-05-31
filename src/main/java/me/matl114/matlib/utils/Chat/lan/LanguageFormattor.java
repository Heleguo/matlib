package me.matl114.matlib.utils.chat.lan;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

public interface LanguageFormattor {
    String processPlaceholder(String raw);
    static LanguageFormattor ID = new LanguageFormattor() {
        @Override
        public String processPlaceholder(String raw) {
            return raw;
        }
    };
    static LanguageFormattor identity(){
        return ID;
    }
    @AllArgsConstructor
    public static class LanguageFormattorImpl implements LanguageFormattor{
        @Nonnull
        LanguageFormattor parent;

        @Override
        public String processPlaceholder(String raw) {
            return parent.processPlaceholder(raw);
        }
    }
}

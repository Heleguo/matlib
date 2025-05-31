package me.matl114.matlib.utils.chat.lan;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.common.functions.core.TriConsumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageRegistry {
    @Getter
    Locale defaultLocale ;
    NamespacedKey key;
    @Nullable
    TranslationRegistry paperRegistry;
    @Setter
    TriConsumer<Locale, String, String> writer;
    private final Map<String, LanguageNode> flattenMap = new ConcurrentHashMap<>();

    private static final Map<String, LanguageContent> EMPTY_MAP = Map.of();
    @Setter
    @Getter
    LanguageFormattor formattor = LanguageFormattor.identity();
    public LanguageRegistry(Locale defaultLocale, NamespacedKey key, boolean toPaper){
        this.defaultLocale = defaultLocale;
        this.key = key;
        if(toPaper){
            this.paperRegistry = TranslationRegistry.create(Key.key(key.getNamespace(), key.getKey()));
            GlobalTranslator.translator().addSource(this.paperRegistry);
        }
    }
    public LanguageRegistry(NamespacedKey key, Map<Locale, Map<String, ?>> yamlConfig, boolean toPaper){
        this(Locale.CHINESE, key, toPaper);
        for (var entry: yamlConfig.entrySet()){

            walk(flattenMap,(Map<String, ?>) entry.getValue(), null, entry.getKey());
        }
    }
    private void walk(Map<String ,LanguageNode> map, Map<String, ?> yamlConfig, String parentPath, Locale locale){
        for (var entry: yamlConfig.entrySet()){
            String nextPath = parentPath == null? String.valueOf( entry.getKey()): parentPath + "."+ entry.getKey();
            if(entry.getValue() instanceof Map sub){
                walk(map, sub, nextPath, locale);
            }else {
                map.computeIfAbsent(nextPath, (p)->new LanguageNode()).languageMap.put(locale, new LanguageContent( String.valueOf(entry.getValue())));
            }
        }
    }

    private void putInternal(Locale locale, String path, String value){
        flattenMap.computeIfAbsent(path, (i)->new LanguageNode()).languageMap.put(locale, new LanguageContent(value));
        if(writer != null){
            writer.accept(locale, path, value);
        }
    }

    public void registerTranslation(Locale locale, String path, String value){
        putInternal(locale, path, value);
        if(this.paperRegistry != null){
            if(this.paperRegistry.contains(path)){
                this.paperRegistry.unregister(path);
            }
            this.paperRegistry.register(path, locale, new MessageFormat(value));
        }
    }

    private LanguageContent getInternal(Locale locale, String value){
        LanguageNode node = this.flattenMap.get(value);
        return  node == null ? null: (node.getOrDefault(locale));
    }

    private LanguageContent getInternal0(Locale locale, String value){
        LanguageNode node = this.flattenMap.get(value);
        return  node == null ? null: (node.languageMap.get(locale));
    }


    public boolean containsKey(Locale locale, String path){
        return getInternal(locale, path) != null;
    }

    public String get(Locale locale, String value){
        var content = getInternal(locale, value);
        return content == null ? null: content.getAsString();
    }
    public String getLocale(Locale locale, String value){
        var content = getInternal0(locale, value);
        return content == null ? null: content.getAsString();
    }
    public String getOrKey(Locale locale, String value){
        return get(locale, value, value);
    }
    public String get(Locale locale, String value, String fallback){
        var content = getInternal(locale, value);
        return content == null ? fallback: content.getAsString();
    }

    public String getColored(Locale locale, String value){
        var content = getInternal(locale, value);
        return content == null ? null: content.getAsColorString();
    }
    public String getColored(Locale locale, String value, String fallbackOrigin){
        var content = getInternal(locale, value);
        if(content != null){
            return content.getAsColorString();
        }else {
            return fallbackOrigin ==null ? null: ChatColor.translateAlternateColorCodes('&', fallbackOrigin);
        }
    }

    public boolean containsPath(String value){
        return this.flattenMap.containsKey(value);
    }

    public String getFormatted(Locale locale, String value){
        var content = getInternal(locale, value);
        return content == null ? null: content.getAsProcessed();
    }
    public String getFormatted(Locale locale, String value, String fallbackOrigin){
        var content = getInternal(locale, value);
        if(content != null){
            return content.getAsProcessed();
        }else {
            return fallbackOrigin ==null ? null: formattor.processPlaceholder(fallbackOrigin);
        }
    }
    private class LanguageNode{
        Map<Locale, LanguageContent> languageMap = new ConcurrentHashMap<>(4);
        public LanguageContent getOrDefault(Locale locale){
            LanguageContent content = languageMap.get(locale);
            return  content == null ? languageMap.get(defaultLocale): content;
        }
    }

    private class LanguageContent{
        public LanguageContent(String data){
            this.rawLanguage = data;
            this.legacyColorString = ChatColor.translateAlternateColorCodes('&' ,rawLanguage);
        }
        String rawLanguage;
        String legacyColorString;
        String processedColorString;
        public String getAsString(){
            return rawLanguage;
        }
        public String getAsColorString(){
            return legacyColorString;
        }

        public String getAsProcessed(){
            if(processedColorString == null){
                processedColorString = formattor.processPlaceholder(legacyColorString);
            }
            return processedColorString;
        }
    }

    public void deconstruct(){
        if(this.paperRegistry != null){
            GlobalTranslator.translator().removeSource(this.paperRegistry);
        }
    }
}

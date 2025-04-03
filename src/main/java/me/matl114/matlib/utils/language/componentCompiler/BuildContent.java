package me.matl114.matlib.utils.language.componentCompiler;

import lombok.Getter;
import me.matl114.matlib.utils.language.PlaceholderProvider;
import net.kyori.adventure.text.format.Style;

public abstract class BuildContent {
    boolean inheriteFormat;

    public BuildContent(boolean inheriteFormat){
        this.inheriteFormat = inheriteFormat;
    }
    private Style.Builder currentStyle =Style.style();
    public Style.Builder getStyle(){
        return currentStyle;
    }
    @Getter
    private boolean constChild = true;
    public void markDynamic(){
        constChild = false;
    }
    public void push(){
       // Debug.logger("push at child");
        if(! inheriteFormat){
            currentStyle = Style.style();
        }
        constChild = true;
    }
    public void pop(){
      //  Debug.logger("pop at child");
    }

    public abstract PlaceholderProvider getPlaceholderProvider();

    public static BuildContent of(PlaceholderProvider provider) {
        return new BuildContent(false) {
            @Override
            public PlaceholderProvider getPlaceholderProvider() {
                return provider;
            }
        };
    }
}

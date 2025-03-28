package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Utils.Language.PlaceholderProvider;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class ClickEventAST implements EventAST<ClickEvent> ,ComponentAST<ClickEvent> {
    //todo complete ClickEvent
    @Override
    public ParameteredLanBuilder<ClickEvent> build(BuildContent content) {
        return null;
    }

    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append("ClickEvent{}");
    }
}

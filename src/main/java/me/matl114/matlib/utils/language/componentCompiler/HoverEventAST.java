package me.matl114.matlib.utils.language.componentCompiler;

import net.kyori.adventure.text.event.HoverEvent;

public class HoverEventAST  implements EventAST<HoverEvent<?>>, ComponentAST<HoverEvent<?>> {
    //todo complete hoverEvent
    @Override
    public ParameteredLanBuilder<HoverEvent<?>> build(BuildContent content) {
        return null;
    }

    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append("hoverEvent{}");
    }
}

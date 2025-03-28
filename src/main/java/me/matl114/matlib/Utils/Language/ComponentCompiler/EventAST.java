package me.matl114.matlib.Utils.Language.ComponentCompiler;

import me.matl114.matlib.Common.Lang.Exceptions.CompileError;
import me.matl114.matlib.Common.Lang.Exceptions.NotImplementedYet;
import net.kyori.adventure.text.format.StyleBuilderApplicable;

import java.util.List;

public interface EventAST<T extends StyleBuilderApplicable> extends ComponentAST<T>{
    static EventAST<?> resolve(String type, List<BaseTypeAST> base) {
        //todo left as not down
        switch (type) {
            case "hover:":return new HoverEventAST();
            case "click:":return new ClickEventAST();
            default: throw new CompileError(CompileError.CompilePeriod.SDT_TRANSLATE,-1,"No such event type:"+type);
        }
    }
}

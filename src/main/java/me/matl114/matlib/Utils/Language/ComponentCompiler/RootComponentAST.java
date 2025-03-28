package me.matl114.matlib.Utils.Language.ComponentCompiler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.ArrayList;
import java.util.List;

public class RootComponentAST implements ComponentAST<Component> {
    List<ComponentLikeAST> children = new ArrayList<>();

    @Override
    public ParameteredLanBuilder<Component> build(BuildContent content) {
        if(children.isEmpty()){
            return (param)-> Component.empty();
        }
        ParameteredLanBuilder<Component>[] childs = new ParameteredLanBuilder[children.size()];
        boolean constant = true;
        for (int i = 0; i < children.size(); i++) {
            content.push();
            childs[i] = children.get(i).build(content);
            constant &= content.isConstChild();
            content.pop();
        }
        //todo try create
        //todo multiple TextComponent can be joint into one? NO
        ParameteredLanBuilder<Component> compBuilder = children.size()>1? ParameteredLanBuilder.linkOf(r -> Component.textOfChildren(r.toArray(ComponentLike[]::new)),childs):(param)->(childs[0].build(param).asComponent());
        if (constant) {
            Component constVal = compBuilder.build(null);
            return (param) -> constVal;
        }else {
            return compBuilder;
        }
    }

    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append("ComponentList{");
        for (ComponentLikeAST child : children) {
            child.walk(outputStream);
            outputStream.append(", ");
        }
    }
}

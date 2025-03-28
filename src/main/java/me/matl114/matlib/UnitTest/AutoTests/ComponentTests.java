package me.matl114.matlib.UnitTest.AutoTests;

import me.matl114.matlib.Algorithms.DataStructures.Frames.PairList;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Language.ComponentCompiler.BuildContent;
import me.matl114.matlib.Utils.Language.ComponentCompiler.ComponentAST;
import me.matl114.matlib.Utils.Language.ComponentCompiler.ComponentFormatParser;
import me.matl114.matlib.Utils.Language.ComponentCompiler.Parameter;
import me.matl114.matlib.Utils.Language.Lan.DefaultPlaceholderProviderImpl;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.Locale;

public class ComponentTests implements TestCase {
    @OnlineTest(name = "Component Compile Test")
    public void test_component(){
        var reg = TranslationRegistry.create(Key.key("test"));
        reg.register("me.matl114.test.message1", Locale.US, new MessageFormat("what the fuck"));
        reg.register("me.matl114.test.message1",Locale.CHINESE,new MessageFormat("你是一个一个"));
        GlobalTranslator.translator().addSource(reg);

        String t1 = "Hello {entity:ababab}, your score is {0}! && &aGreen{hover: cdcdcd} &cRed §#FF0000 &x&6&6&6&6&6&6CustomColor {player_placeholder} is here. translate test{translatable:&a&lme.matl114.test.message1$fallback}";

        ComponentAST ast = ComponentFormatParser.compile(t1);
        var output = new StringBuilder();
        var re =  ast.build(BuildContent.of(new DefaultPlaceholderProviderImpl()));

        ast.walk(output);
        for (int i=0;i<10;++i){
            var builder = (TextComponent) re.build(Parameter.wrap("666"));
            Bukkit.getServer().sendMessage(builder);
        }

    }
    //todo 检测在物品上的一致性，尤其是当有多个不同类型的comp时
    //todo 如果不相符,需要做转化，并研究哪些地方不符，以此契合paper的component解析器格式
    //todo 检测当服务端和客户端同时有相同键的时候会发生什么
    //todo 增加直接构建raw json text,并通过反射设置value,尝试保证他是有效的

}

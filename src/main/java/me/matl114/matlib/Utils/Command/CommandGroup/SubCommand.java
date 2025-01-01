package me.matl114.matlib.Utils.Command.CommandGroup;

import lombok.Getter;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandInputStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class SubCommand implements TabExecutor {
    @Nullable
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command,  String s, String[] elseArg) {
        if(executor!=this){
            return executor.onTabComplete(commandSender, command, s, elseArg);
        }else {
            var tab=this.parseInput(elseArg).getA();
            if(tab!=null){
                return tab.getTabComplete();
            }else {
                return List.of();
            }
        }
    }

    public interface SubCommandCaller{
        public void registerSub(SubCommand command);
    }
    @Getter
    String[] help;
    SimpleCommandArgs template;
    @Getter
    String name;
    @Getter
    TabExecutor executor=this;
    public boolean onCommand(CommandSender var1, Command var2,String var3, String[] var4){
        return true;
    }
    public SubCommand(String name,SimpleCommandArgs argsTemplate,String... help){
        this.name = name;
        this.template=argsTemplate;
        this.help = help;
    }
    public SubCommand(String name,SimpleCommandArgs argsTemplate,List<String> help){
        this(name,argsTemplate,help.toArray(String[]::new));
    }

    public SubCommand register(SubCommandCaller caller){
        caller.registerSub(this);
        return this;
    }
    @Nonnull
    public Pair<SimpleCommandInputStream,String[]> parseInput(String[] args){
        return template.parseInputStream(args);
    }
    public SubCommand setDefault(String arg,String val){
        this.template.setDefault(arg,val);
        return this;
    }
    public SubCommand setTabCompletor(String arg, Supplier<List<String>> completions){
        this.template.setTabCompletor(arg,completions);
        return this;
    }
    public SubCommand setCommandExecutor(TabExecutor executor){
        this.executor=executor;
        return this;
    }

}

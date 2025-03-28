package me.matl114.matlib.Utils.Command.CommandGroup;

import lombok.Getter;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Pair;
import me.matl114.matlib.Utils.Command.CommandUtils;
import me.matl114.matlib.Utils.Command.Params.CommandArgumentMap;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandInputStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SubCommand implements CustomTabExecutor {
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
    boolean hide = false;
    public boolean hasPermission(CommandSender sender){
        return true;
    }
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
    public SubCommand hide(){
        this.hide=true;
        return this;
    }
    public boolean isVisiable(){
        return !this.hide;
    }
    public SubCommand register(SubCommandCaller caller){
        caller.registerSub(this);
        return this;
    }
    @Nonnull
    public Pair<SimpleCommandInputStream,String[]> parseInput(String[] args){
        return template.parseInputStream(args);
    }

    public CommandArgumentMap parseArgument(String[] args){
        return new CommandArgumentMap( CommandUtils.parseArguments(args, this.template.getArgs()));
    }
    public SubCommand setDefault(String arg,String val){
        this.template.setDefault(arg,val);
        return this;
    }
    public SubCommand setInt(String arg){
        setDefault(arg, "0");
        setTabCompletor(arg, AbstractMainCommand.numberSupplier());
        return this;
    }
    public SubCommand setInt(String arg, int val){
        setDefault(arg, String.valueOf(val));
        setTabCompletor(arg, AbstractMainCommand.numberSupplier());
        return this;
    }
    public SubCommand setFloat(String arg){
        setDefault(arg, "0.0");
        setTabCompletor(arg, AbstractMainCommand.floatSupplier());
        return this;
    }
    public SubCommand setFloat(String arg, float val){
        setDefault(arg, String.valueOf(val));
        setTabCompletor(arg, AbstractMainCommand.floatSupplier());
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

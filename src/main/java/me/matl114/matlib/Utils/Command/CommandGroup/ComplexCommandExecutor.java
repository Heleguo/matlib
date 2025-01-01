package me.matl114.matlib.Utils.Command.CommandGroup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public interface ComplexCommandExecutor extends TabExecutor, SubCommand.SubCommandCaller {
    public SubCommand getMainCommand();
    public SubCommand getSubCommand(String name);
    default List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4){
        var re=getMainCommand().parseInput(var4);
        if(re.getB().length==0){
            List<String> provider=re.getA().getTabComplete();
            return provider==null?new ArrayList<>():provider;
        }else{
            SubCommand subCommand= getSubCommand(re.getA().nextArg());
            if(subCommand!=null){
                String[] elseArg=re.getB();
                List<String> tab= subCommand.parseInput(elseArg).getA().getTabComplete();
                if(tab!=null){
                    return tab;
                }
            }
        }
        return new ArrayList<>();
    }
}

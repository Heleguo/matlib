package me.matl114.matlib.Utils.Command.Interruption;

import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

@Getter
@AllArgsConstructor
@Note("interrupt when input type not match")
public class TypeError extends ArgumentException{
    String argument;
    BaseArgumentType typeName;
    String input;
    public TypeError(SimpleCommandArgs.Argument arg, BaseArgumentType typeName, String  input){
        this(arg == null?null:arg.getArgsName(), typeName, input);
    }
    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleTypeError(sender, argument, typeName, input);
    }

    @Getter
    public static enum BaseArgumentType{
        INT("整形","Integer"),
        FLOAT("浮点型","Float"),
        BOOLEAN("布尔型","Boolean"),
        STRING("字符串","String");
        String displayNameZHCN;
        String displayNameENUS;
        BaseArgumentType(String display, String display2){
            this.displayNameZHCN = display;
            this.displayNameENUS = display2;
        }
    }
}

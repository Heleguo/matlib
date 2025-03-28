package me.matl114.matlib.Utils.Command.CommandGroup;

import me.matl114.matlib.Utils.Command.CommandUtils;
import me.matl114.matlib.Utils.Command.Interruption.TypeError;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public interface CustomTabExecutor extends TabExecutor {
    static int gint(String val){
        return CommandUtils.gint(val, (String) null);
    }
    static float gfloat(String val){
        return CommandUtils.gfloat(val, (String) null);
    }
    static double gdouble(String val){
        return CommandUtils.gdouble(val, (String) null);
    }
    static boolean gbool(String val){
        return CommandUtils.gbool(val, (String) null);
    }
}

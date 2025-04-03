package me.matl114.matlib.utils.command.commandGroup;

import me.matl114.matlib.utils.command.CommandUtils;
import org.bukkit.command.TabExecutor;

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

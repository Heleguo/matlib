package me.matl114.matlib.Common.Lang.Enums;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum TaskRequest {
    RUN_LATER_MAIN,
    RUN_ON_CURRENT,
    RUN_ON_CURRENT_OR_LATER_MAIN,
    RUN_ASYNC;
    public static void run(TaskRequest taskRequest, Runnable runnable, Plugin pl) {
        switch (taskRequest) {
            case RUN_LATER_MAIN:
                Bukkit.getScheduler().runTask(pl, runnable);
                break;
            case RUN_ON_CURRENT:
                runnable.run();
                break;
            case RUN_ASYNC:
                Bukkit.getScheduler().runTaskAsynchronously(pl, runnable);
                break;
            case RUN_ON_CURRENT_OR_LATER_MAIN:
                if(Bukkit.isPrimaryThread()){
                    runnable.run();
                }else {
                    Bukkit.getScheduler().runTask(pl, runnable);
                }
                break;
        }
    }
}

package me.matl114.matlib.nmsUtils;

import me.matl114.matlib.algorithms.algorithm.ThreadUtils;
import me.matl114.matlib.nmsMirror.versionedEnv.Env;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class ServerUtils {
    public static void executeSync(Runnable task){
        Env.MAIN_EXECUTOR.execute(task);
    }
    public static <T> FutureTask<T> executeFuture(Callable<T> sup){
        var future = new FutureTask<T>(sup);
        executeSync(future);
        return future;
    }
}

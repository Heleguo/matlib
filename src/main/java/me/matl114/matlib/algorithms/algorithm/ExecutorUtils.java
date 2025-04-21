package me.matl114.matlib.algorithms.algorithm;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.LockSupport;

public class ExecutorUtils {
    public static BukkitRunnable getRunnable(Runnable runnable) {
        return runnable instanceof BukkitRunnable ?(BukkitRunnable)runnable: new BukkitRunnable() {
            public void run() {
                runnable.run();
            }
        };
    }

    public static void sleep(long ms){
        try{
            Thread.sleep(ms);
        }catch (Throwable e){}
    }
    public static void sleepNs(long ns){
        long ms = ns/1_000_000;
        long left = ns%1_000_000;
        if(ms > 0){
            sleep(ms);
        }
        LockSupport.parkNanos(left);
//        do{
//
//        }while(System.nanoTime()<endTime);
    }
    public static FutureTask<Void> getFutureTask(Runnable runnable) {
        return runnable instanceof FutureTask<?> future? (FutureTask<Void>) future : new FutureTask<>(runnable,(Void) null);
    }
    public static <T> FutureTask<T> getFutureTask(Runnable runnable, T val){
        return new FutureTask<>(runnable, val);
    }
    public static <T> FutureTask<T> getFutureTask(Callable<T> callable){
        return new FutureTask<>(callable);
    }


    public static <T extends Object> T awaitFuture(FutureTask<T> futureTask) {
        try{
            return futureTask.get();
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}

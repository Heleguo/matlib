package me.matl114.matlib.Utils;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadUtils {
    private static final int MAX_CACHED_LOCK=8000;
    private static ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, AtomicBoolean>> lockedSet = new ConcurrentHashMap<>();
    public static boolean runAsyncOrBlocked(Object lock,Runnable runnable) {
        var set=lockedSet.computeIfAbsent(lock.getClass(),i->new ConcurrentHashMap<>());
        var locker=set.computeIfAbsent(lock,i->new AtomicBoolean(false));
        if(locker.compareAndSet(false, true)) {
            CompletableFuture.runAsync(()->{
                try{
                    runnable.run();
                }finally {
                    set.remove(lock,locker);
                    locker.set(false);

                }
            });
            return true;
        }
        return false;
    }
    public static BukkitRunnable getRunnable(Runnable runnable) {
        return runnable instanceof BukkitRunnable ?(BukkitRunnable)runnable: new BukkitRunnable() {
            public void run() {
                runnable.run();
            }
        };
    }
    public static CompletableFuture<?> runAsyncLater(Runnable runnable, long delay) {
        return null;
    }
    public static void sleep(long ms){
        try{
            Thread.sleep(ms);
        }catch (Throwable e){}
    }
    public static void sleepNs(long ns){
        long ms = ns/1_000_000;
        long left = ns%1_000_000;
        sleep(ms);
        long endTime = System.nanoTime()+left;
        do{

        }while(System.nanoTime()<endTime);
    }
    public static FutureTask<Void> getFutureTask(Runnable runnable) {
        return new FutureTask<>(runnable,(Void) null);
    }
    public static <T extends Object> T awaitFuture(FutureTask<T> futureTask) {
        try{
            return futureTask.get();
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}

package me.matl114.matlib.Utils;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
}

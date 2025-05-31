package me.matl114.matlib.algorithms.algorithm;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static <T> FutureTask<T> signal(T val){
        return getFutureTask(()->{}, val);
    }
    public static FutureTask<Void> signal(){
        return getFutureTask(()->{});
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

    public static void dumpAllThreads(Logger log){
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
        for ( ThreadInfo thread : threads )
        {
            dumpThread( thread, (syr)->log.log(Level.SEVERE, syr) );
        }
    }
    private static void dumpThread(ThreadInfo thread, Consumer<String> out)
    {
        out.accept(  "------------------------------" );
        //
        out.accept( "Current Thread: " + thread.getThreadName() );
        out.accept( "\tPID: " + thread.getThreadId()
            + " | Suspended: " + thread.isSuspended()
            + " | Native: " + thread.isInNative()
            + " | State: " + thread.getThreadState() );
        if ( thread.getLockedMonitors().length != 0 )
        {
            out.accept( "\tThread is waiting on monitor(s):" );
            for ( MonitorInfo monitor : thread.getLockedMonitors() )
            {
                out.accept( "\t\tLocked on:" + monitor.getLockedStackFrame() );
            }
        }
        out.accept( "\tStack:" );
        //
        for ( StackTraceElement stack : thread.getStackTrace()) // Paper
        {
            out.accept( "\t\t" + stack );
        }
    }
    private static void dumpAllThread(PrintStream printStream){
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
        for ( ThreadInfo thread : threads )
        {
            dumpThread( thread, printStream::println);
        }
    }
}

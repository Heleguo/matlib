package me.matl114.matlib.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.common.lang.enums.TaskRequest;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

public class ThreadUtils {
    private static final Plugin MOCK_PLUGIN = new PluginBase() {
        PluginDescriptionFile pdf = new PluginDescriptionFile("Unknown","unknown","me.matl114.matlib.UnknownClass");
        FileConfiguration config = new YamlConfiguration();
        Logger log = Logger.getLogger("Mock-Plugin");
        @Override
        public File getDataFolder() {
            return new File(".");
        }
        @Override
        public PluginDescriptionFile getDescription() {
            return pdf;
        }

        @Override
        public @NotNull PluginMeta getPluginMeta() {
            return this.pdf;
        }

        @Override
        public FileConfiguration getConfig() {
            return config;
        }

        @Override
        public InputStream getResource(String s) {
            throw new NotImplementedYet();
        }

        @Override
        public void saveConfig() {

        }
        @Override
        public void saveDefaultConfig() {

        }

        @Override
        public void saveResource(String s, boolean b) {

        }

        @Override
        public void reloadConfig() {

        }

        @Override
        public PluginLoader getPluginLoader() {
            return null;
        }

        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void onDisable() {

        }

        @Override
        public void onLoad() {

        }

        @Override
        public void onEnable() {

        }

        @Override
        public boolean isNaggable() {
            return false;
        }

        @Override
        public void setNaggable(boolean b) {

        }

        @Override
        public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
            return null;
        }

        @Override
        public BiomeProvider getDefaultBiomeProvider(String s, String s1) {
            return null;
        }

        @Override
        public Logger getLogger() {
            return this.log;
        }

        @Override
        public @NotNull LifecycleEventManager<Plugin> getLifecycleManager() {
            return null;
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return List.of();
        }
    };
    public static Plugin getFakePlugin(){
        return MOCK_PLUGIN;
    }
//    private static final ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(
//        4, Integer.MAX_VALUE,30L, TimeUnit.SECONDS, new SynchronousQueue<>(),
//        new ThreadFactoryBuilder().setNameFormat("Matlib Async Tasks - %1$d").build()
//    );
    private static final int MAX_CACHED_LOCK=8000;
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, AtomicBoolean>> lockedSet = new ConcurrentHashMap<>();

    private static final Executor MAIN_THREAD_EXECUTOR ;
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


    public static void executeSync(Runnable runnable){
        if(Bukkit.isPrimaryThread()){
            runnable.run();
        }else {
            runSyncNMS(runnable);
        }
    }
    public static void executeSyncSched(Runnable runnable){
        if(Bukkit.isPrimaryThread()){
            CompletableFuture.runAsync(runnable, MAIN_THREAD_EXECUTOR);
        }else {
            runSyncNMS(runnable);
        }
    }

    public static void executeSync(Runnable runnable, int delay){
        Bukkit.getScheduler().runTaskLater(MOCK_PLUGIN, runnable, delay);
    }

    public static void scheduleSync(Runnable runnable, int delay, int period){
        Bukkit.getScheduler().runTaskTimer(MOCK_PLUGIN, runnable, delay, period);
    }



    @Deprecated(forRemoval = true)
    public static void executeSync(Runnable runnable, Plugin pl) {
        executeSync(runnable);
//        if(Bukkit.isPrimaryThread()){
//            runnable.run();
//        }else {
//            runSync(runnable,pl);
//        }
    }

    public static void executeAsync(Runnable runnable){
        Bukkit.getScheduler().runTaskAsynchronously(MOCK_PLUGIN, runnable);
    }

    public static void scheduleAsync(Runnable runnable, int delay, int period){
        Bukkit.getScheduler().runTaskTimerAsynchronously(MOCK_PLUGIN, runnable, delay, period);
    }

    public static void executeAsync(Runnable runnable, int delay){
        Bukkit.getScheduler().runTaskLaterAsynchronously(MOCK_PLUGIN, runnable, delay);
    }


    public <T> FutureTask<T> scheduleFutureSync(Callable<T> callable,int delay){
        FutureTask<T> future = ExecutorUtils.getFutureTask(callable);
        executeSync(future,delay);
        return future;
    }
    public <T> FutureTask<T> scheduleFutureAsync(Callable<T> callable,int delay){
        FutureTask<T> future = ExecutorUtils.getFutureTask(callable);
        executeAsync(future,delay);
        return future;
    }


    public FutureTask<Void> scheduleFutureSync(Runnable callable,int delay){
        FutureTask<Void> future = ExecutorUtils.getFutureTask(callable);
        executeSync(future,delay);
        return future;
    }
    public FutureTask<Void> scheduleFutureAsync(Runnable callable,int delay){
        FutureTask<Void> future = ExecutorUtils.getFutureTask(callable);
        executeAsync(future,delay);
        return future;
    }

//    private static void runSync(Runnable runnable,Plugin pl) {
//        Bukkit.getScheduler().runTask(pl,runnable);
//    }

    private static void runSyncNMS(Runnable runnable){
        MAIN_THREAD_EXECUTOR.execute(runnable);
    }
    public static Plugin getMockPlugin(){
        return MOCK_PLUGIN;
    }

    public static void runWithRequest(TaskRequest taskRequest, Runnable runnable) {
        switch (taskRequest) {
            case RUN_LATER_MAIN:
                ThreadUtils.executeSyncSched(runnable);
                break;
            case RUN_ON_CURRENT:
                runnable.run();
                break;
            case RUN_ASYNC:
                ThreadUtils.executeAsync(runnable);
                break;
            case RUN_ON_CURRENT_OR_LATER_MAIN:
                executeSync(runnable);
                break;
        }
    }

    static {
        try {
            Class<?> mcUtils = Class.forName("io.papermc.paper.util.MCUtil");
            Field field = mcUtils.getDeclaredField("MAIN_EXECUTOR");
            field.setAccessible(true);
            MAIN_THREAD_EXECUTOR = (Executor) field.get(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

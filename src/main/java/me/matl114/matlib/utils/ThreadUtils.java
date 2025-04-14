package me.matl114.matlib.utils;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
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

    public static void test(){

    }
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
        return new FutureTask<>(runnable,(Void) null);
    }


    public static <T extends Object> T awaitFuture(FutureTask<T> futureTask) {
        try{
            return futureTask.get();
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    public static void executeSync(Runnable runnable){
        if(Bukkit.isPrimaryThread()){
            runnable.run();
        }else {
            runSyncNMS(runnable);
        }
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
//    private static void runSync(Runnable runnable,Plugin pl) {
//        Bukkit.getScheduler().runTask(pl,runnable);
//    }

    private static void runSyncNMS(Runnable runnable){
        MAIN_THREAD_EXECUTOR.execute(runnable);
    }
    public static Plugin getMockPlugin(){
        return MOCK_PLUGIN;
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

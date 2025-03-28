package me.matl114.matlib.Utils.Experimential;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Algorithms.Algorithm.ThreadUtils;
import me.matl114.matlib.Common.Lang.Annotations.*;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Version.Version;
import me.matl114.matlib.Utils.Version.VersionAtLeast;
import me.matl114.matlib.core.EnvironmentManager;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

@Experimental
@Note("This util class provides method to create Fake primary thread that you can run sync task on")
@NeedTest("version")
@NotRecommended
@UnsafeOperation
@VersionAtLeast(Version.v1_20_R2)
public class FakeSchedular {
    private static boolean enabled = false;
    private static Constructor<? extends Thread> threadConstructor;
    @ForceOnMainThread
    public static void init(){
        Version v = FakeSchedular.class.getAnnotation(VersionAtLeast.class).value();
        Debug.logger(v);
        Debug.logger(EnvironmentManager.getManager().getVersion());
        if(!EnvironmentManager.getManager().getVersion().isAtLeast(v)){
            enabled = false;
            Debug.logger("Fake Schedular thread not enabled");
//            throw new UnsupportedOperationException("Version should be at least " + FakeSchedular.class.getAnnotation(VersionAtLeast.class).value());
        }else {
            try {
                Preconditions.checkState(Bukkit.isPrimaryThread());
                threadConstructor = Thread.currentThread().getClass().getConstructor(Runnable.class,String.class);
                threadConstructor.setAccessible(true);
                enabled = true;
                Debug.logger("Fake Schedular thread enabled");
            } catch (NoSuchMethodException e) {
                enabled = false;
                Debug.logger("Fake Schedular thread not enabled");
            }
        }

    }
    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * create a fake server thread to run the task,
     * the thread will already be started when return
     * @param runnable
     * @return
     */
    public static Thread runSync(Runnable runnable){
        if(enabled){
            try{
                Thread thread =  threadConstructor.newInstance(runnable,"Fake Server Thread - "+taskCounter.incrementAndGet());
                thread.start();
                return thread;
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        }else {
            Debug.logger("Fake Schedular thread not enabled");
            return null;
        }

    }
}

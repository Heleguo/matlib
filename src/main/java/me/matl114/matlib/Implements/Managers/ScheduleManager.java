package me.matl114.matlib.Implements.Managers;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.Utils.ThreadUtils;
import me.matl114.matlib.core.Manager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class ScheduleManager implements Manager {
    Plugin plugin;
    @Override
    public ScheduleManager init(Plugin pl, String... path) {
        plugin = pl;
        return this;
    }

    @Override
    public ScheduleManager reload() {
        deconstruct();
        init(plugin);
        return this;
    }
    @Override
    public void deconstruct() {
    }

    @Getter
    private static ScheduleManager manager;
    public ScheduleManager(){
        manager = this;
    }
    public <T extends Runnable> void execute(T runnable){
        if(Bukkit.isPrimaryThread()){
            runnable.run();
        }else {
            launchScheduled(runnable,0,true,0);
        }
    }
    public <T extends Runnable> void execute(T runnable,boolean onMainThread){
        if(Bukkit.isPrimaryThread()==onMainThread){
            runnable.run();
        }else {
            launchScheduled(runnable,0,onMainThread,0);
        }
    }
    public <T extends Runnable> void launchScheduled(T r,int delayTick,boolean runSync,int periodTick){
        launchScheduled(ThreadUtils.getRunnable(r),delayTick,runSync,periodTick);
    }
    public void launchScheduled(BukkitRunnable thread, int delay, boolean isSync, int period){
        if(period<=0){
            if(isSync){
                if(delay!=0){
                    thread.runTaskLater(plugin, delay);
                }else{
                    thread.runTask(plugin);
                }
            }else{
                if(delay!=0)
                    thread.runTaskLaterAsynchronously(plugin,delay);
                else{
                    thread.runTaskAsynchronously(plugin);
                }
            }
        }else{
            if(isSync){
                thread.runTaskTimer(plugin, delay, period);
            }else{
                thread.runTaskTimerAsynchronously(plugin, delay,period);
            }
        }
    }
    public void launchRepeatingSchedule(Consumer<Integer> thread , int delay, boolean isSync, int period, int repeatTime){
        launchScheduled(new BukkitRunnable() {
            int runTime=0;
            @Override
            public void run() {
                try{
                    thread.accept(runTime);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                finally {
                    this.runTime+=1;
                    if(this.runTime>=repeatTime){
                        this.cancel();
                    }
                }
            }
        },delay,isSync,period);
    }
    public void asyncWaithRepeatingSchedule(Consumer<Integer> thread , int delay, boolean isSync, int period,int repeatTime){
        Preconditions.checkArgument( !Bukkit.isPrimaryThread(),"This method should be called in async thread");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        launchScheduled(new BukkitRunnable() {
            int runTime=0;
            @Override
            public void run() {
                try{
                    thread.accept(runTime);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                finally {
                    this.runTime+=1;
                    if(this.runTime>=repeatTime){
                        countDownLatch.countDown();
                        this.cancel();
                    }
                }
            }
        },delay,isSync,period);
        try{
            countDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}

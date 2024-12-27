package me.matl114.matlib.Utils.Algorithm;

public class InitializingTasks {
    Runnable r;
    public InitializingTasks(Runnable r) {
        r.run();
        this.r=r;
    }
}

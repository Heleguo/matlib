package me.matl114.matlib.Algorithms.DataStructures.Frames;

public class InitializingTasks {
    Runnable r;
    public InitializingTasks(Runnable r) {
        r.run();
        this.r=r;
    }
}

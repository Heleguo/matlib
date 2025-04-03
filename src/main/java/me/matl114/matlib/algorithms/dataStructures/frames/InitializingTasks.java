package me.matl114.matlib.algorithms.dataStructures.frames;

public class InitializingTasks {
    Runnable r;
    public InitializingTasks(Runnable r) {
        r.run();
        this.r=r;
    }
}

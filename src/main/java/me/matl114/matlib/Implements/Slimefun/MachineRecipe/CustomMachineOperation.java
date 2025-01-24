package me.matl114.matlib.Implements.Slimefun.MachineRecipe;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

public interface CustomMachineOperation extends MachineOperation {
    public abstract void progress(int i);
    @Override
    default void addProgress(int i) {

    }
}

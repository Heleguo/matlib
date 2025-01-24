package me.matl114.matlib.Implements.Slimefun.MachineRecipe;

import me.matl114.matlib.Utils.ItemCache.ItemConsumer;

public class SimpleCraftingOperation implements CustomMachineOperation {
    protected ItemConsumer[] outputItems;
    protected int totalTicks;
    protected int currentTicks;
    public SimpleCraftingOperation(ItemConsumer[] outputItems,int time) {
        this.outputItems = outputItems;
        this.totalTicks = time;
        this.currentTicks = 0;
    }
    public void progress(int var1){
        this.currentTicks += var1;
    }

    public int getProgress(){
        return this.currentTicks;
    }

    public int getTotalTicks(){
        return this.totalTicks;
    }

    public int getRemainingTicks() {
        return this.totalTicks-this.currentTicks;
    }

    public boolean isFinished() {
        return this.totalTicks<=this.currentTicks;
    }
    public ItemConsumer[] getResults(){
        return this.outputItems;
    }
}
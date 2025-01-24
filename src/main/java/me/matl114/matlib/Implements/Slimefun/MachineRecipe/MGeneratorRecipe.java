package me.matl114.matlib.Implements.Slimefun.MachineRecipe;

import org.bukkit.inventory.ItemStack;

public class MGeneratorRecipe extends StackMachineRecipe{
    public MGeneratorRecipe(StackMachineRecipe r){
        super(r.getTicks(),r.getInput(),r.getOutput());
    }
    public MGeneratorRecipe(int ticks, ItemStack[] input, ItemStack[] output){
        super(ticks,input,output);
    }
}

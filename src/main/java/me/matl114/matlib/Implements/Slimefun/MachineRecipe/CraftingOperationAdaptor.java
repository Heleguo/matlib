package me.matl114.matlib.Implements.Slimefun.MachineRecipe;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@Getter
public class CraftingOperationAdaptor<T extends MachineOperation>  extends CraftingOperation {
    public static <W extends MachineOperation> CraftingOperationAdaptor<W> of(W operation){
        return new CraftingOperationAdaptor<>(operation);
    }
    private final T handle;
    public CraftingOperationAdaptor(T operation) {
        super(new ItemStack[0],new ItemStack[0],0);
        handle = operation;
    }
    //for slimehud display use
    @Getter
    @Setter
    private int currentTick = 0 ;
    public int getProgress() {
        return this.currentTick;
    }
    @Getter
    @Setter
    private int totalTick = 0 ;
    public int getTotalTicks() {
        return this.totalTick;
    }
}

package me.matl114.matlib.Utils.ItemCache;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EqProRandomStack extends RandomItemStack  {
    public Random rand=new Random();
    public EqProRandomStack(LinkedHashSet<ItemStack> itemSettings) {
        super(new LinkedHashMap<>(){{
           itemSettings.forEach(item -> {
               this.put(item, 1);
           });
        }});
    }
    public EqProRandomStack copy(){
        EqProRandomStack stack;
        stack=(EqProRandomStack) super.copy();
        return stack;

    }

    public EqProRandomStack(LinkedHashMap<ItemStack,Integer> itemSettings) {
        super(itemSettings);
    }
    public ItemStack clone(){
        return this.itemList[rand.nextInt(this.sum)].clone();
    }
    public ItemStack getInstance(){
        ItemStack it= this.itemList[rand.nextInt(this.sum)];
        return (it instanceof RandOutItem w)?w.getInstance():it;
    }
}

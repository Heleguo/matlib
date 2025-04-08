package me.matl114.matlib.unitTest.manualTests;

import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NMSPlayerTest implements TestCase {
    @OnlineTest(name = "special test", automatic = false)
    public void test_test(CommandSender sender) throws Throwable{
        Player p = (Player) sender;
        PlayerInventory playerInventory = p.getInventory();
        ItemStack stack =  playerInventory.getItemInMainHand();
        Object nms = CraftBukkit.ITEMSTACK.handleGetter(stack);
        NMSItem.ITEMSTACK.setItem(nms, NMSCore.REGISTRIES.getRegistryByKey(BuiltInRegistryEnum.ITEM, NMSCore.NAMESPACE_KEY.newNSKey("minecraft","myitem")));
        Debug.logger(NMSItem.ITEMSTACK.getItem(nms));
        Debug.logger(NMSCore.REGISTRIES.getId(BuiltInRegistryEnum.ITEM,NMSItem.ITEMSTACK.getItem(nms) ));
    }
}

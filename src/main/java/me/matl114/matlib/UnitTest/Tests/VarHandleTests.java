package me.matl114.matlib.UnitTest.Tests;

import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.Utils.CraftUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Inventory.CleanItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VarHandleTests implements TestCase {
    @OnlineTest(name = "CraftUtils VarHandle test")
    public void testDisplayVarHandle(){
        ItemStack item = new CleanItemStack(Material.BOOK,"这是&a一个&c书","这&e是一本&r书","这并&6不是两&3本书");
        ItemMeta meta = item.getItemMeta();
        Debug.logger(CraftUtils.getDisplayNameHandle().get(meta));
        Debug.logger(CraftUtils.getLoreHandle().get(meta));
    }
}

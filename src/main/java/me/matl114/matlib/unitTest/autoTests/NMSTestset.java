package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.nmsTests.CoreTests;
import me.matl114.matlib.unitTest.autoTests.nmsTests.CraftBukkitUtilTests;
import me.matl114.matlib.unitTest.autoTests.nmsTests.InventoryTests;
import me.matl114.matlib.unitTest.autoTests.nmsTests.LevelTests;

public class NMSTestset extends TestSet {
    {
//        addTest(new CoreTests());
//        addTest(new LevelTests());
        addTest(new InventoryTests());
        addTest(new CraftBukkitUtilTests());
    }
}

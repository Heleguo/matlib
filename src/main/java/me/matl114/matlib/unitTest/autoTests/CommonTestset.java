package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.commonUtilsTests.InventoryUtilTests;
import me.matl114.matlib.unitTest.autoTests.commonUtilsTests.ThreadUtilTests;
import me.matl114.matlib.unitTest.autoTests.commonUtilsTests.WorldUtilTests;

public class CommonTestset extends TestSet {
    {
//
//        addTest(new ComponentTests());
        //addTest(new InventoryUtilTests());
//        addTest(new ThreadUtilTests());
        addTest(new WorldUtilTests());
    }
}

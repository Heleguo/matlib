package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.bukkitTests.BukkitAPITests;
import me.matl114.matlib.unitTest.autoTests.bukkitTests.BukkitTranslationTests;
import me.matl114.matlib.unitTest.autoTests.bukkitTests.PdcTests;

public class BukkitTestset extends TestSet {
    {

//        addTest(new BukkitAPITests());
//        addTest(new BukkitTranslationTests());
        addTest(new PdcTests());
    }
}

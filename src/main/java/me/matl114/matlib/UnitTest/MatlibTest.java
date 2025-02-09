package me.matl114.matlib.UnitTest;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.UnitTest.Tests.CommonTests;
import me.matl114.matlib.UnitTest.Tests.InventoryTests;
import me.matl114.matlib.UnitTest.Tests.SlimefunTests;
import me.matl114.matlib.UnitTest.Tests.VarHandleTests;
import me.matl114.matlib.core.AddonInitialization;
import me.matl114.matlib.core.Initialization;
import org.bukkit.plugin.java.JavaPlugin;

public class MatlibTest extends JavaPlugin {
    @Getter
    @Setter
    private static MatlibTest instance;
    private static AddonInitialization initialization ;
    private static TestRunner testRunner;
    public void onEnable() {
        instance = this;
        initialization = new AddonInitialization(this,"Matlib")
                .displayName("Matlib-Unittest")
                .testMode(true)
                .onEnable()
                .cast(AddonInitialization.class);
        testRunner = new TestRunner()
                .init(this)
                .registerTestCase(new VarHandleTests())
                .registerTestCase(new CommonTests())
                .registerTestCase(new InventoryTests())
                .registerTestCase(new SlimefunTests())
        ;
    }
    public void onDisable() {
        instance = null;
        initialization.onDisable();
        initialization = null;
        testRunner . deconstruct();
        testRunner = null;
    }
}

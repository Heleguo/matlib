package me.matl114.matlib.unitTest;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.implement.bukkit.ChatInputManager;
import me.matl114.matlib.unitTest.autoTests.*;
import me.matl114.matlib.unitTest.manualTests.ArgumentedTests;
import me.matl114.matlib.unitTest.manualTests.DisplayManagerTest;
import me.matl114.matlib.unitTest.manualTests.PlayerTest;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.experimential.FakeSchedular;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;


public class MatlibTest extends JavaPlugin {
    public MatlibTest(){
        Debug.stackTrace();
        ClassLoader loader = MatlibTest.class.getClassLoader();
        do{
            Debug.logger(loader);
            loader = loader.getParent();
        }while (loader != null);
    }
    @Getter
    @Setter
    private static MatlibTest instance;
    private static AddonInitialization initialization ;
    private static TestRunner testRunner;
    private static ChatInputManager chatInputManager;
    public void onLoad(){

    }
    public void onEnable() {
        instance = this;
        initialization = new AddonInitialization(this,"Matlib")
            .displayName("Matlib-Unittest")
            .testMode(true)
            .onEnable()
            .cast();
        FakeSchedular.init();
        chatInputManager =  new ChatInputManager()
            .init(this);
        testRunner = new TestRunner()
            .init(this)
            //.registerTestCase(new ReflectionUtilTests())
            //.registerTestCase(new CommonTests())
            //.registerTestCase(new InventoryTests())
//            .registerTestCase(new SlimefunTests())
            //.registerTestCase(new EntityTests())
            //.registerTestCase(new ExperimentialTest())
            .registerTestCase(new DisplayManagerTest())
//            .registerTestCase(new ComponentTests())
            .registerTestCase(new PlayerTest())
            .registerTestCase(new DescriptorTests())
            .registerTestCase(new ArgumentedTests())
        ;
        this.getServer().getPluginManager().registerEvents(new TestListeners(),this);
    }
    public void onDisable() {

        instance = null;
        initialization.onDisable();
        initialization = null;
        chatInputManager.deconstruct();
        chatInputManager = null;
        testRunner.deconstruct();
        testRunner = null;
        HandlerList.unregisterAll(this);
    }
}

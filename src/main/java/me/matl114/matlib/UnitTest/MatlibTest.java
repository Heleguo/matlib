package me.matl114.matlib.UnitTest;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.Algorithms.Algorithm.FileUtils;
import me.matl114.matlib.Implements.Bukkit.ChatInputManager;
import me.matl114.matlib.UnitTest.AutoTests.*;
import me.matl114.matlib.UnitTest.ManualTests.DisplayManagerTest;
import me.matl114.matlib.UnitTest.ManualTests.PlayerTest;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Experimential.FakeSchedular;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class MatlibTest extends JavaPlugin {
    @Getter
    @Setter
    private static MatlibTest instance;
    private static AddonInitialization initialization ;
    private static TestRunner testRunner;
    private static ChatInputManager chatInputManager;

    public void onEnable() {
        instance = this;
        initialization = new AddonInitialization(this,"Matlib")
            .displayName("Matlib-Unittest")
            .testMode(true)
            .onEnable()
            .cast(null);
        FakeSchedular.init();
        chatInputManager =  new ChatInputManager()
            .init(this);
        testRunner = new TestRunner()
            .init(this)
            .registerTestCase(new ReflectionUtilTests())
            .registerTestCase(new CommonTests())
            .registerTestCase(new InventoryTests())
            .registerTestCase(new SlimefunTests())
            .registerTestCase(new EntityTests())
            .registerTestCase(new ExperimentialTest())
            .registerTestCase(new DisplayManagerTest())
            .registerTestCase(new ComponentTests())
            .registerTestCase(new PlayerTest())
        ;
        this.getServer().getPluginManager().registerEvents(new TestListeners(),this);
//        Debug.logger(PlayerFailMoveEvent.class);
//        Debug.logger("delete this");
//        var re =  FileUtils.deleteDirectory(new File("plugins/Slimefun/error-reports"));
//        Debug.logger("successfully delete",re);

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

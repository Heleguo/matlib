package me.matl114.matlib.unitTest;

import lombok.Getter;
import me.matl114.matlib.implement.bukkit.chat.ChatInputManager;
import me.matl114.matlib.implement.nms.chat.PacketTranslator;
import me.matl114.matlib.implement.nms.network.PacketEventManager;
import me.matl114.matlib.implement.nms.network.PacketListener;
import me.matl114.matlib.unitTest.autoTests.*;
import me.matl114.matlib.unitTest.manualTests.DisplayManagerTest;
import me.matl114.matlib.unitTest.manualTests.ManualTests;
import me.matl114.matlib.unitTest.manualTests.NMSPlayerTest;
import me.matl114.matlib.unitTest.manualTests.PlayerTest;
import me.matl114.matlib.utils.chat.lan.LanguageRegistry;
import me.matl114.matlib.utils.experimential.FakeSchedular;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;


public class MatlibTest extends JavaPlugin {
    public MatlibTest(){
    }
    @Getter
    private static MatlibTest instance;
    @Getter
    private static AddonInitialization initialization ;
    private static TestRunner testRunner;
    @Getter
    private static ChatInputManager chatInputManager;
    @Getter
    private static PacketEventManager packetEventManager;
    @Getter
    private static LanguageRegistry itemLanguageRegistry;
    @Getter
    private static LanguageRegistry chatLanguageRegistry;
    @Getter
    private static PacketTranslator packetTranslator;
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
        packetEventManager = new PacketEventManager()
            .init(this);
        itemLanguageRegistry = new LanguageRegistry(Locale.CHINESE, new NamespacedKey("matlib","item-language-registry"),false);
        chatLanguageRegistry = new LanguageRegistry(Locale.CHINESE, new NamespacedKey("matlib","chat-language-registry"),true);
        packetTranslator = new PacketTranslator()
            .init(this)
            .setLanguageRegistry(itemLanguageRegistry);
        testRunner = new TestRunner()
            .init(this)
            .setWarmup(false)
            .registerTestCase(new BukkitTestset())
            .registerTestCase(new CommonTestset())
            .registerTestCase(new NMSTestset())
            .registerTestCase(new ReflectionTestset())

            .registerTestCase(new DependsTestset())
            //project tests tests that havn't complete
            .registerTestCase(new ComponentTests())
//            .registerTestCase(new EntityTests())
//            .registerTestCase(new ExperimentialTest())
            //manual tests
            .registerTestCase(new DisplayManagerTest())
            .registerTestCase(new PlayerTest())
            .registerTestCase(new NMSPlayerTest())
            .registerTestCase(new NetworkTestset())
            .registerTestCase(new AlgorithmTestset())
            .registerTestCase(new ManualTests())
            .registerTestCase(new ImplementationTests())
            .registerTestCase(new TestcaseBuilder())
        ;
        packetEventManager.registerListener(new TestPacketListener(),this);
        this.getServer().getPluginManager().registerEvents(new TestListeners(),this);
    }
    public void onDisable() {

        instance = null;
        if(initialization != null)
            initialization.onDisable();
        initialization = null;
        if(chatInputManager != null)
            chatInputManager.safeDeconstruct();
        chatInputManager = null;
        if(packetEventManager != null){
            packetEventManager.unregisterAll(this);
            packetEventManager.safeDeconstruct();
        }
        if(packetTranslator != null){
            packetTranslator.safeDeconstruct();
        }
        if(itemLanguageRegistry != null){
            itemLanguageRegistry.deconstruct();
        }
        if(chatLanguageRegistry != null){
            chatLanguageRegistry.deconstruct();
        }
        if(testRunner != null)
            testRunner.safeDeconstruct();

        testRunner = null;
        HandlerList.unregisterAll(this);
    }
}

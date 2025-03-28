package me.matl114.matlib.UnitTest.AutoTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.*;
import me.matl114.matlib.Utils.Version.Version;
import me.matl114.matlib.Utils.Version.VersionAtLeast;
import me.matl114.matlibAdaptor.Algorithms.DataStructures.LockFactory;
import me.matl114.matlibAdaptor.Algorithms.Interfaces.Initialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Method;

public class CommonTests implements TestCase {
    @OnlineTest(name = "LoggerUtil Test")
    public void testLogger(){
        Debug.logger("This is logger output 1");
        Debug.logger("This is logger output 2");
        Debug.catchAllOutputs(()->{Debug.logger("this is logger output 3");Debug.logger("this is logger output 4.1");Debug.logger("this is logger output 4.2");
            System.out.println("This is stdoutput 4.3");},false);
        String value = Debug.catchAllOutputs(()->{Debug.logger("this is logger output 4");Debug.logger("this is logger output 4.1");Debug.logger("this is logger output 4.2");
            System.out.println("This is stdoutput 4.3");},true);
        Debug.logger(value);
        Debug.catchAllOutputs(()->{
            new RuntimeException("This is logger output 5").printStackTrace();},false);
        String value2 = Debug.catchAllOutputs(()->{
            new RuntimeException("This is logger output 6").printStackTrace();},true);
        Debug.logger(value2.isEmpty());
        Debug.logger(value2);
    }
//    @OnlineTest(name = "TestRunner Test")
//    public void testThrowError(){
//        throw new NullPointerException("This is a null pointer");
//    }

    @OnlineTest(name = "PaperAPI Version Test")
    public void testDependency() throws Throwable {
        try{
            @VersionAtLeast(Version.v1_20_R2)
            Class<?> clazz = PlayerPickItemEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try{
            @VersionAtLeast(Version.v1_20_R2)
            Class<?>  clazz = PlayerFailMoveEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try{
            Class<?>  clazz = io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try{
            Class<?>  clazz = io.papermc.paper.event.player.PrePlayerAttackEntityEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try{
            Class<?>  clazz = io.papermc.paper.event.player.PlayerPurchaseEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try {
            Class<?>  clazz = io.papermc.paper.event.player.PlayerNameEntityEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try{
            Class<?>  clazz = io.papermc.paper.event.player.PlayerItemCooldownEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }
        try {
            Class<?>  clazz = io.papermc.paper.event.player.PlayerStopUsingItemEvent.class;
        }catch (Throwable e){
            e.printStackTrace();
        }

    }


}

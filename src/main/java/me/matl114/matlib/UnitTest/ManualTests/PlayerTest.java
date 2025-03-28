package me.matl114.matlib.UnitTest.ManualTests;

import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerTest implements TestCase {
    @OnlineTest(name = "player method version test",automatic = false)
    public void test_playermethod(CommandSender sender){
        Player p=(Player) sender;
        p.setNoPhysics(true);
    }

}

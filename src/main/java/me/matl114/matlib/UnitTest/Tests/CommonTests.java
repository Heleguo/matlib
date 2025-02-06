package me.matl114.matlib.UnitTest.Tests;

import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Debug;

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
    @OnlineTest(name = "TestRunner Test")
    public void testThrowError(){
        throw new NullPointerException("This is a null pointer");
    }

}

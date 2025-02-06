package me.matl114.matlib.UnitTest;

import lombok.Getter;
import me.matl114.matlib.Implements.Managers.ScheduleManager;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import me.matl114.matlib.core.Manager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class TestRunner implements Manager {
    private Plugin plugin;
    @Getter
    private TestRunner manager;
    @Override
    public TestRunner init(Plugin pl, String... path) {
        return this;
    }

    @Override
    public TestRunner reload() {
        deconstruct();
        return init(plugin);
    }

    @Override
    public void deconstruct() {

    }
    private final HashMap<TestRunnable,TestCase> testCases = new LinkedHashMap<TestRunnable,TestCase>();
    private final HashMap<Consumer<CommandSender>,TestCase> manuallyExecutedCase = new LinkedHashMap<>();
    public TestRunner registerTestCase(TestCase testCase) {
        var methods = ReflectUtils.getAllMethodsRecursively(testCase.getClass());
        for (var method : methods) {
            if(method.isSynthetic()||method.isBridge()||method.getParameterTypes().length>=2) {continue;}
            OnlineTest testAnnotation = method.getAnnotation(OnlineTest.class);
            if(testAnnotation == null) {continue;}

            method.setAccessible(true);
            if(testAnnotation.automatic()&&method.getParameterTypes().length==0){
                testCases .put(new TestRunnable() {
                    @Override
                    public boolean isAsync() {
                        return testAnnotation.async();
                    }

                    @Override
                    public void run() {
                        long start = System.nanoTime();
                        try{
                            method.invoke(testCase);
                        }catch (Throwable e) {
                            throw new RuntimeException(e);
                        }finally {
                            long end = System.nanoTime();
                            Debug.logger("Running test case:",testAnnotation.name(),"Time cost:",end-start,"ns,(",(end-start)/1_000_000,"ms)");
                        }
                    }
                },testCase);
            }else if(!testAnnotation.automatic()&&method.getParameterTypes().length==1&&method.getParameterTypes()[0]==CommandSender.class){
                manuallyExecutedCase.put((sender -> new TestRunnable() {
                    @Override
                    public boolean isAsync() {
                        return testAnnotation.async();
                    }

                    @Override
                    public void run() {
                        long start = System.nanoTime();
                        Debug.logger("Start Running test case: ",testAnnotation.name(),"in",isAsync()?"Async":"Main","Thread");
                        try{
                            method.invoke(testCase,sender);
                        }catch (Throwable e) {
                            throw new RuntimeException(e);
                        }finally {
                            long end = System.nanoTime();
                            Debug.logger("Running test case:",testAnnotation.name(),"Time cost:",end-start,"ns,(",(end-start)/1_000_000,"ms)");
                        }
                    }
                }.execute()),testCase);
            }

        }
        return this;
    }
    public TestRunner unregisterTestCase(TestCase testCase) {
        testCases.entrySet().removeIf(entry -> entry.getValue() == testCase);
        return this;
    }
    public interface TestRunnable extends Runnable{
        default void execute() {
            ScheduleManager.getManager().execute(this,!isAsync());
        }
        boolean isAsync();
    }

}

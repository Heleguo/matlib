package me.matl114.matlib.UnitTest;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.Implements.Managers.ScheduleManager;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import me.matl114.matlib.Utils.ThreadUtils;
import me.matl114.matlib.core.Manager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public class TestRunner implements Manager {
    private Plugin plugin;
    @Getter
    private TestRunner manager;
    @Override
    public TestRunner init(Plugin pl, String... path) {
        ScheduleManager.getManager().launchScheduled(this::runAutomaticTests,200,false,0);
        this.addToRegistry();
        return this;
    }

    @Override
    public TestRunner reload() {
        deconstruct();
        return init(plugin);
    }

    @Override
    public void deconstruct() {
        this.removeFromRegistry();
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
                        Debug.logger("Start Running test case: ",testAnnotation.name(),"in",isAsync()?"Async":"Main","Thread");
                        try{
                            method.invoke(testCase);
                        }catch (InvocationTargetException | IllegalAccessException e) {
                            Debug.logger("Error While Running test case: ",testAnnotation.name(),"caused by:");
                            e.getCause().printStackTrace();
                        }finally {
                            long end = System.nanoTime();
                            Debug.logger("Finish test case:",testAnnotation.name(),"Time cost:",end-start,"ns,(",(end-start)/1_000_000,"ms)");
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
                        }catch (InvocationTargetException | IllegalAccessException e) {
                            throw new RuntimeException(e.getCause());
                        }
                        finally {
                            long end = System.nanoTime();
                            Debug.logger("Finish test case:",testAnnotation.name(),"Time cost:",end-start,"ns,(",(end-start)/1_000_000,"ms)");
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
        default void executeAwait(){
            Preconditions.checkArgument(!Bukkit.isPrimaryThread());
            var future = ThreadUtils.getFutureTask(this);
            ScheduleManager.getManager().execute(future,!isAsync());
            ThreadUtils.awaitFuture(future);
        }
        boolean isAsync();
    }
    public void runAutomaticTests(){
        Debug.logger("Starting automatic tests");
        Debug.logger("-------------------------------------------");
        for (TestRunnable runnable : testCases.keySet()) {
            ThreadUtils.sleep(1_000);
            runnable.executeAwait();
            Debug.logger("-------------------------------------------");
        }

        Debug.logger("Finished automatic tests");
    }

}

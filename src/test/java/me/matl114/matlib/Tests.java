package me.matl114.matlib;

import me.matl114.matlib.Algorithms.DataStructures.Complex.ObjectLockFactory;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Pair;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Triplet;
import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import me.matl114.matlib.Utils.Command.CommandGroup.SubCommand;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.FieldGetter;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.matl114.matlib.Utils.Reflect.ReflectUtils;
import me.matl114.matlib.Algorithms.Algorithm.ThreadUtils;
import me.matl114.matlib.core.AddonInitialization;
import me.matl114.matlibAdaptor.Proxy.Utils.AnnotationUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.junit.jupiter.api.Test;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    public void log(Object message) {
        System.out.println("Test: "+message);
    }
    public static final AddonInitialization testMockAddon=new AddonInitialization(null,"Test").onEnable();
    private static final FieldAccess testAccess = FieldAccess.ofName(TestClass.class, "d").printError(true);
    private static final VarHandle testHandle = testAccess.getVarHandleOrDefault(()->null);
    @Test
    public void test_reflection() {
        AttributeModifier modifier= new AttributeModifier(new NamespacedKey("m","e"),114, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        FieldAccess amountAccess=FieldAccess.ofName(AttributeModifier.class,"amount");
        amountAccess.ofAccess(modifier).set(514.0);
        log(modifier.getAmount());
        TestClass testClass=new TestClass();
        testClass.a=testClass;
        FieldAccess fieldAccess=FieldAccess.ofName(TestClass.class,"a");
        var ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.set(null);
        log(testClass.a);
        log("1111");
        fieldAccess=FieldAccess.ofName(TestClass.class,"d");
        ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.set(testClass);
        log(testClass.getD());
        log("1111");
        fieldAccess=FieldAccess.ofName(TestClass.class,"c");
        ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.set(testClass);
        log(TestClass.c);
        log("1111");
        fieldAccess=FieldAccess.ofName(TestClass.class,"e");
        ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.set(testClass);
        log("1111");
        log(TestClass.f);
        fieldAccess=FieldAccess.ofName(TestClass.class,"f");
        ac=fieldAccess.ofAccess(testClass);
        log("checkraw "+ac.getRaw());
        ac.setUnsafe(testClass);
        log("checkset "+TestClass.f);
        fieldAccess=FieldAccess.ofName(TestClass.class,"s");
        ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.setUnsafe("我操啊");
        log(TestClass.getS());
    }
    @Test
    public void test_reflection_2(){
        MethodAccess<?> methodAccess;

        TestClass obj=new TestClass();
        try{
            methodAccess=MethodAccess.ofName(TestClass.class,"m1");
            methodAccess.invoke(obj);
            methodAccess=MethodAccess.ofName(TestClass.class,"m2");
            methodAccess.invoke(obj,"sb");
            methodAccess=MethodAccess.ofName(TestClass.class,"m3");
            methodAccess.invoke(obj);
            methodAccess=MethodAccess.ofName(TestClass.class,"m4");
            methodAccess.invoke(obj);
            methodAccess=MethodAccess.ofName(TestClass.class,"m5");
            methodAccess.invoke(obj);
            methodAccess=MethodAccess.ofName(TestClass.class,"m6");
            methodAccess.invoke(obj,"77");
            methodAccess=MethodAccess.ofName(TestClass.class,"m7");
            methodAccess.invokeCallback(ob -> {

            }, () -> {
            }, obj, "byd");
            methodAccess.forceCast(String.class).invokeCallback(str->{
                log(str+"666");
            },()->{},obj,"2b");
            methodAccess=MethodAccess.ofName(TestClass.class,"m8");
            methodAccess.forceCast(void.class).invokeCallback(vo->{
                log("777");
            },()->{},obj,"b");
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_reflection_efficiency(){
        TestClass obj=new TestClass();
        FieldAccess access=FieldAccess.ofName(TestClass.class,"ttt");
        FieldAccess.AccessWithObject<Integer> re=access.ofAccess(obj);
        long a=System.nanoTime();
        for(int i=0;i<100000;i++){
            re.set(re.getRaw()+1);
        }
        long b=System.nanoTime();
        log("check "+obj.ttt);
        log("using time "+(b-a));
        //enable handle: 27683800 19029700 25699400 20924100 27461300 57322300 39966300 23702900
        //disable handle: 28414300 23184400 14584200 18837900 16926600 30481000 15374300 14651700
        //using ReflectASM 9542500
    }
    @Test
    public void test_pair(){
        TestClass obj=new TestClass();
        TestClass obj2=new TestClass();
        Pair<TestClass,TestClass> pair= Pair.of(obj,obj2);
        log(pair.toString());
        long a=System.nanoTime();
        for(int i=0;i<100000;i++){
            pair=Pair.of(obj,obj2);
        }
        long b=System.nanoTime();
        log("check "+(b-a));
        a=System.nanoTime();
        for(int i=0;i<100000;i++){
            pair=new Pair<>(obj,obj2);
        }
        b=System.nanoTime();
        log("using time "+(b-a));
        Triplet<Integer,Integer,Integer> let=Triplet.of(1,2,null);
        log(let.toString());
        log(let.getA()+let.getB());
    }
    @Test
    public void test_math(){
        double d0=1.0;
        int j=(int)(d0 * (double)(4 << 29) + 0.5);
        double f=20;
        log(j);

        log((float)(f+(double) j));
    }
    @Test
    public void test_initializeTool(){
        String a= ChatColor.GREEN + StrUtil.oooooo("惜歘坜佄畜") + "PlayerTasks" + StrUtil.oooooo1("掣二盵儷赈爲]跿辶杺奖什攁阪則斚沤纝纜伅留");
        log(a);
    }
//
    @Test
    public void test_varHandle(){
        TestClass object = new TestExtendClass();
        log("Test Handle");
        log(testAccess);
        log(testHandle);
        testHandle.set(object,114);
        log(testHandle.get(object));
        Field field = ReflectUtils.getFieldsRecursively(TestClass.class,"d").getA();
        FieldGetter getter = testAccess.getter(object);
        long start = System.nanoTime();
        try{
            for (int i=0;i<10000;++i){
                getter.apply(object);
                //field.get(object);
            }
        }catch (Throwable e){

        }
        long end = System.nanoTime();
        log("Test Access time: "+(end-start));
        start = System.nanoTime();
        try{
            for (int i=0;i<10000;++i){
                testHandle.get(object);
            }
        }catch (Throwable e){
        }
        end = System.nanoTime();
        log("Test Handle time: "+(end-start));
        com.esotericsoftware.reflectasm.FieldAccess fieldAccess = com.esotericsoftware.reflectasm.FieldAccess.get(TestClass.class);
        log(fieldAccess.getClass());
        int index = fieldAccess.getIndex("d");
        start = System.nanoTime();
        //public非常快 无法访问private
        for (int i=0;i<10000;++i){
            fieldAccess.get(object,index);
        }
        end = System.nanoTime();
        log("Test Access time: "+(end-start));
        log(fieldAccess.get(object,"d"));
    }
    @Test
    public void test_coommandUtils(){
        TestComomand comomand = new TestComomand();
        var result = comomand.testCommand.parseInput(new String[]{"1"}).getA();
        log(result.nextArg());
        log(result.nextArg());
        result = comomand.testCommand.parseInput(new String[]{"--operation","3","-arg1"}).getA();
        log(result.nextArg());
        log(result.nextArg());
    }

    @Test
    public void test_lockFactory(){
        ObjectLockFactory<String> testFactory = new ObjectLockFactory<>(String.class).init(null);
        List<CompletableFuture<Void>> futures=new ArrayList<>();
        AtomicInteger runningThread =new AtomicInteger(0);
        for(int i=0;i<30;++i){
            int index = i;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
                    log("Task %d - 1 start".formatted(index));
                    ThreadUtils.sleep(10);
                    log("Task %d - 1 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ab","cd","ef");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
                    log("Task %d - 1 - 1 start".formatted(index));
                    ThreadUtils.sleep(10);
                    log("Task %d - 1 - 1 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ef","cd","ab");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
                    log("Task %d - 1 - 2 start".formatted(index));
                    ThreadUtils.sleep(10);
                    log("Task %d - 1 - 2 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ef","cd","ab");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
                    log("Task %d - 2 start".formatted(index));
                    ThreadUtils.sleep(10);
                    log("Task %d - 2 done".formatted(index));
                    runningThread.decrementAndGet();
                },"666","33","ab","666","666","666","666","666","666","666","666","666");
            }));
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
                    log("Task %d - 3 start".formatted(index));
                    ThreadUtils.sleep(10);
                    log("Task %d - 3 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ss","rr","ab","asc");
            }));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        log(runningThread.get());
        //total wait time in requesting lock
        //30697362000
        futures=new ArrayList<>();
        runningThread.set(0);
        for(int i=0;i<30;++i){
            int index = i;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
//                    log("Task %d - 1 start".formatted(index));
//                    log("Task %d - 1 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ab","cd","ef");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
//                    log("Task %d - 1 - 1 start".formatted(index));
//                    log("Task %d - 1 - 1 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ef","cd","ab");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
//                    log("Task %d - 1 - 2 start".formatted(index));
//                    log("Task %d - 1 - 2 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ef","cd","ab");
            }))
            ;
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
//                    log("Task %d - 2 start".formatted(index));
//                    log("Task %d - 2 done".formatted(index));
                    runningThread.decrementAndGet();
                },"666","33","ab","666","666","666","666","666","666","666","666","666");
            }));
            futures.add(CompletableFuture.runAsync(()->{
                testFactory.ensureLock(()->{
                    if(runningThread.incrementAndGet()>1){
                        Debug.logger("Catch Async Thread Run");
                    }
//                    log("Task %d - 3 start".formatted(index));
//                    log("Task %d - 3 done".formatted(index));
                    runningThread.decrementAndGet();
                },"ss","rr","ab","asc");
            }));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        log(runningThread.get());
        //sort time 477000 235800 898200
        // 288200
    }
    @Test
    public void test_method() throws NoSuchMethodException {
        log(AnnotationUtils.ADAPTOR_ANNOTATION_IDENTIFIER);

    }
    public static int GetRandom(int end) {
        return Math.abs(new Random().nextInt()) % (end + 1);
    }
    public static long GetRandomL(long end) {
        return Math.abs(new Random().nextLong()) % (end + 1);
    }
    public static boolean predicate(double test){
        return GetRandom( Math.min(20,20) * 16777216 * 6 ) <= 10 *test;
    }
    @Test
    public void test_probablity(){
        int testCount = 10000;
        int successCount = 0;
        for(int i=0;i<testCount;++i){
            if(predicate(10000*10000)){
                successCount++;
            }
        }
        log("Prob: %d/%d".formatted(successCount,testCount));
    }
    @Test
    public void test_threadpool(){
//        var pool = new ForkJoinPool();
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        for(int i=0;i<65536;++i){
//            futures.add(  CompletableFuture.runAsync(()->{
//                ThreadUtils.sleep(1000);
//            },pool));
//        }
//        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
//        log("Finish");
    }


}
class TestComomand extends AbstractMainCommand {
    @Override
    public String permissionRequired() {
        return null;
    }

    public SubCommand mainCommand = new SubCommand("test",new SimpleCommandArgs("_operation"))
            .setTabCompletor("_operation",()->this.getSubCommands().stream().map(SubCommand::getName).toList());
    public SubCommand testCommand = new SubCommand("argument",new SimpleCommandArgs("arg1","operation"))
            .setTabCompletor("arg1", ()->List.of("1"))
            .setDefault("operation","2")
            .setTabCompletor("arg2", ()->List.of("2"))
            .register(this);
}

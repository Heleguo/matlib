package me.matl114.matlib;

import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.HashContainer;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.dataStructures.struct.Triplet;
import me.matl114.matlib.utils.language.componentCompiler.BuildContent;
import me.matl114.matlib.utils.language.componentCompiler.ComponentAST;
import me.matl114.matlib.utils.language.componentCompiler.ComponentFormatParser;
import me.matl114.matlib.utils.language.componentCompiler.Parameter;
import me.matl114.matlib.utils.language.lan.DefaultPlaceholderProviderImpl;
import me.matl114.matlib.utils.reflect.*;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import me.matl114.matlib.common.functions.reflect.FieldGetter;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.junit.jupiter.api.Test;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Random;

public class Tests {
    public void log(Object message) {
        System.out.println("Test: "+message);
    }
//    public static final AddonInitialization testMockAddon=new AddonInitialization(null,"Test").onEnable();
    private static final FieldAccess testAccess = FieldAccess.ofName(TestClass.class, "d").printError(true);
    private static final VarHandle testHandle = testAccess.getVarHandleOrDefault(()->null);
  //  @Test
    public void test_reflection() {
        AttributeModifier modifier= new AttributeModifier(new NamespacedKey("m","e"),114, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        FieldAccess amountAccess=FieldAccess.ofName(AttributeModifier.class,"amount");
        amountAccess.ofAccess(modifier).set(514.0);

        TestClass testClass=new TestClass();
        testClass.a=testClass;
        FieldAccess fieldAccess=FieldAccess.ofName(TestClass.class,"a");
        var ac=fieldAccess.ofAccess(testClass);

        ac.set(null);

        fieldAccess=FieldAccess.ofName(TestClass.class,"d");
        ac=fieldAccess.ofAccess(testClass);

        ac.set(testClass);

        fieldAccess=FieldAccess.ofName(TestClass.class,"c");
        ac=fieldAccess.ofAccess(testClass);

        ac.set(testClass);

        fieldAccess=FieldAccess.ofName(TestClass.class,"e");
        ac=fieldAccess.ofAccess(testClass);

        ac.set(testClass);

        fieldAccess=FieldAccess.ofName(TestClass.class,"f");
        ac=fieldAccess.ofAccess(testClass);
        ac.setUnsafe(testClass);

        fieldAccess=FieldAccess.ofName(TestClass.class,"s");
        ac=fieldAccess.ofAccess(testClass);
        log(ac.getRaw());
        ac.setUnsafe("我操啊");
        log(TestClass.getS());
    }
  //  @Test
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
            },()->{},obj,"2b");
            methodAccess=MethodAccess.ofName(TestClass.class,"m8");
            methodAccess.forceCast(void.class).invokeCallback(vo->{
            },()->{},obj,"b");
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
   // @Test
    public void test_reflection_efficiency(){
        TestClass obj=new TestClass();
        FieldAccess access=FieldAccess.ofName(TestClass.class,"ttt");
        FieldAccess.AccessWithObject<Integer> re=access.ofAccess(obj);
        long a=System.nanoTime();
        for(int i=0;i<100000;i++){
            re.set(re.getRaw()+1);
        }
        long b=System.nanoTime();

        //enable handle: 27683800 19029700 25699400 20924100 27461300 57322300 39966300 23702900
        //disable handle: 28414300 23184400 14584200 18837900 16926600 30481000 15374300 14651700
        //using ReflectASM 9542500
    }
 //   @Test
    public void test_pair(){
        TestClass obj=new TestClass();
        TestClass obj2=new TestClass();
        Pair<TestClass,TestClass> pair= Pair.of(obj,obj2);
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

    }
  //  @Test
    public void test_math(){
        double d0=1.0;
        int j=(int)(d0 * (double)(4 << 29) + 0.5);
        double f=20;
    }
    //@Test
    public void test_initializeTool(){
        String a= ChatColor.GREEN + StrUtil.oooooo("惜歘坜佄畜") + "PlayerTasks" + StrUtil.oooooo1("掣二盵儷赈爲]跿辶杺奖什攁阪則斚沤纝纜伅留");
        log(a);
    }
//
   // @Test
    public void test_varHandle(){
        TestClass object = new TestExtendClass();
        log("Test Handle");

        testHandle.set(object,114);
        Field field = ReflectUtils.getFieldsRecursively(TestClass.class,"d").getA();
        FieldGetter getter = testAccess.getter(object);
        long start = System.nanoTime();
        try{
            for (int i=0;i<10000;++i){
                getter.getField(object);
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
       me.matl114.matlib.utils.reflect.reflectasm.FieldAccess fieldAccess = me.matl114.matlib.utils.reflect.reflectasm.FieldAccess.get(TestClass.class);
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
   // @Test
//    public void test_coommandUtils(){
//        TestComomand comomand = new TestComomand();
//        var result = comomand.testCommand.parseInput(new String[]{"1"}).getA();
//        log(result.nextArg());
//        log(result.nextArg());
//        result = comomand.testCommand.parseInput(new String[]{"--operation","3","-arg1"}).getA();
//        log(result.nextArg());
//        log(result.nextArg());
//    }

//    @Test
//    public void test_lockFactory(){
//        ObjectLockFactory<String> testFactory = new ObjectLockFactory<>(String.class).init(null);
//        List<CompletableFuture<Void>> futures=new ArrayList<>();
//        AtomicInteger runningThread =new AtomicInteger(0);
//        for(int i=0;i<30;++i){
//            int index = i;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
//                    log("Task %d - 1 start".formatted(index));
//                    ThreadUtils.sleep(10);
//                    log("Task %d - 1 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ab","cd","ef");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
//                    log("Task %d - 1 - 1 start".formatted(index));
//                    ThreadUtils.sleep(10);
//                    log("Task %d - 1 - 1 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ef","cd","ab");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
//                    log("Task %d - 1 - 2 start".formatted(index));
//                    ThreadUtils.sleep(10);
//                    log("Task %d - 1 - 2 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ef","cd","ab");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
//                    log("Task %d - 2 start".formatted(index));
//                    ThreadUtils.sleep(10);
//                    log("Task %d - 2 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"666","33","ab","666","666","666","666","666","666","666","666","666");
//            }));
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
//                    log("Task %d - 3 start".formatted(index));
//                    ThreadUtils.sleep(10);
//                    log("Task %d - 3 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ss","rr","ab","asc");
//            }));
//        }
//        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
//        log(runningThread.get());
//        //total wait time in requesting lock
//        //30697362000
//        futures=new ArrayList<>();
//        runningThread.set(0);
//        for(int i=0;i<30;++i){
//            int index = i;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
////                    log("Task %d - 1 start".formatted(index));
////                    log("Task %d - 1 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ab","cd","ef");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
////                    log("Task %d - 1 - 1 start".formatted(index));
////                    log("Task %d - 1 - 1 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ef","cd","ab");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
////                    log("Task %d - 1 - 2 start".formatted(index));
////                    log("Task %d - 1 - 2 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ef","cd","ab");
//            }))
//            ;
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
////                    log("Task %d - 2 start".formatted(index));
////                    log("Task %d - 2 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"666","33","ab","666","666","666","666","666","666","666","666","666");
//            }));
//            futures.add(CompletableFuture.runAsync(()->{
//                testFactory.ensureLock(()->{
//                    if(runningThread.incrementAndGet()>1){
//                        Debug.logger("Catch Async Thread Run");
//                    }
////                    log("Task %d - 3 start".formatted(index));
////                    log("Task %d - 3 done".formatted(index));
//                    runningThread.decrementAndGet();
//                },"ss","rr","ab","asc");
//            }));
//        }
//        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
//        log(runningThread.get());
//        //sort time 477000 235800 898200
//        // 288200
//    }
   // @Test
    public void test_container()  {
        HashContainer<Integer> container = new HashContainer<>();
        container.add(1);
        container.add(2);
        container.add(3);
        container.add(4);
        log(container.toString());
        log(container.contains(1));
        log(container.contains(2));
        log(container.findFirst(Integer.hashCode(1),x->x==1));

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
    //@Test
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
   // @Test
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
    //@Test
    public void test_quaterion(){
        var tran = TransformationUtils.builder().postRotation(0,1,0,90).build();
        log(tran);
        var id = TransformationUtils.LCTransformation.ofIdentical();
        log(id.transformOrigin(tran));
    }
    @Test
    public void test_fastMatch(){
        log("Hello?2");
        log(ComponentFormatParser.TYPE_QUICK_MATCHER);
        String t1 = "ENTITY:ababab";
        int code = 0;
        int index = 0;
//        do{
//            code = ComponentTokenizer.TYPE_QUICK_MATCHER.checkMatchStatus(code,t1.charAt(index));
//            log(code+"  "+ t1.charAt(index));
//
//            index ++;
//        }while (code > 0);
    }
    @Test
    public void test_tokenization(){
        log("Hello?");
        String t1 = "Hello {entity:ababab}, your score is {0}! && &aGreen{hover: cdcdcd} &cRed §#FF0000 &x&6&6&6&6&6&6CustomColor {player_placeholder} is here. translate test{translatable:me.matl114.test.message1$fallback}";
//        String t1 = "&x&E&B&3&3&E&B链接的坐标: &f";
        ComponentAST ast0 = ComponentFormatParser.compile(t1);
//        log(ComponentFormatParser.tokenize(t1,new PairList<>()));
//        long a1 = System.nanoTime();
//        for (int i=0;i<100;++i){
//            ComponentAST ast = ComponentFormatParser.compile(t1);
//
//            var re =  ast.build(BuildContent.of(new DefaultPlaceholderProviderImpl()));
//        }
//        long a2 = System.nanoTime();
//        log("time "+(a2-a1));
        ComponentAST ast = ComponentFormatParser.compile(t1);
                var output = new StringBuilder();
        ast.walk(output);
        log(output.toString());
        var re =  ast.build(BuildContent.of(new DefaultPlaceholderProviderImpl()));
        re.build(Parameter.wrap("666"));
        var param = Parameter.wrap("666");
        long a3 = System.nanoTime();
        for (int i=0;i<10000;++i){
            TextComponent cp = (TextComponent) re.build(param);
        }
        long a4 = System.nanoTime();
        log("time "+(a4-a3));
    }
    @Test
    public void test_class(){
        Class[][] a = new Class[0][0];
        log(a.getClass().getName());
        log(ByteCodeUtils.toJvmType(a.getClass()));
    }

}


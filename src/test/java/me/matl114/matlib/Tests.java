package me.matl114.matlib;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Algorithm.Triplet;
import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import me.matl114.matlib.Utils.Command.CommandGroup.SubCommand;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Tests {
    public void log(Object message) {
        System.out.println("Test: "+message);
    }
    AddonInitialization testMockAddon=new AddonInitialization(null,"Test").onEnable();
    private static final FieldAccess testAccess = FieldAccess.ofName(TestClass.class, "d");
    private static final VarHandle testHandle = testAccess.getVarHandleOrDefault(()->null);
    //@Test
    public void test_reflection() {
        if(true){
            return;
        }
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
    //@Test
    public void test_reflection_2(){
        if(true){
            return;
        }
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
    //@Test
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
    }
    //@Test
    public void test_pair(){
        if(true){
            return;
        }
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
        Object object = new TestClass();
        log("Test Handle");
        log(testAccess);
        log(testHandle);
        testHandle.set(object,object);
        log(testHandle.get(object));
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

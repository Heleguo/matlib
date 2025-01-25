package me.matl114.matlib;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Algorithm.Triplet;
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
    @Test
    public void test_base64(){
        String value =new String(Base64Coder.decodeLines("rO0ABXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGph\n" +
                "dmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAk\n" +
                "U2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAkwABGtleXN0ABJMamF2YS9sYW5nL09iamVjdDtMAAZ2\n" +
                "YWx1ZXNxAH4ABHhwdXIAE1tMamF2YS5sYW5nLk9iamVjdDuQzlifEHMpbAIAAHhwAAAABHQAAj09\n" +
                "dAABdnQABHR5cGV0AARtZXRhdXEAfgAGAAAABHQAHm9yZy5idWtraXQuaW52ZW50b3J5Lkl0ZW1T\n" +
                "dGFja3NyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5n\n" +
                "Lk51bWJlcoaslR0LlOCLAgAAeHAAAA2JdAAFUEFQRVJzcQB+AABzcQB+AAN1cQB+AAYAAAAFcQB+\n" +
                "AAh0AAltZXRhLXR5cGV0AAxkaXNwbGF5LW5hbWV0AARsb3JldAASUHVibGljQnVra2l0VmFsdWVz\n" +
                "dXEAfgAGAAAABXQACEl0ZW1NZXRhdAAKVU5TUEVDSUZJQ3QAmnsiZXh0cmEiOlt7ImJvbGQiOmZh\n" +
                "bHNlLCJpdGFsaWMiOmZhbHNlLCJ1bmRlcmxpbmVkIjpmYWxzZSwic3RyaWtldGhyb3VnaCI6ZmFs\n" +
                "c2UsIm9iZnVzY2F0ZWQiOmZhbHNlLCJjb2xvciI6IiNFQjMzRUIiLCJ0ZXh0Ijoi6Jav54mH5ZCI\n" +
                "6YeR5p2/In1dLCJ0ZXh0IjoiIn1zcgA2Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                "bGVMaXN0JFNlcmlhbGl6ZWRGb3JtAAAAAAAAAAACAAFbAAhlbGVtZW50c3QAE1tMamF2YS9sYW5n\n" +
                "L09iamVjdDt4cHVxAH4ABgAAAAJ0AJ17ImV4dHJhIjpbeyJib2xkIjpmYWxzZSwiaXRhbGljIjpm\n" +
                "YWxzZSwidW5kZXJsaW5lZCI6ZmFsc2UsInN0cmlrZXRocm91Z2giOmZhbHNlLCJvYmZ1c2NhdGVk\n" +
                "IjpmYWxzZSwiY29sb3IiOiJncmF5IiwidGV4dCI6IuespuWQiOmAu+i+keeahOmFjeaWuSJ9XSwi\n" +
                "dGV4dCI6IiJ9dACveyJleHRyYSI6W3siYm9sZCI6ZmFsc2UsIml0YWxpYyI6ZmFsc2UsInVuZGVy\n" +
                "bGluZWQiOmZhbHNlLCJzdHJpa2V0aHJvdWdoIjpmYWxzZSwib2JmdXNjYXRlZCI6ZmFsc2UsImNv\n" +
                "bG9yIjoiZ3JheSIsInRleHQiOiLorqnku5blub/ms5vnlKjkuo7lkITnp43pgLvovpHmnLrlmajk\n" +
                "uK0ifV0sInRleHQiOiIifXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZh\n" +
                "Y3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAAWc2xpbWVmdW46c2xpbWVmdW5f\n" +
                "aXRlbXQAD0xPR0lURUNIX0xQTEFURXg=\n"));
        log(value);
    }
    @Test
    public void test_varHandle(){
        Object object = new TestClass();
        log("Test Handle");
        log(testAccess);
        log(testHandle);
        testHandle.set(object,object);
        log(testHandle.get(object));
    }

}

package me.matl114.matlib;

import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Algorithm.Triplet;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.Supplier;

public class Tests {
    public void log(Object message) {
        System.out.println("Test: "+message);
    }
    AddonInitialization testMockAddon=new AddonInitialization(null,"Test").onEnable();

    //@Test
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
    //@Test
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
        int value= new InitializeSafeProvider<>(Integer.class,()->6).v();
        log(value);
    }

}

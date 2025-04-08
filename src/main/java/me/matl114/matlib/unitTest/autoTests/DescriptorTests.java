package me.matl114.matlib.unitTest.autoTests;

import com.google.errorprone.annotations.Var;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.demo.DemoLoad;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.ReflectUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Random;

public class DescriptorTests implements TestCase {
    volatile int tmp = 0;

    //@OnlineTest(name = "Descriptor Build Test")
    public void test_descriptor() throws Throwable{
        Class<?> clazz =  DemoLoad.initDemo();;
        Field field = clazz.getDeclaredField("d");
        field.setAccessible(true);
        Debug.logger(field.get(null));
        VarHandle handle = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflectVarHandle(field);
        Debug.logger(handle.get());
        DemoDescriptor I = DescriptorImplBuilder.createHelperImpl( DemoDescriptor.class);
        Debug.logger(I);
        I.c();
        Debug.logger(I.dSetter(333));
        Debug.logger(I.newInstance());
        Debug.logger(I.newInstance(114));
//        Assert(int.class.isAssignableFrom(int.class));
//        Object t = new DemoTargetClass();
//        DemoTargetClass answer = (DemoTargetClass) t;
//        DemoDescriptor I = DescriptorImplBuilder.createHelperImplAt(t.getClass(), DemoDescriptor.class);
//        Debug.logger(I);
//        Debug.logger(I.newInstance(114));
//
//        Debug.logger("Starting invocation tests");
//        Assert(I.getTargetClass() == DemoTargetClass.class);
//        //fields
//        Assert(I.aGetter(t) == 114);
//        I.bSetter(t, 10);
//        Assert(answer.b == 10);
//        Assert(I.cccGetter(t) == 514);
//        I.dSetter(Float.valueOf(3.15f));
//        Assert(DemoTargetClass.d == 3.15f);
//        answer.e = answer;
//        Assert(I.eGetter(t) == t);
//        I.fSetter(t, "cnmd");
//        AssertEq("cnmd",answer.f);
//        AssertEq(I.gGetter(t) ,"www");
//        AssertEq(I.hGetter(t), "http");
//        Assert(I.iGetter() == -114);
//        //methods
//        Assert(I.a(t) == 0);
//        I.b(t);
//        I.c();
//        Assert(I.d(t).length == 0);
//        Assert(I.e(t) == null);
//        I.f(t);
//        I.g(t);
//        I.newInstance();
//        int start = new Random().nextInt();
//        //test invocation cost;
//        long a = System.nanoTime();
//        for (int i=0; i< 1_000_000; ++i){
//            I.bSetter(t, i + start);
//            //using a volatile field to stop it from optimizing for-loop
//            tmp = i;
//        }
//        long b = System.nanoTime();
//        Assert(answer.b  == 999_999+ start);
//        Debug.logger("Field set cost",b-a);
//        a = System.nanoTime();
//        for (int i=0; i< 1_000_000; ++i){
//            answer.b = i + start;
//            tmp = i;
//        }
//        b = System.nanoTime();
//        Assert(answer.b == 999_999 + start);
//        Debug.logger("Field direct set cost",b-a);
//        a = System.nanoTime();
//        for (int i=0; i<1_000_000; ++i){
//            I.aGetter(t);
//            tmp = i;
//        }
//        b = System.nanoTime();
//        Debug.logger("Private field get cost ",b-a);
//        Debug.logger(ReflectUtils.getAllFieldsRecursively(I.getClass()).get(2).get(null).getClass());
//        Field f = this.getClass().getDeclaredField("first");
//        VarHandle handle = MethodHandles.privateLookupIn(this.getClass(), MethodHandles.lookup()).unreflectVarHandle(f);
//        Debug.logger(handle.getClass());
//        Debug.logger(handle.withInvokeExactBehavior().getClass());
    }
    private boolean first;





    @Descriptive(target = "me.matl114.matlib.unitTest.demo.DemoTargetClass")
    public static interface DemoDescriptor extends TargetDescriptor {
        //field tests
        @Note("test get, test RedirectType")
        @FieldTarget
        @RedirectType("I")
        public int aGetter(Object v);
        @Note("test set")
        @FieldTarget
        public void bSetter(Object v, int val);
        @Note("test more primitive, test redirect name")
        @FieldTarget
        @RedirectName("cGetter")
        public long cccGetter(Object v);
        @Note("test object default return, test primitive cast, test static fields")
        @FieldTarget(isStatic = true)
        public Object dSetter(float v);
        @Note("test Object return value")
        @FieldTarget
        public Object eGetter(Object v);
        @Note("test int default return, test value cast")
        @FieldTarget
        public int fSetter(Object v, Object str);
        @Note("test super field")
        @FieldTarget
        public String gGetter(Object v);
        @Note("test super static field, test argument passed")
        @FieldTarget(isStatic = true)
        public String hGetter(Object v);
        @Note("test interface static field")
        @FieldTarget(isStatic = true)
        public int iGetter();
        // method tests

        @Note("test method, test type match")
        @MethodTarget
        @RedirectType("V")
        public int a(Object v);

        @Note("test private method")
        @MethodTarget
        public void b(Object v);

        @Note("test static method")
        @MethodTarget(isStatic = true)
        public void c();

        @Note("test return value cast")
        @MethodTarget
        @RedirectType("Ljava/lang/Object")
        public byte[] d(Object v);

        @Note("test thrown error")
        @MethodTarget
        public Object e(Object v);

        @Note("test interface default method")
        @MethodTarget
        public void f(Object e);

        @Note("test super method")
        @MethodTarget
        public void g(Object g);

        //constructor tests;
        @ConstructorTarget()
        public Object newInstance();

        @ConstructorTarget
        public Object newInstance(int x);
    }
}

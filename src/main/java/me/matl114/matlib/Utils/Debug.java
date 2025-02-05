package me.matl114.matlib.Utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Debug {
    public static void init(String name){
        log= Logger.getLogger(name);

    }
    public  static Logger log ;
    public static Logger testlog=Logger.getLogger("TEST");
    public static boolean start=false;
    public static boolean pos=false;
    @Getter
    @Setter
    private static boolean debugMod=false;
    public static AtomicBoolean[] breakPoints=null;
    public static Object[] breakValues=null;
    public static AtomicBoolean[] setValues=null;
    public static byte[] lock=new byte[0];
    public static AtomicBoolean getBreakPoint(int i){
        if(breakPoints==null){
            breakPoints=new AtomicBoolean[80];
            breakValues=new Object[80];
            setValues=new AtomicBoolean[80];
            for(int j=0;j<80;j++){
                breakPoints[j]=new AtomicBoolean(false);
                setValues[j]=new AtomicBoolean(false);
            }
        }

        return breakPoints[i];
    }
    public static void debug(Object debug){
        if(debugMod){
            logger(debug);
        }
    }
    public static void debug(Object... debug){
        if(debugMod){
            logger(debug);
        }
    }
    public static void setBreakPoint(int i, boolean b){
        synchronized(lock){
            AtomicBoolean boo= getBreakPoint(i);
            setValues[i].set(false);
            breakValues[i]=null;
            breakPoints[i].set(b);
        }
    }
    public static Object getBreakValue(int i){
        synchronized(lock){
            return breakValues[i];
        }
    }
    public static boolean getHasSetValue(int i){
        synchronized(lock){
            return setValues[i].get();
        }
    }
    public static void setBreakValue(int i,Object value){
        synchronized(lock){
            breakValues[i]=value;
            setValues[i].set(true);
        }
    }

    public static  void logger(String message) {
        log.info(s(message));
    }
    public static void warn(String... message) {
        log.warning(s(message));
    }
    public static void severe(String... message) {
        log.severe(s(message));
    }
    public static  void logger(int message) {
        logger(Integer.toString(message));
    }
    private static String s(Object o) {
        return o!=null?o.toString():"null";
    }

    public static void logger(Object ... msgs){
        String msg="";
        logger(String.join(" ", Arrays.stream(msgs).map(Debug::s).toArray(String[]::new)));
    }
    public static void logger(Throwable t) {

        t.printStackTrace();

    }
    public static void logger(Supplier<String> str){
        logger(s(str.get()));
    }

    public static void test(Object ...msgs) {
        if(start){
            logger(msgs);
        }
    }
    public static void stackTrace(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for(StackTraceElement stackTraceElement : stackTraceElements) {
            Debug.logger(stackTraceElement.toString());
        }
    }

}

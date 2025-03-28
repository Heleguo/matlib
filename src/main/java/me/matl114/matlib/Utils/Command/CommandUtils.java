package me.matl114.matlib.Utils.Command;

import me.matl114.matlib.Utils.Command.Interruption.TypeError;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandUtils {
    public static String getOrDefault(String[] args,int index,String defaultValue){
        return args.length>index?args[index]:defaultValue;
    }
    public static int parseIntOrDefault(String value,int defaultValue){
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
    public static Integer parseIntegerOrDefault(String value,Integer defaultValue){
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
    public static double parseDoubleOrDefault(String value, double defaultValue){
        try{
            return Double.parseDouble(value);
        }catch (Throwable e){
            return defaultValue;
        }
    }
    public static int validRange(int value,int min,int max){
        return Math.max( Math.min(max,value),min);
    }
    public static Map<String,String> parseArguments(String[] args, SimpleCommandArgs.Argument[] requiredDefault){
        Map<String,String> arguments = new HashMap<>();
        var iter = Arrays.stream(args).iterator();
        var argIter =Arrays.stream(requiredDefault).iterator();
        while(iter.hasNext()){
            String arg=iter.next();
            if(arg.startsWith("-")){
                SimpleCommandArgs.Argument selected=null;
                String trueName = arg.replaceFirst("^-+","");
                for(SimpleCommandArgs.Argument a:requiredDefault){
                    if(a.isAlias(trueName)){
                        trueName = a.getArgsName();
                        break;
                    }
                }
                if(arg.startsWith("--")){
                    // --args inputValue
                    if(iter.hasNext()){
                        String arg2=iter.next();

                        arguments.put(trueName,arg2);
                    }else {
                        //ignored
                    }
                }else{
                    //-f -v means boolean
                    arguments.put(trueName,"true");
                }
            }
            else {
                SimpleCommandArgs.Argument arg1 = null;
                while (argIter.hasNext() && arguments.containsKey((arg1 = argIter.next()).getArgsName())){
                    //find next argument which is not already collected
                }
                if(arg1 != null){
                    arguments.put(arg1.getArgsName(), arg);
                }else{
                    //no more argument in list, but still --args -flag should be collected, so no break here
                }
            }
        }
        while (argIter.hasNext()){
            var re =argIter.next();
            arguments.putIfAbsent(re.getArgsName(), re.getDefaultValue());
        }
        return arguments;
    }
    public static int gint(String val,@Nullable SimpleCommandArgs.Argument arg){
        try{
            return Integer.parseInt(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.INT, val);
        }
    }
    public static float gfloat(String val,@Nullable SimpleCommandArgs.Argument arg){
        try{
            return Float.parseFloat(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.FLOAT, val);
        }
    }

    public static double gdouble(String val,@Nullable SimpleCommandArgs.Argument arg){
        try{
            return Double.parseDouble(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.FLOAT, val);
        }
    }
    public static boolean gbool(String val,@Nullable SimpleCommandArgs.Argument arg){
        switch (val){
            case "true": return true;
            case "false": return false;
            default: throw new TypeError(arg, TypeError.BaseArgumentType.BOOLEAN, val);
        }
    }
    public static int gint(String val,@Nullable String arg){
        try{
            return Integer.parseInt(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.INT, val);
        }
    }
    public static float gfloat(String val,@Nullable String arg){
        try{
            return Float.parseFloat(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.FLOAT, val);
        }
    }

    public static double gdouble(String val,@Nullable String arg){
        try{
            return Double.parseDouble(val);
        }catch (Throwable e){
            throw new TypeError(arg, TypeError.BaseArgumentType.FLOAT, val);
        }
    }
    public static boolean gbool(String val,@Nullable String arg){
        switch (val){
            case "true": return true;
            case "false": return false;
            default: throw new TypeError(arg, TypeError.BaseArgumentType.BOOLEAN, val);
        }
    }
}

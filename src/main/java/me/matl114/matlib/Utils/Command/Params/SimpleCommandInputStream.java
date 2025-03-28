package me.matl114.matlib.Utils.Command.Params;

import lombok.AllArgsConstructor;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Pair;
import me.matl114.matlib.Utils.Command.CommandUtils;
import me.matl114.matlib.Utils.Command.Interruption.TypeError;
import me.matl114.matlib.Utils.Command.Interruption.ValueAbsentError;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleCommandInputStream {
    public SimpleCommandInputStream(SimpleCommandArgs.Argument[] args, Map<SimpleCommandArgs.Argument,String> argsMap){
        this.arguments = args;
        this.argsMap = argsMap;
    }
    SimpleCommandArgs.Argument[] arguments;
    Map<SimpleCommandArgs.Argument, String> argsMap;
    int i = 0;
    public boolean hasNext(){
        return i < arguments.length;
    }
    public SimpleCommandArgs.Argument nextArgument(){
        return arguments[i++];
    }
    
    public String nextArg() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            return argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    
    public int nextInt() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            String val = argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);
            if(val == null){
                throw new ValueAbsentError(arg);
            }
            return CommandUtils.gint(val, arg);
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    
    public boolean nextBoolean() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            String val = argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);
            if(val == null){
                throw new ValueAbsentError(arg);
            }
            return CommandUtils.gbool(val, arg);
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    
    public double nextDouble() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            String val = argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);
            if(val == null){
                throw new ValueAbsentError(arg);
            }
            return CommandUtils.gdouble(val, arg);
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    
    public float nextFloat() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            String val = argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);
            if(val == null){
                throw new ValueAbsentError(arg);
            }
            return CommandUtils.gfloat(val, arg);
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    
    public String nextNonnull() {
        if(hasNext()){
            SimpleCommandArgs.Argument arg = nextArgument();
            String val = argsMap.computeIfAbsent(arg, SimpleCommandArgs.Argument::getDefaultValue);

            if(val == null){
                throw new ValueAbsentError(arg);
            }
            return val;
        }else {
            throw new RuntimeException("Illegal to access undeclared argument");
        }
    }

    @Nullable
    
    public List<String> getTabComplete(){
        for(int i=0;i<=arguments.length;i++){
            if(i==arguments.length ||argsMap.get(arguments[i])==null){
                if(i==0){
                    return null;
                }
                final int index=i-1;
                List<String> tablist=arguments[index].tabCompletor.get();
                tablist=tablist==null?List.of():tablist;
                return tablist.stream().filter(s->s.contains(argsMap.get(arguments[index]))).toList();
            }
        }
        return null;
    }
}

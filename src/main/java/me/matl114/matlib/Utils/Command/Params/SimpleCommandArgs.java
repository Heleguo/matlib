package me.matl114.matlib.Utils.Command.Params;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Debug;

import java.util.*;
import java.util.function.Supplier;

public class SimpleCommandArgs {
    public static class Argument implements TabProvider{
        @Getter
        private final String argsName;
        public HashSet<String> argsAlias;
        @Getter
        @Setter
        private String defaultValue=null;
        public Supplier<List<String>> tabCompletor=null;
        public Argument(String argsName){
            this.argsName = argsName;
            this.argsAlias = new HashSet<>();
            argsAlias.add(argsName);
            argsAlias.add(argsName.toLowerCase());
            argsAlias.add(argsName.toUpperCase());
            argsAlias.add(argsName.substring(0,1));
            argsAlias.add(argsName.substring(0, 1).toLowerCase());
            argsAlias.add(argsName.substring(0, 1).toUpperCase());
        }
        public boolean isAlias(String arg){
            return argsAlias.contains(arg);
        }
        public List<String> getTab(){
            return tabCompletor.get();
        }
    }
    Argument[] args;
    public SimpleCommandArgs(String... args){
        this.args= Arrays.stream(args).map(Argument::new).toArray(Argument[]::new);
    }
    public void setDefault(String arg,String defaultValue){
        for(Argument a : args){
            if(a.argsName.equals(arg)){
                a.defaultValue=defaultValue;
            }
        }
    }
    public void setTabCompletor(String arg,Supplier<List<String>> tabCompletor){
        for(Argument a : args){
            if(a.argsName.equals(arg)){
                a.tabCompletor=tabCompletor;
            }
        }
    }
    public Pair<SimpleCommandInputStream,String[]> parseInputStream(String[] input){
        List<String> commonArgs=new ArrayList<>();
        final HashMap<Argument,String> argsMap=new HashMap<>();
        Iterator<String > iter= Arrays.stream(input).iterator();
        while(iter.hasNext()){
            String arg=iter.next();
            if(arg.startsWith("-")){
                Argument selected=null;
                String trueName = arg.replaceFirst("^-+","");
                for(Argument a:args){
                    if(a.isAlias(trueName)){
                        selected=a;
                        break;
                    }
                }
                if(selected!=null){
                    if(arg.startsWith("--")){
                        // --args inputValue
                        if(iter.hasNext()){
                            String arg2=iter.next();

                            argsMap.put(selected,arg2);
                        }
                    }else{
                        //-f -v means boolean
                        argsMap.put(selected,"true");
                    }

                }
                else {
                    //输入了一个无效参数 加入commonArgs
                    commonArgs.add(arg);
                }
            }
            else {
                commonArgs.add(arg);
            }
        }
        for(Argument a:args){
            if(!argsMap.containsKey(a)){
                if(!commonArgs.isEmpty()){
                    argsMap.put(a,commonArgs.remove(0));
                }
            }
        }
        return new Pair<>(new SimpleCommandInputStream() {
            Iterator<Argument> iter= Arrays.stream(args).iterator();
            @Override
            public String nextArg() {
                if(iter.hasNext()){
                    Argument a= iter.next();
                    return argsMap.computeIfAbsent(a,Argument::getDefaultValue);
                }else {
                    throw new RuntimeException("there is no next argument in your command definition!");
                }
            }
            public List<String> getTabComplete(){
                for(int i=0;i<=args.length;i++){
                    if(i==args.length ||argsMap.get(args[i])==null){
                        if(i==0){
                            return null;
                        }
                        final int index=i-1;
                        List<String> tablist=args[index].tabCompletor.get();
                        tablist=tablist==null?List.of():tablist;
                        return tablist.stream().filter(s->s.startsWith(argsMap.get(args[index]))).toList();
                    }
                }
                return null;
            }
        },commonArgs.toArray(String[]::new));
    }
}

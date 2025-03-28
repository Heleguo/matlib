package me.matl114.matlib.Utils.Command.CommandGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import lombok.Getter;
import me.matl114.matlib.Utils.Command.Interruption.ArgumentException;
import me.matl114.matlib.Utils.Command.Interruption.InterruptionHandler;
import me.matl114.matlib.Utils.Command.Interruption.TypeError;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractMainCommand implements ComplexCommandExecutor, InterruptionHandler {
    @Getter
    private LinkedHashSet<SubCommand> subCommands = new LinkedHashSet<>();
    private SubCommand mainInternal;
    private boolean registered = false;
    private Logger Debug;
    @Getter
    private Plugin plugin;
    void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    public <T extends AbstractMainCommand> T registerCommand(Plugin plugin){
        Preconditions.checkArgument(!registered, "Command have already been registered!");
        this.plugin = plugin;
        this.Debug = plugin.getLogger();
        plugin.getServer().getPluginCommand(getMainName()).setExecutor(this);
        plugin.getServer().getPluginCommand(getMainName()).setTabCompleter(this);
        this.registered=true;
        return (T)this;
    }
    public <T extends AbstractMainCommand> T unregisterCommand(){
        Preconditions.checkArgument(registered, "Command functional havem't been unregistered!");
        plugin.getServer().getPluginCommand(getMainName()).setExecutor(null);
        plugin.getServer().getPluginCommand(getMainName()).setTabCompleter(null);
        this.registered=false;
        return (T)this;
    }
    public SubCommand getSubCommand(String name) {
        for(SubCommand command:subCommands){
            if(command.getName().equalsIgnoreCase(name)){
                return command;
            }
        }return null;
    }
    public List<String> getDisplayedSubCommand(){
        return this.subCommands.stream().filter(SubCommand::isVisiable).map(SubCommand::getName).toList();
    }
    protected SubCommand genMainCommand(String name){
        return new SubCommand(name,genArgument("_operation"),"")
                .setTabCompletor("_operation",this::getDisplayedSubCommand);
    }
    @Override
    public void registerSub(SubCommand command) {
        this.subCommands.add(command);
    }
    private SubCommand getMainInternal() {
        if(mainInternal == null){
            try{
                Field field=this.getClass().getDeclaredField("mainCommand");
                field.setAccessible(true);
                mainInternal=(SubCommand)field.get(this);
            }catch (Throwable e){
                Debug.info("Error in "+this.getClass().getName()+": main Command Not Found");
                e.printStackTrace();
            }
        }
        return mainInternal;
    }
    public SubCommand getMainCommand() {
        return getMainInternal();
    }
    public String getMainName(){
        return getMainInternal().getName();
    }
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4){
        if(permissionRequired()==null|| var1.hasPermission(permissionRequired())){
            if(var4.length>=1){
                SubCommand command=getSubCommand(var4[0]);
                if(command != null ){
                    //add permission check
                    if( command.hasPermission(var1)){
                        String[] elseArg= Arrays.copyOfRange(var4,1,var4.length);
                        try{
                            return command.getExecutor().onCommand(var1,var2,var3,elseArg);
                        }catch (ArgumentException e){
                            e.handleAbort(var1, this);
                            return false;
                        }
                    }else {
                        noPermission(var1);
                        return false;
                    }

                }
            }
            showHelpCommand(var1);
        }else{
            noPermission(var1);
        }
        return true;
    }
    //todo add argumentException to quick interrupt command process


    public void handleTypeError(CommandSender sender, String argument, TypeError.BaseArgumentType type, String input){
        if(argument != null){
            sendMessage(sender, "&c类型错误:参数\""+ argument+"\"需要输入一个"+type.getDisplayNameZHCN()+",但是输入了:" + input);
        }else {
            sendMessage(sender, "&c类型错误: 需要输入一个" + type.getDisplayNameZHCN()+",但是输入了:" + input);
        }
    }
    public void handleValueAbsent(CommandSender sender, String argument){
        sendMessage(sender, "&c值缺失: 并未输入参数\"" + argument + "\"的值");
    }
    public void noPermission(CommandSender var1){
        sendMessage(var1,"&c你没有权限使用该指令!");
    }
    public abstract String permissionRequired();

    public void showHelpCommand(CommandSender sender){
        sendMessage(sender,"&a/%s 全部指令大全".formatted(getMainName()));
        for(SubCommand cmd:subCommands){
            for (String help:cmd.getHelp()){
                sendMessage(sender,"&a"+help);
            }
        }
    }
    public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4){
        //add permission check
        if(permissionRequired()==null|| var1.hasPermission(permissionRequired())){
            var re=getMainCommand().parseInput(var4);
            if(re.getB().length==0){
                List<String> provider=re.getA().getTabComplete();
                return provider==null?new ArrayList<>():provider;
            }else{
                SubCommand subCommand= getSubCommand(re.getA().nextArg());
                if(subCommand!=null && subCommand.hasPermission(var1)){
                    String[] elseArg=re.getB();
                    return subCommand.onTabComplete(var1,var2,var3,elseArg);
                }
            }
        }
        return new ArrayList<>();
    }
    public Player isPlayer(CommandSender sender, boolean sendMessage){
        if(sender instanceof Player player){
            return player;
        }else {
            if(sendMessage){
                sendMessage(sender,"&c该指令只能在游戏内执行!");
            }
            return null;
        }
    }
    public static SimpleCommandArgs genArgument(String... args){
        return new SimpleCommandArgs(args);
    }
    public static Supplier<List<String>> numberSupplier(){
        return ()->List.of("0","1","16","114514","2147483647");
    }
    public static Supplier<List<String>> floatSupplier(){
        return ()->List.of("0.0","1.0","3.14159","1.57079","6.283185");
    }
    public Supplier<List<String>> subCommandsSupplier(){
        return this::getDisplayedSubCommand;
    }
    public static Supplier<List<String>> playerNameSupplier(){
        return ()-> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
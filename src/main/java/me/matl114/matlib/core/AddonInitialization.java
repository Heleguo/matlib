package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Implements.Managers.BlockDataCache;
import me.matl114.matlib.Implements.Managers.ScheduleManager;
import me.matl114.matlib.Implements.Slimefun.core.CustomRegistries;
import me.matl114.matlib.Utils.Algorithm.InitializeSafeProvider;
import me.matl114.matlib.Utils.Slimefun.SlimefunSetup;
import org.bukkit.plugin.Plugin;

public class AddonInitialization {
    private Plugin plugin = null;
    private String name = null;
    private String displayName = null;
    private boolean testMode = false;
    UtilInitialization util = null;
    @Getter
    private EnvironmentManager environment=null;
    @Getter
    private BlockDataCache dataManager=null;
    @Getter
    private CustomRegistries registries=null;
    @Getter
    private ScheduleManager scheduleManager=null;
    @Getter
    private static final boolean hookSlimefun = new InitializeSafeProvider<>(Boolean.class,()->{
        try{
            return io.github.thebusybiscuit.slimefun4.implementation.Slimefun.instance()!=null;
        }catch (Throwable e){
            return false;
        }
    }).v();
    /**
     * load as a Slimefun addon helper
     * @param plugin
     * @param addonName
     */
    public AddonInitialization(Plugin plugin,String addonName) {
        this.plugin = plugin;
        this.name = addonName;


    }
    public AddonInitialization displayName(String displayName){
        this.displayName = displayName;
        return this;
    }
    public AddonInitialization testMode(boolean testMode){
        this.testMode = testMode;
        return this;
    }
    public AddonInitialization onEnable(){
        this.util = new UtilInitialization(plugin,name).displayName(this.displayName).testMode(this.testMode).onEnable();
        this.environment = this.util.getEnvironment();

        if(plugin!=null){
            this.scheduleManager=new ScheduleManager().init(plugin);
            if(hookSlimefun){
                //inject hooks
                SlimefunSetup.init();
                //core
                this.registries=new CustomRegistries().init(plugin);
                //datas
                this.dataManager=new BlockDataCache().init(plugin);
            }

            //tasks
        }
        return this;
    }
    public AddonInitialization onDisable(){
        Manager.onDisable();
        return this;
    }
}

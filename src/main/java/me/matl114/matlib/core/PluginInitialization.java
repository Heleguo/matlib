package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Implements.Managers.ScheduleManager;
import org.bukkit.plugin.Plugin;

public class PluginInitialization extends UtilInitialization{

    @Getter
    private ScheduleManager scheduleManager=null;
    public PluginInitialization(Plugin plugin,String pluginName) {
        super(plugin, pluginName);
    }
    public PluginInitialization onEnable(){
        super.onEnable();
        if(this.plugin !=null){
            this.scheduleManager=new ScheduleManager().init(plugin);
        }
        return this;
    }
}

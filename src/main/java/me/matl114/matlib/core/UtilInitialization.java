package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Utils.ConfigLoader;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.PluginUtils;
import me.matl114.matlibAdaptor.Algorithms.Interfaces.Initialization;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class UtilInitialization implements Initialization {
    protected final Plugin plugin;

    protected final String name;
    @Getter
    protected String displayName;
    @Getter
    protected boolean testMode = false;
    @Getter
    protected EnvironmentManager environment;
    public UtilInitialization testMode(boolean testMode){
        this.testMode = testMode;
        return this;
    }
    public UtilInitialization displayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    /**
     * load as a Util lib
     * when entering from this entry ,final jar file may be smaller
     * @param loader
     * @param loaderName
     */
    public UtilInitialization(Plugin loader, String loaderName) {
        this.plugin = loader;
        this.name = loaderName;
    }
    public UtilInitialization onEnable(){
        Manager.onEnable();
        PluginUtils.init(this.plugin);
        Debug.init(name);
        Debug.setDebugMod(this.testMode);
        if(this.plugin!=null){
            ConfigLoader.init(plugin);
            this.environment=new EnvironmentManager().init(plugin);
            //AddUtils.init(name,displayName==null?name:displayName,plugin);
        }
//        else{
//            //in test
//        }
        return this;
    }
    public UtilInitialization onDisable(){
        Manager.onDisable();
        return this;
    }

    @Override
    public Logger getLogger() {
        return Debug.log;
    }


}

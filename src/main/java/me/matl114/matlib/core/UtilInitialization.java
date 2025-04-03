package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.ConfigLoader;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlibAdaptor.algorithms.interfaces.Initialization;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.logging.Logger;

@Note("Manage class marked as @AutoInit(level = \"Util\")")
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
        try{
            Method init = Debug.class.getDeclaredMethod("init",String.class);
            init.setAccessible(true);
            init.invoke(null, name);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
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
        return Debug.getLog();
    }


}

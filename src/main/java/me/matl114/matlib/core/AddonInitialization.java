package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Implements.Managers.BlockDataCache;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.PersistentDataContainer.PdcTypes;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

public class AddonInitialization {
    public Plugin plugin = null;
    public String name = null;
    public String displayName = null;
    @Getter
    BlockDataCache dataManager=null;
    public AddonInitialization(Plugin plugin,String addonName) {
        this.plugin = plugin;
        this.name = addonName;


    }
    public AddonInitialization displayName(String displayName){
        this.displayName = displayName;
        return this;
    }
    public AddonInitialization onEnable(){
        Manager.onEnable();
        Debug.init(name);
        AddUtils.init(name,displayName==null?name:displayName,plugin);
        PdcTypes.init();

        this.dataManager=new BlockDataCache();
        return this;
    }
    public AddonInitialization onDisable(){
        Manager.onDisable();
    }
}

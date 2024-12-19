package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Implements.Managers.BlockDataCache;
import me.matl114.matlib.Implements.Slimefun.core.CustomRegistries;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.ConfigLoader;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.PersistentDataContainer.PdcTypes;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

public class AddonInitialization {
    private Plugin plugin = null;
    private String name = null;
    private String displayName = null;
    @Getter
    private BlockDataCache dataManager=null;
    @Getter
    private CustomRegistries registries=null;
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
        ConfigLoader.init(plugin);
        AddUtils.init(name,displayName==null?name:displayName,plugin);
        PdcTypes.init();
        //core
        this.registries=new CustomRegistries().init(plugin);
        //datas
        this.dataManager=new BlockDataCache().init(plugin);

        return this;
    }
    public AddonInitialization onDisable(){
        Manager.onDisable();
        return this;
    }
}

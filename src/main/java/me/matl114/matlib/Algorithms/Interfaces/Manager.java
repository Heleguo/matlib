package me.matl114.matlib.Algorithms.Interfaces;

import org.bukkit.plugin.Plugin;

import java.util.HashSet;

public interface Manager {
    static HashSet<Manager> managers = new HashSet<>();
    static void onEnable(){
        managers.clear();
    }
    static void onDisable(){
        new HashSet<>(managers).forEach(Manager::deconstruct);
        managers.clear();
    }
    default Manager addToRegistry() {
        //must called in init
        managers.add(this);
        return this;
    }
    default Manager removeFromRegistry() {
        //must called in deconstruct
        managers.remove(this);
        return this;
    }
    public <T extends Manager> T init(Plugin pl, String... path);
    public <T extends Manager> T reload();
    public void deconstruct();
}

package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Version.Version;
import me.matl114.matlib.Utils.Version.VersionedFeature;
import org.bukkit.plugin.Plugin;

public class EnvironmentManager implements Manager {
    private Plugin plugin;
    @Getter
    private static EnvironmentManager manager;
    public EnvironmentManager() {
        manager = this;
    }
    @Override
    public EnvironmentManager init(Plugin pl, String... path) {
        this.plugin = pl;
        Debug.logger("Initializing environment manager...");
        versioned= Version.getVersionInstance().getFeature();
        this.addToRegistry();
        return this;
    }
    @Override
    public EnvironmentManager reload() {
        deconstruct();
        return init(plugin);
    }
    @Override
    public void deconstruct() {
        this.removeFromRegistry();
    }
    @Getter
    private VersionedFeature versioned;

}

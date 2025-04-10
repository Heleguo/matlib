package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionedFeature;
import org.bukkit.plugin.Plugin;

@AutoInit(level = "Util")
public class EnvironmentManager implements Manager {
    private Plugin plugin;
    @Getter
    private static EnvironmentManager manager;
    @Getter
    private Version version;
    public EnvironmentManager() {
        manager = this;
    }
    @Override
    public EnvironmentManager init(Plugin pl, String... path) {
        this.plugin = pl;
        Debug.logger("Initializing environment manager...");
        version = Version.getVersionInstance();
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

    private VersionedFeature versioned;
    public VersionedFeature getVersioned() {
        //lazily init
        if (versioned == null) {
            versioned= VersionedFeature.getFeature();
        }
        return versioned;
    }
}

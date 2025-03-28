package me.matl114.matlib.Utils.Entity.Groups.Implementation;

import me.matl114.matlib.Utils.Entity.Groups.EntityGroup;
import me.matl114.matlib.Utils.Entity.Groups.EntityGroupManager;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleGroupManager<W extends EntityGroup<? extends Entity>> implements EntityGroupManager<W> {
    public SimpleGroupManager(Map<String, W> groups) {
        this.groups = groups;
    }
    public SimpleGroupManager() {
        this.groups = new HashMap<String, W>();
    }

    final Map<String,W> groups;
    @Override
    public void addGroup(String key, W group) {
        groups.put(key, group);
    }

    @Override
    public W getGroup(String key) {
        return groups.get(key);
    }

    @Override
    public W removeGroup(String key) {
        return groups.remove(key);
    }

    @Override
    public Iterator<W> getGroups() {
        return groups.values().iterator();
    }

    @Override
    public EntityGroupManager<W> init(Plugin pl, String... path) {
        return this;
    }

    @Override
    public EntityGroupManager<W> reload() {
        return this;
    }
}

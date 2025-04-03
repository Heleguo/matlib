package me.matl114.matlib.utils.entity.groups.implement;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.utils.entity.groups.EntityGroup;
import me.matl114.matlib.utils.entity.groups.EntityGroupManager;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SingleGroupManager<W extends EntityGroup<? extends Entity>> implements EntityGroupManager<W> {
    public SingleGroupManager() {
        this.handle = null;
    }
    @Setter
    @Getter
    W handle;
    @Override
    public void addGroup(String key, W group) {
        handle = group;
    }

    @Override
    public W getGroup(String key) {
        return handle;
    }

    @Override
    public W removeGroup(String key) {
        W group = handle;
        handle = null;
        return group;
    }

    @Override
    public Iterator<W> getGroups() {
        Set<W> set  = new HashSet<W>();
        set.add(handle);
        return set.iterator();
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

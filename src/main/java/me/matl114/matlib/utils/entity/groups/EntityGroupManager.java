package me.matl114.matlib.utils.entity.groups;

import me.matl114.matlib.algorithms.algorithm.ThreadUtils;
import me.matl114.matlib.utils.entity.groups.implement.SimpleGroupManager;
import me.matl114.matlib.core.Manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface EntityGroupManager<T extends EntityGroup<?>> extends Manager {
    public void addGroup(String key,T group);
    public T getGroup(String key);
    public T removeGroup(String key);
    public Iterator<T> getGroups();
    default void deconstruct(){
        var groups = getGroups();
        while(groups.hasNext()){
            var group = groups.next();
            groups.remove();
            if(group.autoRemovalOnShutdown()){
                ThreadUtils.executeSync(group::killGroup);
            }
        }
    }
    public static <W extends EntityGroup<?>> EntityGroupManager<W> create(Class<W> clazz){
        Map<String,W> groups = new HashMap<>();
        return of(groups);
    }
    public static <W extends EntityGroup<?>> EntityGroupManager<W> of(Map<String,W> groups){
        return new SimpleGroupManager<>(groups);
    }

}

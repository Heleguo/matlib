package me.matl114.matlib.Utils.Entity.Display;

import me.matl114.matlib.Common.Lang.Annotations.ForceOnMainThread;
import me.matl114.matlib.Utils.Entity.Groups.EntityGroup;
import me.matl114.matlib.Utils.Entity.Groups.EntityGroupManager;
import org.bukkit.Location;
import org.bukkit.entity.*;

public interface DisplayManager extends EntityGroupManager<EntityGroup<Display>>, BuildableBluePrinted {
    @ForceOnMainThread
    public DisplayManager buildDisplay(Location location, EntityGroup.EntityGroupBuilder<Display> entityGroupCreator);
}

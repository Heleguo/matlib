package me.matl114.matlib.Utils.Entity.Groups.Implementation;

import lombok.Setter;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.Entity.EntityRecords.EntityRecord;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.function.Function;

public abstract class AbstractBlock2EntityGroup<T extends Entity> extends ListedEntityGroup<T> {
    Location blockPos ;
    public AbstractBlock2EntityGroup(Function<T, EntityRecord<T>> recorder, Location blockPos) {
        super(recorder);
        this.blockPos = blockPos.toBlockLocation();
    }

    @Override
    public String getGroupIdentifier() {
        return identifierOfBlockPos(blockPos);
    }
    public static String identifierOfBlockPos(Location blockPos) {
        return "Smp_"+ AddUtils.blockLocationToString(blockPos);
    }

    public boolean autoRemovalOnShutdown() {
        return false;
    }

    @Override
    public abstract void postAddSyncTask(String childName, T entityChild) ;
    @Override
    public abstract void postRemoveSyncTask(String childName, T entityChild);
}

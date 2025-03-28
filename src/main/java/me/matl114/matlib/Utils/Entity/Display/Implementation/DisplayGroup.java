package me.matl114.matlib.Utils.Entity.Display.Implementation;

import me.matl114.matlib.Algorithms.Algorithm.TransformationUtils;
import me.matl114.matlib.Utils.Entity.Display.BluePrinted;
import me.matl114.matlib.Utils.Entity.Display.BuildableBluePrinted;
import me.matl114.matlib.Utils.Entity.Display.DisplayManager;
import me.matl114.matlib.Utils.Entity.Display.TransformApplicable;
import me.matl114.matlib.Utils.Entity.Groups.EntityGroup;
import me.matl114.matlib.Utils.Entity.Groups.Implementation.FixedEntityGroup;
import me.matl114.matlib.Utils.Entity.Groups.Implementation.ListedEntityGroup;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Marker;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DisplayGroup extends FixedEntityGroup<Display,Marker> implements BuildableBluePrinted {
    public DisplayGroup(String namespace,@Nonnull Marker entityParent) {
        super(namespace, entityParent);
        this.partMap = new HashMap<>();
        this.coreLocation = entityParent.getLocation();
    }

    TransformationUtils.LCTransformation trans = TransformationUtils.LCTransformation.ofIdentical();
    Map<String, DisplayPart> partMap;
    Location coreLocation;
    @Override
    public Map<String, DisplayPart> getDisplayParts() {
        return partMap;
    }

    public DisplayGroup addDisplayPart(DisplayManager.DisplayPart part){
        partMap.put(part.partIdentifier, part);
        return this;
    }

    @Override
    public Location getCoreLocation() {
        return coreLocation;
    }

    @Override
    public void setCoreLocation(Location location) {
        var parent = getParent();
        if(parent != null){
            parent.setLocation(location);
            coreLocation = parent.getLocation();
        }else {
            coreLocation = location.clone();
        }
        updateLocation();
    }

    @Override
    public EntityGroup<Display> getDisplayGroup() {
        return this;
    }


    @Override
    public TransformationUtils.LCTransformation getCurrentTransformation() {
        return trans;
    }

    @Override
    public void setCurrentTransformation(TransformationUtils.LCTransformation transformation) {
        this.trans = transformation;
    }
}

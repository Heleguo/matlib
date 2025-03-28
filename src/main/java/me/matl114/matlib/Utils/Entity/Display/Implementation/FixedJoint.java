package me.matl114.matlib.Utils.Entity.Display.Implementation;

import me.matl114.matlib.Algorithms.Algorithm.TransformationUtils;
import me.matl114.matlib.Utils.Entity.Display.Joint;
import me.matl114.matlib.Utils.Entity.Display.RobotConfigure;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FixedJoint extends Joint {
    public FixedJoint(String id, Vector3f translation, Quaternionf fixedRotation){
        super(id, translation);
        this.fixedRotation = fixedRotation;

    }

    final Quaternionf fixedRotation;
    @Override
    public Quaternionf getRotation(RobotConfigure config) {
        return fixedRotation;
    }


}

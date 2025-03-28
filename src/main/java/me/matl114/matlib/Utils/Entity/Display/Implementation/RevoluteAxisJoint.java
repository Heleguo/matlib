package me.matl114.matlib.Utils.Entity.Display.Implementation;

import me.matl114.matlib.Algorithms.Algorithm.TransformationUtils;
import me.matl114.matlib.Utils.Entity.Display.Joint;
import me.matl114.matlib.Utils.Entity.Display.RobotConfigure;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RevoluteAxisJoint extends Joint {
    Vector3f axis;
    public RevoluteAxisJoint(String id, Vector3f bias, Vector3f axis) {
        super(id, bias);
        this.axis = axis;
    }
    @Override
    public Quaternionf getRotation(RobotConfigure config) {
        return TransformationUtils.fromAxisAngle(axis, (float) config.getRotationAngle(id));
    }
}

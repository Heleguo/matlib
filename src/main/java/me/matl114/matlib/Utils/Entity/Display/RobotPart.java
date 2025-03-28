package me.matl114.matlib.Utils.Entity.Display;

import me.matl114.matlib.Algorithms.Algorithm.TransformationUtils;
import me.matl114.matlib.Algorithms.DataStructures.Complex.Transformation.MatrixStack;
import me.matl114.matlib.Common.Lang.Annotations.ForceOnMainThread;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import org.bukkit.Location;

public interface RobotPart {
    String getId();
    @ForceOnMainThread
    @Note("this method requires location change of robot part, so it is forced on main thread")
    public void forwardKinematics(MatrixStack currentTransformation, RobotConfigure configure);
}

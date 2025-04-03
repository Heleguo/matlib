package me.matl114.matlib.utils.entity.display;

import me.matl114.matlib.algorithms.dataStructures.complex.transformation.MatrixStack;
import me.matl114.matlib.common.lang.Annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.Annotations.Note;

public interface RobotPart {
    String getId();
    @ForceOnMainThread
    @Note("this method requires location change of robot part, so it is forced on main thread")
    public void forwardKinematics(MatrixStack currentTransformation, RobotConfigure configure);
}

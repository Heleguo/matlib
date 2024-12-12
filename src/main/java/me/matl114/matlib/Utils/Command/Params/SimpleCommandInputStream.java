package me.matl114.matlib.Utils.Command.Params;

import javax.annotation.Nullable;
import java.util.List;

public interface SimpleCommandInputStream {
    String nextArg();
    @Nullable
    List<String> getTabComplete();
}

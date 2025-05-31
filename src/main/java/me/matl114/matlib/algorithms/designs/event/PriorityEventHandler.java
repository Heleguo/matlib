package me.matl114.matlib.algorithms.designs.event;

import lombok.AllArgsConstructor;
import lombok.ToString;
import me.matl114.matlib.common.lang.annotations.Note;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@AllArgsConstructor
@ToString
public class PriorityEventHandler<W, T extends Event> implements Comparable<PriorityEventHandler<?, T>>{
    final W owner;
    @Note("Priority 从低到高依次执行,同Priority,按注册顺序执行")
    final int priority;
    final boolean ignoreIfCancel;
    final Consumer<T> task;

    @Override
    public int compareTo(@NotNull PriorityEventHandler<?, T> o) {
        return Integer.compare(this.priority, o.priority);
    }
}

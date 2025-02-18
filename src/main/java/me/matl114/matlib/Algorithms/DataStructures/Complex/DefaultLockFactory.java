package me.matl114.matlib.Algorithms.DataStructures.Complex;

import me.matl114.matlibAdaptor.Algorithms.DataStructures.LockFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * no lock at all
 * make sure to run task sync~
 */
public class DefaultLockFactory<T extends Object> implements LockFactory<T> {

    @Override
    public void ensureLock(Runnable task, T... objs) {
        task.run();
    }

    @Override
    public <C> C ensureLock(Supplier<C> task, T... objs) {
        return task.get();
    }

    @Override
    public void asyncEnsureLock(Runnable task, T... objs) {
        task.run();
    }

    @Override
    public <C> FutureTask<C> ensureFuture(int delay, Callable<C> task, T... objs) {
        FutureTask<C> future = new FutureTask<>(task);
        future.run();
        return future;
    }

    @Override
    public boolean checkThreadStatus(T... objs) {
        return true;
    }
}

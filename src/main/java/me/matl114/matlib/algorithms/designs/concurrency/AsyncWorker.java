package me.matl114.matlib.algorithms.designs.concurrency;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncWorker implements Runnable, Executor {
    final ArrayBlockingQueue<Runnable> taskQueue;
    final int fixedSize;
    @Getter
    volatile boolean shutdown = true;
    public AsyncWorker(){
        this(10_000);
    }
    public AsyncWorker(int size){
        this.taskQueue = new ArrayBlockingQueue<>(size);
        this.fixedSize = size - 8;
    }
    @Override
    public void run() {
        Runnable task;
        while (true){
            try{
                task = this.taskQueue.take();
                //bugfix : should run StoppingSignalTask to complete future
                if(task instanceof StoppingSignalTask){
                    shutdown = true;
                    task.run();
                    return;
                }else {
                    task.run();
                }
            }catch (Throwable ignored){
            }
        }
    }

    private void submitTask(Runnable task){
        Preconditions.checkArgument(!shutdown,"This worker is in shutdown state and can not submit task anyMore");
        if(this.taskQueue.size() < fixedSize){
            //
            this.taskQueue.add(task);
        }else {
            //caller run policy
            task.run();
        }
    }

    @Override
    public void execute(@NotNull Runnable task) {
        submitTask(task);
    }

    public void stopWork(){
        submitTask(new StoppingSignalTask());
        this.shutdown = true;
    }
    public Future<Void> stopWorkFuture(){
        var signal = new StoppingSignalTask();
        this.shutdown = true;
        return signal;
    }

    public void waitForStopWork() throws Throwable{
        StoppingSignalTask task = new StoppingSignalTask();
        submitTask(task);
        task.get();
    }


    public void startWork(){
        startWork(ForkJoinPool.commonPool());
    }

    public void shutdown(){
        this.taskQueue.clear();
        try{
            waitForStopWork();
        }catch (Throwable e){
        }
    }

    public List<Runnable> shutdownNow(){
        List<Runnable> lst = new ArrayList<>();
        this.taskQueue.removeIf(r->{lst.add(r); return true;});
        try{
            waitForStopWork();
        }catch (Throwable e){
        }
        return lst;
    }

    public void startWork(Executor asyncExecutor){
        Preconditions.checkArgument(shutdown,"This worker is in running state and can not start Working now");
        this.shutdown = false;
        asyncExecutor.execute(this);
    }


    public static AsyncWorker bindToSingleThread(int size){
        return new AsyncWorker(size){
            final ExecutorService executor = Executors.newSingleThreadExecutor();

            @Override
            public void startWork() {
                startWork(executor);
            }

            @Override
            public void shutdown() {
                try {
                    super.shutdown();
                }finally {
                    this.executor.shutdown();
                }
            }
        };
    }



    public static class StoppingSignalTask extends FutureTask{
        public StoppingSignalTask() {
            super(()->null);
        }
    }
}

package me.matl114.matlib.algorithms.dataStructures.struct;

import me.matl114.matlib.common.functions.core.UnsafeBiFunction;
import me.matl114.matlib.common.functions.core.UnsafeFunction;

import javax.annotation.Nonnull;
import java.util.function.*;

class HolderExceptional<T> implements Holder<T>, Cloneable{
    final Throwable e;
    T value;
    boolean answered = false;
    HolderExceptional(T value, @Nonnull Throwable e){
        this.value = value;
        this.e = e;
    }
    @Override
    public <W> Holder<W> thenApply(Function<T, W> function) {
        return (Holder<W>) this;
    }

    @Override
    public Holder<T> thenRun(Runnable task) {
        return this;
    }


    @Override
    public Holder<T> thenPeek(Consumer<T> task) {
        return this;
    }

    @Override
    public <R> Holder<T> thenPeek(BiConsumer<T, R> task, R value) {
        return this;
    }

    @Override
    public <W> Holder<W> thenApplyUnsafe(UnsafeFunction<T, W> function) {
        return (Holder<W>) this;
    }

    @Override
    public <W> Holder<W> thenApplyCaught(UnsafeFunction<T, W> function) {
        return (Holder<W>) this;
    }

    @Override
    public <W, R> Holder<W> thenApply(BiFunction<T, R, W> function, R value) {
        return (Holder<W>) this;
    }

    @Override
    public <W, R> Holder<W> thenApplyCaught(UnsafeBiFunction<T, R, W> function, R value) {
        return (Holder<W>) this;
    }

    @Override
    public Holder<T> whenException(BiConsumer<T, Throwable> exceptionHandler) {
        if(!answered){
            exceptionHandler.accept(value, e);
            answered = true;
        }
        return this;
    }

    @Override
    public Holder<T> whenException(BiFunction<T, Throwable, T> exceptionHandler) {
        return this;
    }

    @Override
    public <W> Holder<W> whenComplete(BiFunction<T, Throwable, W> completeHandler) {
        return (Holder<W>) this;
    }

    @Override
    public Holder<T> whenNoException(Consumer<T> task) {
        return this;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public <W> Holder<W> cast() {
        return (Holder<W>) this;
    }

    @Override
    public <W> Holder<W> branch(Predicate<T> predicate, Function<T, W> func1, Function<T, W> func2) {
        return (Holder<W>) this;
    }

    @Override
    public Holder<T> failHard() {
        return this;
    }

    @Override
    public Holder<T> peekFail(Consumer<Throwable> task) {
        task.accept(this.e);
        return this;
    }

    @Override
    public Holder<T> ifFail(Function<Throwable, T> defaultValue) {
        this.value = defaultValue.apply(this.e);
        return this;
    }

    @Override
    public Holder<T> shouldRecover(Predicate<Throwable> predicate) {
        if(predicate.test(this.e)){
            return Holder.of(this.value);
        }
        return this;
    }

    @Override
    public Holder<T> throwException(Predicate<Throwable> predicate) {
        if(predicate.test(this.e)){
            throw new RuntimeException(this.e);
        }
        return this;
    }


}
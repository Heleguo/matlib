package me.matl114.matlib.algorithms.dataStructures.frames.dirtyCollection;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DirtyCollectionImpl<S extends Collection<V>,V> implements Collection<V>, Set<V>, DirtyCollection<S> {
    protected final S delegate;
    public DirtyCollectionImpl(S value){
        this.delegate = value;
    }
    private static final VarHandle DIRTY;
    static {
        try{
            Field f = DirtyCollectionImpl.class.getDeclaredField("dirty");
            DIRTY = MethodHandles.privateLookupIn(DirtyCollection.class, MethodHandles.lookup()).unreflectVarHandle(f).withInvokeExactBehavior();
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    private volatile boolean dirty = false;



    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean cleanIfDirty(){
        return DIRTY.compareAndSet(this, (boolean)true, (boolean)false);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public S getHandle() {
        return this.delegate;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }


    @NotNull
    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(V v) {
        if(this.delegate.add(v)){
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if(this.delegate.remove(o)){
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        if(this.delegate.addAll(c)){
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if(this.delegate.retainAll(c)){
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if(this.delegate.removeAll(c)){
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if(!this.delegate.isEmpty()){
            setDirty();
            this.delegate.clear();;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set<?> val && val.equals(this.delegate);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return new DirtyIterator(this.delegate.iterator());
    }


    private class DirtyIterator implements Iterator<V>{
        final Iterator<V> delegate;
        DirtyIterator(Iterator<V> delegate){
            this.delegate = delegate;
        }
        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public V next() {
            return delegate.next();
        }
        public void remove(){
            setDirty();
            this.delegate.remove();
        }
    }

}

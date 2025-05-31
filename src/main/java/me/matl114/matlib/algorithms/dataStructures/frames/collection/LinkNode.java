package me.matl114.matlib.algorithms.dataStructures.frames.collection;

public class LinkNode<T> implements Cloneable{
    T value;
    LinkNode<T> next;
    private static final LinkNode<?> INSTANCE = new LinkNode<>();
    public static <W> LinkNode<W> getInstance(){
        return (LinkNode<W>) INSTANCE.clone();
    }
    @Override
    public LinkNode<T> clone() {
        try {
            LinkNode clone = (LinkNode) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

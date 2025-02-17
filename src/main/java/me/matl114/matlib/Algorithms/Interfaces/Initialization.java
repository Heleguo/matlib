package me.matl114.matlib.Algorithms.Interfaces;

public interface Initialization {
    public Initialization testMode(boolean testMode);
    public Initialization displayName(final String displayName) ;
    public Initialization onEnable();
    public Initialization onDisable();
    default  <T extends Initialization> T cast(Class<T> clazz) {
        return (T)this;
    }
}

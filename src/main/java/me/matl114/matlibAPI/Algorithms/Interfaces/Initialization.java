package me.matl114.matlibAPI.Algorithms.Interfaces;

import java.util.logging.Logger;

/**
 * Initialization contains the startup/shutdown information of a progress,
 * the management of Utils,PluginHelper,Plugin
 */
public interface Initialization {
    /**
     * return if this progress is in test
     * @return
     */
    public boolean isTestMode();
    public Initialization testMode(boolean testMode);

    /**
     * return the progress's displayName
     * @return
     */
    public String getDisplayName();
    public Initialization displayName(final String displayName) ;

    /**
     * control the progress's startup/shutdown
     * @return
     */
    public Initialization onEnable();
    public Initialization onDisable();

    /**
     * get the progress's logger
     * @return
     */
    public Logger getLogger();
    /**
     * builder method
     * @param clazz
     * @return
     * @param <T>
     */
    default  <T extends Initialization> T cast(Class<T> clazz) {
        return (T)this;
    }
}

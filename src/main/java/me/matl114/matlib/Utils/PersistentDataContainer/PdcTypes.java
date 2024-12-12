package me.matl114.matlib.Utils.PersistentDataContainer;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PdcTypes {
    public static void init(){

    }
    public static final PersistentDataType<PersistentDataContainer, List<String>> STRING_LIST = new AbstractStringList();
    public final static AbstractItemStack BYTED_ITEM = new AbstractItemStack();
}

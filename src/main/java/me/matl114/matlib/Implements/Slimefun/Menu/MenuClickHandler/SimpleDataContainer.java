package me.matl114.matlib.Implements.Slimefun.Menu.MenuClickHandler;

public class SimpleDataContainer implements DataContainer {
    boolean isInit = false;
    public boolean getStatue(){
        return isInit;
    }
    public void setStatue(boolean statue){
        isInit = statue;
    }
}

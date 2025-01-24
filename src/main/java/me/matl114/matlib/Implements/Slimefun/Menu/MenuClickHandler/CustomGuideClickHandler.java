package me.matl114.matlib.Implements.Slimefun.Menu.MenuClickHandler;

import me.matl114.matlib.Implements.Slimefun.Menu.MenuGroup.CustomMenu;
import me.matl114.matlib.Implements.Slimefun.Menu.MenuGroup.CustomMenuGroup;

public interface CustomGuideClickHandler extends CustomMenuGroup.CustomMenuClickHandler {
    public GuideClickHandler getHandler(CustomMenu menu);
}

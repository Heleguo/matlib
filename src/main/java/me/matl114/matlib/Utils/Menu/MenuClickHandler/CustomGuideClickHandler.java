package me.matl114.matlib.Utils.Menu.MenuClickHandler;

import me.matl114.matlib.Utils.Menu.MenuGroup.CustomMenu;
import me.matl114.matlib.Utils.Menu.MenuGroup.CustomMenuGroup;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

public interface CustomGuideClickHandler extends CustomMenuGroup.CustomMenuClickHandler {
    public GuideClickHandler getHandler(CustomMenu menu);
}

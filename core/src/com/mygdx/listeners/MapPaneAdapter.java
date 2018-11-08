package com.mygdx.listeners;

import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.mygdx.managers.MapManager;
import com.mygdx.ui.MapTab;

public class MapPaneAdapter extends TabbedPaneAdapter {
    @Override
    public void switchedTab(Tab tab) {
        super.switchedTab(tab);
        MapTab newCurrent = (MapTab) tab;
        MapManager.setCurrentMap(newCurrent.getMap());
    }
}

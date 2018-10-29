package com.mygdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mygdx.game.TableTopMap;

public class MapTab extends Tab {

    private String title;
    private Table content;
    private TableTopMap map;

    public MapTab(String name, TableTopMap map) {
        super(true, true);
        this.title = name;
        this.map = map;

    }


    public void rename(String newTitle) {
        this.title = newTitle;
        map.setName(newTitle);
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    public TableTopMap getMap() {
        return map;
    }
}

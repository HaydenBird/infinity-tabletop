package com.mygdx.listeners;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mygdx.game.TableTopToken;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.MapManager;

public class SelectImageAdapter extends FileChooserAdapter {
    private final int layer;

    public SelectImageAdapter(int layer) {
        this.layer = layer;
    }

    @Override
    public void selected(Array<FileHandle> file) {
        TableTopToken newToken = new TableTopToken(MapManager.getMapStage().getCamera().position.x, MapManager.getMapStage().getCamera().position.y,
                file.get(0).path(), MapManager.getCurrentMap(), layer, EngineManager.getCurrentPlayer());
        MapManager.getCurrentMap().addToken(newToken, layer);
    }
}

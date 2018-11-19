package com.mygdx.listeners;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mygdx.game.TableTopToken;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.MapManager;
import sun.security.ssl.Debug;

import java.io.File;

public class SelectImageAdapter extends FileChooserAdapter {
    private final int layer;

    public SelectImageAdapter(int layer) {
        this.layer = layer;
    }

    @Override
    public void selected(Array<FileHandle> file) {
        FileHandle fileHandle = file.get(0);
        String path = "assets/" + fileHandle.name();
        fileHandle.copyTo(new FileHandle(new File("assets/" + fileHandle.name())));
        TableTopToken newToken = new TableTopToken(MapManager.getMapStage().getCamera().position.x, MapManager.getMapStage().getCamera().position.y,
                path, MapManager.getCurrentMap(), layer, EngineManager.getCurrentPlayer());
        MapManager.getCurrentMap().addToken(newToken, layer);
        Debug.println("Path was found to be", path);
    }
}

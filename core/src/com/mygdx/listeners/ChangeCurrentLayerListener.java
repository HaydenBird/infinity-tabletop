package com.mygdx.listeners;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.managers.MapManager;

public class ChangeCurrentLayerListener extends ChangeListener {

    private int layer;

    public ChangeCurrentLayerListener(int layer) {
        this.layer = layer;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        MapManager.setLayer(layer);
        MapManager.clearSelectedTokens();
    }
}





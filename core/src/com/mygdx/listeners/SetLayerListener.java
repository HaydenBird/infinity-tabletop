package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopToken;
import com.mygdx.managers.MapManager;

public class SetLayerListener extends ChangeListener {
    private final int layer;

    public SetLayerListener(int layer) {
        this.layer = layer;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        for (TableTopToken token : MapManager.getSelectedTokens()) {
            token.setLayer(layer);
        }
        MapManager.setLayer(TableTopMap.Layer.TOKEN);
    }
}

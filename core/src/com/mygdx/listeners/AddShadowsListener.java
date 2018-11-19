package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.TableTopToken;
import com.mygdx.managers.MapManager;

public class AddShadowsListener extends ChangeListener {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        for (TableTopToken token : MapManager.getSelectedTokens()) {
            if (!token.hasBody()) {
                token.destroyBody();
                token.createBody(MapManager.getCurrentMap().getWorld());
            } else {
                token.destroyBody();
            }
        }
    }
}

package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.managers.UIManager;

public class CreateTokenListener extends ChangeListener {

    private int layer;

    public CreateTokenListener(int layer) {
        this.layer = layer;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        UIManager.createToken(layer);
    }
}

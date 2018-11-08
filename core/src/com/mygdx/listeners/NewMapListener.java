package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.TableTopMap;
import com.mygdx.managers.UIManager;

public class NewMapListener extends ChangeListener {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        new TableTopMap("New Map", UIManager.getGame(), true);
    }
}

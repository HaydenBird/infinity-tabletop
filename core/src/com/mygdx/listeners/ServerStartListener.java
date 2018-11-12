package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.managers.NetworkManager;


public class ServerStartListener extends ChangeListener {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        NetworkManager.getInstance().startServer(199);
    }
}

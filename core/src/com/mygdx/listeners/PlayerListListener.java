package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.NetworkManager;
import com.mygdx.managers.UIManager;
import com.mygdx.tabletop.Player;

import java.util.List;

public class PlayerListListener extends ClickListener {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        List<Player> players = NetworkManager.getPlayers();
        VisDialog playerList = new VisDialog("Active Players");
        Table container = new Table();
        for (Player player : players) {
            container.add(new Label(player.getDisplayName(), EngineManager.getSkin()));
            container.row();
        }
        playerList.add(container);
        playerList.setModal(true);
        playerList.addListener(new InputListener() {//Add listener to close the dialogue box
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playerList.hide();
                return true;
            }
        });
        playerList.show(UIManager.getStage());
        playerList.setPosition(UIManager.getStage().getWidth(), 0);

    }
}

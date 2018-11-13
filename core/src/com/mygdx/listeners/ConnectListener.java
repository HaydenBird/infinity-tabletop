package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.NetworkManager;
import com.mygdx.managers.UIManager;

import java.util.UUID;

public class ConnectListener extends ChangeListener {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        //Open the connect dialog
        Dialogs.showInputDialog(UIManager.getStage(), "Enter the IP Address", "Host: ", new InputDialogAdapter() {
            @Override
            public void finished(String host) {
                Dialogs.showInputDialog(UIManager.getStage(), "enter int number", null, new Validators.IntegerValidator(), new InputDialogAdapter() {
                    @Override
                    public void finished(String port) {
                        Dialogs.showInputDialog(UIManager.getStage(), "Enter a display name", "Name: ", new InputDialogAdapter() {
                            @Override
                            public void finished(String input) {
                                EngineManager.getCurrentPlayer().setDisplayName(input);
                                EngineManager.getCurrentPlayer().setId(UUID.randomUUID().toString());
                                NetworkManager.getInstance().connectToServer(host, Integer.parseInt(port));
                            }
                        });
                    }
                });
            }
        });
    }
}

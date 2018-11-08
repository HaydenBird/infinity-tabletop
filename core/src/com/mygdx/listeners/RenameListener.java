package com.mygdx.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.mygdx.managers.UIManager;
import com.mygdx.ui.MapTab;

public class RenameListener extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        MapTab tab = (MapTab) UIManager.getMapPanes().getActiveTab();
        Dialogs.showInputDialog(UIManager.getStage(), "Rename Map", "Name: ", new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                tab.rename(input);
                tab.dirty();
            }
        });
    }

}

package com.mygdx.listeners;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.mygdx.game.TableTopToken;
import com.mygdx.managers.MapManager;
import com.mygdx.managers.UIManager;

public class AddOmniLightListener extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                Dialogs.showInputDialog(UIManager.getStage(), "Light distance", "Distance: ", new InputDialogAdapter() {
                    @Override
                    public void finished(String input) {
                        for (TableTopToken token : MapManager.getSelectedTokens()) {
                            token.enableOmniLight(newColor, Integer.parseInt(input) * 110);
                        }

                    }
                });
            }
        });
        UIManager.getStage().addActor(picker);
    }
}

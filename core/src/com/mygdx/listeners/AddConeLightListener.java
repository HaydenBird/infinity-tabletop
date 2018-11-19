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

public class AddConeLightListener extends ChangeListener {


    @Override
    public void changed(ChangeEvent event, Actor actor) {
        ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                Dialogs.showInputDialog(UIManager.getStage(), "Light distance", "Distance: ", new InputDialogAdapter() {
                    @Override
                    public void finished(String distance) {
                        Dialogs.showInputDialog(UIManager.getStage(), "Light angle", "Angle: ", new InputDialogAdapter() {
                            @Override
                            public void finished(String angle) {
                                Dialogs.showInputDialog(UIManager.getStage(), "Light direction", "Direction: ", new InputDialogAdapter() {
                                    @Override
                                    public void finished(String rotation) {
                                        for (TableTopToken token : MapManager.getSelectedTokens()) {
                                            token.enableConeLight(newColor, Float.parseFloat(distance) * 110, Float.parseFloat(angle), Float.parseFloat(rotation));
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        UIManager.getStage().addActor(picker);
    }
}

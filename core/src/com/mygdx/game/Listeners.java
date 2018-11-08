package com.mygdx.game;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.MapManager;
import com.mygdx.managers.UIManager;
import com.mygdx.ui.MapTab;

public class MapPaneAdapter extends TabbedPaneAdapter {
    @Override
    public void switchedTab(Tab tab) {
        super.switchedTab(tab);
        MapTab newCurrent = (MapTab) tab;
        MapManager.setCurrentMap(newCurrent.getMap());
    }
}

class ExitListener extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {

    }
}

public class ChatBoxListener extends InputListener {
    private TextField chatEntry;
    private ScrollPane chatLogContainer;
    private Table chatLog;

    public ChatBoxListener(TextField chatEntry, Table chatLog, ScrollPane chatLogContainer) {
        this.chatEntry = chatEntry;
        this.chatLog = chatLog;
        this.chatLogContainer = chatLogContainer;
    }

    @Override
    public boolean keyDown(InputEvent e, int keyCode) {
        if (keyCode == Input.Keys.ENTER && !chatEntry.getText().trim().isEmpty()) {
            boolean isBottom = false;
            if (chatLogContainer.isBottomEdge()) {
                isBottom = true;
            }
            chatLog.add(EngineManager.getRollManager().parseMessage(chatEntry.getText())).fill().expand().row();
            chatEntry.setText(null);
            if (isBottom) {
                chatLogContainer.layout();
                chatLogContainer.scrollTo(0, 0, 0, 0);
            }
        }
        return false;
    }


}

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

public class NewMapListener extends ChangeListener {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        new TableTopMap("New Map", UIManager.getGame(), true);
    }
}

public class SelectImageAdapter extends FileChooserAdapter {
    private final int layer;

    public SelectImageAdapter(int layer) {
        this.layer = layer;
    }

    @Override
    public void selected(Array<FileHandle> file) {
        TableTopToken newToken = new TableTopToken(MapManager.getMapStage().getCamera().position.x, MapManager.getMapStage().getCamera().position.y,
                file.get(0).path(), MapManager.getCurrentMap(), layer, EngineManager.getCurrentPlayer());
        MapManager.getCurrentMap().addToken(newToken, layer);
    }
}

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

public class ChangeCurrentLayerListener extends ChangeListener {

    private int layer;

    public ChangeCurrentLayerListener(int layer) {
        this.layer = layer;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        MapManager.setLayer(layer);
        MapManager.clearSelectedTokens();
    }
}





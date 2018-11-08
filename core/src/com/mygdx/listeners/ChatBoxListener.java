package com.mygdx.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.managers.EngineManager;

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

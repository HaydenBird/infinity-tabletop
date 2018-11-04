package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.managers.MapManager;
import sun.security.ssl.Debug;

import java.util.List;

public class KeyListener extends InputListener {

    private TableTopRenderer renderer;


    public KeyListener(TableTopRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        Debug.println("Scrolled", "x: " + x + ", y: " + y + ", amount: " + amount);
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //Zoom if ctrl+scroll
            MapManager.zoomMap(-amount, renderer);
        } else {//Otherwise pan
            MapManager.translateMap(0, 10 * amount, renderer);
        }
        return true;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        Debug.println("Key down", "Key is " + Input.Keys.toString(keycode));
        List<TableTopToken> tokens = MapManager.getSelectedTokens();
        if (tokens.size() == 0 || tokens == null) return false;
        for (TableTopToken token : tokens) {
            switch (keycode) {
                case Input.Keys.UP:
                    token.snapToGrid(token.getX(), token.getY() + 70);
                    break;
                case Input.Keys.DOWN:
                    token.snapToGrid(token.getX(), token.getY() - 70);
                    break;
                case Input.Keys.LEFT:
                    token.snapToGrid(token.getX() - 70, token.getY());
                    break;
                case Input.Keys.RIGHT:
                    token.snapToGrid(token.getX() + 70, token.getY());
                    break;
            }
        }

        return true;
    }

}

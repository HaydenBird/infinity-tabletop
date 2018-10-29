package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.managers.EngineManager;
import sun.security.ssl.Debug;

public class KeyListener extends InputListener {

    private TableTopRenderer renderer;


    public KeyListener(TableTopRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        Debug.println("Scrolled", "x: " + x + ", y: " + y + ", amount: " + amount);
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //Zoom if ctrl+scroll
            EngineManager.zoomMap(-amount, renderer);
        } else {//Otherwise pan
            EngineManager.translateMap(0, 10 * amount, renderer);
        }
        return true;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        Debug.println("Key down", "Key is " + Input.Keys.toString(keycode));
        TableTopToken token = EngineManager.getSelectedToken();
        if (token == null) return false;
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
        return true;
    }

}

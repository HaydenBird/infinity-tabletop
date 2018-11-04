package com.mygdx.game;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.managers.MapManager;

/**
 * This class is used to allow panning
 */

public class BackgroundListener extends ClickListener implements GestureDetector.GestureListener {

    private final TableTopRenderer renderer;

    public BackgroundListener(TableTopRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;

    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        MapManager.translateMap(deltaX, deltaY, renderer);
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}

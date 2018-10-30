package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.managers.EngineManager;

public class DragBox extends Image {

    private final TextureRegion textureRegion;


    public DragBox(Side side, String imagePath) {
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        createListener(side);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    private void createListener(Side side) {
        switch (side) {
            case TOP_LEFT:
                createTopLeft();
                break;
            case LEFT:
                createLeft();
                break;
            case BOTTOM_LEFT:
                createBottomLeft();
                break;
            case TOP:
                createTop();
                break;
            case BOTTOM:
                createBottom();
                break;
            case TOP_RIGHT:
                createTopRight();
                break;
            case BOTTOM_RIGHT:
                createBottomRight();
                break;
            case RIGHT:
                createRight();
                break;
            case ROTATION:
                createRotation();
                break;
        }
    }

    private void createLeft() {

    }

    private void createBottomLeft() {

    }

    private void createTop() {

    }

    private void createBottom() {

    }

    private void createTopRight() {

    }

    private void createBottomRight() {

    }

    private void createRight() {

    }

    private void createRotation() {

    }

    private void createTopLeft() {

    }

    public enum Side {
        TOP_LEFT,
        LEFT,
        BOTTOM_LEFT,
        TOP,
        BOTTOM,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        RIGHT,
        ROTATION;
    }
}

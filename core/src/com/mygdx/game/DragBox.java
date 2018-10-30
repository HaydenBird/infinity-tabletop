package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.managers.EngineManager;

public class DragBox extends Image {

    private final TextureRegion textureRegion;
    private int side;

    public DragBox(int side, String imagePath) {
        this.side = side;
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        createListener(side);
        this.setSize(8, 8);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    private void createListener(int side) {
        switch (side) {
            case Side.TOP_LEFT:
                createTopLeft();
                break;
            case Side.LEFT:
                createLeft();
                break;
            case Side.BOTTOM_LEFT:
                createBottomLeft();
                break;
            case Side.TOP:
                createTop();
                break;
            case Side.BOTTOM:
                createBottom();
                break;
            case Side.TOP_RIGHT:
                createTopRight();
                break;
            case Side.BOTTOM_RIGHT:
                createBottomRight();
                break;
            case Side.RIGHT:
                createRight();
                break;
            case Side.ROTATION:
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

    public void moveTo(float minX, float minY, float width, float height) {

        switch (side) {
            case Side.TOP_LEFT:
                setPosition(minX - 5, minY + height + 5);
                break;
            case Side.LEFT:
                setPosition(minX - 5, minY + height / 2);
                break;
            case Side.BOTTOM_LEFT:
                setPosition(minX - 5, minY - 5);
                break;
            case Side.TOP:
                setPosition(minX + width / 2, minY + height + 5);
                break;
            case Side.BOTTOM:
                setPosition(minX + width / 2, minY - 5);
                break;
            case Side.TOP_RIGHT:
                setPosition(minX + 5 + width, minY + height + 5);
                break;
            case Side.BOTTOM_RIGHT:
                setPosition(minX + 5 + width, minY - 5);
                break;
            case Side.RIGHT:
                setPosition(minX + 5 + width, minY + height / 2);
                break;
            case Side.ROTATION:
                setPosition(minX + width / 2, minY + height + 20);
                break;
        }

    }

    public class Side {
        public static final int TOP_LEFT = 0;
        public static final int LEFT = 1;
        public static final int BOTTOM_LEFT = 2;
        public static final int TOP = 3;
        public static final int BOTTOM = 4;
        public static final int TOP_RIGHT = 5;
        public static final int BOTTOM_RIGHT = 6;
        public static final int RIGHT = 7;
        public static final int ROTATION = 8;
    }
}

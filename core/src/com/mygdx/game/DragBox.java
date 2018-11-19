package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.MapManager;
import sun.security.ssl.Debug;

import java.util.List;

public class DragBox extends Image {

    private final TextureRegion textureRegion;
    private int side;

    public DragBox(int side, String imagePath) {
        this.side = side;
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        createListener(side);
        this.setSize(8, 8);
        debug();
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    private void createListener(int side) {
        switch (side) {
            case Side.TOP_LEFT:
                setDragListener(-1, 1, 1, 0);
                break;
            case Side.LEFT:
                setDragListener(-1, 0, 1, 0);
                break;
            case Side.BOTTOM_LEFT:
                setDragListener(-1, -1, 1, 1);
                break;
            case Side.TOP:
                setDragListener(0, 1, 0, 0);
                break;
            case Side.BOTTOM:
                setDragListener(0, -1, 0, 1);
                break;
            case Side.TOP_RIGHT:
                setDragListener(1, 1, 0, 0);
                break;
            case Side.BOTTOM_RIGHT:
                setDragListener(1, -1, 0, 1);
                break;
            case Side.RIGHT:
                setDragListener(1, 0, 0, 0);
                break;
            case Side.ROTATION:

                break;
        }
    }

    /**
     * THis method creates the listener so when the boxes are dragged the tokens are resized
     *
     * @param xMultiplier this determines how much the x movement of the drag affects the resizing
     * @param yMultiplier this determines how much the y movement of the drag affects the resizing
     * @param moveX       this determines how much the token is moved when dragging
     * @param moveY       this determines how much the token is moved when dragging
     */
    private void setDragListener(float xMultiplier, float yMultiplier, float moveX, float moveY) {
        DragBox dragBox = this;
        List<TableTopToken> tokens = MapManager.getSelectedTokens();
        this.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                Debug.println("Dragbox dragged", "");
                float deltaX = this.getDeltaX();
                float deltaY = this.getDeltaY();
                for (TableTopToken token : tokens) {
                    token.setWidth(token.getWidth() + x * xMultiplier);
                    token.setHeight(token.getHeight() + y * yMultiplier);
                    token.setX(token.getX() + x * moveX);
                    token.setY(token.getY() + y * moveY);
                    token.setPosition(token.getX(), token.getY());
                }
                MapManager.getMapStage().getCamera().update();
            }

            public void dragStop(InputEvent event, float x, float y, int pointer) {
                Debug.println("Drag stopped", "Snap to size");
                for (TableTopToken token : tokens) {
                    token.snapToSize();
                }
            }
        });

    }

    private void setRotateListener() {

    }

    /**
     * This method takes the bottom left corner of the bounding box (the minimum X and Y position) and the width and height of the box,
     * the method then moves the dragbox to the correct position on the bounding box depending on its side
     * @param minX the minimum X coord of the bounding box
     * @param minY the minimum Y coord of the bounding box
     * @param width the width of the bounding box
     * @param height the height of the bounding box
     */

    public void moveTo(float minX, float minY, float width, float height) {
        switch (side) {
            case Side.TOP_LEFT:
                super.setPosition(minX - 10, minY + height + 2);
                break;
            case Side.LEFT:
                super.setPosition(minX - 10, minY + height / 2);
                break;
            case Side.BOTTOM_LEFT:
                super.setPosition(minX - 10, minY - 10);
                break;
            case Side.TOP:
                super.setPosition(minX + width / 2, minY + height + 2);
                break;
            case Side.BOTTOM:
                super.setPosition(minX + width / 2, minY - 10);
                break;
            case Side.TOP_RIGHT:
                super.setPosition(minX + 2 + width, minY + height + 2);
                break;
            case Side.BOTTOM_RIGHT:
                super.setPosition(minX + 2 + width, minY - 10);
                break;
            case Side.RIGHT:
                super.setPosition(minX + 2 + width, minY + height / 2);
                break;
            case Side.ROTATION:
                super.setPosition(minX + width / 2, minY + height + 20);
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

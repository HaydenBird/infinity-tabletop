package com.mygdx.game;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.mygdx.managers.EngineManager;
import com.mygdx.tabletop.Entry;
import sun.security.ssl.Debug;

/**
 * This class represents a token on the map tokens have an image that they display, and they can be dragged around on the map
 * Tokens can exist on one of 3 layers:
 * Map - These tokens cannot be moved unless the layer is opened, this is to stop you from accidentally dragging backgrounds
 * Blocking - These tokens have physics bodies and lighting, so they can create lights and cast shadows, this layer needs to be unlocked to move them
 * Token = These tokens can generate light, and the layer is unlock by default
 * <p>
 * Tokens have a map they are associated with, and are only shown while on that map.
 * Tokens can have an associated entry
 */
public class TableTopToken extends Image {
    private final float DEFAULT_WIDTH = 70;
    private final float DEFAULT_HEIGHT = 70;
    private final int NUMBER_OF_VALUES = 4;
    private final Vector3 position;

    private final TextureRegion textureRegion;

    /**
     * The constructor for a token
     *
     * @param xPos      what x position to create it at
     * @param yPos      what y position to create it at
     * @param imagePath the filepath to the image used for the token
     * @param parentMap the map the token is on
     */
    public TableTopToken(float xPos, float yPos, String imagePath, TableTopMap parentMap) {
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        //this.setDrawable(new TextureRegionDrawable(textureRegion);
        debug();
        valuesCurrent = new float[NUMBER_OF_VALUES];
        valuesMax = new float[NUMBER_OF_VALUES];
        this.parentMap = parentMap;
        parentMap.addToken(this, TableTopMap.Layer.TOKEN);
        parentMap.setSaved(false);
        this.texturePath = imagePath;
        position = new Vector3(xPos, yPos * EngineManager.getRatio(), 0);
        omniLight = null;
        coneLight = null;
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);
        this.setPosition(xPos, yPos);


        TableTopToken thisToken = this;
        this.addListener(new DragListener() {
            public void drag(InputEvent event, float x, float y, int pointer) {
                Debug.println("Dragging", "X: " + x + ", Y: " + y);
                if (!EngineManager.getSelectedTokens().contains(thisToken)) {
                    if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                        EngineManager.setSelectedToken(thisToken);
                    } else {
                        EngineManager.clearSelectedTokens();
                        EngineManager.setSelectedToken(thisToken);
                    }


                }
                for (TableTopToken a : EngineManager.getSelectedTokens()) {
                    a.position.x += x;
                    a.position.y += y;
                    a.setPosition(a.position.x - a.getWidth() / 2, a.position.y - a.getHeight() / 2);
                    EngineManager.getMapStage().getCamera().update();
                }

            }

            public void dragStop(InputEvent event, float x, float y, int pointer) {
                for (TableTopToken a : EngineManager.getSelectedTokens()) {
                    a.snapToGrid(a.position.x, a.position.y);
                }
            }
        });

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    if (EngineManager.getSelectedTokens().contains(thisToken)) {
                        EngineManager.getSelectedTokens().remove(thisToken);
                    } else {
                        EngineManager.setSelectedToken(thisToken);
                    }
                } else {
                    EngineManager.clearSelectedTokens();
                    EngineManager.setSelectedToken(thisToken);
                }
            }
        });

    }

    private Body body;
    private int layer;
    private float width;
    private float height;
    private TableTopMap parentMap;
    private Entry linkedEntry;
    private float[] valuesCurrent;
    private float[] valuesMax;
    private ConeLight coneLight;
    private PointLight omniLight;
    private boolean omnidirectionalLightOn = false, coneLightOn = false;
    private String texturePath;

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    /**
     * This method updates the locations of the physics bodies and lights so they match the position of the token
     */
    private void updateLightPositions() {
        if (omniLight != null) {
            omniLight.setPosition(position.x + width / 2, position.y + height / 2);
        }
        if (coneLight != null) {
            coneLight.setPosition(position.x + width / 2, position.y + height / 2);
        }

        if (body != null) {
            body.setTransform(position.x + width / 2, position.y + height / 2, 0);
        }
    }

    /**
     * This method creates a physics body to block light around the token.
     *
     * @param world the world in which to place the body
     */
    public void createBody(World world) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + width / 2, position.y + height / 2);
        body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        Fixture fixture = body.createFixture(fixtureDef);
        polygonShape.dispose();
        body.setUserData(this);

    }

    /**
     * This method creates a omnidirectional light on the token
     *
     * @param color             the color of the light
     * @param omniLightDistance how far the light is cast
     */
    public void enableOmniLight(Color color, float omniLightDistance) {
        if (omniLight == null) {
            omniLight = new PointLight(EngineManager.getRayHandler(null), 128, color, omniLightDistance, position.x + width / 2, position.y + height / 2);
        }
        omniLight.setStaticLight(false);
        omniLight.setActive(true);
        omniLight.setSoft(true);
        omnidirectionalLightOn = true;
    }

    public void disableOmniLight() {
        if (omniLight != null) {
            omniLight.setActive(false);
        }
    }

    /**
     * This method overrides the normal draw method
     *
     * @param batch       the batch to draw with
     * @param parentAlpha the parent alpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(EngineManager.getTexture(texturePath), this.position.x, this.position.y, this.getWidth(), this.getHeight());
    }

    /**
     * This method snaps the token to the closest grid
     *
     * @param x the x location
     * @param y the y location
     */
    public void snapToGrid(float x, float y) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            return;
        }
        float newX = x;
        float newY = y;
        newX = newX - newX % DEFAULT_WIDTH;
        newY = newY - newY % DEFAULT_HEIGHT;
        this.setPosition(newX, newY);
    }

    /**
     * Sets the position of the token and all bodies and lights
     *
     * @param x the new x
     * @param y the new y
     */
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.position.set(x, y, 0);
        updateLightPositions();
    }


    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }


    public PointLight getOmniLight() {
        return omniLight;
    }

    public void renableOmniLight() {
        if (omniLight != null) {
            omniLight.setActive(true);
        }
    }
}


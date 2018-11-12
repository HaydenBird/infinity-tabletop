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
import com.mygdx.containers.Command;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.MapManager;
import com.mygdx.managers.NetworkManager;
import com.mygdx.tabletop.Entry;
import com.mygdx.tabletop.Player;
import sun.security.ssl.Debug;

import java.util.*;

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
    private List<Player> owners;
    private final String tokenID;

    private final TextureRegion textureRegion;

    private PointLight selfLight;

    private static Map<String, TableTopToken> tokenMap;


    /**
     * The constructor for a token
     *
     * @param xPos      what x position to create it at
     * @param yPos      what y position to create it at
     * @param imagePath the filepath to the image used for the token
     * @param parentMap the map the token is on
     */
    public TableTopToken(float xPos, float yPos, String imagePath, TableTopMap parentMap, int layer, Player creator) {
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        valuesCurrent = new float[NUMBER_OF_VALUES];
        valuesMax = new float[NUMBER_OF_VALUES];
        this.parentMap = parentMap;
        owners = new LinkedList<>();
        owners.add(creator);
        parentMap.addToken(this, TableTopMap.Layer.TOKEN);
        parentMap.setSaved(false);
        this.texturePath = imagePath;
        position = new Vector3(xPos, yPos * EngineManager.getRatio(), 0);
        if (owners.contains(EngineManager.getCurrentPlayer())) {
            selfLight = new PointLight(EngineManager.getRayHandler(parentMap.getWorld()), 128, Color.WHITE, 50, this.getX(), this.getY());
        }
        coneLight = null;
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);
        this.setPosition(xPos, yPos);
        setLayer(layer);
        addClickListeners();
        tokenID = UUID.randomUUID().toString();
        TableTopToken.addToMap(this);
        if (NetworkManager.isHost()) sendCreationMessage();
    }

    /**
     * The constructor for a token
     *
     * @param xPos      what x position to create it at
     * @param yPos      what y position to create it at
     * @param imagePath the filepath to the image used for the token
     * @param parentMap the map the token is on
     * @param layer
     * @param creator
     * @param tokenID   a unique id to give
     */
    public TableTopToken(float xPos, float yPos, String imagePath, TableTopMap parentMap, int layer, Player creator, String tokenID) {
        textureRegion = new TextureRegion(EngineManager.getTexture(imagePath));
        valuesCurrent = new float[NUMBER_OF_VALUES];
        valuesMax = new float[NUMBER_OF_VALUES];
        this.parentMap = parentMap;
        owners = new LinkedList<>();
        owners.add(EngineManager.getGM());
        parentMap.addToken(this, TableTopMap.Layer.TOKEN);
        parentMap.setSaved(false);
        this.texturePath = imagePath;
        position = new Vector3(xPos, yPos * EngineManager.getRatio(), 0);
        coneLight = null;
        if (owners.contains(EngineManager.getCurrentPlayer())) {
            selfLight = new PointLight(EngineManager.getRayHandler(parentMap.getWorld()), 128, Color.WHITE, 50, this.getX(), this.getY());
        }
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);
        this.setPosition(xPos, yPos);
        this.layer = layer;
        addClickListeners();
        this.tokenID = tokenID;
        TableTopToken.addToMap(this);
        if (NetworkManager.isHost()) sendCreationMessage();
    }

    private void sendCreationMessage() {
        //  token [parent map id] [token id] [token X] [token Y] [layer] [image asset name] [file size] [message id]
        List<String> args = new LinkedList<>();
        args.add(parentMap.getName() + " ");
        args.add(tokenID + " ");
        args.add(tokenID + " ");
        args.add(position.x + " ");
        args.add(position.y + " ");
        args.add(texturePath + " ");
        args.add("0");
        args.add("MessageID");
        Command command = new Command(Command.CommandType.TOKEN, args, null);
        NetworkManager.sendCommand(command, NetworkManager.getPlayers());
    }

    public static void addToMap(TableTopToken token) {
        if (tokenMap == null) {
            tokenMap = new HashMap<>();
        }
        tokenMap.put(token.getTokenID(), token);
    }

    /**
     * This method adds the listeners that allow you to interact with the token with your mouse
     */
    private void addClickListeners() {
        TableTopToken thisToken = this;
        this.addListener(new TokenDragListener(this));
        this.addListener(new TokenClickListener(this));
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

    public static Map<String, TableTopToken> getTokenMap() {
        return tokenMap;
    }
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

        if (selfLight != null) {
            selfLight.setPosition(position.x + width / 2, position.y + height / 2);
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
        } else {
            omniLight.setDistance(omniLightDistance);
            omniLight.setColor(color);
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
        float newX = Math.round(x / DEFAULT_WIDTH) * DEFAULT_WIDTH;
        float newY = Math.round(y / DEFAULT_HEIGHT) * DEFAULT_HEIGHT;
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
        sendMovementMessage();
    }

    private void sendMovementMessage() {
        List<String> arguments = new LinkedList<>();
        arguments.add(tokenID + " ");
        arguments.add(this.position.x + " ");
        arguments.add(this.position.y + " ");
        arguments.add(this.layer + " ");
        arguments.add(this.width + " ");
        arguments.add(this.height + " ");
        arguments.add(this.getRotation() + " ");
        arguments.add("MessageID");
        Command command = new Command(Command.CommandType.MOVE, arguments, null);
        NetworkManager.sendCommand(command, NetworkManager.getPlayers());

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
        if (this.owners == null) Debug.println("SetLayer", "Owners is null");
        if (EngineManager.getCurrentPlayer() == null) Debug.println("SetLayer", "Player is null");
        if (layer == TableTopMap.Layer.TOKEN && this.owners.contains(EngineManager.getCurrentPlayer())) {
            if (selfLight == null)
                selfLight = new PointLight(EngineManager.getRayHandler(parentMap.getWorld()), 128, Color.WHITE, 50, this.getX(), this.getY());
            selfLight.setActive(true);
        } else {
            selfLight.setActive(false);
        }
    }


    public PointLight getOmniLight() {
        return omniLight;
    }

    public void renableOmniLight() {
        if (omniLight != null) {
            omniLight.setActive(true);
        }
    }

    public void snapToSize() {
        Debug.println("Snap to size", "Width=" + this.getWidth() + ", Height=" + this.getHeight());
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            this.setPosition(this.getX(), this.getY());
        } else {
            float roundedWidth = Math.round(width / DEFAULT_WIDTH) * DEFAULT_WIDTH;
            float roundedHeight = Math.round(height / DEFAULT_HEIGHT) * DEFAULT_HEIGHT;
            this.setWidth(roundedWidth);
            this.setHeight(roundedHeight);
            snapToGrid(this.getX(), this.getY());
        }
        width = Math.max(width, DEFAULT_WIDTH);
        height = Math.max(height, DEFAULT_HEIGHT);
        MapManager.getCurrentMap().getWorld().destroyBody(body);
        createBody(MapManager.getCurrentMap().getWorld());
        super.setHeight(height);
        super.setWidth(width);
    }

    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
        super.setWidth(width);
        super.setHeight(height);
    }

    public String getTokenID() {
        return tokenID;
    }


    /**
     * This class is the listener for the token being dragged
     */
    private class TokenDragListener extends DragListener {
        private TableTopToken thisToken;

        public TokenDragListener(TableTopToken thisToken) {
            this.thisToken = thisToken;
        }

        public void drag(InputEvent event, float x, float y, int pointer) {
            Debug.println("Dragging", "X: " + x + ", Y: " + y);
            if (!MapManager.getSelectedTokens().contains(thisToken)) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    MapManager.setSelectedToken(thisToken);
                } else {
                    MapManager.clearSelectedTokens();
                    MapManager.setSelectedToken(thisToken);
                }


            }
            for (TableTopToken a : MapManager.getSelectedTokens()) {
                a.position.x += x;
                a.position.y += y;
                a.setPosition(a.position.x - a.getWidth() / 2, a.position.y - a.getHeight() / 2);
                MapManager.getMapStage().getCamera().update();
            }

        }

        public void dragStop(InputEvent event, float x, float y, int pointer) {

            for (TableTopToken a : MapManager.getSelectedTokens()) {
                a.snapToGrid(a.position.x, a.position.y);
            }
        }
    }

    /**
     * This class is the listener that handles the token being clicked
     */
    private class TokenClickListener extends ClickListener {

        private TableTopToken thisToken;

        public TokenClickListener(TableTopToken thisToken) {
            this.thisToken = thisToken;
        }


        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                if (MapManager.getSelectedTokens().contains(thisToken)) {
                    MapManager.getSelectedTokens().remove(thisToken);
                } else {
                    MapManager.setSelectedToken(thisToken);
                }
            } else {
                MapManager.clearSelectedTokens();
                MapManager.setSelectedToken(thisToken);
            }
        }
    }


}


package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.managers.MapManager;
import com.mygdx.managers.UIManager;

import java.util.*;

/**
 * This object represents a map screen. It acts as a list of all the tokens and images on the map, and calls for them to be rendered
 */
public class TableTopMap implements Screen {

    private static Map<String, TableTopMap> mapMap;
    private String id;

    /**
     * Constructor method
     *
     * @param name    The name of the map
     * @param game    the parent game
     * @param showTab whether to add the tab to the map tabs list
     */
    public TableTopMap(String name, final MyGdxGame game, boolean showTab) {
        this.game = game;
        this.name = name;
        this.world = new World(new Vector2(0, 0), true);
        this.isSaved = true;
        tokenLayerTokens = new LinkedList<>();
        mapLayerTokens = new LinkedList<>();
        blockingLayerTokens = new LinkedList<>();
        if (showTab) UIManager.addMap(this);
        this.id = UUID.randomUUID().toString();
        addToMap(this);
    }

    /**
     * Constructor method
     *
     * @param name    The name of the map
     * @param game    the parent game
     * @param showTab whether to add the tab to the map tabs list
     */
    public TableTopMap(String name, final MyGdxGame game, boolean showTab, String id) {
        this.game = game;
        this.name = name;
        this.world = new World(new Vector2(0, 0), true);
        this.isSaved = true;
        tokenLayerTokens = new LinkedList<>();
        mapLayerTokens = new LinkedList<>();
        blockingLayerTokens = new LinkedList<>();
        if (showTab) UIManager.addMap(this);
        this.id = id;
        addToMap(this);
    }

    /**
     * This method adds a new token to the map
     *
     * @param token the token to be added
     * @param layer the layer to be added to, 0 = map, 1 = blocking, 2 = token
     */
    public void addToken(TableTopToken token, int layer) {
        switch (layer) {
            case Layer.MAP:
                this.mapLayerTokens.add(token);
                break;
            case Layer.BLOCKING:
                this.blockingLayerTokens.add(token);
                break;
            case Layer.TOKEN:
                this.tokenLayerTokens.add(token);
                break;
        }
        MapManager.getMapStage().addActor(token);
        this.isSaved = false;
    }

    private final MyGdxGame game;
    private String name;
    private boolean isSaved;
    private List<TableTopToken> tokenLayerTokens;
    private List<TableTopToken> blockingLayerTokens;
    private List<TableTopToken> mapLayerTokens;

    public static Map<String, TableTopMap> getMapMap() {
        return mapMap;
    }

    private World world;

    public static void addToMap(TableTopMap map) {
        if (mapMap == null) {
            mapMap = new HashMap<>();
        }
        mapMap.put(map.getId(), map);
    }

    private String getId() {
        return id;
    }


    /**
     * The name of the map
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the map
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
        this.isSaved = false;
    }

    public class Layer {
        public static final int MAP = 0;
        public static final int BLOCKING = 1;
        public static final int TOKEN = 2;

    }

    /**
     * Returns a list of all the tokens on the top token layer
     *
     * @return the token list
     */
    public List<TableTopToken> getTokenLayerTokens() {
        return tokenLayerTokens;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    /**
     * Returns whether the map has had its data changed since it was last saved
     *
     * @return wheter the map is saved in its current state
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * Marks the map as saved or unsaved
     *
     * @param saved save state
     */
    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    /**
     * The map layer tokens
     *
     * @return token list
     */
    public List<TableTopToken> getMapLayerTokens() {
        return mapLayerTokens;
    }

    /**
     * The blocking layer tokens
     *
     * @return token list
     */
    public List<TableTopToken> getBlockingLayerTokens() {
        return blockingLayerTokens;
    }
}

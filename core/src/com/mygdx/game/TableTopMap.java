package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.UIManager;

import java.util.LinkedList;
import java.util.List;

/**
 * This object represents a map screen. It acts as a list of all the tokens and images on the map, and calls for them to be rendered
 */
public class TableTopMap implements Screen {

    private final MyGdxGame game;
    private String name;
    private boolean isSaved;
    private List<TableTopToken> tokenLayerTokens;
    private List<TableTopToken> blockingLayerTokens;
    private List<TableTopToken> mapLayerTokens;

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

    /**
     * This method adds a new token to the map
     *
     * @param token the token to be added
     * @param layer the layer to be added to, 0 = map, 1 = blocking, 2 = token
     */
    public void addToken(TableTopToken token, int layer) {
        switch (layer) {
            case 0:
                this.mapLayerTokens.add(token);
                break;
            case 1:
                this.blockingLayerTokens.add(token);
                break;
            case 2:
                this.tokenLayerTokens.add(token);
                break;
        }
        EngineManager.getMapStage().addActor(token);
        this.isSaved = false;
    }

    /**
     * Returns a list of all the tokens on the top token layer
     *
     * @return the token list
     */
    public List<TableTopToken> getTokenLayerTokens() {
        return tokenLayerTokens;
    }

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
        this.isSaved = true;
        EngineManager.loadMap(this);
        tokenLayerTokens = new LinkedList<>();
        mapLayerTokens = new LinkedList<>();
        blockingLayerTokens = new LinkedList<>();
        if (showTab) UIManager.addMap(this);
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

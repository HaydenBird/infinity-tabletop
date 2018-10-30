package com.mygdx.managers;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopRenderer;
import com.mygdx.game.TableTopToken;
import com.mygdx.tabletop.Campaign;
import com.mygdx.tabletop.Player;
import sun.security.ssl.Debug;

import java.util.LinkedList;
import java.util.List;

/**
 * This class contains a lot of things that will eventually be split up into more logical parts
 */
public class EngineManager {

    private static EngineManager engine;
    private static ListManager listManager;
    private static ChatManager chatManager;
    private static NetworkManager networkManager;
    private static RollManager rollManager;
    private static SaveManager saveManager;
    private static UIManager uiManager;
    private static Campaign loadedCampaign;
    private static OrthographicCamera hudCamera;
    private static InputMultiplexer multiplexer;
    private static List<TableTopToken> selectedToken = new LinkedList<>();
    private static World world;

    /**
     * This returns the stage that is used for tokens
     *
     * @return the stage
     */
    public static Stage getMapStage() {
        return mapStage;
    }

    private static Stage mapStage;


    private static OrthographicCamera mapCamera;
    private static Skin skin;
    private static TextureAtlas atlas;
    private static TableTopMap currentMap;
    private static AssetManager assetManager;
    private static RayHandler rayHandler;

    /**
     * The constructor used to initialize all the needed stuff
     */
    public EngineManager() {
        if (engine == null) {
            EngineManager.engine = this;
        }

        hudCamera = new OrthographicCamera();
        EngineManager.getUiManager();
        UIManager.init();
        mapStage = new Stage(new ScreenViewport(), UIManager.getStage().getBatch());
        mapStage.getCamera().viewportHeight = 1000 / getRatio();
        mapStage.getCamera().viewportWidth = 1000;
        mapStage.getCamera().position.set(0, 0, 10);
        mapStage.getCamera().lookAt(0, 0, 0);


    }

    /**
     * This method gets the ratio of map view width to height
     *
     * @return the ratio
     */
    public static float getRatio() {
        return (float) (Gdx.graphics.getWidth() * 6 / 10) / (float) (Gdx.graphics.getHeight() * 9 / 10);
    }

    /**
     * This returns the UI camera
     *
     * @return the camera
     */
    public static OrthographicCamera getCamera() {
        return hudCamera;
    }

    /**
     * Gets the instance of this
     *
     * @return this
     */
    public static EngineManager getEngine() {
        return engine;
    }

    /**
     * Initializes and returns the UI manager
     *
     * @return the UI manager
     */
    public static UIManager getUiManager() {
        if (uiManager == null) EngineManager.uiManager = new UIManager();
        return uiManager;
    }

    /**
     * Initializes and returns the list manager
     *
     * @return the list manager
     */
    public static ListManager getListManager() {
        if (listManager == null) EngineManager.listManager = new ListManager();
        return listManager;
    }

    /**
     * Initializes and returns the chat manager
     *
     * @return the chat manager
     */
    public static ChatManager getChatManager() {
        if (chatManager == null) EngineManager.chatManager = new ChatManager();
        return chatManager;
    }

    /**
     * Initializes and returns the network manager
     *
     * @return the network manager
     */
    public static NetworkManager getNetworkManager() {
        if (networkManager == null) EngineManager.networkManager = new NetworkManager();
        return networkManager;
    }

    /**
     * Initializes and returns the roll manager
     *
     * @return the roll manager
     */
    public static RollManager getRollManager() {
        if (rollManager == null) EngineManager.rollManager = new RollManager();
        return rollManager;
    }

    /**
     * Initializes and returns the Save manager
     *
     * @return the save manager
     */

    public static SaveManager getSaveManager() {
        if (saveManager == null) EngineManager.saveManager = new SaveManager();
        return saveManager;
    }

    /**
     * returns the selected campaign
     *
     * @return the campaign
     */
    public static Campaign getLoadedCampaign() {
        return loadedCampaign;
    }

    /**
     * This method initializes all managers, and clears all activity
     */
    private static void clean() {
        getListManager();
        getChatManager();
        getNetworkManager();
        getSaveManager();
        getUiManager();
        getRollManager();
        ChatManager.clearMessages();
        NetworkManager.clearCommandQueue();
        loadedCampaign = null;
        ListManager.nukeLists();

    }

    /**
     * Creates a new campaign
     */
    public static void createNewCampaign() {
        clean();
        loadedCampaign = new Campaign();
        NetworkManager.startServer("", 0);
        ListManager.nukeLists();


    }

    /**
     * Joins into a remote campaign
     *
     * @param ipAddress the ip to connect to
     * @param port      the port to connect on
     */
    public static void joinNetworkGame(String ipAddress, String port) {
        loadedCampaign = NetworkManager.getNetworkCampaign(ipAddress, port);


    }

    /**
     * This method returns a path to the save file of the previosly loaded campaign
     *
     * @return the path to the save file, or null if there is not previous
     */
    public static String checkPrevLoadedGame() {
        //TODO: Check if a game has previously been loaded locally
        return null;
    }

    /**
     * This method returns the libgdx skin to be used
     *
     * @return the skin
     */
    public static Skin getSkin() {
        if (skin == null) {
            VisUI.load();
            skin = VisUI.getSkin();
            UIManager.scrollPaneLayout(skin.get(ScrollPane.ScrollPaneStyle.class));
        }

        return skin;

    }

    /**
     * This method initializes or returns the already existing instance of asset manager
     *
     * @return the asset manager
     */
    public static AssetManager getAssetManager() {
        if (assetManager == null) assetManager = new AssetManager();
        return EngineManager.assetManager;
    }

    /**
     * This method loads the UI skin altlas
     *
     * @return the atlas
     */
    public static TextureAtlas getAtlas() {
        if (atlas == null) {

            getAssetManager().load("assets/VIS def/uiskin.atlas", TextureAtlas.class);
            getAssetManager().finishLoading();
            atlas = getAssetManager().get("assets/VIS def/uiskin.atlas", TextureAtlas.class);
        }
        return atlas;
    }

    /**
     * Returns who the player who is running this instance of the program
     *
     * @return
     */
    public static Player getCurrentPlayer() {
        return null;//TODO: Return the current player
    }

    /**
     * Loads a map as the current map
     *
     * @param map the new map to use
     */
    public static void loadMap(TableTopMap map) {
        if (currentMap != null) {
            currentMap.pause();
        }
        currentMap = map;

    }

    /**
     * @return the currently selected map
     */
    public static TableTopMap getCurrentMap() {
        return currentMap;
    }

    /**
     * This method initializes the ray handler and returns the instance
     *
     * @param zaWurdo the physics world
     * @return the ray handler
     */
    public static RayHandler getRayHandler(World zaWurdo) {
        if (rayHandler == null) {
            RayHandler.useDiffuseLight(true);
            rayHandler = new RayHandler(zaWurdo);
            rayHandler.setCombinedMatrix(mapStage.getCamera().combined, 0, 0, 1, 1);
            rayHandler.setShadows(true);
            rayHandler.setCulling(true);
            rayHandler.setAmbientLight(0.11f, 0.1f, 0.1f, 0f);
            rayHandler.setBlurNum(5);
        }
        return rayHandler;
    }

    /**
     * This method takes a filepath to an image and returns a texture loaded from the image
     *
     * @param path the file path
     * @return the texture
     */
    public static Texture getTexture(String path) {
        if (!EngineManager.getAssetManager().isLoaded(path)) {
            EngineManager.getAssetManager().load(path, Texture.class);
            EngineManager.getAssetManager().finishLoading();
        }
        return EngineManager.getAssetManager().get(path);
    }

    /**
     * This method moves the map camera but keeps it within certain bounds
     *
     * @param x        how much x to move
     * @param y        how much y to move
     * @param renderer the renderer being used to render the map
     */
    public static void translateMap(float x, float y, TableTopRenderer renderer) {
        Debug.println("Safe translate", " ");
        OrthographicCamera mapCamera = (OrthographicCamera) mapStage.getCamera();

        float newX = MathUtils.clamp(mapCamera.position.x - x, -1 * mapCamera.viewportWidth / 2, (renderer.getGridWidth() + mapCamera.viewportWidth / 2) * mapCamera.zoom);
        float newY = MathUtils.clamp(mapCamera.position.y + y, -1 * mapCamera.viewportHeight / 2, (renderer.getGridHeight() + mapCamera.viewportHeight / 2) * mapCamera.zoom);
        Debug.println("Translate", "newX: " + newX + ", newY: " + newY);
        mapCamera.position.set(newX, newY, 10);
        mapCamera.update();
    }

    /**
     * This method zooms the map camera within bounds
     *
     * @param distance whether to to zoom in or out, 1 for out, -1 for in
     * @param renderer the renderer
     */
    public static void zoomMap(float distance, TableTopRenderer renderer) {
        OrthographicCamera mapCamera = (OrthographicCamera) mapStage.getCamera();
        Debug.println("Zoom", "Current: " + mapCamera.zoom);
        mapCamera.zoom += 0.02 * distance;
        mapCamera.zoom = MathUtils.clamp(mapCamera.zoom, 0.3f, 4f);
        mapCamera.update();
    }

    public static List<TableTopToken> getSelectedTokens() {
        return EngineManager.selectedToken;
    }

    public static void clearSelectedTokens() {
        if (selectedToken != null) selectedToken.clear();
    }

    public static void setSelectedToken(TableTopToken a) {
        if (selectedToken == null) selectedToken = new LinkedList<>();
        if (!selectedToken.contains(a)) selectedToken.add(a);
    }

    public static void setCurrentMap(TableTopMap map) {
        if (currentMap != null) {
            for (TableTopToken token : currentMap.getMapLayerTokens()) {
                token.disableOmniLight();
            }
            for (TableTopToken token : currentMap.getTokenLayerTokens()) {
                token.disableOmniLight();
            }
            for (TableTopToken token : currentMap.getBlockingLayerTokens()) {
                token.disableOmniLight();
            }
        }
        EngineManager.currentMap = map;
        EngineManager.setWorld(map.getWorld());
        if (rayHandler != null) {
            EngineManager.rayHandler.setWorld(map.getWorld());
        }

        for (TableTopToken token : currentMap.getMapLayerTokens()) {
            token.renableOmniLight();
        }
        for (TableTopToken token : currentMap.getTokenLayerTokens()) {
            token.renableOmniLight();
        }
        for (TableTopToken token : currentMap.getBlockingLayerTokens()) {
            token.renableOmniLight();
        }
        clearSelectedTokens();
    }

    public static void setWorld(World world) {
        EngineManager.world = world;

    }
}

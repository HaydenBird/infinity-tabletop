package com.mygdx.managers;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.game.DragBox;
import com.mygdx.game.TableTopRenderer;
import com.mygdx.tabletop.Campaign;
import com.mygdx.tabletop.Player;

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
    private static World world;
    private static TableTopRenderer renderer;
    private static Player currentPlayer;




    private static OrthographicCamera mapCamera;
    private static Skin skin;
    private static TextureAtlas atlas;

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
        MapManager.getInstance();
        initPlayer();
    }

    /**
     * Returns who the player who is running this instance of the program
     *
     * @return
     */
    public static Player getCurrentPlayer() {
        return currentPlayer;
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
        NetworkManager.getInstance().startServer(199);
        ListManager.nukeLists();
    }

    /**
     * Joins into a remote campaign
     *
     * @param ipAddress the ip to connect to
     * @param port      the port to connect on
     */
    public static void joinNetworkGame(String ipAddress, String port) {
        clean();
        MapManager.clean();
        NetworkManager.getInstance().connectToServer(ipAddress, Integer.parseInt(port));

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

    public static Player getGM() {
        return currentPlayer;
    }

    private void initPlayer() {
        EngineManager.currentPlayer = new Player("Mee", "1234");

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
            rayHandler.setCombinedMatrix(MapManager.getMapStage().getCamera().combined, 0, 0, 1, 1);
            rayHandler.setShadows(true);
            rayHandler.setCulling(true);
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

    public static void setWorld(World world) {
        EngineManager.world = world;

    }


    private static void disableDragBoxes() {
        for (DragBox dragBox : renderer.dragBoxes()) {
            dragBox.setVisible(false);
        }
    }

    public static void setRenderer(TableTopRenderer renderer) {
        EngineManager.renderer = renderer;
    }


}

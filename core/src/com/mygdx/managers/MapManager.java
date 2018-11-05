package com.mygdx.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopRenderer;
import com.mygdx.game.TableTopToken;
import sun.security.ssl.Debug;

import java.util.LinkedList;
import java.util.List;

public class MapManager {
    private static MapManager instance;
    private static Stage mapStage;
    private static int layer;
    private static List<TableTopToken> selectedToken = new LinkedList<>();
    private static TableTopMap currentMap;

    public MapManager() {
        mapStage = new Stage(new ScreenViewport(), UIManager.getStage().getBatch());
        mapStage.getCamera().viewportHeight = 1000 / EngineManager.getRatio();
        mapStage.getCamera().viewportWidth = 1000;
        mapStage.getCamera().position.set(0, 0, 10);
        mapStage.getCamera().lookAt(0, 0, 0);
        layer = TableTopMap.Layer.TOKEN;
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }


    /**
     * This returns the stage that is used for tokens
     *
     * @return the stage
     */
    public static Stage getMapStage() {
        return mapStage;
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
        currentMap = map;
        EngineManager.setWorld(map.getWorld());
        if (EngineManager.getRayHandler(map.getWorld()) != null) {
            EngineManager.getRayHandler(map.getWorld()).setWorld(map.getWorld());
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
        return selectedToken;
    }

    public static void clearSelectedTokens() {
        if (selectedToken != null) selectedToken.clear();
    }

    public static void setSelectedToken(TableTopToken a) {
        if (selectedToken == null) selectedToken = new LinkedList<>();
        if (!selectedToken.contains(a) && a.getLayer() == layer) selectedToken.add(a);
    }

    public static int getLayer() {
        return layer;
    }

    public static void setLayer(int layer) {
        MapManager.layer = layer;
    }


    public static void clean() {
        currentMap = null;
        layer = TableTopMap.Layer.TOKEN;
        clearSelectedTokens();
    }
}

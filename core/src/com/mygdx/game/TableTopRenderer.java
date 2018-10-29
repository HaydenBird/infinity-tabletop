package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.UIManager;

/**
 * The class that handles all of the rendering for the map screen and the lighting shaders
 */
public class TableTopRenderer {

    private final ShapeRenderer shapeRenderer;
    private TextureRegion gridTexture;
    private boolean gridPopulated = false;
    private Image[][] gridActors;
    private float gridHeight;
    private float gridWidth;
    private TextureRegion gridLines;


    /**
     * Constructor
     */
    public TableTopRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * This method is called to render the map screen
     *
     * @param rayHandler the ray handler that will handle the lighting
     * @param batch      The batch that will handle rendering the tokens
     * @param world      the world object that contains the physics objects
     */
    public void render(RayHandler rayHandler, SpriteBatch batch, World world) {
        Stage mapStage = EngineManager.getMapStage();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        //Render a generic background
        renderGrid(Color.LIGHT_GRAY, Color.RED, 50, 50, batch);
        //Render the Map

        mapStage.act();
        mapStage.draw();
        //Render lighting
        rayHandler.setCombinedMatrix((OrthographicCamera) mapStage.getCamera());
        rayHandler.updateAndRender();
        //Render the UI on top
        UIManager.getStage().act();
        UIManager.getStage().draw();

    }

    /**
     * This method creates a texture that is grid of 68 pixel wide squares with 2 pixel borders and is used to
     *
     * @param background the color of the background
     * @param lines      the color of the lines
     * @param width      how many squares wide
     * @param height     how many squares high
     * @param batch      the batch used to render
     */
    public void renderGrid(Color background, Color lines, int width, int height, Batch batch) {

        gridWidth = width * 70; //How many pixels the grid should be
        gridHeight = height * 70;
        //Render overall background
        TextureRegion mapBackground = UIManager.makeFlatColor(Color.DARK_GRAY); //Create the background for outside the grid
        TextureRegion gridBackground = UIManager.makeFlatColor(background); //Create the background of the grid
        OrthographicCamera mapCam = (OrthographicCamera) EngineManager.getMapStage().getCamera();
        if (gridLines == null) { //Create grid texture only if we dont already have one
            gridLines = createLines(width, height, lines);
        }

        batch.setProjectionMatrix(EngineManager.getCamera().combined);

        batch.begin(); //Draw the textures
        batch.draw(mapBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(gridBackground, 0, 0, gridWidth, gridHeight);
        batch.draw(gridLines, 0, 0, gridWidth, gridHeight);
        batch.end();


    }

    /**
     * This method creates the texture for the grid
     *
     * @param width  how many squares wide
     * @param height how many squares high
     * @param lines  the color of the lines
     * @return the new texture
     */
    private TextureRegion createLines(int width, int height, Color lines) {
        Pixmap bgPixmap = new Pixmap(width * 30, height * 30, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(lines);
        for (int i = 0; i <= width; i++) {
            bgPixmap.drawLine(i * 30, 0, i * 30, height * 30);
        }
        for (int i = 0; i <= height; i++) {
            bgPixmap.drawLine(0, i * 30, width * 30, i * 30);
        }

        TextureRegion textureRegion = new TextureRegion(new Texture(bgPixmap));
        bgPixmap.dispose();
        return textureRegion;
    }


    public float getGridHeight() {
        return gridHeight;
    }

    public float getGridWidth() {
        return gridWidth;
    }
}

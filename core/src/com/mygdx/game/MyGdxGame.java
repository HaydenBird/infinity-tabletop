package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.UIManager;

/**
 * This is the main class of the program. It handles set up and the render loop
 */
public class MyGdxGame extends Game {
    private EngineManager engineManager;
    private TableTopRenderer renderer;
    private World world;
    private RayHandler rayHandler;
    private SpriteBatch batch;
    private InputMultiplexer inputMultiplexer;

    /**
     * This method is used to set up the initial requirement for the game to run
     */
    @Override
    public void create() {
        inputMultiplexer = new InputMultiplexer(); //Create the multiplexer that will let the various intractable objects all be used
        engineManager = new EngineManager(); //Initialize the engine manager
        batch = new SpriteBatch(); //Initialize the batch that will be used for rendering
        EngineManager.createNewCampaign(); //Set up the user data for the campaign

        renderer = new TableTopRenderer(); //Initialize the renderer
        this.setScreen(new TableTopMap("New Map", this, true)); //Set the currently rendered map to be a new one
        rayHandler = EngineManager.getRayHandler(world); //Initialize and set up the ray handler
        EngineManager.setCurrentMap(EngineManager.getCurrentMap());
        UIManager.setGame(this);
        //Test tokens for the map
        TableTopToken t = new TableTopToken(0, 0 / EngineManager.getRatio(), "assets/badlogic.jpg", EngineManager.getCurrentMap());
        TableTopToken t2 = new TableTopToken(200, 200 / EngineManager.getRatio(), "assets/badlogic.jpg", EngineManager.getCurrentMap());
        t2.enableOmniLight(Color.WHITE, 1000);
        t.createBody(EngineManager.getCurrentMap().getWorld());

        EngineManager.getMapStage().addListener(new KeyListener(renderer)); //Add a listener for zooming
        inputMultiplexer.addProcessor(UIManager.getStage()); //Add all the things that need to have inputs handled to the multiplexer
        inputMultiplexer.addProcessor(EngineManager.getMapStage());
        GestureDetector gestureDetector = new GestureDetector(new BackgroundListener(renderer));
        inputMultiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(inputMultiplexer);
        EngineManager.getMapStage().addListener(new DragListener() { //Enable dragging with rightclick
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (this.getButton() == 1) {
                    EngineManager.translateMap(x, y, renderer);
                }
            }
        });
    }

    /**
     * This is the main render loop of the game, called for each fram
     */
    @Override
    public void render() {
        //Clear the screen
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        //Call the renderer to handle all the rendering
        renderer.render(rayHandler, batch);

    }

    @Override
    public void dispose() {
        rayHandler.dispose();
    }


    /**
     * This method is called when the window is resized
     *
     * @param width  the new screen width
     * @param height the new screen height
     */
    @Override
    public void resize(int width, int height) {
        UIManager.getStage().getViewport().update(width, height, true);
        EngineManager.getMapStage().getViewport().update(width, height);
        EngineManager.getCamera().update();
        OrthographicCamera mC = (OrthographicCamera) EngineManager.getMapStage().getCamera();
        mC.setToOrtho(false, width, height);
        EngineManager.getMapStage().getCamera().update();
    }
}

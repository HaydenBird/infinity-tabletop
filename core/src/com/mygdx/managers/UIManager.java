package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.TableTopMap;
import com.mygdx.tabletop.Entry;
import com.mygdx.tabletop.Npc;
import com.mygdx.tabletop.PlayerCharacter;
import com.mygdx.ui.MapTab;
import sun.security.ssl.Debug;

public class UIManager {

    private static TabbedPane mapPanes;
    private static Table mapContainer;
    private static Table tokenArea;
    private static MenuBar menuBar;
    private static Menu fileMenu;
    private static Menu tokenMenu;
    private static Menu mapMenu;
    private static MyGdxGame game;

    public static Stage getStage() {
        return stage;
    }

    private static Stage stage;
    private static OrthographicCamera camera;
    private static Table container;

    private static Table topRow;
    private static ScrollPane entryList;
    private static Table chatLog;
    private static TabbedPane.TabbedPaneTable mapPaneWindow;
    private static Table entryContainer;

    private static VisTree entryTree;
    private static VisTree.Node pcNode;
    private static VisTree.Node npcNode;
    private static VisTree.Node handoutNode;

    public static void init() {
        //TODO: Build the base UI
        camera = EngineManager.getCamera();
        stage = new Stage(new ScreenViewport(camera));

        Gdx.input.setInputProcessor(stage);
        buildFrame();
        buildTopRow();
        buildEntriesBox();
        buildMapWindow();
        buildChatBox();

    }

    public static MyGdxGame getGame() {
        return game;
    }

    public static void setGame(MyGdxGame game) {
        UIManager.game = game;
    }

    private static void buildMapWindow() {
        mapContainer = new Table();
        mapPanes = new TabbedPane();
        mapPaneWindow = mapPanes.getTable();
        mapContainer.add(mapPaneWindow).growX();
        mapContainer.row();
        tokenArea = new Table();
        mapContainer.add(tokenArea).grow();
        container.add(mapContainer).grow().colspan(6);

    }

    public static void scrollPaneLayout(ScrollPane.ScrollPaneStyle style) {
        TextureAtlas atlas = EngineManager.getAtlas();
        style.background = new TextureRegionDrawable(atlas.findRegion("window-noborder"));
    }


    public static void colorBackground(Table a, Color c) {
        a.setBackground(makeBackground(c));
    }

    public static TextureRegion makeFlatColor(Color c) {
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        bgPixmap.setColor(c);
        bgPixmap.fill();
        TextureRegion textureRegion = new TextureRegion(new Texture(bgPixmap));
        bgPixmap.dispose();
        return textureRegion;
    }

    public static Drawable makeBackground(Color c) {
        return new TextureRegionDrawable(makeFlatColor(c));
    }

    private static void buildFrame() {
        container = new Table();
        container.setFillParent(true);
        stage.addActor(container);
    }

    private static void buildTopRow() {
        EngineManager.getSkin();
        menuBar = new MenuBar();


        buildFileMenu();
        buildTokenMenu();
        buildMapMenu();
        container.add(menuBar.getTable()).fill().top().prefHeight(container.getHeight() / 10).colspan(12);
        container.row();

    }

    private static void buildFileMenu() {
        fileMenu = new Menu("File");
        fileMenu.addItem(new MenuItem("New"));
        fileMenu.addItem(new MenuItem("Open"));
        fileMenu.addItem(new MenuItem("Save"));
        fileMenu.addItem(new MenuItem("Save As"));
        fileMenu.addSeparator();
        MenuItem exit = new MenuItem("Exit", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
        exit.setShortcut(Input.Keys.ALT_LEFT, Input.Keys.F4);
        fileMenu.addItem(exit);
        menuBar.addMenu(fileMenu);
    }

    private static void buildTokenMenu() {
        tokenMenu = new Menu("Token");
        MenuItem layerSelect = new MenuItem("Layer");
        PopupMenu layerSubmenu = new PopupMenu();
        layerSubmenu.addItem(new MenuItem("Map"));
        layerSubmenu.addItem(new MenuItem("Blocking"));
        layerSubmenu.addItem(new MenuItem("Token"));
        layerSelect.setSubMenu(layerSubmenu);
        tokenMenu.addItem(layerSelect);
        tokenMenu.addItem(new MenuItem("Copy"));
        tokenMenu.addItem(new MenuItem("Cut"));
        tokenMenu.addItem(new MenuItem("Paste"));
        tokenMenu.addItem(new MenuItem("Change image"));
        tokenMenu.addItem(new MenuItem("Size"));
        tokenMenu.addItem(new MenuItem("Set entry"));
        menuBar.addMenu(tokenMenu);
    }

    private static void buildMapMenu() {
        mapMenu = new Menu("Map");
        MenuItem addMenu = new MenuItem("New Map", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new TableTopMap("New Map", UIManager.getGame(), true);
            }

        });
        mapMenu.addItem(addMenu);
        menuBar.addMenu(mapMenu);

    }

    private static void buildEntriesBox() {
        entryTree = new VisTree();
        entryList = new ScrollPane(entryTree, EngineManager.getSkin().get(ScrollPane.ScrollPaneStyle.class));
        entryContainer = new Table();
        colorBackground(entryContainer, Color.DARK_GRAY);
        entryContainer.add(entryList).fill().left().expand();
        container.add(entryContainer).fill().left().expand().colspan(2);
        pcNode = new VisTree.Node(new Label("Player Characters", EngineManager.getSkin()));
        npcNode = new VisTree.Node(new Label("NPCs", EngineManager.getSkin()));
        handoutNode = new VisTree.Node(new Label("Handouts", EngineManager.getSkin()));
        entryTree.add(pcNode);
        entryTree.add(npcNode);
        entryTree.add(handoutNode);


    }


    private static void buildChatBox() {
        chatLog = new Table();
        Table chatContainer = new Table();
        ScrollPane chatLogContainer = new ScrollPane(chatLog, EngineManager.getSkin());
        chatLogContainer.setScrollingDisabled(false, false);
        chatContainer.add(chatLogContainer).fill().expand();
        colorBackground(chatContainer, Color.DARK_GRAY);
        TextField chatEntry = new TextField("", EngineManager.getSkin());
        chatEntry.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent e, int keyCode) {
                if (keyCode == Input.Keys.ENTER && !chatEntry.getText().trim().isEmpty()) {
                    boolean isBottom = false;
                    if (chatLogContainer.isBottomEdge()) {
                        isBottom = true;
                    }
                    chatLog.add(EngineManager.getRollManager().parseMessage(chatEntry.getText())).fill().expand().row();
                    chatEntry.setText(null);
                    if (isBottom) {
                        chatLogContainer.layout();
                        chatLogContainer.scrollTo(0, 0, 0, 0);
                    }
                }
                return false;
            }
        });
        chatContainer.row();
        chatContainer.add(chatEntry).fillX().expandX();
        container.add(chatContainer).fill().expand().colspan(2);
        for (int i = 1; i < 5; i++) {
            chatLog.add(EngineManager.getRollManager().parseMessage("Chat message [1d12] [1d12] [1d12]")).fill().expand();
            chatLog.row();
        }

    }

    private static void buildToolBox() {

    }

    private static void buildDropdownMenus() {

    }

    public static void addEntry(Entry entry) {
        switch (entry.getEntryType()) {
            case "PlayerCharacter":
                pcNode.add(new VisTree.Node(entry));
                break;
            case "Npc":
                npcNode.add(new VisTree.Node(entry));
                break;
            case "Handout":
                handoutNode.add(new VisTree.Node(entry));
                break;
        }

    }

    public static void addSubentry(PlayerCharacter character, Entry entry) {
        if (entry.getEntryType().equals("PlayerCharacter") || entry.getEntryType().equals("Npc")) return;
        for (VisTree.Node node : pcNode.getChildren()) {
            if (node.getActor().equals(character)) node.add(new VisTree.Node(entry));
        }
    }

    public static void addSubentry(Npc character, Entry entry) {
        if (entry.getEntryType().equals("PlayerCharacter") || entry.getEntryType().equals("Npc")) return;
        for (VisTree.Node node : npcNode.getChildren()) {
            if (node.getActor().equals(character)) node.add(new VisTree.Node(entry));
        }
    }

    public static void addMap(TableTopMap tableTopMap) {
        mapPanes.add(new MapTab(tableTopMap.getName(), tableTopMap));
    }


    public static Vector2 getTokenAreaCoords() {
        Vector2 coord = new Vector2(0, 0);
        tokenArea.localToStageCoordinates(coord);
        tokenArea.getStage().stageToScreenCoordinates(coord);
        Debug.println("Debug", "(" + coord.x + "," + coord.y + ")");
        return coord;
    }


    public static Table getTokenArea() {
        return tokenArea;
    }
}

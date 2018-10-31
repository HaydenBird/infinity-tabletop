package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopToken;
import com.mygdx.tabletop.Entry;
import com.mygdx.tabletop.Npc;
import com.mygdx.tabletop.PlayerCharacter;
import com.mygdx.ui.MapTab;

/**
 * This class handles the creation of all the HUD elements, accessing them, and the methods that they rely on.
 */
public class UIManager {

    private static TabbedPane mapPanes; //The tabbed pane used to swap between maps
    private static Table mapContainer; //An empty table used to create the hole in the hud in which we can view the map
    private static Table tokenArea;
    private static MyGdxGame game; //The instance of the game

    private static MenuBar menuBar; //The menu bar
    private static Menu fileMenu;
    private static Menu tokenMenu;
    private static Menu mapMenu;


    private static Stage stage; //The Stage on which all the HUD elements are added
    private static OrthographicCamera camera; //
    private static Table container; //The container for the HUD

    private static ScrollPane entryList; // The list of entires on the left
    private static Table chatLog; //Chat log
    private static TabbedPane.TabbedPaneTable mapPaneWindow;
    private static Table entryContainer;

    private static VisTree entryTree;
    private static VisTree.Node pcNode;
    private static VisTree.Node npcNode;
    private static VisTree.Node handoutNode;

    /**
     * This method is used to build the UI and set up the stage
     */
    public static void init() {
        camera = EngineManager.getCamera();
        stage = new Stage(new ScreenViewport(camera));

        Gdx.input.setInputProcessor(stage);
        buildFrame();
        buildTopRow();
        buildEntriesBox();
        buildMapWindow();
        buildChatBox();

    }

    /**
     * @return the UI stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * @return the game
     */
    public static MyGdxGame getGame() {
        return game;
    }

    /**
     * Sets the game
     * @param game the game
     */
    public static void setGame(MyGdxGame game) {
        UIManager.game = game;
    }

    /**
     * This method builds the tabbed pane and empty window in the middle of the UI
     */
    private static void buildMapWindow() {
        mapContainer = new Table();
        mapPanes = new TabbedPane();
        mapPanes.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                super.switchedTab(tab);
                MapTab newCurrent = (MapTab) tab;
                EngineManager.setCurrentMap(newCurrent.getMap());
            }
        });
        mapPaneWindow = mapPanes.getTable();
        mapContainer.add(mapPaneWindow).growX();
        mapContainer.row();
        tokenArea = new Table();
        mapContainer.add(tokenArea).grow();
        container.add(mapContainer).grow().colspan(6);

    }

    /**
     * This method changes edits a scrollpane style to have no border
     * @param style the style to be edited
     */
    public static void scrollPaneLayout(ScrollPane.ScrollPaneStyle style) {
        TextureAtlas atlas = EngineManager.getAtlas();
        style.background = new TextureRegionDrawable(atlas.findRegion("window-noborder"));
    }

    /**
     * This method takes a table and gives it a background of a chosen color
     * @param a the table
     * @param c the color
     */
    public static void colorBackground(Table a, Color c) {
        a.setBackground(makeBackground(c));
    }

    /**
     * This method creates a 1x1 texture of a color
     * @param c the color
     * @return the texture
     */
    public static TextureRegion makeFlatColor(Color c) {
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        bgPixmap.setColor(c);
        bgPixmap.fill();
        TextureRegion textureRegion = new TextureRegion(new Texture(bgPixmap));
        bgPixmap.dispose();
        return textureRegion;
    }

    /**
     * This method creates a 1x1 drawable of a given color
     * @param c the color
     * @return the drawable
     */
    public static Drawable makeBackground(Color c) {
        return new TextureRegionDrawable(makeFlatColor(c));
    }

    /**
     * This method sets up the container
     */
    private static void buildFrame() {
        container = new Table();
        container.setFillParent(true);
        stage.addActor(container);
    }

    /**
     * This method creates the menu bar
     */
    private static void buildTopRow() {
        EngineManager.getSkin();
        menuBar = new MenuBar();


        buildFileMenu();
        buildTokenMenu();
        buildMapMenu();
        container.add(menuBar.getTable()).fill().top().prefHeight(container.getHeight() / 10).colspan(12);
        container.row();

    }

    /**
     * This method populates the file menu option
     */
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

    /**
     * This method populates the token menu option
     */
    private static void buildTokenMenu() {
        tokenMenu = new Menu("Token");
        createAddTokenButton(tokenMenu);
        createEnableLightButton(tokenMenu);
        MenuItem layerSelect = new MenuItem("Move to layer");
        createChangeLayerButton(layerSelect);
        tokenMenu.addItem(layerSelect);
        tokenMenu.addItem(new MenuItem("Copy"));
        tokenMenu.addItem(new MenuItem("Cut"));
        tokenMenu.addItem(new MenuItem("Paste"));
        tokenMenu.addItem(new MenuItem("Change image"));
        tokenMenu.addItem(new MenuItem("Size"));
        tokenMenu.addItem(new MenuItem("Set entry"));
        menuBar.addMenu(tokenMenu);
    }

    private static void createSelectLayerButton(MenuItem layerSelect) {
        PopupMenu layerSubmenu = new PopupMenu();
        MenuItem mapLayer = new MenuItem("Map");
        MenuItem blockingLayer = new MenuItem("Blocking");
        MenuItem tokenLayer = new MenuItem("Token");

        mapLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EngineManager.setLayer(TableTopMap.Layer.MAP);
                EngineManager.clearSelectedTokens();
            }
        });

        blockingLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EngineManager.setLayer(TableTopMap.Layer.BLOCKING);
                EngineManager.clearSelectedTokens();
            }
        });

        tokenLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EngineManager.setLayer(TableTopMap.Layer.TOKEN);
                EngineManager.clearSelectedTokens();
            }
        });
        tokenLayer.setChecked(true);
        layerSubmenu.addItem(mapLayer);
        layerSubmenu.addItem(blockingLayer);
        layerSubmenu.addItem(tokenLayer);

        layerSelect.setSubMenu(layerSubmenu);
    }


    private static void createChangeLayerButton(MenuItem layerSelect) {
        PopupMenu layerSubmenu = new PopupMenu();
        MenuItem mapLayer = new MenuItem("Map");
        MenuItem blockingLayer = new MenuItem("Blocking");
        MenuItem tokenLayer = new MenuItem("Token");

        mapLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (TableTopToken token : EngineManager.getSelectedTokens()) {
                    token.setLayer(TableTopMap.Layer.MAP);
                }
                EngineManager.setLayer(TableTopMap.Layer.MAP);
            }
        });

        blockingLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (TableTopToken token : EngineManager.getSelectedTokens()) {
                    token.setLayer(TableTopMap.Layer.BLOCKING);
                }
                EngineManager.setLayer(TableTopMap.Layer.BLOCKING);
            }
        });

        tokenLayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (TableTopToken token : EngineManager.getSelectedTokens()) {
                    token.setLayer(TableTopMap.Layer.TOKEN);
                }
                EngineManager.setLayer(TableTopMap.Layer.TOKEN);
            }
        });
        tokenLayer.setChecked(true);
        layerSubmenu.addItem(mapLayer);
        layerSubmenu.addItem(blockingLayer);
        layerSubmenu.addItem(tokenLayer);

        layerSelect.setSubMenu(layerSubmenu);
    }


    /**
     * This method creates the enable light button
     */
    private static void createEnableLightButton(Menu tokenMenu) {
        MenuItem addLight = new MenuItem("Lighting");
        PopupMenu lightSubMenu = new PopupMenu();
        MenuItem omniLightButton = new MenuItem("Omnidirectional Light", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {
                    @Override
                    public void finished(Color newColor) {
                        Dialogs.showInputDialog(getStage(), "Light distance", "Distance: ", new InputDialogAdapter() {
                            @Override
                            public void finished(String input) {
                                for (TableTopToken token : EngineManager.getSelectedTokens()) {
                                    token.enableOmniLight(newColor, Integer.parseInt(input));
                                }

                            }
                        });
                    }
                });
                getStage().addActor(picker);
            }
        });
        lightSubMenu.addItem(omniLightButton);
        addLight.setSubMenu(lightSubMenu);
        tokenMenu.addItem(addLight);
    }

    /**
     * This method creates the submenu to create a token
     * @param tokenMenu the menu toadd the submenu to
     */
    private static void createAddTokenButton(Menu tokenMenu) {
        MenuItem newToken = new MenuItem("New");
        PopupMenu newSubmenu = new PopupMenu();
        MenuItem addToMap = new MenuItem("Map", new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UIManager.createToken(TableTopMap.Layer.MAP);
            }
        });

        MenuItem addToBlocking = new MenuItem("Blocking", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UIManager.createToken(TableTopMap.Layer.BLOCKING);
            }
        });

        MenuItem addToToken = new MenuItem("Token", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UIManager.createToken(TableTopMap.Layer.TOKEN);
            }
        });
        newSubmenu.addItem(addToMap);
        newSubmenu.addItem(addToBlocking);
        newSubmenu.addItem(addToToken);
        newToken.setSubMenu(newSubmenu);
        tokenMenu.addItem(newToken);


    }

    /**
     * This method takes a layer and prompts the user for an image file, it then creates a token centred on the screen
     * @param layer the layer to add the token to
     */
    private static void createToken(int layer) {
        FileChooser.setDefaultPrefsName("com.mygdx");
        FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setDirectory(Gdx.files.getLocalStoragePath());
        FileTypeFilter typeFilter = new FileTypeFilter(true);
        typeFilter.addRule("Image files (*.png, *.jpg, *.gif)", "png", "jpg", "gif");
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> file) {
                TableTopToken newToken = new TableTopToken(EngineManager.getMapStage().getCamera().position.x, EngineManager.getMapStage().getCamera().position.y,
                        file.get(0).path(), EngineManager.getCurrentMap(), layer);
                EngineManager.getCurrentMap().addToken(newToken, layer);
            }
        });
        getStage().addActor(fileChooser.fadeIn());
    }

    /**
     * This method builds the Map menu option
     */
    private static void buildMapMenu() {
        mapMenu = new Menu("Map");
        MenuItem addMenu = new MenuItem("New Map", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new TableTopMap("New Map", UIManager.getGame(), true);
            }

        });
        mapMenu.addItem(addMenu);

        MenuItem renameMenu = new MenuItem("Rename", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapTab tab = (MapTab) mapPanes.getActiveTab();
                Dialogs.showInputDialog(getStage(), "Rename Map", "Name: ", new InputDialogAdapter() {
                    @Override
                    public void finished(String input) {
                        tab.rename(input);
                        tab.dirty();
                    }
                });
            }

        });
        mapMenu.addItem(renameMenu);
        MenuItem layerSelect = new MenuItem("Layer");
        createSelectLayerButton(layerSelect);
        mapMenu.addItem(layerSelect);

        MenuItem movePlayer = new MenuItem("Move players to current map");
        mapMenu.addItem(movePlayer);

        menuBar.addMenu(mapMenu);

    }

    /**
     * This method builds the e ntry box
     */
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

    /**
     * This method creates the chat box
     */
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

    /**
     * This method adds an entry to the list of entries under the entries tree
     * @param entry the entry to add
     */
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

    /**
     * This method adds an entry as a child of an existing entry, which must be a PC
     * @param character the entry that will have the child
     * @param entry the child
     */
    public static void addSubentry(PlayerCharacter character, Entry entry) {
        if (entry.getEntryType().equals("PlayerCharacter") || entry.getEntryType().equals("Npc")) return;
        for (VisTree.Node node : pcNode.getChildren()) {
            if (node.getActor().equals(character)) node.add(new VisTree.Node(entry));
        }
    }


    /**
     * This method adds an entry as a child of an existing entry, which must be a NPC
     * @param character the entry that will have the child
     * @param entry the child
     */
    public static void addSubentry(Npc character, Entry entry) {
        if (entry.getEntryType().equals("PlayerCharacter") || entry.getEntryType().equals("Npc")) return;
        for (VisTree.Node node : npcNode.getChildren()) {
            if (node.getActor().equals(character)) node.add(new VisTree.Node(entry));
        }
    }

    /**
     * This method adds a map to the tabs
     * @param tableTopMap the map to add
     */
    public static void addMap(TableTopMap tableTopMap) {
        mapPanes.add(new MapTab(tableTopMap.getName(), tableTopMap));
    }
}

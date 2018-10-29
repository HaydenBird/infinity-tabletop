package com.mygdx.managers;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class SaveManager {

    public SaveManager() {

    }

    public static void saveGameState(String filepath) {
        saveHandouts(filepath);
        savePlayers(filepath);
        saveMaps(filepath);
        saveChatLog(filepath);
        saveCampaign(filepath);

    }

    private static void saveCampaign(String filepath) {

    }

    public static void saveChatLog(String filepath) {
        //Convert each chat message to json
    }

    public static void saveMaps(String filepath) {
        //Convert each Map to json

    }

    public static void savePlayers(String filepath) {
        //Convert each Player to json
        Gson gson = new Gson();
        String playerJson = gson.toJson(EngineManager.getLoadedCampaign().getPlayers());
        saveFile(filepath, "Player.json", playerJson);
    }

    public static void saveHandouts(String filepath) {
        //Convert each handout to json
        Gson gson = new Gson();
        //PC
        String pcJson = gson.toJson(ListManager.getCharacterEntries());
        saveFile(filepath, "PC.json", pcJson);
        //NPCs
        String npcJson = gson.toJson(ListManager.getNpcEntries());
        saveFile(filepath, "NPC.json", npcJson);
        //Handout
        String handoutJson = gson.toJson(ListManager.getHandoutEntries());
        saveFile(filepath, "Handout.json", handoutJson);
        //Item
        String itemJson = gson.toJson(ListManager.getItemEntries());
        saveFile(filepath, "Item.json", itemJson);
        //Boon
        String boonJson = gson.toJson(ListManager.getBoonEntries());
        saveFile(filepath, "Boon.json", boonJson);
    }


    private static void saveFile(String filepath, String filename, String data) {
        Path file = Paths.get(filepath + filename);
        try {
            Files.write(file, Collections.singleton(data), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //TODO: Loading from file

}

package com.mygdx.managers;

import com.badlogic.gdx.graphics.Texture;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class TextureManager {

    private static HashMap<String, Texture> textureMap = new HashMap<>();


    public static Texture getTexture(String filepath) throws FileNotFoundException {
        if (textureMap.containsKey(filepath)) {
            return textureMap.get(filepath);
        } else {
            EngineManager.getAssetManager().load(filepath, Texture.class);
            EngineManager.getAssetManager().update();
            return EngineManager.getAssetManager().get(filepath);
        }
    }


}

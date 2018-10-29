package com.mygdx.tabletop;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.LinkedList;
import java.util.List;

public class Campaign {

    /* Things we need to store
     *
     * List of players
     * List of hashes of player passwords
     * List of chat messages
     * List of maps - maps will save their state
     * Who is the gm
     *
     *
     * We will store these to a local files
     *
     *
     *
     * Save file formats
     *
     *
     * */

    private List<Player> playerList; //Players are stored in the hashmap to see if they are
    private List<Player> onlinePlayers;
    private List<Player> gameMasters;


    public Campaign() {
        playerList = new LinkedList<>();
        onlinePlayers = new LinkedList<>();
        gameMasters = new LinkedList<>();
    }


    public List<Player> getPlayers() {
        return playerList;
    }

    public void addNewMap() {
        Map newMap = new Map();
        World newWorld = new World(new Vector2(0, 0), true);
    }

}

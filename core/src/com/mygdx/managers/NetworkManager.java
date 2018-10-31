package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.mygdx.tabletop.Campaign;

/**
 * This class will handle all the communication between instances of the program
 *
 *
 * Messages that we can send:
 *
 * response [message id] [response code]
 *
 *
 * Player connect message:
 *  connect [display name] [id] [password hash] [message id]
 *
 * Token created message:
 *  token [parent map id] [token id] [token X] [token Y] [layer] [image asset name] [id of the image to transfer] [message id]
 *
 * Token moved:
 * move [token id] [new X] [new Y] [new layer] [new width] [new height] [new rotation] [message id]
 *
 * Token image changed:
 * changeimage [token id] [new asset name] [id of the message transfer] [message id]
 *
 * Token light changed:
 * lightChange [token id] [light type] [light color] [light distance]
 *
 * Associate token with entry
 * associate [token id] [entry id] [message id]
 *
 * Create entry:
 * newentry [entry id] [entry name] [entry type] [message id]
 *
 * Link entry:
 * linkentry [entry id] [URL] [message id]
 *
 * New Map Created:
 * newmap [map id] [map name] [map width] [map height] [ambient light r] [ambient light g] [ambient light b] [ambient light a] [move players true/false] [message id]
 *
 * Move players to map:
 * movetomap [map id] [player id 1] ... [player id n] [message id]
 * movealltomap [map id] [message id]
 *
 * Chat message sent
 * chat [message 1] [rolls 1] ... [message n] [rolls n] [style] [from] [gm only true/false] [message id]
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class NetworkManager {

    private static ServerSocket serverSocket;

    public static void startServer(String host, int port) {
        ServerSocketHints serverHints = new ServerSocketHints();
        serverSocket = Gdx.net.newServerSocket(Protocol.TCP, host, port, serverHints);

    }

    public static Campaign getNetworkCampaign(String ipAddress, String port) {
        return null;
        //TODO: load a gamestate from network
    }

    public static void clearCommandQueue() {
        //TODO: clear command queue
    }


    private class listenForPlayers implements Runnable {

        @Override
        public void run() {
            while (true) {
                //TODO: Add new player in a threadsafe way
            }
        }
    }


    //TODO: Sending gamestate to a player

    //TODO: Checking if players are still connected

    //TODO: Determine what assets a player can see

    //TODO: Determine what has changed since player last updated

    //TODO: cache image assets


}

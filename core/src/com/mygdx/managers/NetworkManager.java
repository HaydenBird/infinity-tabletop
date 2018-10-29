package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.mygdx.tabletop.Campaign;

/**
 * This class will handle all the communication between instances of the program
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

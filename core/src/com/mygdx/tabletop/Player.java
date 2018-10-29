package com.mygdx.tabletop;

import com.badlogic.gdx.net.Socket;

public class Player {

    private String displayName;
    private String playerID;
    private Socket playerSocket;
    private String passwordHash;
    private boolean online;

    public Player(String displayName, String playerID, Socket playerSocket) {
        this.displayName = displayName;
        this.playerID = playerID;
        this.playerSocket = playerSocket;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean attemptLogon(Socket newSocket, String passwordHash) {
        if (this.passwordHash != passwordHash) return false;
        setOnline(true);
        this.playerSocket = newSocket;
        return true;
    }
}

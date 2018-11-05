package com.mygdx.tabletop;

import com.badlogic.gdx.net.Socket;

public class Player {

    private boolean isSelf = false;
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


    public Player(String displayName, String playerID) {
        this.displayName = displayName;
        this.playerID = playerID;
        this.playerSocket = null;
        this.isSelf = true;
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

    public String getDisplayName() {
        return "Me";
    }

    public String getUserId() {
        return "user1";
    }

    public String promptPassword() {
        return "xysssx";
    }
}

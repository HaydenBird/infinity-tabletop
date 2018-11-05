package com.mygdx.containers;

import com.badlogic.gdx.net.Socket;

import java.util.List;

public class Command {


    private CommandType type;
    private List<String> arguments;
    private Socket originSocket;

    public Command(CommandType type, List<String> arguments, Socket originSocket) {
        this.type = type;
        this.arguments = arguments;
        this.originSocket = originSocket;
    }

    public CommandType getType() {
        return type;
    }

    public String get(int i) {
        return arguments.get(i);
    }

    public Socket getSocket() {
        return originSocket;
    }

    public static CommandType getType(String type) {
        switch (type) {
            case "response":
                return CommandType.RESPONSE;
            case "newasset":
                return CommandType.NEW_ASSET;
            case "connect":
                return CommandType.CONNECT;
            case "checkin":
                return CommandType.CHECK_IN;
            case "token":
                return CommandType.TOKEN;
            case "move":
                return CommandType.MOVE;
            case "changeimage":
                return CommandType.CHANGE_IMAGE;
            case "lightchange":
                return CommandType.LIGHT_CHANGE;
            case "addowner":
                return CommandType.ADD_OWNER;
            case "removeowner":
                return CommandType.REMOVE_OWNER;
            case "associate":
                return CommandType.ASSOCIATE;
            case "newentry":
                return CommandType.NEW_ENTRY;
            case "linkentry":
                return CommandType.LINK_ENTRY;
            case "newmap":
                return CommandType.NEW_MAP;
            case "movetomap":
                return CommandType.MOVE_TO_MAP;
            case "movealltomap":
                return CommandType.MOVE_ALL_TO_MAP;
            case "chat":
                return CommandType.CHAT;
            case "removetoken":
                return CommandType.REMOVE_TOKEN;
            case "removemap":
                return CommandType.REMOVE_MAP;
            case "removeentry":
                return CommandType.REMOVE_ENTRY;
            default:
                return CommandType.ERROR;
        }
    }

    public enum CommandType {
        RESPONSE,
        CONNECT,
        CHECK_IN,
        TOKEN,
        MOVE,
        CHANGE_IMAGE,
        LIGHT_CHANGE,
        ASSOCIATE,
        NEW_ENTRY,
        LINK_ENTRY,
        NEW_MAP,
        MOVE_TO_MAP,
        MOVE_ALL_TO_MAP,
        CHAT,
        NEW_FILE,
        NEW_ASSET,
        ADD_OWNER,
        REMOVE_OWNER,
        REMOVE_TOKEN,
        REMOVE_MAP,
        REMOVE_ENTRY,
        ERROR

    }

}

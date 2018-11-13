package com.mygdx.containers;

import com.badlogic.gdx.net.Socket;
import sun.security.ssl.Debug;

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

    public static String commandTypeToString(CommandType type) {
        switch (type) {
            case RESPONSE:
                return "response";
            case CONNECT:
                return "connect";
            case CHECK_IN:
                return "checkin";
            case TOKEN:
                return "token";
            case MOVE:
                return "move";
            case CHANGE_IMAGE:
                return "changeimage";
            case LIGHT_CHANGE:
                return "lightchange";
            case ASSOCIATE:
                return "associate";
            case NEW_ENTRY:
                return "newentry";
            case LINK_ENTRY:
                return "linkentry";
            case NEW_MAP:
                return "newmap";
            case MOVE_TO_MAP:
                return "movetomap";
            case MOVE_ALL_TO_MAP:
                return "movealltomap";
            case CHAT:
                return "chat";
            case NEW_FILE:
                return "newfile";
            case NEW_ASSET:
                return "newasset";
            case ADD_OWNER:
                return "addowner";
            case REMOVE_OWNER:
                return "removeowner";
            case REMOVE_TOKEN:
                return "removetoken";
            case REMOVE_MAP:
                return "removemap";
            case REMOVE_ENTRY:
                return "removeentry";
            case ERROR:
                return "error";
        }
        return "error";
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

    @Override
    public String toString() {
        String commandString = commandTypeToString(type);
        for (String s : arguments) {
            Debug.println("Part", s);
            commandString += "‗";
            commandString += s;
        }
        return commandString;
    }
}

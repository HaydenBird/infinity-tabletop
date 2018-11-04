package com.mygdx.containers;

import java.net.Socket;
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
        NEW_FILE
    }

}

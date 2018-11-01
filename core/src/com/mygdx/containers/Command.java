package com.mygdx.containers;

public class Command {


    private CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
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
        NEW_FILE;
    }

}

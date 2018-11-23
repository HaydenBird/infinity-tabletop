package com.mygdx.containers;

public class MessageComponent {

    private Object messageComponent;
    private boolean isRollContainer = true;


    public void addRollContainer(RollContainer rollContainer) {
        messageComponent = rollContainer;
        isRollContainer = true;
    }

    public void addString(String string) {
        isRollContainer = false;
    }

    public Class getStringOrContainer() {
        if (isRollContainer) return RollContainer.class;
        else return String.class;
    }

    public Object getMessageComponent() {
        return messageComponent;
    }
}

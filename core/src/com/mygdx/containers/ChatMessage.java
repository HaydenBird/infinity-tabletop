package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.managers.EngineManager;
import com.mygdx.tabletop.Player;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This object represents a message sent by a user and works to display them on the stage. It can be generated with or without any rolls.
 */
public class ChatMessage extends Table {

    private Date timeStamp;
    private List<Player> recipients;
    private Player sender;
    private List<MessageComponent> message;
    private HorizontalGroup group;

    public ChatMessage(Player sender, List<Player> recipients, Skin skin, List<MessageComponent> components) {
        timeStamp = new Date();
        this.recipients = recipients;
        if (this.recipients == null) this.recipients = new LinkedList<>();
        this.sender = sender;
        this.message = components;
        buildAppearance(components);
    }

    /**
     * This method formats the object so it can display properly in the chat log.
     */
    private void buildAppearance(List<MessageComponent> components) {
        this.clearChildren(); //Clear children so this method can be called again to reformat
        this.left(); //Align left
        this.padBottom(25);//Set padding
        this.padRight(25);
        this.addListener(new TextTooltip(timeStamp.toString(), EngineManager.getSkin())); //Create the tooltip
        this.group = new HorizontalGroup();
        for (MessageComponent component : components) {
            if (component.getStringOrContainer() == RollContainer.class) {
                group.addActor((RollContainer) component.getMessageComponent());
                ((RollContainer) component.getMessageComponent()).getRollResult();
            } else {
                Label label = new Label((String) component.getMessageComponent(), EngineManager.getSkin());
                group.addActor(label);
            }
        }


        //group.wrap();
        this.add(new Label("From: " + sender.getDisplayName(), EngineManager.getSkin()));
        this.row();
        this.add(group);

    }

    public Command getNetworkCommand() {
        //chat [number of messages] [number of message components] [message 1] ... [message n] [style] [from] [number of recipients] [recipient id 1] .. [recipient id n] [message id]
        List<String> args = new LinkedList<>();
        //TODO chat network commands
        args.add(message.size() + "");

        Command command = new Command(Command.CommandType.CHAT, args, null);
        return command;

    }

}

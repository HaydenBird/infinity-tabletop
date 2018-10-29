package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
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
    private List<String> message;
    private RollContainer rolls;
    private int messageLength;

    /**
     * Constructor without any rolls
     *
     * @param newMessage The text to be displayed, each item in the list is displayed on a new row
     * @param sender     The player who sent the message
     * @param recipients The list of players who will be able to see the message
     * @param skin       The libgdx skin to be styled by.
     */
    public ChatMessage(List<String> newMessage, Player sender, List<Player> recipients, Skin skin) {
        //TODO: Chat message constructor without rolls
        timeStamp = new Date();
        this.recipients = recipients;
        this.sender = sender;
        this.message = new LinkedList<>();
        this.message = newMessage;
        this.rolls = null;
        this.messageLength = newMessage.size();
        buildAppearance();
    }

    /**
     * Constructor with rolls
     *
     * @param fragmentedMessage The text to be displayed, each item in the list is displayed on a new row followed by one roll
     * @param sender            The player who sent the message
     * @param recipients        The list of players who will be able to see the message
     * @param rolls             The object that contains all the roll information
     * @param skin              The libgdx skin to be styled by
     */
    public ChatMessage(List<String> fragmentedMessage, Player sender, List<Player> recipients, RollContainer rolls, Skin skin) {
        timeStamp = new Date();
        this.recipients = recipients;
        this.sender = sender;
        this.message = fragmentedMessage;
        this.rolls = rolls;
        this.messageLength = Math.max(fragmentedMessage.size(), rolls.getRollResults().size());
        buildAppearance();
    }

    /**
     * This method formats the object so it can display properly in the chat log.
     */
    private void buildAppearance() {
        this.clearChildren(); //Clear children so this method can be called again to reformat
        this.left(); //Align left
        this.padBottom(25);//Set padding
        this.padRight(25);
        this.addListener(new TextTooltip(timeStamp.toString(), EngineManager.getSkin())); //Create the tooltip
        for (int i = 0; i < messageLength; i++) { //Iterate over the messages
            //Add text
            if ((i < message.size())) {
                Label nextLabel = new Label(message.get(i), EngineManager.getSkin());
                nextLabel.setWrap(true);
                this.add(nextLabel).expandX().fillX();
            }
            //Add Roll box
            if (rolls != null) {
                if ((i < rolls.getRollResults().size())) {
                    this.add(rolls.getRollResults().get(i));
                }
            }
            this.row();
        }
    }

}

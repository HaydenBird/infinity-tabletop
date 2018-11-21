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
    private List<String> message;
    private RollContainer rolls;
    private int messageLength;
    private HorizontalGroup group;

    public ChatMessage(Player sender, List<Player> recipients, Skin skin, List<MessageComponent> components) {
        timeStamp = new Date();
        this.recipients = recipients;
        if (this.recipients == null) this.recipients = new LinkedList<>();
        this.sender = sender;
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
        String labelText = "";
        for (MessageComponent component : components) {
            if (component.getStringOrContainer() == RollContainer.class) {
                labelText += ((RollContainer) component.getMessageComponent()).getRollResult();
            } else {
                labelText += (String) component.getMessageComponent();
            }
        }
        Label label = new Label(labelText, EngineManager.getSkin());
        group.addActor(label);
        group.wrap();
        this.add(new Label("From: " + sender.getDisplayName(), EngineManager.getSkin()));
        this.row();
        this.add(group);

    }

    public Command getNetworkCommand() {
        //chat [number of messages] [number of rolls] [message 1] ... [message n] [roll 1] ... [rolls n] [style] [from] [number of recipients] [recipient id 1] .. [recipient id 2] [message id]
        List<String> args = new LinkedList<>();
        args.add(message.size() + "");
        args.add(rolls.getRollResults().size() + "");
        for (String s : message) {
            args.add(s);
        }
        for (DicePool dp : rolls.getRollResults()) {
            String diceResultString = "";
            for (DiceResult dr : dp.getDice()) {
                //[hover string]--[final result]]@[result 2]...@[result n]

                diceResultString += dr.getHistory();
                diceResultString += "--";
                diceResultString += dr.getFinalResult();
                diceResultString += "@";

            }
            diceResultString += dp.getMod() + "";
            args.add(diceResultString);
        }
        //Style
        args.add("STYLEPLACEHOLDER");
        //from
        args.add(sender.getUserId());
        //number of recipients
        args.add(recipients.size() + "");
        //Recipient ids
        for (Player p : recipients) {
            args.add(p.getUserId());
        }
        Command command = new Command(Command.CommandType.CHAT, args, null);
        return command;

    }

}

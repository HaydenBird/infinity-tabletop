package com.mygdx.managers;

import com.mygdx.containers.*;
import sun.security.ssl.Debug;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles the creation of new rolls and chat messages from a string
 */
public class RollManager {

    /**
     * This method creates a chat message from a string, checking for any roll commands and handling them
     *
     * @param newMessage the string
     * @return the chat message
     */

    public ChatMessage parseMessage(String newMessage) {
        String pattern = "\\[[^\\]\\[]+\\]"; //this regex pattern matches for things encased in braces
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(newMessage);
        List<String> rollCommands = new LinkedList<>();
        while (m.find()) {
            rollCommands.add(m.group(0));
        }
        List<String> text = new LinkedList<>(Arrays.asList(newMessage.split("\\[[^\\]\\[]+\\]")));
        try {
            RollContainer rollResults = parseRollCommands(rollCommands);
            ChatMessage message = new ChatMessage(text, EngineManager.getCurrentPlayer(), null, rollResults, EngineManager.getSkin());
            NetworkManager.sendCommand(message.getNetworkCommand(), NetworkManager.getPlayers());
            return message;
        } catch (IncorrectFormattingError e) {
            Debug.println("Formatting error", "Debug");
            ChatMessage message = new ChatMessage(text, EngineManager.getCurrentPlayer(), null, EngineManager.getSkin());
            NetworkManager.sendCommand(message.getNetworkCommand(), NetworkManager.getPlayers());
            return message;
        }

    }

    /**
     * Roll commands come in the following forms:
     * [number of dice]d[sides of dice]+modifier
     * <p>
     * Modifiers:
     * <p>
     * -a -   advantage
     * -d -   disadvantage
     * Adv and disadv cancel eachother out
     * <p>
     * -rer [< or > or =] x   -   reroll any number greater than or less than or equal to x once
     * -rep [< or > or =] x y    - replace all rolls greater than or less than or equal to X with Y
     * <p>
     * -crit x   -   any number above x is a crit     *
     *
     * @param rollCommands the split list of commands in braces
     * @return the roll container that has the rolls for the message
     * @throws IncorrectFormattingError This error means the formatting of a roll command was wrong
     */
    private RollContainer parseRollCommands(List<String> rollCommands) throws IncorrectFormattingError {
        Pattern dicePattern = Pattern.compile("(\\d+)d(\\d+)"); //Finds the number and size of the dice
        Pattern modPattern = Pattern.compile("\\d+d\\d+\\+(\\d*)"); //Finds the modifier of the dice
        Pattern commandPattern = Pattern.compile("-[^-]+"); //Finds the roll parameters
        List<DicePool> diceResults = new LinkedList<>();
        for (String command : rollCommands) { //For each command
            command = command.replaceAll("\\[", "").replaceAll("\\]", ""); //Strip the unneeded symbols
            diceResults.add(new DicePool());
            List<Integer> diceNumber = new LinkedList<>();
            List<Integer> diceSize = new LinkedList<>();
            Matcher diceMatcher = dicePattern.matcher(command);
            Matcher argumentMatcher = commandPattern.matcher(command);
            Matcher modMatcher = modPattern.matcher(command);
            RollArguments rollArguments = new RollArguments();
            //Check for dice properties
            try {
                //Find the dice
                addDice(diceMatcher, diceSize, diceNumber);
                //Check for modifier
                float mod = findDiceModifier(modMatcher);
                //Check for arguments
                findArguments(rollArguments, argumentMatcher, command);
                //Roll the dice
                createDice(diceNumber, diceSize, rollArguments.getAdvantage(), rollArguments.getReroll(), rollArguments.getRerollTrigger(),
                        rollArguments.getReplace(), rollArguments.getReplaceValue(), rollArguments.getReplaceTrigger(), diceResults);
                diceResults.get(diceResults.size() - 1).addMod(mod);
            } catch (Exception e) {
                Debug.println(e.getClass().getName(), "Debug");
                throw new IncorrectFormattingError();
            }
        }
        return new RollContainer(diceResults, rollCommands);
    }

    /**
     * This method matches for dice size and number
     *
     * @param diceMatcher the matcher
     * @param diceSize    the list used to store the sizes of the dice
     * @param diceNumber  the list used to store the number of dice
     */
    private void addDice(Matcher diceMatcher, List<Integer> diceSize, List<Integer> diceNumber) {
        while (diceMatcher.find()) {
            diceNumber.add(Integer.parseInt(diceMatcher.group(1)));
            diceSize.add(Integer.parseInt(diceMatcher.group(2)));
        }
    }

    /**
     * This method finds the total modifier for a roll
     *
     * @param modMatcher the matcher
     * @return the total modifier
     */
    private float findDiceModifier(Matcher modMatcher) {
        float diceModifier = 0;
        while (modMatcher.find()) diceModifier += Integer.parseInt(modMatcher.group(1));
        return diceModifier;
    }

    /**
     * This method rolls the dice and adds them to the list
     *
     * @param diceNumber     the list of how many dice for each roll
     * @param diceSize       what size those dice are
     * @param advantage      whether the dice have advantage
     * @param reroll         wheter the dice need to reroll
     * @param rerollTrigger  what triggers the reroll
     * @param replace        whether the dice are replaced
     * @param replaceValue   what to recplace with
     * @param replaceTrigger what triggers the replace
     * @param diceResults    the list tostore the results
     */
    private void createDice(List<Integer> diceNumber, List<Integer> diceSize, int advantage, int reroll, String rerollTrigger, int replace, int replaceValue, String replaceTrigger, List<DicePool> diceResults) {
        //Roll the dice
        for (int i = 0; (i < diceNumber.size()) && (i < diceSize.size()); i++) {
            for (int j = 0; j < diceNumber.get(i); j++) {
                DiceResult newDice = new DiceResult(diceSize.get(i), advantage, rerollTrigger, reroll, replaceTrigger, replace, replaceValue);
                diceResults.get(diceResults.size() - 1).addDice(newDice);
            }
        }
    }

    /**
     * This method finds the parameters of the commands
     *
     * @param rollArguments   the container to store the parameters
     * @param argumentMatcher the matcher
     * @param command         the command that is being checked
     */
    private void findArguments(RollArguments rollArguments, Matcher argumentMatcher, String command) {
        while (argumentMatcher.find()) {
            String[] arguments = argumentMatcher.group(0).split("\\s");
            List<String> argList = Arrays.asList(arguments);
            switch (argList.get(0)) {
                case "-a":
                    rollArguments.plusAdvantage();
                    break;
                case "-d":
                    rollArguments.subAdvantage();
                    break;
                case "-rer":
                    rollArguments.setRerollTrigger(argList.get(1));
                    rollArguments.setReroll(Integer.parseInt(argList.get(2)));
                    break;
                case "-rep":
                    rollArguments.setReplaceTrigger(argList.get(1));
                    rollArguments.setReplace(Integer.parseInt(argList.get(2)));
                    rollArguments.setReplaceValue(Integer.parseInt(argList.get(3)));
                    break;
            }
        }
    }
}

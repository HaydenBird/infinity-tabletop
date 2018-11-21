package com.mygdx.managers;

import com.mygdx.containers.*;
import org.mariuszgromada.math.mxparser.Expression;
import sun.security.ssl.Debug;

import java.util.Arrays;
import java.util.Iterator;
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
        String pattern = "([^\\[\\]]*)(\\[[^\\]\\[]+\\])?"; //this regex pattern matches for things encased in braces
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(newMessage);
        List<String> rollCommands = new LinkedList<>();
        List<MessageComponent> message = new LinkedList<>();
        while (m.find()) {
            Debug.println("Regex found", "Group 0: " + m.group(0) + " Group 1: " + m.group(1) + " Group 2: " + m.group(2));
            MessageComponent text = new MessageComponent();
            message.add(text);
            if (m.group(2) != null) {
                MessageComponent roll = new MessageComponent();
                message.add(roll);
                rollCommands.add(m.group(2));
            }
            text.addString(m.group(1));

        }
        List<String> text = new LinkedList<>(Arrays.asList(newMessage.split("\\[[^\\]\\[]+\\]")));
        try {
            List<RollContainer> rollContainers = parseRollCommands(rollCommands);
            ChatMessage newChatMessage = makeChatMessage(rollContainers, message);
            //NetworkManager.sendCommand(message.getNetworkCommand());
            return newChatMessage;
        } catch (IncorrectFormattingError e) {
            Debug.println("Formatting error", "Debug");
            //NetworkManager.sendCommand(message.getNetworkCommand());
            return null;
        }

    }

    private ChatMessage makeChatMessage(List<RollContainer> rollContainers, List<MessageComponent> message) {
        Iterator<RollContainer> rollContainerIterator = rollContainers.iterator();
        for (MessageComponent messageComponent : message) {
            if (messageComponent.getStringOrContainer() == RollContainer.class && rollContainerIterator.hasNext()) {
                messageComponent.addRollContainer(rollContainerIterator.next());
            }
        }
        return new ChatMessage(EngineManager.getCurrentPlayer(), NetworkManager.getPlayers(), EngineManager.getSkin(), message);
    }

    /**
     * Roll commands come in the following forms:
     * [number of dice 1]d[sides of dice 1]_[parameter 1]_..._[parameter n]+...+[number of dice n]d[dice size n]+modifier
     * <p>
     * Parameters:
     * <p>
     * _a -   advantage
     * _d -   disadvantage
     * Adv and disadv cancel eachother out
     * <p>
     * _dl:x    drop the lowest x dice, if x is greater than the number of dice, then all will be dropped
     * _dh:x    drop the highest x dice
     * _kl:x    keep the lowest x dice
     * _kh:x    keep the highest x dice
     * <p>
     * _rer:[< or > or =]:x   -   reroll any number greater than or less than or equal to x once
     * _rep:[< or > or =]:x:y    - replace all rolls greater than or less than or equal to X with Y
     * <p>
     * _success:[< or > or =]:x   - count each dice that is in the range
     * _condition:[condition]:operator:value:flagname   -   if a conditiion is met, set the flag named
     *<p>
     *  Conditions:
     *          total:[=, >, <]:[value]
     *          highestDice:[=, >, <]:[value]
     *          lowestDice:[=, >, <]:[value]
     *          numberSuccesses:[=, >, <]:[value]   -   successes is always 0 if the roll does not flag it
     *          alldice:[=, >, <]:[value]
     *<p>
     * -if:flagname     -   only perform this command if the named flag is flagged
     *
     * @param rollCommands the split list of commands in braces
     * @return the roll container that has the rolls for the message
     * @throws IncorrectFormattingError This error means the formatting of a roll command was wrong
     */
    private List<RollContainer> parseRollCommands(List<String> rollCommands) throws IncorrectFormattingError {
        try {
            List<RollContainer> rollContainers = new LinkedList<>();
            List<String> flagsMet = new LinkedList<>();
            for (String rollCommand : rollCommands) {
                Debug.println("Parse roll command", "Roll command: " + rollCommand);
                RollContainer container = new RollContainer();
                rollCommand = rollCommand.substring(1, rollCommand.length() - 1);
                Debug.println("Parse roll command", "Stripped Roll command: " + rollCommand);
                //Build the regex
                Pattern rollPartPattern = Pattern.compile("([\\/\\+\\-\\*]?)\\s*(\\d+|\\([^\\(\\)]*\\)\\b)d(\\b\\([^\\(\\)]*\\)|[^\\/\\+\\*\\-]*)");
                Matcher rollPartMatcher = rollPartPattern.matcher(rollCommand);
                //Search and find all groupings of dice and what operation they are performing ( + - * /)
                while (rollPartMatcher.find()) { //For each grouping
                    if (rollPartMatcher.group(2).isEmpty()) {
                        Debug.println("Parse roll command", "Matcher found nothing");
                        continue;
                    }
                    Debug.println("Parse roll command", "Regex match, group 1: " + rollPartMatcher.group(1) + ", group 2: " + rollPartMatcher.group(2));
                    DicePool dicePool = new DicePool();
                    //Find the operator
                    String operator = rollPartMatcher.group(1);
                    if ((operator == null)) {
                        dicePool.setOperator("+");
                    } else {
                        dicePool.setOperator(operator);
                    }
                    //Find the dice number and size
                    String[] parameters = rollPartMatcher.group(3).split("_");
                    parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
                    Debug.println("Parse roll command", "dice number: " + rollPartMatcher.group(2) + ", size: " + rollPartMatcher.group(3));
                    int diceNumber = (int) (doMath(rollPartMatcher.group(2)));
                    int diceSize = (int) (doMath(rollPartMatcher.group(3)));
                    //find all the parameters
                    Debug.println("Parse roll command", "Parameter count: " + parameters.length);
                    RollArguments rollArguments = findArguments(parameters);
                    dicePool.addDice(diceNumber, diceSize, rollArguments, flagsMet);
                    flagsMet.addAll(rollArguments.calculateConditions(dicePool));

                    //Do something with the dice pool
                    container.addDicePool(dicePool);

                }
                rollContainers.add(container);
            }


            //Create  a chat message out of the containers
            return rollContainers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IncorrectFormattingError();
        }
    }

    private double doMath(String equation) {
        Expression ex = new Expression(equation);
        Debug.println("Math expression", ex.calculate() + "");
        return ex.calculate();
    }

    private RollArguments findArguments(String[] listArguments) {
        RollArguments rollArguments = new RollArguments();
        for (String parameter : listArguments) {
            handleParameter(rollArguments, parameter);
        }
        return rollArguments;
    }

    private void handleParameter(RollArguments rollArguments, String parameter) {
        String[] parameterParts = parameter.split(":");
        switch (parameterParts[0]) {
            //Advantage cases
            case "a":
                rollArguments.setAdvantage(rollArguments.getAdvantage() + 1);
                Debug.println("Parse roll command", "Advantage");
                break;
            case "d":
                rollArguments.setAdvantage(rollArguments.getAdvantage() - 1);
                Debug.println("Parse roll command", "Disadvantage");
                break;
            //Reroll Case
            case "rer":
                rollArguments.setRerollTrigger(Operator.fromString(parameterParts[1]));
                rollArguments.setRerollThreshold(Integer.parseInt(parameterParts[2]));
                Debug.println("Parse roll command", "Reroll " + parameterParts[1] + " " + parameterParts[2]);
                break;
            //Replace case
            case "rep":
                rollArguments.setReplaceTrigger(Operator.fromString(parameterParts[1]));
                rollArguments.setReplaceThreshold(Integer.parseInt(parameterParts[2]));
                rollArguments.setReplaceValue(Integer.parseInt(parameterParts[2]));
                Debug.println("Parse roll command", "Replace all dice " + parameterParts[1] + " " + parameterParts[2] + " with " + parameterParts[3]);
                break;
            //successes case
            case "success":
                rollArguments.setSuccessTrigger(Operator.fromString(parameterParts[1]));
                rollArguments.setSuccessValue(Integer.parseInt(parameterParts[2]));
                Debug.println("Parse roll command", "Success on " + parameterParts[1] + " " + parameterParts[2]);
                break;
            //condition casea
            case "condition":
                Condition newCondition = new Condition(Condition.typeFromString(parameterParts[1]), Operator.fromString(parameterParts[2]),
                        parameterParts[4], Float.parseFloat(parameterParts[3]));
                rollArguments.getConditions().add(newCondition);
                Debug.println("Parse roll command", "A condition has been set, if " + parameterParts[1] + " is " + parameterParts[2]
                        + " " + parameterParts[3] + " then set flag with the name" + parameterParts[4]);
                break;
            //if case
            case "if":
                rollArguments.setAddIfFlagName(parameterParts[1]);
                Debug.println("Parse roll command", "If flag " + parameterParts[1]);
                break;
            //Keep lowest
            case "kl":
                rollArguments.setKeepLowest(Integer.parseInt(parameterParts[1]));
                break;
            //Drop lowest
            case "dl":
                rollArguments.setDropLowest(Integer.parseInt(parameterParts[1]));
                break;
            //Keep highest
            case "kh":
                rollArguments.setKeepHighest(Integer.parseInt(parameterParts[1]));
                break;
            //Drop highest
            case "dh":
                rollArguments.setDropHighest(Integer.parseInt(parameterParts[1]));
                break;
        }
    }

}

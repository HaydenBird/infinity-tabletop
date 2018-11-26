package com.mygdx.managers;

import com.mygdx.containers.*;
import org.mariuszgromada.math.mxparser.Expression;
import sun.security.ssl.Debug;

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
        Debug.println("Parse message", "Begin");
        Matcher bracesMatcher = Pattern.compile("(\\[([^\\[\\]]+)\\])?([^\\[\\]]*)?").matcher(newMessage);
        List<MessageComponent> messageComponents = new LinkedList<>();
        while (bracesMatcher.find()) {
            if (bracesMatcher.group(0) == null || bracesMatcher.group(0).isEmpty()) continue;
            Debug.println("Parse message", "Found message in brackets as follows: " + bracesMatcher.group(0));
            if (bracesMatcher.group(2) != null) {
                Matcher diceRollFinder = Pattern.compile("(roll\\(([^,]+),([^\\)]+)\\))(_[^\\)\\s]+)?").matcher(bracesMatcher.group(2));
                RollContainer container = new RollContainer();
                while (diceRollFinder.find()) {
                    if (diceRollFinder.group(0) == null || diceRollFinder.group(0).isEmpty()) continue;
                    Debug.println("Parse message", "Found dice command as follows: " + diceRollFinder.group(0));
                    Debug.println("Parse message", "Number of dice is as follows: " + diceRollFinder.group(2));
                    Debug.println("Parse message", "Size of dice is as follows: " + diceRollFinder.group(3));
                    RollArguments arguments;
                    if (diceRollFinder.group(4) != null) {
                        Debug.println("Parse message", "Arguments for the roll are as follows: " + diceRollFinder.group(4));
                        arguments = findArguments(diceRollFinder.group(4));
                    } else {
                        Debug.println("Parse message", "No arguments found");
                        arguments = new RollArguments();
                    }
                    int number = Math.round((float) doMath(diceRollFinder.group(2)));
                    int sides = Math.round((float) doMath(diceRollFinder.group(3)));
                    DicePool dicePool = new DicePool();
                    List<String> flagsMet = new LinkedList<>();
                    dicePool.addDice(number, sides, arguments, flagsMet);
                    flagsMet.addAll(arguments.getFlags());
                    container.addDicePool(dicePool);
                    MessageComponent messageComponent = new MessageComponent();
                    messageComponent.addRollContainer(container);
                    messageComponents.add(messageComponent);
                }
            }

            if (bracesMatcher.group(3) != null || !bracesMatcher.group(3).isEmpty()) {
                MessageComponent messageComponent = new MessageComponent();
                Debug.println("Parse message", "String found: " + bracesMatcher.group(3));
                messageComponent.addString(bracesMatcher.group(3));
                messageComponents.add(messageComponent);
            }
        }
        return new ChatMessage(EngineManager.getCurrentPlayer(), NetworkManager.getPlayers(), EngineManager.getSkin(), messageComponents);
    }

    /**
     * Roll commands come in the following forms:
     * roll(number, sides)[arguments]
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
     *
     * @return the roll container that has the rolls for the message
     * @throws IncorrectFormattingError This error means the formatting of a roll command was wrong
     */

    private double doMath(String equation) {
        Expression ex = new Expression(equation);
        Debug.println("Math expression", "Got string :" + equation + " calculated:" + ex.calculate() + "");
        return ex.calculate();
    }

    private RollArguments findArguments(String parameters) {
        RollArguments rollArguments = new RollArguments();
        String[] parametersSplit = parameters.split("_");
        for (String parameter : parametersSplit) {
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

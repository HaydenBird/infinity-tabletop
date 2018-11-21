package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.mygdx.managers.EngineManager;

import java.util.Random;

public class DiceResult extends Label {
    private String hoverText;
    private int finalResult;
    private Random random;


    /**
     * This object represents a single dice, and will generate a roll based on the parameters given.
     * Resolution order of parameters is
     * Advantage -> Reroll -> Replace
     *
     */
    public DiceResult(RollArguments arguments, int sides) {
        super("", EngineManager.getSkin());
        this.hoverText = "";
        handleRolls(arguments, sides);
        TextTooltip.TextTooltipStyle style = EngineManager.getSkin().get(TextTooltip.TextTooltipStyle.class);
        style.wrapWidth = 350;
        this.addListener(new TextTooltip(getHistory(), EngineManager.getSkin()));
        this.setText("" + finalResult);
    }

    private void handleRolls(RollArguments arguments, int sides) {
        random = new Random();
        int currentRoll;
        int initialRoll = roll(sides);
        currentRoll = initialRoll;
        hoverText = "Rolled: " + initialRoll + ".";
        //advantage
        if (arguments.getAdvantage() > 0) {
            int advRoll = roll(sides);
            currentRoll = Math.max(currentRoll, advRoll);
            hoverText += " With advantage we roll a second dice: " + advRoll + " and keep the highest:" + currentRoll + ".";
        } else if (arguments.getAdvantage() < 0) {
            int advRoll = roll(sides);
            currentRoll = Math.min(currentRoll, advRoll);
            hoverText += " With disadvantage we roll a second dice: " + advRoll + " and keep the lowest:" + currentRoll + ".";
        }

        //Rerolls
        if (arguments.getRerollThreshold() > -1 && arguments.getRerollTrigger() != null) {
            if (Operator.performCompare(arguments.getRerollTrigger(), currentRoll, arguments.getRerollThreshold())) {
                currentRoll = roll(sides);
                hoverText += " The roll was " + Operator.operatorToString(arguments.getRerollTrigger()) + " " + arguments.getRerollThreshold() + " so we rerolled to: " + currentRoll + ".";
            }
        }

        //Replace
        if (arguments.getReplaceThreshold() > -1 && arguments.getReplaceTrigger() != null) {
            if (Operator.performCompare(arguments.getReplaceTrigger(), currentRoll, arguments.getReplaceThreshold())) {
                currentRoll = arguments.getReplaceValue();
                hoverText += " The roll was " + Operator.operatorToString(arguments.getReplaceTrigger()) + " " + arguments.getReplaceThreshold() + " so we replaced it with: " + currentRoll + ".";
            }
        }

        finalResult = currentRoll;
    }

    public DiceResult(String hoverText, int finalResult) {
        super(finalResult + "", EngineManager.getSkin());
        this.hoverText = hoverText;
        this.finalResult = finalResult;
        TextTooltip.TextTooltipStyle style = EngineManager.getSkin().get(TextTooltip.TextTooltipStyle.class);
        style.wrapWidth = 350;
        this.addListener(new TextTooltip(getHistory(), EngineManager.getSkin()));
    }


    private int roll(int sides) {
        return random.nextInt(sides - 1) + 1;

    }

    /**
     * @return the sum of all the dice and the modifier
     */
    public int getFinalResult() {
        return finalResult;
    }

    /**
     * @return A string that shows what each step of the resolution process looked like
     */
    public String getHistory() {
        return hoverText;
    }
}

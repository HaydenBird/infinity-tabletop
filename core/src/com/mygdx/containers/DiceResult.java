package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.mygdx.managers.EngineManager;

import java.util.Random;

public class DiceResult extends Label {
    private final String hoverText;
    private int initialRoll;
    private int advantage;
    private int advantageRoll;
    private boolean rerolled = false;
    private boolean replaced = false;
    private int rerolledRoll;
    private int replacedValue;
    private int finalResult;
    private String rerollTriger, replaceTrigger;
    private int rerollThreshold;
    private int replaceThreshold;

    /**
     * This object represents a single dice, and will generate a roll based on the parameters given.
     * Resolution order of parameters is
     * Advantage -> Reroll -> Replace
     *
     * @param size             how many sides the dice has
     * @param advantage        If > 0, reroll the dice and take the higher dice, if < 0 take the lowest, if = 0 then do not reroll
     * @param rerollTrigger    either ">""<"  or "=", this indicates if the dice should be compared to the threshold
     * @param rerollThreshold  The number that is compared against the result, if the condition is met the dice is rerolled, if no reroll then it is equal to -1
     * @param replaceTrigger   either ">""<"  or "=", this indicates if the dice should be replaced when the result is ">""<"  or "=" the threshold
     * @param replaceThreshold The number that is compared against the result, if the condition is met the dice is replaced with a value, if no reroll then it is equal to -1
     * @param replaceValue     The value to replace with
     */
    public DiceResult(int size, int advantage, String rerollTrigger, int rerollThreshold, String replaceTrigger, int replaceThreshold, int replaceValue) {
        super("", EngineManager.getSkin());
        hoverText = null;
        Random generator = new Random();
        this.replacedValue = replaceValue;
        this.advantage = advantage;
        this.replaceTrigger = replaceTrigger;
        this.rerollTriger = rerollTrigger;
        this.rerollThreshold = rerollThreshold;
        this.replaceThreshold = replaceThreshold;
        //Make the initial roll
        initialRoll = generator.nextInt(size) + 1;
        finalResult = initialRoll;
        //Advantage
        if (advantage > 0) {
            advantageRoll = generator.nextInt(size) + 1;
            if (advantageRoll > finalResult) finalResult = advantageRoll;
        } else if (advantage < 0) {
            advantageRoll = generator.nextInt(size) + 1;
            if (advantageRoll < finalResult) finalResult = advantageRoll;
        }
        //Reroll
        switch (rerollTrigger) {
            case "<":
                if (finalResult < rerollThreshold) {
                    rerolledRoll = finalResult = generator.nextInt(size) + 1;
                    rerolled = true;
                }
                break;
            case ">":
                if (finalResult > rerollThreshold) {
                    rerolledRoll = finalResult = generator.nextInt(size) + 1;
                    rerolled = true;
                }
                break;
            case "=":
                if (finalResult == rerollThreshold) {
                    rerolledRoll = finalResult = generator.nextInt(size) + 1;
                    rerolled = true;
                }
                break;
        }
        //Replace
        switch (replaceTrigger) {
            case "<":
                if (finalResult < replaceThreshold) {
                    finalResult = replaceValue;
                    replaced = true;
                }
                break;
            case ">":
                if (finalResult > replaceThreshold) {
                    finalResult = replaceValue;
                    replaced = true;
                }
                break;
            case "=":
                if (finalResult == replaceThreshold) {
                    finalResult = replaceValue;
                    replaced = true;
                }
                break;
        }

        setText(String.valueOf(finalResult));
        TextTooltip.TextTooltipStyle style = EngineManager.getSkin().get(TextTooltip.TextTooltipStyle.class);
        style.wrapWidth = 350;
        this.addListener(new TextTooltip(getHistory(), EngineManager.getSkin()));
    }

    public DiceResult(String hoverText, int finalResult) {
        super(finalResult + "", EngineManager.getSkin());
        this.hoverText = hoverText;
        this.finalResult = finalResult;
        TextTooltip.TextTooltipStyle style = EngineManager.getSkin().get(TextTooltip.TextTooltipStyle.class);
        style.wrapWidth = 350;
        this.addListener(new TextTooltip(getHistory(), EngineManager.getSkin()));
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
        if (hoverText != null) return hoverText;
        String history = "Rolled a " + initialRoll;
        if (advantage < 0) {
            history += ". The dice had disadvantage so roll a second dice: " + advantageRoll + ", choose to keep " + Math.min(advantageRoll, initialRoll);
        }
        if (advantage > 0) {
            history += ". The dice had advantage so roll a second dice: " + advantageRoll + ", choose to keep " + Math.max(advantageRoll, initialRoll);
        }
        if (rerolled)
            history += ". The dice was " + rerollTriger + rerollThreshold + " so it was rerolled to " + rerolledRoll;
        if (replaced)
            history += ". The dice was " + replaceTrigger + replaceThreshold + " so it was replaced with" + replacedValue;
        history += ".";
        return history;
    }
}

package com.mygdx.containers;

/**
 * This object stores the parameters for a dice roll
 */
public class RollArguments {
    int reroll;
    int replace;
    int replaceValue;
    int advantage;

    /**
     * This returns the reroll threshhold
     *
     * @return the reroll threshold
     */
    public int getReroll() {
        return reroll;
    }

    /**
     * This sets the reroll threshold
     *
     * @param reroll the new threshold
     */
    public void setReroll(int reroll) {
        this.reroll = reroll;
    }

    /**
     * @return the replace threshold
     */
    public int getReplace() {
        return replace;
    }

    /**
     * @param replace the new replace threshold
     */
    public void setReplace(int replace) {
        this.replace = replace;
    }

    /**
     * @return the replace value
     */
    public int getReplaceValue() {
        return replaceValue;
    }

    /**
     * @return the replace value
     */
    public void setReplaceValue(int replaceValue) {
        this.replaceValue = replaceValue;
    }

    /**
     * @return whether it has advantage
     */
    public int getAdvantage() {
        return advantage;
    }

    /**
     * increments advantage
     */
    public void plusAdvantage() {
        this.advantage = this.advantage + 1;
    }

    /**
     * decrements advantage
     */
    public void subAdvantage() {
        this.advantage = this.advantage - 1;
    }

    /**
     * @return the reroll trigger
     */
    public String getRerollTrigger() {
        return rerollTrigger;
    }

    /**
     * @param rerollTrigger the reroll trigger
     */
    public void setRerollTrigger(String rerollTrigger) {
        this.rerollTrigger = rerollTrigger;
    }

    /**
     * @return the replace trigger
     */
    public String getReplaceTrigger() {
        return replaceTrigger;
    }

    /**
     * @param replaceTrigger the replace trigger
     */
    public void setReplaceTrigger(String replaceTrigger) {
        this.replaceTrigger = replaceTrigger;
    }

    private String rerollTrigger;
    private String replaceTrigger;

    /**
     * The constructor.
     */
    public RollArguments() {
        reroll = -1;
        replace = -1;
        replaceValue = 0;
        advantage = 0;
        rerollTrigger = "";
        replaceTrigger = "";
    }
}

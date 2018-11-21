package com.mygdx.containers;

import java.util.LinkedList;
import java.util.List;

/**
 * This object stores the parameters for a dice roll
 */
public class RollArguments {
    private int advantage;

    private int rerollThreshold;
    private Operator rerollTrigger;

    private int replaceValue;
    private int replaceThreshold;
    private Operator replaceTrigger;

    private List<String> ifFlagName;

    private Operator successTrigger;
    private int successValue;

    private int keepHighest;
    private int keepLowest;

    private int dropHighest;
    private int dropLowest;


    private List<Condition> conditions;

    public RollArguments() {
        conditions = new LinkedList<>();
        ifFlagName = new LinkedList<>();
        rerollThreshold = -1;
        replaceThreshold = -1;
    }


    public int getAdvantage() {
        return advantage;
    }

    public void setAdvantage(int advantage) {
        this.advantage = advantage;
    }

    public int getRerollThreshold() {
        return rerollThreshold;
    }

    public void setRerollThreshold(int rerollThreshold) {
        this.rerollThreshold = rerollThreshold;
    }

    public Operator getRerollTrigger() {
        return rerollTrigger;
    }

    public void setRerollTrigger(Operator rerollTrigger) {
        this.rerollTrigger = rerollTrigger;
    }

    public int getReplaceValue() {
        return replaceValue;
    }

    public void setReplaceValue(int replaceValue) {
        this.replaceValue = replaceValue;
    }

    public int getReplaceThreshold() {
        return replaceThreshold;
    }

    public void setReplaceThreshold(int replaceThreshold) {
        this.replaceThreshold = replaceThreshold;
    }

    public Operator getReplaceTrigger() {
        return replaceTrigger;
    }

    public void setReplaceTrigger(Operator replaceTrigger) {
        this.replaceTrigger = replaceTrigger;
    }

    public List<String> getFlags() {
        return ifFlagName;
    }

    public void setAddIfFlagName(String ifFlagName) {
        this.ifFlagName.add(ifFlagName);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public Operator getSuccessTrigger() {
        return successTrigger;
    }

    public void setSuccessTrigger(Operator successTrigger) {
        this.successTrigger = successTrigger;
    }

    public int getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(int successValue) {
        this.successValue = successValue;
    }


    public int getKeepHighest() {
        return keepHighest;
    }

    public void setKeepHighest(int keepHighest) {
        this.keepHighest = keepHighest;
    }

    public int getKeepLowest() {
        return keepLowest;
    }

    public void setKeepLowest(int keepLowest) {
        this.keepLowest = keepLowest;
    }

    public int getDropHighest() {
        return dropHighest;
    }

    public void setDropHighest(int dropHighest) {
        this.dropHighest = dropHighest;
    }

    public int getDropLowest() {
        return dropLowest;
    }

    public void setDropLowest(int dropLowest) {
        this.dropLowest = dropLowest;
    }

    public List<String> calculateConditions(DicePool pool) {
        List<String> metConditions = new LinkedList<>();
        for (Condition condition : conditions) {
            condition.calculate(pool);
            if (condition.isResult()) metConditions.add(condition.getFlag());
        }
        return metConditions;
    }

}

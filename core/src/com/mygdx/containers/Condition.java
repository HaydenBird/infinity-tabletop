package com.mygdx.containers;

public class Condition {

    private Type conditionType;
    private Operator conditionOperator;
    private String flagName;
    private float value;
    private boolean result;

    public Condition(Type type, Operator operator, String flagName, float value) {
        this.conditionType = type;
        this.conditionOperator = operator;
        this.flagName = flagName;
        this.value = value;
    }

    public static Type typeFromString(String s) {
        switch (s) {
            case "total":
                return Type.TOTAL;
            case "highestdice":
                return Type.HIGHEST_DICE;
            case "lowestdice":
                return Type.LOWEST_DICE;
            case "alldice":
                return Type.ALL_DICE;
            case "atleastonedice":
                return Type.AT_LEAST_ONE_DICE;
            case "numberofsuccesses":
                return Type.NUMBER_OF_SUCCESSES;
            default:
                return Type.NULL;
        }
    }

    public static String stringFromType(Type t) {
        switch (t) {
            case TOTAL:
                return "total";
            case HIGHEST_DICE:
                return "highestdice";
            case LOWEST_DICE:
                return "lowestdice";
            case ALL_DICE:
                return "alldice";
            case AT_LEAST_ONE_DICE:
                return "atleastonedice";
            case NUMBER_OF_SUCCESSES:
                return "numberofsuccesses";
            default:
                return "";
        }
    }

    public boolean isMet(DicePool pool) {
        switch (conditionType) {
            case TOTAL:
                return checkTotal(pool);
            case HIGHEST_DICE:
                return checkHighestDice(pool);
            case LOWEST_DICE:
                return checkLowestDice(pool);
            case ALL_DICE:
                return checkAllDice(pool);
            case AT_LEAST_ONE_DICE:
                return checkAtLeastOne(pool);
            case NUMBER_OF_SUCCESSES:
                return checkSuccess(pool);
            default:
                return false;
        }
    }

    private boolean checkSuccess(DicePool pool) {
        return true;
    }

    private boolean checkAtLeastOne(DicePool pool) {
        return true;
    }

    private boolean checkAllDice(DicePool pool) {
        return true;
    }

    private boolean checkLowestDice(DicePool pool) {
        return true;
    }

    private boolean checkHighestDice(DicePool pool) {
        return true;
    }

    private boolean checkTotal(DicePool pool) {
        return true;
    }

    public void calculate(DicePool pool) {
        this.result = result || isMet(pool); //A new pool failing to meet a condition shouldn't cancel it
    }

    public boolean isResult() {
        return result;
    }

    public String getFlag() {
        return this.flagName;
    }

    public enum Type {
        TOTAL, HIGHEST_DICE, LOWEST_DICE, ALL_DICE, AT_LEAST_ONE_DICE, NUMBER_OF_SUCCESSES, NULL;
    }
}

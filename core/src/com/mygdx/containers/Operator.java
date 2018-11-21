package com.mygdx.containers;

public enum Operator {
    PLUS, MINUS, DIVIDE, MULTIPLY, GREATER_THAN, LESS_THAN, EQUAL;

    public static Operator fromString(String s) {
        switch (s) {
            case "+":
                return PLUS;
            case "-":
                return MINUS;
            case "/":
                return DIVIDE;
            case "*":
                return MULTIPLY;
            case ">":
                return GREATER_THAN;
            case "<":
                return LESS_THAN;
            case "=":
                return EQUAL;
            default:
                return PLUS;
        }
    }

    public static String operatorToString(Operator o) {
        switch (o) {
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case DIVIDE:
                return "/";
            case MULTIPLY:
                return "*";
            case GREATER_THAN:
                return ">";
            case LESS_THAN:
                return "<";
            case EQUAL:
                return "=";
            default:
                return "+";
        }
    }

    public static boolean performCompare(Operator operator, float firstValue, float secondValue) {
        switch (operator) {
            case EQUAL:
                return firstValue == secondValue;
            case LESS_THAN:
                return firstValue < secondValue;
            case GREATER_THAN:
                return firstValue > secondValue;
            default:
                return false;
        }
    }

    public static float performCalculation(String operator, float firstValue, float secondValue) {
        return firstValue + secondValue; //TODO operators
    }
}

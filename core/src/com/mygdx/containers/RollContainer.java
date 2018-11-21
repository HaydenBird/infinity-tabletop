package com.mygdx.containers;

import java.util.LinkedList;
import java.util.List;

/**
 * An object that stores a list of groups of dice rolls, and the commands used to generate them
 */
public class RollContainer {

    public RollContainer() {
        this.rollResults = new LinkedList<>();
    }


    /**
     * @return the roll results
     */
    public List<DicePool> getRollResults() {
        return rollResults;
    }

    private List<DicePool> rollResults;

    public void addDicePool(DicePool pool) {
        rollResults.add(pool);
    }

    public RollContainer(List<DicePool> rollResults) {
        this.rollResults = rollResults;
    }

    public float getRollResult() {
        float initial = 0;
        for (DicePool dicePool : rollResults) {
            initial = Operator.performCalculation(dicePool.getOperator(), initial, dicePool.getTotal());
        }
        return initial;
    }
}

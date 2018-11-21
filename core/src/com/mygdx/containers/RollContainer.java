package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.managers.EngineManager;

import java.util.LinkedList;
import java.util.List;

/**
 * An object that stores a list of groups of dice rolls, and the commands used to generate them
 */
public class RollContainer extends TextButton {

    public RollContainer() {
        super("", EngineManager.getSkin());
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
        super("", EngineManager.getSkin());
        this.rollResults = rollResults;
    }

    public float getRollResult() {
        float initial = 0;
        for (DicePool dicePool : rollResults) {
            initial = Operator.performCalculation(dicePool.getOperator(), initial, dicePool.getTotal());
        }
        this.setText("" + initial);
        return initial;

    }
}

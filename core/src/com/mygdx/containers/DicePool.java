package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.UIManager;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * This object acts as a container for a group of dice rolls, and displays the total result, and can be clicked to open a dialogue
 * that will display the individual rolls
 */
public class DicePool extends TextButton {

    private List<DiceResult> diceResults;
    private float modifier;
    private float result;

    /**
     * The constructor method
     */
    public DicePool() {
        super("NaN", EngineManager.getSkin()); //Set up the text button
        result = 0;
        diceResults = new LinkedList<>();
        this.addListener(new ChangeListener() { //Create the Listener for the dialogue box
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VisDialog diceDialog = new VisDialog("Dice Results"); //Create and format the dialogue box
                diceDialog.setModal(true);
                int count = 0;
                for (DiceResult d : diceResults) { //Add the rolls
                    //Add the dice to the dialog
                    diceDialog.text(d);
                    diceDialog.text("+");
                    count++;
                    if (count > 10) {
                        diceDialog.getContentTable().row();
                        count = 0;
                    }

                }
                BigDecimal bd = new BigDecimal(String.valueOf(modifier)); //Strip all the trailing zeroes from the modifier
                diceDialog.text(" +" + bd.stripTrailingZeros().toPlainString());
                diceDialog.addListener(new InputListener() {//Add listener to close the dialogue box
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        diceDialog.hide();
                        return true;
                    }
                });
                diceDialog.show(UIManager.getStage()); //Show the dialogue
                diceDialog.setPosition(UIManager.getStage().getWidth(), 0);
            }
        });
    }

    public static DicePool createFromString(String string) {
        //String format for dice rolls [[hover string]--[final result]]@[mod1]@[result 2]@[mod2]@...@[result n]@[modn]
        DicePool newPool = new DicePool();
        String[] parts = string.split("@");
        for (int i = 0; i < parts.length - 1; i += 2) {
            String[] format = parts[i].split("--");
            String hoverText = format[0];
            int finalVal = Integer.parseInt(format[1]);
            float mod = Float.parseFloat(parts[i + 1]);
            DiceResult result = new DiceResult(hoverText, finalVal);
            newPool.addDice(result);
            newPool.addMod(mod);
        }
        return newPool;
    }

    /**
     * This method adds a new dice to the pool
     *
     * @param dice The dice to add
     */
    public void addDice(DiceResult dice) {
        diceResults.add(dice);
        result += dice.getFinalResult();
        BigDecimal bd = new BigDecimal(String.valueOf(result));
        this.setText(bd.stripTrailingZeros().toPlainString());
    }

    /**
     * This method increments the modifier
     *
     * @param modifier the amount to increase the modifier by
     */
    public void addMod(float modifier) {
        this.modifier += modifier;
        this.result += modifier;
    }

    /**
     * This method returns the list of dice
     *
     * @return the list of dice
     */
    public List<DiceResult> getDice() {
        return diceResults;
    }


    public float getMod() {
        return modifier;
    }
}

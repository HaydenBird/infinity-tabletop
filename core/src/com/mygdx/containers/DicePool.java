package com.mygdx.containers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.mygdx.managers.EngineManager;
import com.mygdx.managers.UIManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * This object acts as a container for a group of dice rolls, and displays the total result, and can be clicked to open a dialogue
 * that will display the individual rolls
 */
public class DicePool extends TextButton {

    private List<DiceResult> diceResults;
    private float modifier;
    private float result;
    private String operator;

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
                DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                df.setMaximumFractionDigits(340);
                diceDialog.text(df.format(result));
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
        return null;
    }

    /**
     * This method adds a new dice to the pool
     *
     * @param number the number of dice
     * @param size the number of sides of each dice
     * @param arguments the arguments for the roll
     */
    public void addDice(int number, int size, RollArguments arguments, List<String> flagsMet) {
        //Check for an if parameter
        for (String flagName : arguments.getFlags()) {
            if (!flagsMet.contains(flagName)) {
                sun.security.ssl.Debug.println("Add dice", "Failed to meet condition");
                return;
            }
        }
        DiceResult[] dice = new DiceResult[number];
        for (int i = 0; i < number; i++) {
            //Roll the dice
            dice[i] = new DiceResult(arguments, size);
        }
        //Drop and keep dice
        Arrays.sort(dice, (d1, d2) -> d1.getFinalResult() - d2.getFinalResult());

        sun.security.ssl.Debug.println("Dice result:", "Drop results");
        //Drop lowest
        if (arguments.getDropLowest() > dice.length) arguments.setDropLowest(dice.length);
        if (arguments.getDropLowest() > 0) dice = Arrays.copyOfRange(dice, arguments.getDropLowest(), dice.length);

        //Drop Highest
        if (arguments.getDropHighest() > dice.length) arguments.setDropHighest(dice.length);
        if (arguments.getDropHighest() > 0)
            dice = Arrays.copyOfRange(dice, 0, dice.length - arguments.getDropHighest());

        //Keep lowest
        if (arguments.getKeepLowest() > dice.length) arguments.setKeepLowest(dice.length);
        if (arguments.getKeepLowest() > 0) dice = Arrays.copyOfRange(dice, 0, arguments.getKeepLowest());

        //Keep Highest
        if (arguments.getKeepHighest() > dice.length) arguments.setKeepHighest(dice.length);
        if (arguments.getKeepHighest() > 0)
            dice = Arrays.copyOfRange(dice, dice.length - arguments.getKeepHighest(), dice.length);


        //Check any conditions and mark flags
        arguments.calculateConditions(this);
        for (DiceResult die : dice) {
            result += die.getFinalResult();
        }
        this.setText(result + "");
    }

    /**
     * This method increments the modifier
     *
     * @param modifier the amount to increase the modifier by
     */
    public void addMod(float modifier) {
        this.modifier += modifier;
        this.result += modifier;
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        this.setText(df.format(result));
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public float getTotal() {
        return result;
    }
}

package com.mygdx.containers;

import java.util.List;

/**
 * An object that stores a list of groups of dice rolls, and the commands used to generate them
 */
public class RollContainer {

    /**
     * @return the roll results
     */
    public List<DicePool> getRollResults() {
        return rollResults;
    }

    /**
     * @return the roll commands
     */
    public List<String> getRollCommands() {
        return rollCommands;
    }

    private List<DicePool> rollResults;
    private List<String> rollCommands;

    /**
     * The constructor for a rollcontainer, an object that stores a list of groups of dice rolls, and the commands used to generate them
     *
     * @param rollResults  the list of dice results
     * @param rollCommands the list of commands
     */


    public RollContainer(List<DicePool> rollResults, List<String> rollCommands) {
        this.rollResults = rollResults;
        this.rollCommands = rollCommands;
    }
}

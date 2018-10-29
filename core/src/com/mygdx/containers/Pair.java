package com.mygdx.containers;

@SuppressWarnings("WeakerAccess")
/**
 * A simple generic pair class
 */
public class Pair<F, S> {

    private F first;
    private S second;

    /**
     * The constructor
     *
     * @param first  the first element
     * @param second the second element
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first element
     *
     * @return the first element
     */
    public F getFirst() {
        return first;
    }

    /**
     * Sets the first element
     *
     * @param first the new first element
     */
    public void setFirst(F first) {
        this.first = first;
    }


    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

}

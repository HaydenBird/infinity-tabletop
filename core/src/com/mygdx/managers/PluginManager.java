package com.mygdx.managers;

import java.util.Scanner;

/**
 * This class will handle communication with the browser plugin that hooks into DNDbeyond
 */
public class PluginManager {

    public PluginManager() {

    }

    public String getPluginInput() {
        Scanner s = new Scanner(System.in);
        String output = "";
        while (s.hasNext()) {
            output += s.next();
        }
        return output;
    }

    public void sendPluginMessage() {
        //I don't think this is needed
    }


}

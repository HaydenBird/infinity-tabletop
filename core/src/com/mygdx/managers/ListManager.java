package com.mygdx.managers;

import com.mygdx.tabletop.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ListManager {

    private static List<PlayerCharacter> characterEntries;
    private static List<Npc> npcEntries;
    private static List<Handout> handoutEntries;
    private static List<Item> itemEntries;
    private static List<Boon> boonEntries;
    private static List<Entry> genericEntries;

    /**
     * This class maintains a list of all the entries, sorting them by type, and handles the adding of new ones
     */

    public ListManager() {
        // TODO Auto-generated constructor stub

        if (characterEntries == null) characterEntries = new LinkedList<>();
        if (npcEntries == null) npcEntries = new LinkedList<>();
        if (handoutEntries == null) handoutEntries = new LinkedList<>();
        if (itemEntries == null) itemEntries = new LinkedList<>();
        if (boonEntries == null) boonEntries = new LinkedList<>();
        if (genericEntries == null) genericEntries = new LinkedList<>();
    }

    public static List<PlayerCharacter> getCharacterEntries() {
        return characterEntries;
    }

    public static List<Npc> getNpcEntries() {
        return npcEntries;
    }

    public static List<Handout> getHandoutEntries() {
        return handoutEntries;
    }

    public static List<Item> getItemEntries() {
        return itemEntries;
    }

    public static List<Boon> getBoonEntries() {
        return boonEntries;
    }

    public static void nukeLists() {
        characterEntries = new LinkedList<>();
        npcEntries = new LinkedList<>();
        handoutEntries = new LinkedList<>();
        itemEntries = new LinkedList<>();
        boonEntries = new LinkedList<>();
        genericEntries = new LinkedList<>();
    }

    public int addToList(Entry newEntry) {
        int newIndex = 0;
        switch (newEntry.getEntryType()) {
            case "PlayerCharacter":
                newIndex = characterEntries.size();
                characterEntries.add((PlayerCharacter) newEntry);
                break;
            case "Npc":
                newIndex = npcEntries.size();
                npcEntries.add((Npc) newEntry);
                break;
            case "Handout":
                newIndex = handoutEntries.size();
                handoutEntries.add((Handout) newEntry);
                break;
            case "Item":
                newIndex = itemEntries.size();
                itemEntries.add((Item) newEntry);
                break;
            case "Boon":
                newIndex = boonEntries.size();
                boonEntries.add((Boon) newEntry);
                break;
            case "Entry":
                newIndex = genericEntries.size();
                genericEntries.add(newEntry);
                break;

        }

        return newIndex;
    }

    public void swapEntries(Entry ent1, Entry ent2) {
        int placeHolder1 = ent1.getListOrder();
        int placeHolder2 = ent2.getListOrder();
        ent1.setListOrder(placeHolder2);
        ent2.setListOrder(placeHolder1);
        this.sortLists();
    }

    public void setEntryIndex(Entry entry, int newIndex) {
        if (newIndex < 0) return;
        switch (entry.getEntryType()) {
            case "PlayerCharacter":
                shiftList(characterEntries, entry, newIndex);
                break;
            case "Npc":
                shiftList(npcEntries, entry, newIndex);
                break;
            case "Handout":
                shiftList(handoutEntries, entry, newIndex);
                break;
            case "Item":
                shiftList(itemEntries, entry, newIndex);
                break;
            case "Boon":
                shiftList(boonEntries, entry, newIndex);
                break;
            case "Entry":
                shiftList(genericEntries, entry, newIndex);
        }
    }

    private <T> void shiftList(List<T> list, Entry entry, int newIndex) {
        while (entry.getListOrder() != newIndex) {
            if (entry.getListOrder() > newIndex) {
                swapEntries(entry, (Entry) list.get(entry.getListOrder() - 1));
            } else {
                swapEntries(entry, (Entry) list.get(entry.getListOrder() + 1));
            }
        }
        this.sortLists();
    }

    private void sortLists() {
        Collections.sort(characterEntries);
        Collections.sort(itemEntries);
        Collections.sort(npcEntries);
        Collections.sort(boonEntries);
        Collections.sort(handoutEntries);
        Collections.sort(genericEntries);
    }

}

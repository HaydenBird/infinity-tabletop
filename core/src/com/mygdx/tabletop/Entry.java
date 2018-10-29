package com.mygdx.tabletop;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.managers.EngineManager;

import java.util.LinkedList;
import java.util.List;

public class Entry extends Label implements Comparable<Entry> {

    private int listOrder;
    private List<Player> owners;
    private boolean expanded = false;
    private boolean opened = false;
    private String name;


    public Entry(String name, Skin skin) {
        //List Manager Get Index
        super(name, skin);
        listOrder = EngineManager.getListManager().addToList(this);
        owners = new LinkedList<>();
    }


    public int getListOrder() {
        return listOrder;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }

    public List<Player> getOwners() {
        return owners;
    }

    public void addOwner(Player owner) {
        this.owners.add(owner);
    }

    public void removeOwner(Player owner) {
        this.owners.remove(owner);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEntryType() {
        return this.getClass().getSimpleName();
    }


    @Override
    public int compareTo(Entry otherEntry) {
        return this.listOrder - otherEntry.listOrder;
    }

}

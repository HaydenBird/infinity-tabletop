package com.mygdx.tabletop;

import com.mygdx.managers.EngineManager;
import com.mygdx.managers.ListManager;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryTest {

    @Before
    public void before() {
        ListManager.nukeLists();
        EngineManager.getListManager();

    }

    @Test
    public void getListOrder() {
        Entry e1 = new Entry();
        Entry e2 = new Entry();
        Entry e3 = new Entry();
        assertEquals(e1.getListOrder(), 0);
        assertEquals(e2.getListOrder(), 1);
        assertEquals(e3.getListOrder(), 2);
    }

    @Test
    public void setListOrder() {
        Entry e1 = new Entry();
        Entry e2 = new Entry();
        Entry e3 = new Entry();
        e3.setListOrder(0);
        e1.setListOrder(2);
        assertEquals(e1.getListOrder(), 2);
        assertEquals(e2.getListOrder(), 1);
        assertEquals(e3.getListOrder(), 0);
    }

    @Test
    public void OwnersTest() {
        Entry e1 = new Entry();
        Player p1 = new Player("1", "1", null);
        Player p2 = new Player("2", "2", null);
        Player p3 = new Player("2", "2", null);
        e1.addOwner(p1);
        e1.addOwner(p2);
        e1.addOwner(p3);
        assertEquals(e1.getOwners().size(), 3);
        assertEquals(e1.getOwners().get(0), p1);
    }

    @Test
    public void getEntryType() {
        Handout h1 = new Handout();
        Item i1 = new Item();
        PlayerCharacter p1 = new PlayerCharacter();
        Boon b1 = new Boon();
        Entry e1 = new Entry();
        Npc n1 = new Npc();
        assertEquals(h1.getEntryType(), "Handout");
        assertEquals(i1.getEntryType(), "Item");
        assertEquals(p1.getEntryType(), "PlayerCharacter");
        assertEquals(b1.getEntryType(), "Boon");
        assertEquals(e1.getEntryType(), "Entry");
        assertEquals(n1.getEntryType(), "Npc");
    }

    @Test
    public void compareTo() {
        List<Entry> entries = new LinkedList<Entry>();
        for (int i = 0; i < 15; i++) {
            entries.add(new Entry());
            assertTrue(entries.get(i).compareTo(entries.get(i)) == 0);
        }
        for (int i = 0; i < 14; i++) {
            assertTrue(entries.get(i).compareTo(entries.get(i + 1)) < 0);
        }
        for (int i = 14; i > 0; i--) {
            assertTrue(entries.get(i).compareTo(entries.get(i - 1)) > 0);
        }
    }


/*
    @Test
    public void isExpanded() {
    }

    @Test
    public void setExpanded() {
    }

    @Test
    public void isOpened() {
    }

    @Test
    public void setOpened() {
    }

    @Test
    public void getName() {
    }

    @Test
    public void setName() {
    }


    */
}
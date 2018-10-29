package managers;

import com.mygdx.managers.EngineManager;
import com.mygdx.managers.ListManager;
import com.mygdx.tabletop.Handout;
import com.mygdx.tabletop.Item;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListManagerTest {

    @Before
    public void setUp() {
        new EngineManager();
        ListManager.nukeLists();
        EngineManager.getListManager();
    }

    @Test
    public void AddTest() {
        Handout e1 = new Handout();
        Handout e2 = new Handout();
        Handout e3 = new Handout();
        Handout e4 = new Handout();
        Item e5 = new Item();
        Item e6 = new Item();
        Item e7 = new Item();
        Item e8 = new Item();

        assertEquals(e1.getListOrder(), 0);
        assertEquals(e2.getListOrder(), 1);
        assertEquals(e3.getListOrder(), 2);
        assertEquals(e4.getListOrder(), 3);
        assertEquals(e5.getListOrder(), 0);
        assertEquals(e6.getListOrder(), 1);
        assertEquals(e7.getListOrder(), 2);
        assertEquals(e8.getListOrder(), 3);

        List<Handout> handouts = ListManager.getHandoutEntries();
        assertEquals(e1, handouts.get(e1.getListOrder()));
        assertEquals(e2, handouts.get(e2.getListOrder()));
        assertEquals(e3, handouts.get(e3.getListOrder()));
        assertEquals(e4, handouts.get(e4.getListOrder()));
    }

    @SuppressWarnings("unused")
    @Test
    public void RearrangeTest() {
        Handout e1 = new Handout();
        Handout e2 = new Handout();
        Handout e3 = new Handout();
        Handout e4 = new Handout();
        Handout e5 = new Handout();
        Handout e6 = new Handout();
        Handout e7 = new Handout();
        Handout e8 = new Handout();


        EngineManager.getListManager().swapEntries(e1, e4);

        assertEquals(e4.getListOrder(), 0);
        assertEquals(e2.getListOrder(), 1);
        assertEquals(e3.getListOrder(), 2);
        assertEquals(e1.getListOrder(), 3);

        EngineManager.getListManager().setEntryIndex(e7, 0);
        assertEquals(e7.getListOrder(), 0);
        assertEquals(e4.getListOrder(), 1);
        assertEquals(e8.getListOrder(), 7);

        assertEquals(e7, ListManager.getHandoutEntries().get(0));

    }
}

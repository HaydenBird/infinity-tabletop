package managers;

import com.mygdx.managers.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EngineManagerTest {

    @Test
    public void Construction() {
        new EngineManager();
        EngineManager eman1 = EngineManager.getEngine();
        ChatManager cman1 = EngineManager.getChatManager();
        RollManager rman1 = EngineManager.getRollManager();
        NetworkManager nman1 = EngineManager.getNetworkManager();
        ListManager lman1 = EngineManager.getListManager();
        new EngineManager();
        EngineManager eman2 = EngineManager.getEngine();
        ChatManager cman2 = EngineManager.getChatManager();
        RollManager rman2 = EngineManager.getRollManager();
        NetworkManager nman2 = EngineManager.getNetworkManager();
        ListManager lman2 = EngineManager.getListManager();
        assertEquals(eman1, eman2);
        assertEquals(rman1, rman2);
        assertEquals(cman1, cman2);
        assertEquals(lman1, lman2);
        assertEquals(nman1, nman2);
    }

}

package model;

import junit.framework.TestCase;

public class LocalControllerTest extends TestCase {

    public void testCreateBeamGroup() {
        LocalController localController = new LocalController("pippo");
        localController.createBeamGroup("Topolinolandia");
    }

}
package de.uniwue.algogis.viewshed;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;

public class ExportDemsTest {

    @Test
    public void test() {
        Dem d = new Dem("resources/dgm_2.grd");
        ExportDems.toPng(d, "resources/dgm_2.grd.png");
        assertTrue(new File("resources/dgm_2.grd.png").exists());
    }

}

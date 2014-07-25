package org.pentaho.jmeter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.jmeter.parse.JMeterAnnotationParser;
import org.powermock.api.mockito.PowerMockito;

import java.io.FileNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {

    @Before
    public void setup() {

    }

    @Test
    public void testMain() throws Exception {
        String[] args = new String[0];

        try {
            Main.main(args);
        }
        catch (Exception e) {
            Assert.assertEquals(Main.ERROR_INSUFFICIENT_ARGS, e.getMessage());
            return;
        }

        Assert.fail();
    }

    @Test
    public void testExecute() throws Exception {
//        PowerMockito.whenNew(JMeterAnnotationParser.class).withNoArguments().thenReturn(Mockito.mock(JMeterAnnotationParser.class));
//
//        try {
//            Main.execute("", "");
//        } catch (Exception e) {
//            Assert.assertEquals(Main.ERRO_NO_ANNOTATIONS, e.getMessage());
//        }
//
//        Assert.fail();
    }
}

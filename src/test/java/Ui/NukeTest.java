package ui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class NukeTest {
    /**
     * Test that program initiates and exits correctly (to be updated)
     *
     * @throws Exception if initiation and exit outputs are wrong
     */
    @Test
    @Disabled("deprecated")
    public void initExitTest() throws Exception {
        // Capture everything printed by main
        String simulatedInput = "bye\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        String output = tapSystemOut(() -> {
        });
        String expected = "Nuke is launching";
        assertEquals(expected, output);
    }
}

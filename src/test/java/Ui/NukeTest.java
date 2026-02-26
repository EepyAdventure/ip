package ui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import data.Bank;

public class NukeTest {
    /**
     * Test that program initiates and exits correctly
     *
     * @throws Exception if initiation and exit outputs are wrong
     */
    @Test
    public void initExitTest() throws Exception {
        // Capture everything printed by main
        String simulatedInput = "bye\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        String output = tapSystemOut(() -> {
            ui.Nuke.main(new String[]{});
        });
        String expected = Bank.LOGO_LOBOTOMY + System.lineSeparator()
                        + Bank.GREETING + System.lineSeparator()
                        + Bank.LINE + System.lineSeparator()
                        + Bank.LOGO_AME + System.lineSeparator()
                        + Bank.FAREWELL + System.lineSeparator();
        assertEquals(expected, output);
    }
}

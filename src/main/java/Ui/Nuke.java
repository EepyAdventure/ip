package ui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

import process.Process;

/**
 * Main executable class to handle UI
 */
public class Nuke {
    private Process process;
    private boolean running = true;

    /**
     * Method to initialize and start the program
     *
     * @param config string representation of the path to the config file
     */
    protected void start(String config) {
        try {
            process = Process.init(config);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) throws Exception {
        boolean[] result = {true};
        String output = tapSystemOut(() -> {
            result[0] = process.process(input);
        });
        running = result[0];
        return output.trim();
    }

    public boolean isRunning() {
        return running;
    }
}

package process;

/**
 * Custom exception class to handle all the unique failures that can happen when running NUKE
 */
public class NukeException extends RuntimeException {
    public NukeException(String message) {
        super("You Dun Goofed, Here's Why: " + message);
    }
}

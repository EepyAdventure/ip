public class NukeException extends RuntimeException {
    public NukeException(String message) {
        super("You Dun Goofed, Here's Why: " + message);
    }
}

import java.util.Arrays;

public class DeadlinesTask extends Task {
    private String deadline;
    protected DeadlinesTask(String... description) {
        super("Deadline", Arrays.copyOfRange(description, 1, description.length));
        this.deadline = description[0];
    }
    @Override
    public String toString() {
        String format = "%s (by: %s)";
        return String.format(format, super.toString(), deadline);
    }
}

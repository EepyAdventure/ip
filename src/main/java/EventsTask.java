import java.util.Arrays;

public class EventsTask extends Task {
    private String startTime;
    private String endTime;
    protected EventsTask(String... description) {
        super("Event", Arrays.copyOfRange(description, 2, description.length));
        this.startTime = description[0];
        this.endTime = description[1];
    }
    @Override
    public String toString() {
        String format = "%s (from: %s to: %s)";
        return String.format(format, super.toString(), startTime, endTime);
    }
}

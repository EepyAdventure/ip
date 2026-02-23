import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.time.LocalDateTime;

public class DeadlinesTask extends Task {
    private LocalDateTime deadline;

    protected DeadlinesTask(String... description) throws NukeException {
        super("Deadline", Arrays.copyOfRange(description, 2, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            // First try full datetime
            this.deadline = LocalDateTime.parse(description[1]);
        } catch (DateTimeParseException e) {
            // If only date is provided, assume midnight
            LocalDate date = LocalDate.parse(description[1], dateFmt);
            this.deadline = date.atStartOfDay();
        } catch (Exception e) {
            throw new NukeException("at the end of a time is another unrecognised time format");
        }

    }
    @Override
    public String toString() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, deadline);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        String timeLeft = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        return String.format("%s (by: %s, %s Left)", super.toString(), deadline, timeLeft);

    }
    @Override
    public String toSave() {
        String format = "%s %b %s %s";
        return String.format(format, super.getTaskType(), super.getStatus(), deadline, super.getDescription());
    }
}

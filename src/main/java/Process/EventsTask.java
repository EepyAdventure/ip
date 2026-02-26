package Process;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class EventsTask extends Task {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Constructor for a EventsTask object
     *
     * @param description description of Task to construct
     * @throws NukeException is description is invalid
     */
    protected EventsTask(String... description) throws NukeException {
        super("Event", Arrays.copyOfRange(description, 3, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
        // Define two possible formats: with time or just date
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse start time
        try {
            this.startTime = LocalDateTime.parse(description[1]);
        } catch (DateTimeParseException e) {
            LocalDate date = LocalDate.parse(description[1], dateFmt);
            this.startTime = date.atStartOfDay();
        } catch (Exception e) {
            throw new NukeException("at the end of a time is another unrecognised time format");
        }

        // Parse end time
        try {
            this.endTime = LocalDateTime.parse(description[2]);
        } catch (DateTimeParseException e) {
            LocalDate date = LocalDate.parse(description[2], dateFmt);
            this.endTime = date.atStartOfDay();
        } catch (Exception e) {
            throw new NukeException("at the end of a time is another unrecognised time format");
        }

    }

    /**
     * Returns a string representation of this EventsTask object
     *
     * @return String representation of this EventsTask object
     */
    @Override
    public String toString() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        Duration to = Duration.between(now, startTime);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        String length = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        days = to.toDays();
        hours = to.toHours() % 24;
        minutes = to.toMinutes() %60;

        String till = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        String format = "%s (from: %s to: %s, duration: %s, in: %s)";
        return String.format(format, super.toString(), startTime, endTime, length, till);
    }

    /**
     * Returns the save format of this EventsTask object
     *
     * @return String that is the save format of this EventsTask object
     */
    @Override
    public String toSave() {
        String format = "%s %s %s %s %s";
        return String.format(format, super.getTaskType(), super.getStatus(), startTime, endTime, super.getDescription());
    }
}


package process;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * Subclass of Tasks with a startDate
 */

public class DoAfterTask extends Task {
    private LocalDateTime startDate;

    /**
     * Constructor for a DoAftersTask object
     *
     * @param description description of Task to construct
     * @throws NukeException is description is invalid
     */
    protected DoAfterTask(String... description) throws NukeException {
        super("DoAfter", Arrays.copyOfRange(description, 2, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            // First try full datetime
            this.startDate = LocalDateTime.parse(description[1]);
        } catch (DateTimeParseException e) {
            // If only date is provided, assume midnight
            LocalDate date = LocalDate.parse(description[1], dateFmt);
            this.startDate = date.atStartOfDay();
        } catch (Exception e) {
            throw new NukeException("at the end of a time is another unrecognised time format");
        }
        assert this.startDate != null : "startDate was not initialized";
    }

    /**
     * Returns a string representation of this DoAftersTask object
     *
     * @return String representation of this DoAftersTask object
     */
    @Override
    public String toString() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, startDate);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        String timeLeft = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        return String.format("%s (in: %s, %s Left)", super.toString(), startDate, timeLeft);

    }

    /**
     * Returns the save format of this DoAftersTask object
     *
     * @return String that is the save format of this DoAftersTask object
     */
    @Override
    public String toSave() {
        String format = "%s %b %s %s";
        return String.format(format, super.getTaskType(), super.getStatus(), startDate, super.getDescription());
    }
}

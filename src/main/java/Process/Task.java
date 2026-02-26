package Process;

import java.util.Arrays;
import java.util.Map;

public class Task {
    private final String description;
    protected String taskType = "Task";
    private boolean status;
    private static Map<String, String> taskTypeToString = Map.of(
            "Task", " ",
            "Deadline", "D",
            "Event", "E",
            "ToDo", "T"
    );
    private static Map<String, Class> taskTypeToClass = Map.of(
            "Task", Task.class,
            "Deadline", DeadlinesTask.class,
            "Event", EventsTask.class,
            "ToDo", ToDoTask.class
    );

    /**
     * Constructor for a new Task object
     *
     * @param description description of Task object
     */
    protected Task(String... description) {
        this.status = Boolean.parseBoolean(description[0]);
        this.description = String.join(" ", Arrays.copyOfRange(description, 1, description.length));
    }

    /**
     * Constructor for a new Task object
     *
     * @param taskType type of Task object
     * @param description description of Task object
     */
    protected Task(String taskType, String... description) {
        this.description = String.join(" ", Arrays.copyOfRange(description, 0, description.length));
        this.taskType = taskType;
    }

    /**
     * Factory method to make a new Task Object
     *
     * @param taskType type of task to make
     * @param description description of the task to make
     * @return new Task object
     * @throws Exception when description is invalid
     */
    @SuppressWarnings("unchecked")
    public static Task makeTask(String taskType, String... description) throws Exception {
        if (description.length == 0) {
            throw new NukeException("You forgor the description :skull");
        }
        if (taskTypeToClass.get(taskType) != null) {
            return (Task) taskTypeToClass.get(taskType).getDeclaredConstructor(String[].class).newInstance((Object) description);
        } else {
            throw new NukeException("What kinda task is this? May I eat it?");
        }

    }

    /**
     * Getter for the description of this Task object
     *
     * @return description of this Task object
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter for the taskType of this Task object
     *
     * @return String representation of the taskType of this Task object
     */
    public String getTaskType() {
        return this.taskType;
    }

    /**
     * Getter for the status of this Task object
     *
     * @return the boolean status of this Task object
     */
    public boolean getStatus() {
        return this.status;
    }

    /**
     * Setter for the status of this Task object
     *
     * @param status new status of this Task object
     * @return updated Task object
     */
    public Task setStatus(boolean status) {
        this.status = status;
        return this;
    }

    /**
     * Returns a string representation of this Task object
     *
     * @return String representation of this Task object
     */
    @Override
    public String toString() {
        String format = "[%s][%s] %s";
        String state = " ";
        String type = taskTypeToString.get(this.taskType);
        if (status) {
            state = "X";
        }
        if (type == null) {
            return String.format(format, state, " ", this.description);
        }
        return String.format(format, state, type, this.description);
    }

    /**
     * Returns the save format of this Task object
     *
     * @return String that is the save format of this Task object
     */
    public String toSave() {
        String format = "%s %b %s";
        return String.format(format, taskType, status, description);
    }
}

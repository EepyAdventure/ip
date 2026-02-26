package Process;

import java.util.Arrays;
import java.util.Map;

public class Task {
    private final String description;
    protected String taskType = "Task";
    private boolean status;
    private static final Map<String, String> taskTypeToString = Map.of(
            "Task", " ",
            "Deadline", "D",
            "Event", "E",
            "ToDo", "T"
    );
    private static final Map<String, Class> taskTypeToClass = Map.of(
            "Task", Task.class,
            "Deadline", DeadlinesTask.class,
            "Event", EventsTask.class,
            "ToDo", ToDoTask.class
    );
    protected Task(String... description) {
        this.status = Boolean.parseBoolean(description[0]);
        this.description = String.join(" ", Arrays.copyOfRange(description, 1, description.length));
    }
    protected Task(String taskType, String... description) {
        this.description = String.join(" ", Arrays.copyOfRange(description, 0, description.length));
        this.taskType = taskType;
    }
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
    public String getDescription() {
        return this.description;
    }
    public String getTaskType() {
        return this.taskType;
    }
    public boolean getStatus() {
        return this.status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
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
    public String toSave() {
        String format = "%s %b %s";
        return String.format(format, taskType, status, description);
    }
}

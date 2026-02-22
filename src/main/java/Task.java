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
    protected Task(String... description) {
        this.description = String.join(" ", description);
        this.status = false;
    }
    protected Task(String taskType, String... description) {
        this.description = String.join(" ", description);
        this.status = false;
        this.taskType = taskType;
    }
    public static Task makeTask(String taskType, String... args) throws Exception {
        if (args.length == 0) {
            throw new NukeException("You forgor the description :skull");
        }
        if (taskTypeToClass.get(taskType) != null) {
            return (Task) taskTypeToClass.get(taskType).getDeclaredConstructor(String[].class).newInstance((Object) args);
        } else {
            throw new NukeException("What kinda task is this? May I eat it?");
        }

    }
    public void setStatus(boolean newStatus) {
        this.status = newStatus;
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
}

package Process;

import java.util.Arrays;

public class ToDoTask extends Task {
    protected ToDoTask(String... description) {
        super("ToDo", Arrays.copyOfRange(description, 1, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
    }
}

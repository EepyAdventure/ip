package Process;

import java.util.Arrays;

public class ToDoTask extends Task {

    /**
     * Constructor for a new ToDoTask object
     *
     * @param description
     */
    protected ToDoTask(String... description) {
        super("ToDo", Arrays.copyOfRange(description, 1, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
    }
}

package process;

import java.util.Arrays;

/**
 * Class of Task where its a ToDo with no deadline
 */
public class ToDoTask extends Task {

    /**
     * Constructor for a new ToDoTask object
     *
     * @param description description of the ToDoTask object
     */
    protected ToDoTask(String... description) {
        super("ToDo", Arrays.copyOfRange(description, 1, description.length));
        super.setStatus(Boolean.parseBoolean(description[0]));
    }
}

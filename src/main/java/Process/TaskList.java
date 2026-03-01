package process;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class that is an arrayList of tasks
 */
public class TaskList extends ArrayList<Task> {

    /**
     * Constructor for a new TaskList object
     *
     * @param save path to save file to read
     * @throws Exception if there is an error reading the file and converting it to Task objects
     */
    public TaskList(Path save) throws Exception {
        if (!Files.readAllLines(save).isEmpty()) {
            Scanner permLines = new Scanner(save);
            String type;
            String[] line;
            while (permLines.hasNext()) {
                type = permLines.next();
                line = permLines.nextLine().trim().split("\\s+");
                this.add(Task.makeTask(type, line));
            }
        }
    }

    /**
     * Constructor for a new TaskList object
     */
    public TaskList() {
        super();
    }

    /**
     * Method that finds all Tasks from this TaskList object that contains a specific substring in its description
     *
     * @param substring substring to search for
     * @return TaskList of all tasks that contains a specific substring in its description
     */
    public TaskList find(String substring) {
        assert substring != null : "Cannot search for null substring";
        TaskList res = this.stream()
                .filter(task -> task.getDescription().contains(substring))
                .collect(Collectors.toCollection(TaskList::new));
        assert res.size() <= this.size() : "find returned more results than exist";
        return res;
    }

    /**
     * Returns a string representation of this TaskList object
     *
     * @return String representation of this TaskList object
     */
    @Override
    public String toString() {
        return IntStream.range(0, this.size())
                .mapToObj(i -> String.format("%d. %s%n", i, super.get(i).toString()))
                .collect(Collectors.joining());
    }

    /**
     * Returns the save format of this TaskList object
     *
     * @return byte[] that is the save format of this TaskList object
     */
    public byte[] toSave() {
        return this.stream()
                .map(task -> task.toSave() + System.lineSeparator())
                .collect(Collectors.joining())
                .getBytes();
    }
}

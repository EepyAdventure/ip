package Process;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskList extends ArrayList<Task> {
    private int count;

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
     * Getter for the count of Tasks in this TaskList object
     *
     * @return count of Tasks in this TaskList object
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Removes all Tasks in this TaskList object
     */
    @Override
    public void clear() {
        this.count = 0;
        super.clear();
    }

    /**
     * Method that adds a new Task to this TaskList object
     *
     * @param task task to be added to this TaskList Object
     * @return boolean to signal that the Task has been added successfully
     */
    @Override
    public boolean add(Task task) {
        this.count++;
        return super.add(task);
    }

    /**
     * Method that removes a Task from this TaskList object based on its index
     *
     * @param idx the index of the Task to be removed from this TaskList object
     * @return new TaskList without the object
     */
    @Override
    public Task remove(int index) {
        this.count--;
        return super.remove(index);
    }

    /**
     * Returns a string representation of this TaskList object
     *
     * @return String representation of this TaskList object
     */
    @Override
    public String toString() {
        String format = "%d. %s \n";
        String res = "";
        for (int i = 0; i < this.count; i++) {
            res = res + String.format(format, i, super.get(i).toString());
        }
        return res;
    }

    /**
     * Returns the save format of this TaskList object
     *
     * @return byte[] that is the save format of this TaskList object
     */
    public byte[] toSave() {
        String format = "%s \n";
        String res = "";
        for (int i = 0; i <this.count; i++) {
            res = res + String.format(format, super.get(i).toSave());
        }
        return res.getBytes();
    }
}

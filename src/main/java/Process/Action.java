package process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Action class is a collection of methods the user can execute
 * Process class calls Actions based on user input
 * Action methods are all static and Action cannot be instantiated
 */

public abstract class Action {
    private static TaskList taskList;
    private static Path save;

    /**
     * Method that initializes pointers and references
     *
     * @param taskList tasklist Action acts on
     * @param save save location Action acts on
     * @return signal to continue program execution
     */
    protected static boolean set(TaskList taskList, Path save) {
        assert taskList != null : "TaskList cannot be null";
        assert save != null : "Save path cannot be null";
        Action.taskList = taskList;
        Action.save = save;
        return true;
    }

    /**
     * Method that cleans up the program and prepares to terminate program execution
     *
     * @return signal to terminate program execution
     */
    protected static boolean exit() {
        taskList.clear();
        return false;
    }

    /**
     * Method that takes in an input and outputs it to system
     *
     * @param echo string input from user to echo
     * @return signal to continue program execution
     */
    protected static boolean echo(String... echo) {
        System.out.println(String.join(" ", echo));
        return true;
    }

    /**
     * Method that adds a new task to taskList
     *
     * @param taskType type of task to add to list
     * @param description details of the task to be added
     * @return signal to continue program execution
     * @throws NukeException if description is empty or wrong length
     */
    protected static boolean add(String taskType, String... description) throws Exception {
        if (description.length == 0) {
            throw new NukeException("You forgor the description :skull");
        }
        String[] fullDescription = new String[description.length + 1];
        fullDescription[0] = "false";
        System.arraycopy(description, 0, fullDescription, 1, description.length);
        Task task = Task.makeTask(taskType, fullDescription);
        taskList.add(task);
        assert taskList.contains(task) : "Task was not added to list";
        System.out.printf("Task Added to list%n");
        System.out.printf("  %s%n", task);
        System.out.printf("Now you have %d tasks in the list%n", taskList.size());
        return true;
    }

    /**
     * Method that deletes a task based on index in taskList
     *
     * @param index index of the element to be deleted
     * @return true signal to continue program execution
     */
    protected static boolean delete(String index) {
        int idx = Integer.parseInt(index) - 1;
        assert idx >= 0 && idx < taskList.size() : "Delete index out of bounds: " + idx;
        Task task = taskList.get(idx - 1);
        assert task != null : "Task does not exist";
        taskList.remove(idx - 1);
        System.out.printf("I dragged them out the back%n");
        System.out.printf("  %s%n", task.toString());
        System.out.printf("Now you have %d tasks in the list%n", taskList.size());
        return true;
    }

    /**
     * Method that marks a task as complete based on index in taskList
     *
     * @param index index of the element to be deleted
     * @return true signal to continue program execution
     */
    protected static boolean mark(String index) {
        Task task = taskList.get(Integer.parseInt(index) - 1).setStatus(true);
        System.out.printf("Task marked as complete%n");
        System.out.printf("  %s%n", task.toString());
        return true;
    }

    /**
     * Method that unmarks a task as complete based on index in taskList
     *
     * @param index index of the element to be deleted
     * @return true signal to continue program execution
     */
    protected static boolean unmark(String index) {
        Task task = taskList.get(Integer.parseInt(index) - 1).setStatus(false);
        System.out.printf("Task marked as complete%n");
        System.out.printf("  %s%n", task.toString());
        return true;
    }

    /**
     * Method that lists all task currently in taskList
     *
     * @return true signal to continue program execution
     */
    protected static boolean list() {
        System.out.printf(taskList.toString());
        return true;
    }

    /**
     * Method that lists all tasks in tasklist with a description containing a specific substring
     *
     * @param substring to find in the description of tasks
     * @return true signal to continue program execution
     */
    protected static boolean find(String substring) {
        TaskList res = taskList.find(substring);
        if (!res.isEmpty()) {
            System.out.println("We found private Ryan, Here are the matches");
            System.out.println(res);
        } else {
            System.out.println("There is NOTHING HERE");
        }
        return true;
    }

    /**
     * Method that saves current state of taskList to save
     *
     * @return true signal to continue program execution
     * @throws NukeException if unable to save
     */
    protected static boolean save() {
        try {
            Files.write(save, taskList.toSave(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("You are filled with determination");
        } catch (IOException e) {
            throw new NukeException("Your SAVE file DOES NOT EXIST");
        }
        return true;
    }
}

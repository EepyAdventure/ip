import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Action {
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    private static Process process;
    private static TaskList listTemp;
    private static Path listPerm;
    protected static void start(Process process_, TaskList listTemp_, Path listPerm_) {
        process = process_;
        listTemp = listTemp_;
        listPerm = listPerm_;
    }
    protected static void exit() {
        listTemp.clear();
    }
    protected static void echo(String echo) {
        System.out.println(echo);
    }
    protected static void add(String taskType, String... description) throws Exception {
        if (description.length == 0) {
            throw new NukeException("You forgor the description :skull");
        }
        String[] fullDescription = new String[description.length + 1];
        fullDescription[0] = "false";
        System.arraycopy(description, 0, fullDescription, 1, description.length);
        Task task = Task.makeTask(taskType, fullDescription);
        listTemp.add(task);
        System.out.printf("Task Added to list\n");
        System.out.printf("  %s\n", task.toString());
        System.out.printf("Now you have %d tasks in the list\n", listTemp.getCount());

    }
    protected static void delete(String index) {
        Task task = listTemp.get(Integer.valueOf(index) - 1);
        listTemp.remove(Integer.valueOf(index) - 1);
        System.out.printf("I dragged them out the back\n");
        System.out.printf("  %s\n", task.toString());
        System.out.printf("Now you have %d tasks in the list\n", listTemp.getCount());
    }
    protected static void mark(String index) {
        listTemp.get(Integer.valueOf(index) - 1).setStatus(true);
    }
    protected static void unmark(String index) {
        listTemp.get(Integer.valueOf(index) - 1).setStatus(false);
    }
    protected static void list() throws Exception {
        System.out.printf(listTemp.toString());
    }
    protected static void save() {
        try {
            Files.write(listPerm, listTemp.toSave(),
                    StandardOpenOption.CREATE);
            System.out.println("You are filled with determination");
        } catch (IOException e) {
            throw new NukeException("Your SAVE file DOES NOT EXIST");
        }
    }
}

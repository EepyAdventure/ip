import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Action {
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    private static Boolean status = false;
    private static TaskList listTemp = new TaskList(0);
    private static Path listPerm = Paths.get("data\\User Data\\defaultPerm.txt");
    protected static void start(String config) {
        Process.init(config);
    }
    protected static void exit() {
        listTemp.clear();
        status = true;
    }
    protected static void chat() {
        while (!status) {
            input = scanner.nextLine();
            try {
                Process.process(input);
            } catch (InvocationTargetException e) {
                System.out.println(e.getCause().getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    protected static void echo(String echo) {
        System.out.println(echo);
    }
    protected static void add(String taskType, String... description) throws Exception {
        if (description.length == 0) {
            throw new NukeException("You forgor the description :skull");
        }
        Task task = Task.makeTask(taskType, description);
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
    protected static void list() throws Exception{
        int count = 1;
        if (!Files.readAllLines(listPerm).isEmpty()) {
            Scanner permLines = new Scanner(listPerm);
            while (permLines.hasNext()) {
                System.out.printf("%d %s \n", count, permLines.nextLine());
                count++;
            }
        }
        if (count == 1 && listTemp.isEmpty()) {
            throw new NukeException("The List is empty and so nobody came");
        }
        listTemp.updateIndex(count);
        System.out.printf(listTemp.toString());
    }
    protected static void save() {
        try {
            System.out.println("Warning, this feature is incomplete");
            Files.write(listPerm, listTemp.toString().getBytes());
            listTemp.clear();
        } catch (IOException e) {
            throw new NukeException("Your SAVE file DOES NOT EXIST");
        }
    }
}

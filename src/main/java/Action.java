import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Action {
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    private static Boolean status = false;
    private static Path listPathTemp = Paths.get("data\\User Data\\defaultTemp.txt");
    private static Path listPathPerm = Paths.get("data\\User Data\\defaultPerm.txt");
    protected static void start(String config) {
        Process.init(config);
    }
    protected static void exit() {
        try {
            Files.write(listPathTemp, new byte[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        status = true;
    }
    protected static void echo(String echo) {
        System.out.println(echo);
    }
    protected static void chat() {
        while (!status) {
            input = scanner.nextLine();
            Process.process(input);
        }
    }
    protected static void add(String taskName) {
        try{
            taskName = taskName + "\n";
            Files.write(listPathTemp, taskName.getBytes(), StandardOpenOption.APPEND);
            System.out.printf("added: %s", taskName);
        } catch (IOException e) {
            System.out.println("List not found");
            throw new RuntimeException(e);
        }
    }
    protected static void list() {
        try {
            Scanner lines = new Scanner(listPathTemp);
            int count = 1;
            while (lines.hasNext()) {
                System.out.printf("%d %s \n", count, lines.nextLine());
                count++;
            }
        } catch (Exception e) {
            System.out.println("List empty or not initiated");
        }
    }
    protected static void save() {
        try {
            Files.copy(listPathTemp, listPathPerm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

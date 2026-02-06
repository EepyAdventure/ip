import java.util.Scanner;

public class Action {
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    private static Boolean status = false;
    protected static void start(String config) {
        Process.init(config);
    }
    protected static void exit() {
        status = true;
    }
    protected static void echo() {
        System.out.println(input);
    }
    protected static void chat() {
        while (!status) {
            input = scanner.nextLine();
            Process.process(input);
        }
    }
}

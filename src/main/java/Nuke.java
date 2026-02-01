
import java.util.Scanner;

public class Nuke {

    public static void start() {
        System.out.println(Bank.LOGO_LOBOTOMY);
        System.out.println(Bank.GREETING);
        System.out.println(Bank.LINE);
    }
    public static void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }
    public static void echo() {}
    public static void main(String[] args) {
        Nuke.start();
        Scanner scanner = new Scanner(System.in);
        String input;
        Nuke.exit();
    }
}

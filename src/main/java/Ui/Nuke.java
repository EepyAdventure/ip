package Ui;
import Process.Process;
import Data.Bank;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Nuke {
    private static Process process;
    private static String input;
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        start(".\\config.txt");
        chat();
        exit();
    }
    protected static void start(String config) {
        try {
            process = Process.init(config);
            System.out.println(Bank.LOGO_LOBOTOMY);
            System.out.println(Bank.GREETING);
            System.out.println(Bank.LINE);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    protected static void chat() {
        try {
            input = scanner.nextLine();
            while (process.process(input)) {
                input = scanner.nextLine();
            }
        } catch (InvocationTargetException e) {
            System.out.println(e.getCause().getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    protected static void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }
}

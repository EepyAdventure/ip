package ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import data.Bank;
import process.Process;

/**
 * Main executable class to handle UI
 */
public class Nuke {
    private static Process process;
    private static String input;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Main method and program to be run
     *
     * @param args
     */
    public static void main(String[] args) {
        start(".\\config\\config.txt");
        chat();
        exit();
    }

    /**
     * Method to initialize and start the program
     *
     * @param config string representation of the path to the config file
     */
    protected static void start(String config) {
        try {
            process = Process.init(config);
            System.out.println(Bank.LOGO_LOBOTOMY);
            System.out.println(Bank.GREETING);
            System.out.println(Bank.LINE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to execute the chat function of the program
     * Chat will loop until process returns false
     */
    protected static void chat() {
        try {
            input = scanner.nextLine();
            while (process.process(input)) {
                System.out.println(Bank.LINE);
                input = scanner.nextLine();
            }
        } catch (InvocationTargetException e) {
            System.out.println(e.getCause().getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to terminate the program
     */
    protected static void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }
}

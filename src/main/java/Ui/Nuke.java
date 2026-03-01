package ui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

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
     * Method to initialize and start the program
     *
     * @param config string representation of the path to the config file
     */
    protected void start(String config) {
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
    @Deprecated
    protected void chat() {
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
    @Deprecated
    protected void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) throws Exception {
        String output = tapSystemOut(() -> {
            process.process(input);
        });
        return output;
    }
}

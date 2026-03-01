package process;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class ProcessTest {
    private static Process process;
    private static File configure;
    private static Path saves;
    private static long listCount;

    @BeforeEach
    void initEach() throws Exception {
        process = Process.init(Paths.get("config", "config.txt").toString());
        configure = new File(Paths.get("config", "config.txt").toString());
        Scanner scanner = new Scanner(configure);
        scanner.nextLine();
        saves = Paths.get(scanner.nextLine());
        listCount = Files.lines(saves).count();
    }


    /**
     * Test that echo input executes correctly
     * @throws Exception if echo output is wrong
     */
    @Test
    public void echoTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("echo hello world");
        });
        assertEquals("hello world", output.trim());
    }

    /**
     * Test that TaskList is initialised correctly
     * Test that list outputs the correct number of elements
     *
     * @throws Exception if number of elements in TaskList that is listed is wrong
     */
    @Test
    @Order(1)
    public void listInitTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("list");
        });
        assertEquals(listCount, output.split("\\R").length);
    }

    /**
     * Test that add adds element to TaskList
     * Test that list updates after add
     *
     * @throws Exception if output from adding elements is wrong or list is not updated
     */
    @Test
    @Order(2)
    public void listAddTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("add Task slime");
        });
        listCount = listCount + 1;
        assertEquals(String.format("Task Added to list%n  %s%nNow you have %d tasks in the list%n",
                Task.makeTask("Task", "false", "slime"), listCount), output);
        output = tapSystemOut(() -> {
            process.process("list");
        });
        assertEquals(listCount, output.split("\\R").length);
    }

    /**
     * Test that bye returns the exit signal
     *
     * @throws Exception if bye returns the wrong signal
     */
    @Test
    public void exitTest() throws Exception {
        assertFalse(process.process("bye"));
    }
}

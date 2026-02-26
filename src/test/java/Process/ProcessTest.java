package Process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ProcessTest {
    private static Process process;
    private static File configure;
    private static Path saves;
    private static long listCount;

    @BeforeEach
    void initEach() throws Exception{
        process = Process.init(".\\config.txt");
        configure = new File(".\\config.txt");
        Scanner scanner = new Scanner(configure);
        scanner.nextLine();
        saves = Paths.get(scanner.nextLine());
        listCount = Files.lines(saves).count();
    }


    @Test
    public void echoTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("echo hello world");
        });
        assertEquals("hello world\n", output);
    }

    @Test
    @Order(1)
    public void listInitTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("list");
        });
        assertEquals(listCount, output.split("\\R").length);
    }


    @Test
    @Order(2)
    public void listAddTest() throws Exception {
        String output = tapSystemOut(() -> {
            process.process("add Task slime");
        });
        listCount = listCount + 1;
        assertEquals(String.format("Task Added to list\n  %s\nNow you have %d tasks in the list\n",
                Task.makeTask("Task", "false", "slime"),listCount), output);
        output = tapSystemOut(() -> {
            process.process("list");
        });
        assertEquals(listCount, output.split("\\R").length);
    }

    @Test
    public void exitTest() throws Exception {
        assertFalse(process.process("bye"));
    }
}

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Process {
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    private static String commands;
    private static Map<String, Method> commandsTable = new HashMap<>();
    private Process(String config) {
        try {
            File configure = new File(config);
            Scanner scanner = new Scanner(configure);
            commands = scanner.nextLine();
            File commander = new File(commands);
            scanner = new Scanner(commander);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Scanner lineScanner = new Scanner(line);
                String className = lineScanner.next();
                String methodName = lineScanner.next();
                List<Class<?>> paramTypes = new ArrayList<>();
                String curr = "";
                while (lineScanner.hasNext()) {
                    curr = lineScanner.next();
                    if (!lineScanner.hasNext()) {
                        break;
                    }
                    paramTypes.add(Class.forName(curr));
                }
                Method method = Class.forName(className).getDeclaredMethod(
                        methodName,
                        paramTypes.toArray(new Class<?>[0])
                );
                File ref = new File(curr);
                Scanner mapper = new Scanner(ref);
                String key;
                while (mapper.hasNextLine()) {
                    key = mapper.nextLine();
                    System.out.println(key);
                    commandsTable.put(key, method);
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Config Error");
            e.printStackTrace();
            throw new RuntimeException();
        }

    }
    protected static Process init(String config) {
        return new Process(config);
    }
    protected static void process(String input) {
        Method m = commandsTable.get(input);
        try {
            m.invoke(null);
        } catch (Exception e) {
            Action.echo();
        }
    }
}

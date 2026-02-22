import java.io.File;
import java.lang.reflect.Method;
import java.util.*;


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
        try {
            Scanner command = new Scanner(input);
            Method m = commandsTable.get(command.next());

            String[] args;
            if (command.hasNextLine()) {
                String rest = command.nextLine().trim();
                args = rest.isEmpty() ? new String[0] : rest.split("\\s+");
            } else {
                args = new String[0];
            }

            Class<?>[] paramTypes = m.getParameterTypes();
            Object[] finalArgs = new Object[paramTypes.length];

            // Handle varargs vs fixed parameters
            if (paramTypes.length > 0 && paramTypes[paramTypes.length - 1].isArray()) {
                // Last parameter is varargs (String...)
                int fixedCount = paramTypes.length - 1;

                // Fill fixed parameters
                for (int i = 0; i < fixedCount; i++) {
                    finalArgs[i] = (i < args.length) ? args[i] : null;
                }

                // Remaining tokens go into the varargs array
                String[] varargs = (args.length > fixedCount)
                        ? Arrays.copyOfRange(args, fixedCount, args.length)
                        : new String[0];
                finalArgs[fixedCount] = varargs;

            } else {
                // No varargs: just map tokens directly
                for (int i = 0; i < paramTypes.length; i++) {
                    finalArgs[i] = (i < args.length) ? args[i] : null;
                }
            }

            m.invoke(null, finalArgs);

        } catch (Exception e) {
            Action.echo(input);
        }
    }

}

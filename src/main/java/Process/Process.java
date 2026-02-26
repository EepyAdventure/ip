package Process;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Process {
    private Path commands;
    private Path saves;

    /**
     * Constructor for a new Process object
     *
     * @param config config file path in String
     */
    private final Map<String, Method> commandsToMethods = new HashMap<>();
    private Process(String config) {
        try {
            File configure = new File(config);
            Scanner scanner = new Scanner(configure);
            commands = Paths.get(scanner.nextLine());
            saves = Paths.get(scanner.nextLine());
            File commander = commands.toFile();
            File saver = saves.toFile();
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
                    commandsToMethods.put(key, method);
                }
            }
            scanner.close();
            scanner = new Scanner(saver);
            int count = 0;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                count++;
            }
            Action.start(this, new TaskList(saves), saves);

        } catch (Exception e) {
            throw new NukeException("config ERROR, why did you lie to me?");
        }

    }

    /**
     * Factory method to make a new Process object
     *
     * @param config config file path in string
     * @return a new Process object
     */
    public static Process init(String config) {
        return new Process(config);
    }

    /**
     * Method that takes in user input and invokes the appropriate Action
     *
     * @param input user input in String
     * @return signal for program execution
     * @throws NukeException when the input is invalid
     */
    public boolean process(String input) throws Exception {
        try {
            Scanner command = new Scanner(input);
            Method m = commandsToMethods.get(command.next());

            if (m == null) {
                throw new NukeException("Sorry I don't speak skibiddi");
            }

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
            return (boolean) m.invoke(null, finalArgs);
        } catch (NukeException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

}

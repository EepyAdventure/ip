package process;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * Bootstraps the application's config and data files on first launch.
 *
 * If the jar is not already inside a folder named "NUKE", Bootstrap will:
 *   1. Pre-create all config and data files into the target NUKE/ folder
 *   2. Write a temporary relocator script
 *   3. Launch the script as a separate process
 *   4. Exit the JVM so the jar file lock is released
 *   5. The script moves the jar into NUKE/ and relaunches it
 *
 * On subsequent launches (jar already inside NUKE/), Bootstrap simply
 * creates any missing config and data files then returns normally.
 *
 * Final layout:
 *
 * NUKE/
 * ├── NUCLEAR.jar
 * ├── config/
 * │   ├── config.txt
 * │   └── api.txt          ← paste Anthropic API key here (optional)
 * ├── audio/               ← extracted from jar on first launch
 * │   ├── track1.mp3
 * │   └── ...
 * ├── espeak/              ← extracted from jar on first launch
 * │   ├── espeak-ng(.exe)
 * │   └── espeak-ng-data/
 * └── data/
 *     ├── commands.txt
 *     ├── commands/
 *     │   ├── add.txt
 *     │   ├── bye.txt
 *     │   └── ...
 *     └── User Data/
 *         └── defaultPerm.txt
 */
public class Bootstrap {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String NUKE_FOLDER = "NUKE";
    private static final String POWERSHELL =
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";

    private static final String CONFIG =
            ".\\data\\commands.txt\n"
                    + ".\\data\\User Data\\defaultPerm.txt\n";

    private static final String COMMANDS =
            "process.Action exit .\\data\\commands\\bye.txt\n"
                    + "process.Action echo [Ljava.lang.String; .\\data\\commands\\echo.txt\n"
                    + "process.Action add java.lang.String [Ljava.lang.String; .\\data\\commands\\add.txt\n"
                    + "process.Action delete java.lang.String .\\data\\commands\\delete.txt\n"
                    + "process.Action list .\\data\\commands\\list.txt\n"
                    + "process.Action mark java.lang.String .\\data\\commands\\mark.txt\n"
                    + "process.Action unmark java.lang.String .\\data\\commands\\unmark.txt\n"
                    + "process.Action save .\\data\\commands\\save.txt\n"
                    + "process.Action find java.lang.String .\\data\\commands\\find.txt\n";

    private static final Map<String, String> COMMAND_FILES = Map.of(
            "bye.txt", "bye\nfarewell\nsee you later\n",
            "echo.txt", "echo\nrepeat\n",
            "add.txt", "add\n",
            "delete.txt", "delete\n",
            "list.txt", "list\n",
            "mark.txt", "mark\ncomplete\n",
            "unmark.txt", "unmark\nuncomplete\n",
            "save.txt", "save\n",
            "find.txt", "find\n"
    );

    /**
     * Returns the path to config.txt relative to the jar location.
     *
     * @return path to config.txt
     */
    public static Path getConfigPath() {
        try {
            Path jar = getJarPath();
            if (jar.toString().endsWith(".jar")) {
                return jar.getParent().resolve(Paths.get("config", "config.txt"));
            }
            return Paths.get("").toAbsolutePath().resolve(Paths.get("config", "config.txt"));
        } catch (URISyntaxException e) {
            throw new NukeException("Bootstrap could not resolve config path: " + e.getMessage());
        }
    }

    /**
     * Ensures the jar is inside a NUKE/ folder, relocating if necessary,
     * then creates all missing config and data files, and extracts audio and espeak.
     */
    public static void ensureFilesExist() {
        try {
            Path jar = getJarPath();

            // only relocate when actually running as a jar
            if (jar.toString().endsWith(".jar")) {
                Path parentFolder = jar.getParent().getFileName();
                if (parentFolder == null || !parentFolder.toString().equals(NUKE_FOLDER)) {
                    relocateAndRelaunch(jar);
                    return;
                }
            }

            // jar is already in NUKE/ (or running in dev) — create missing files
            Path base = jar.toString().endsWith(".jar")
                    ? jar.getParent()
                    : Paths.get("").toAbsolutePath();

            // clean up any leftover relocator scripts from previous first launch
            try {
                Files.deleteIfExists(base.resolve("_nuke_relaunch.bat"));
                Files.deleteIfExists(base.resolve("_nuke_relaunch.sh"));
                new File(base.getParent().resolve("_nuke_relocate.bat").toString()).delete();
                new File(base.getParent().resolve("_nuke_relocate.sh").toString()).delete();
            } catch (IOException ignored) {
                //ignored
            }

            writeIfMissing(base.resolve(Paths.get("config", "config.txt")), CONFIG);
            writeIfMissing(base.resolve(Paths.get("config", "api.txt")), "");
            writeIfMissing(base.resolve(Paths.get("data", "commands.txt")), COMMANDS);
            writeIfMissing(base.resolve(Paths.get("data", "User Data", "defaultPerm.txt")), "");

            // extract audio files from jar to NUKE/audio/ if not already there
            Path audioDir = base.resolve("audio");
            Files.createDirectories(audioDir);
            extractFromJar("/audio", audioDir, false);

            // extract espeak binaries from jar to NUKE/espeak/ if not already there
            Path espeakDir = base.resolve("espeak");
            Files.createDirectories(espeakDir);
            String platform = OS.contains("win") ? "win" : OS.contains("mac") ? "mac" : "linux";
            extractFromJar("/espeak/" + platform, espeakDir, !OS.contains("win"));

            Path commandsDir = base.resolve(Paths.get("data", "commands"));
            Files.createDirectories(commandsDir);
            for (Map.Entry<String, String> entry : COMMAND_FILES.entrySet()) {
                writeIfMissing(commandsDir.resolve(entry.getKey()), entry.getValue());
            }

        } catch (IOException | URISyntaxException e) {
            throw new NukeException("Bootstrap failed: " + e.getMessage());
        }
    }

    /**
     * Extracts files and directories from a jar resource path to a target directory.
     * Skips files that already exist on disk.
     * Optionally marks extracted files as executable (for binaries on Mac/Linux).
     *
     * @param resourcePath  path inside the jar to extract from (e.g. "/audio", "/espeak/win")
     * @param targetDir     directory on disk to extract into
     * @param setExecutable whether to mark extracted files as executable
     */
    private static void extractFromJar(String resourcePath, Path targetDir, boolean setExecutable) {
        try {
            java.net.URL resource = Bootstrap.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("Bootstrap: no resource found at " + resourcePath);
                return;
            }

            URI uri = resource.toURI();
            java.nio.file.FileSystem fs = null;

            if (uri.getScheme().equals("jar")) {
                fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            }

            Path resourceDir = Paths.get(uri);

            try (var stream = Files.walk(resourceDir)) {
                stream.forEach(p -> {
                    Path relative = resourceDir.relativize(p);
                    Path target = targetDir.resolve(relative.toString());
                    try {
                        if (Files.isDirectory(p)) {
                            Files.createDirectories(target);
                        } else if (!Files.exists(target)) {
                            Files.createDirectories(target.getParent());
                            Files.copy(p, target);
                            if (setExecutable) {
                                target.toFile().setExecutable(true);
                            }
                            System.out.println("Bootstrap: extracted " + relative);
                        }
                    } catch (IOException e) {
                        System.err.println("Bootstrap: failed to extract "
                                + relative + " — " + e.getMessage());
                    }
                });
            }

            if (fs != null) {
                fs.close();
            }

        } catch (Exception e) {
            System.err.println("Bootstrap: extraction failed for " + resourcePath
                    + " — " + e.getMessage());
        }
    }

    /**
     * Pre-creates config files, then spawns a relocator script that moves
     * the jar into NUKE/ and relaunches it after the JVM exits.
     *
     * @param jar path to the currently running jar
     */
    private static void relocateAndRelaunch(Path jar) throws IOException {
        Path parentDir = jar.getParent();
        Path nukeDir = parentDir.resolve(NUKE_FOLDER);
        Path targetJar = nukeDir.resolve(jar.getFileName());

        // pre-create config files into NUKE/ before we exit
        // so the relaunched jar finds them immediately
        writeIfMissing(nukeDir.resolve(Paths.get("config", "config.txt")), CONFIG);
        writeIfMissing(nukeDir.resolve(Paths.get("config", "api.txt")), "");
        writeIfMissing(nukeDir.resolve(Paths.get("data", "commands.txt")), COMMANDS);
        writeIfMissing(nukeDir.resolve(Paths.get("data", "User Data", "defaultPerm.txt")), "");
        Path commandsDir = nukeDir.resolve(Paths.get("data", "commands"));
        Files.createDirectories(commandsDir);
        for (Map.Entry<String, String> entry : COMMAND_FILES.entrySet()) {
            writeIfMissing(commandsDir.resolve(entry.getKey()), entry.getValue());
        }

        if (OS.contains("win")) {
            Path script = parentDir.resolve("_nuke_relocate.bat");
            String relaunchScript = nukeDir.resolve("_nuke_relaunch.bat").toString();
            String batch =
                    "@echo off\n"
                            + "echo Setting up NUKE for the first time...\n"
                            + "echo This may take a moment. Please wait.\n"
                            + ":waitloop\n"
                            + "move /Y \"" + jar + "\" \"" + targetJar + "\" >nul 2>&1\n"
                            + "if exist \"" + targetJar + "\" goto success\n"
                            + "\"" + POWERSHELL + "\" -Command \"Start-Sleep -Seconds 1\"\n"
                            + "goto waitloop\n"
                            + ":success\n"
                            + "echo Done! Launching NUKE...\n"
                            + "echo @echo off > \"" + relaunchScript + "\"\n"
                            + "echo start \"NUKE\" /D \"" + nukeDir
                            + "\" \"javaw\" -jar \"" + targetJar + "\" >> \"" + relaunchScript + "\"\n"
                            + "echo exit >> \"" + relaunchScript + "\"\n"
                            + "start \"\" \"" + relaunchScript + "\"\n"
                            + "exit\n";
            Files.writeString(script, batch);
            new ProcessBuilder("cmd", "/c", "start", "\"Relocating NUKE...\"", script.toString())
                    .start();

        } else {
            // Mac/Linux: two-script approach
            Path script = parentDir.resolve("_nuke_relocate.sh");
            String relaunchScript = nukeDir.resolve("_nuke_relaunch.sh").toString();
            String shell =
                    "#!/bin/bash\n"
                            + "echo 'Setting up NUKE for the first time...'\n"
                            + "echo 'This may take a moment. Please wait.'\n"
                            + "while lsof \"" + jar + "\" > /dev/null 2>&1; do\n"
                            + "    sleep 1\n"
                            + "done\n"
                            + "mkdir -p \"" + nukeDir + "\"\n"
                            + "mv \"" + jar + "\" \"" + targetJar + "\"\n"
                            + "if [ -f \"" + targetJar + "\" ]; then\n"
                            + "    echo 'Done! Launching NUKE...'\n"
                            + "    echo '#!/bin/bash' > \"" + relaunchScript + "\"\n"
                            + "    echo 'cd \"" + nukeDir + "\"' >> \"" + relaunchScript + "\"\n"
                            + "    echo 'java -jar \"" + targetJar + "\" &' >> \"" + relaunchScript + "\"\n"
                            + "    echo 'rm -- \"$0\"' >> \"" + relaunchScript + "\"\n"
                            + "    chmod +x \"" + relaunchScript + "\"\n"
                            + "    bash \"" + relaunchScript + "\"\n"
                            + "    rm -- \"$0\"\n"
                            + "else\n"
                            + "    echo 'Failed to move jar, is it still open?'\n"
                            + "    read -p 'Press any key to continue...'\n"
                            + "fi\n";
            Files.writeString(script, shell);
            script.toFile().setExecutable(true);
            new ProcessBuilder("/bin/bash", script.toString()).start();
        }

        System.exit(0);
    }

    /**
     * Returns the path to the currently running jar or class directory.
     *
     * @return path to the code source
     */
    private static Path getJarPath() throws URISyntaxException {
        return Paths.get(
                Bootstrap.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        );
    }

    /**
     * Writes content to a file only if it does not already exist.
     *
     * @param path    path to write to
     * @param content content to write
     */
    private static void writeIfMissing(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
        }
    }
}

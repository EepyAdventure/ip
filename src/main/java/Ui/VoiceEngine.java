package ui;

/**
 * Handles text-to-speech using eSpeak NG on all platforms.
 * Falls back to OS native TTS if eSpeak NG is not installed.
 *
 * Windows: bundled espeak-ng.exe extracted to NUKE/espeak/
 * Mac:     bundled espeak-ng extracted to NUKE/espeak/
 * Linux:   bundled espeak-ng extracted to NUKE/espeak/
 * Fallback: OS native TTS if espeak not found
 */
public class VoiceEngine {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String POWERSHELL =
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";

    private static Process currentProcess;
    private static Thread currentThread;
    private static boolean espeakAvailable = false;

    // incremented on each speak() call — lets the thread know if it's been superseded
    private static volatile int speakGeneration = 0;

    /** Detects whether extracted eSpeak NG binary is available. */
    public static void init() {
        espeakAvailable = detectEspeak();
        System.out.println("VoiceEngine: espeak available = " + espeakAvailable);
        System.out.println("VoiceEngine: espeak path = " + getEspeakBinary());

        // kill TTS on JVM exit regardless of how the app is closed
        Runtime.getRuntime().addShutdownHook(new Thread(VoiceEngine::shutdown));
    }

    /** Kills any currently playing speech and the thread waiting on it. */
    public static void shutdown() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
            currentProcess = null;
        }
        if (currentThread != null && currentThread.isAlive()) {
            currentThread.interrupt();
            currentThread = null;
        }
    }

    /**
     * Detects whether the extracted eSpeak NG binary exists and runs correctly.
     *
     * @return true if espeak-ng is available
     */
    private static boolean detectEspeak() {
        try {
            String binary = getEspeakBinary();
            java.io.File binaryFile = new java.io.File(binary);
            System.out.println("VoiceEngine: binary exists = " + binaryFile.exists());
            if (!binaryFile.exists()) {
                return false;
            }
            Process p = new ProcessBuilder(binary, "--version")
                    .redirectErrorStream(true)
                    .start();
            // print whatever espeak outputs
            String output = new String(p.getInputStream().readAllBytes());
            System.out.println("VoiceEngine: espeak output = " + output);
            p.waitFor();
            System.out.println("VoiceEngine: espeak exit code = " + p.exitValue());
            return p.exitValue() == 0;
        } catch (Exception e) {
            System.err.println("VoiceEngine: espeak detection failed — " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the path to the eSpeak NG binary extracted to NUKE/espeak/.
     * Resolved at runtime so user.dir is correct after relocation.
     */
    private static String getEspeakBinary() {
        if (OS.contains("win")) {
            return System.getProperty("user.dir") + "\\espeak\\espeak-ng.exe";
        } else {
            return System.getProperty("user.dir") + "/espeak/espeak-ng";
        }
    }

    /**
     * Returns the path to the eSpeak NG data directory.
     */
    private static String getEspeakDataPath() {
        if (OS.contains("win")) {
            return System.getProperty("user.dir") + "\\espeak\\espeak-ng-data";
        } else {
            return System.getProperty("user.dir") + "/espeak/espeak-ng-data";
        }
    }

    /**
     * Speaks the given text on a background thread.
     * Ducks music while speaking, restores after.
     * Silently does nothing if text is null or blank.
     *
     * @param text text to speak
     */
    public static void speak(String text) {
        if (text == null || text.isBlank()) return;
        String clean = text.replaceAll("[^a-zA-Z0-9 .,!?]", " ").trim();
        if (clean.isBlank()) return;

        speakGeneration++;
        final int myGeneration = speakGeneration;
        shutdown();
        MusicEngine.duck();

        currentThread = new Thread(() -> {
            try {
                synthesize(clean);
            } finally {
                if (speakGeneration == myGeneration) {
                    MusicEngine.unduck();
                }
            }
        });
        currentThread.setDaemon(true);
        currentThread.start();
    }

    /**
     * Invokes the TTS engine with robotic settings.
     *
     * @param text cleaned text to synthesize
     */
    private static void synthesize(String text) {
        try {
            String[] cmd = espeakAvailable ? buildEspeakCommand(text) : buildFallbackCommand(text);
            if (cmd == null) {
                System.err.println("VoiceEngine: unsupported OS — " + OS);
                return;
            }
            currentProcess = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();
            currentProcess.waitFor();
        } catch (InterruptedException ignored) {
            // interrupted by shutdown() — kill process if still alive
            if (currentProcess != null && currentProcess.isAlive()) {
                currentProcess.destroyForcibly();
            }
        } catch (Exception e) {
            System.err.println("VoiceEngine: TTS failed — " + e.getMessage());
        }
    }

    /**
     * Builds the eSpeak NG command using the extracted binary.
     *
     * @param text text to speak
     * @return command array
     */
    private static String[] buildEspeakCommand(String text) {
        return new String[]{
                getEspeakBinary(),
                "--path", getEspeakDataPath(),
                "-v", "en",
                "-s", "120",
                "-p", "20",
                "-a", "200",
                "--punct",
                text
        };
    }

    /**
     * Builds the OS native TTS fallback command.
     * Used when eSpeak NG binary is not found.
     *
     * @param text text to speak
     * @return command array, or null if OS is unsupported
     */
    private static String[] buildFallbackCommand(String text) {
        if (OS.contains("win")) {
            return new String[]{
                    POWERSHELL, "-Command",
                    "Add-Type -AssemblyName System.Speech; "
                            + "$s = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
                            + "$s.Rate = -6; "
                            + "$s.Volume = 100; "
                            + "$s.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::Female); "
                            + "$s.Speak('" + text + "');"
            };
        } else if (OS.contains("mac")) {
            return new String[]{"say", "-v", "Zarvox", "-r", "120", text};
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("linux")) {
            return new String[]{"espeak", "-v", "en", "-s", "120", "-p", "20", text};
        }
        return null;
    }
}
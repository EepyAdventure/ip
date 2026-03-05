package ui;

/**
 * Handles text-to-speech using eSpeak NG on all platforms.
 * Falls back to OS native TTS if eSpeak NG is not installed.
 *
 * Windows: espeak-ng via winget, fallback to PowerShell System.Speech
 * Mac:     espeak-ng via brew, fallback to say command with Zarvox
 * Linux:   espeak-ng via apt, fallback to espeak
 */
public class VoiceEngine {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String POWERSHELL =
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";

    // eSpeak NG binary paths per platform
    private static final String ESPEAK_WIN =
            "C:\\Program Files\\eSpeak NG\\espeak-ng.exe";
    private static final String ESPEAK_UNIX = "espeak-ng";

    private static Process currentProcess;
    private static boolean espeakAvailable = false;

    /** Detects whether eSpeak NG is installed. */
    public static void init() {
        espeakAvailable = detectEspeak();
    }

    /** Kills any currently playing speech and releases resources. */
    public static void shutdown() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
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
        if (text == null || text.isBlank()) {
            return;
        }
        String clean = text.replaceAll("[^a-zA-Z0-9 .,!?]", " ").trim();
        if (clean.isBlank()) {
            return;
        }
        shutdown();
        MusicEngine.duck();
        new Thread(() -> synthesize(clean)).start();
        // unduck after speech process finishes
        // poll briefly since currentProcess may not be assigned yet
        new Thread(() -> {
            try {
                Thread.sleep(100);
                if (currentProcess != null) {
                    currentProcess.waitFor();
                }
            } catch (InterruptedException ignored) {
            } finally {
                MusicEngine.unduck();
            }
        }).start();
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
        } catch (Exception e) {
            System.err.println("VoiceEngine: TTS failed — " + e.getMessage());
        }
    }

    /**
     * Builds the eSpeak NG command.
     * Uses a low pitch and slow rate for a retro robotic sound.
     *
     * @param text text to speak
     * @return command array
     */
    private static String[] buildEspeakCommand(String text) {
        String binary = OS.contains("win") ? ESPEAK_WIN : ESPEAK_UNIX;
        return new String[]{
                binary,
                "-v", "en",    // English voice
                "-s", "120",   // slow rate (default 175)
                "-p", "20",    // low pitch (default 50)
                "-a", "200",   // amplitude/volume (0-200)
                "--punct",     // speak punctuation for robotic effect
                text
        };
    }

    /**
     * Builds the OS native TTS fallback command.
     * Used when eSpeak NG is not installed.
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

    /**
     * Detects whether eSpeak NG is installed on this machine.
     *
     * @return true if espeak-ng is available
     */
    private static boolean detectEspeak() {
        try {
            String binary = OS.contains("win") ? ESPEAK_WIN : ESPEAK_UNIX;
            Process p = new ProcessBuilder(binary, "--version")
                    .redirectErrorStream(true)
                    .start();
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
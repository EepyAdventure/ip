package ui;

/**
 * Handles text-to-speech using the OS native TTS engine.
 * Windows: PowerShell System.Speech with Microsoft David
 * Mac: say command with Trinoids (built-in robot voice)
 * Linux: espeak with robotic pitch/speed settings
 */
public class VoiceEngine {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String POWERSHELL =
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";
    private static Process currentProcess;

    /** No-op — OS handles resources per-call */
    public static void init() {}

    /** No-op — OS cleans up per-call resources automatically */
    public static void shutdown() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
        }
    }

    /**
     * Speaks the given text on a background thread using the OS TTS engine.
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
        // kill any currently playing speech before starting new one
        shutdown();
        new Thread(() -> synthesize(clean)).start();
    }

    /**
     * Invokes the OS TTS engine with robotic settings.
     *
     * @param text cleaned text to synthesize
     */
    private static void synthesize(String text) {
        try {
            String[] cmd = buildCommand(text);
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
     * Builds the OS-specific TTS command.
     *
     * @param text text to speak
     * @return command array, or null if OS is unsupported
     */
    private static String[] buildCommand(String text) {
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
            // Zarvox is a built-in Mac robot voice (female-ish)
            return new String[]{"say", "-v", "Zarvox", "-r", "120", text};
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("linux")) {
            // espeak: low pitch (20) + slow rate (120) = robotic
            return new String[]{"espeak", "-v", "en", "-s", "120", "-p", "20", text};
        }
        return null;
    }
}

package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Handles background music with shuffle playback.
 * Tracks are extracted to NUKE/audio/ by Bootstrap on first launch.
 * MediaPlayer requires files on disk — it cannot play from inside a jar.
 */
public class MusicEngine {

    private static MediaPlayer currentPlayer;
    private static List<String> shuffled = new ArrayList<>();
    private static int index = 0;
    private static java.nio.file.Path audioDir;
    private static double normalVolume = 0.8;

    public static void setVolume(double volume) {
        normalVolume = volume;
        if (currentPlayer != null) {
            currentPlayer.setVolume(volume);
        }
    }


    /**
     * Lowers music volume
     */
    public static void duck() {
        if (currentPlayer != null) {
            currentPlayer.setVolume(normalVolume * 0.2);
        }
    }

    /**
     * Returns music volume to default
     */
    public static void unduck() {
        if (currentPlayer != null) {
            currentPlayer.setVolume(normalVolume);
        }
    }

    /**
     * Initializes the music engine and starts playback.
     * audioPath should point to the NUKE/audio/ directory.
     *
     * @param audioPath path to the directory containing the audio files
     */
    public static void start(java.nio.file.Path audioPath) {
        audioDir = audioPath;
        shuffled = new ArrayList<>();

        // scan directory for all mp3 files instead of hardcoded list
        try (var stream = java.nio.file.Files.list(audioPath)) {
            stream.filter(p -> p.toString().toLowerCase().endsWith(".mp3"))
                    .map(p -> p.getFileName().toString())
                    .forEach(shuffled::add);
        } catch (java.io.IOException e) {
            System.err.println("MusicEngine: could not scan audio directory — " + e.getMessage());
        }

        if (shuffled.isEmpty()) {
            System.err.println("MusicEngine: no mp3 files found in " + audioPath);
            return;
        }

        Collections.shuffle(shuffled);
        index = 0;
        playNext();
    }

    /**
     * Stops the currently playing track and disposes the player.
     */
    public static void stop() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }

    /**
     * Advances to the next track, reshuffling when all tracks have played.
     */
    private static void playNext() {
        if (index >= shuffled.size()) {
            Collections.shuffle(shuffled);
            index = 0;
        }

        java.nio.file.Path trackPath = audioDir.resolve(shuffled.get(index++));
        if (!java.nio.file.Files.exists(trackPath)) {
            System.err.println("MusicEngine: track not found — " + trackPath);
            playNext(); // skip missing tracks
            return;
        }

        Media media = new Media(trackPath.toUri().toString());
        currentPlayer = new MediaPlayer(media);
        currentPlayer.setOnEndOfMedia(MusicEngine::playNext);
        currentPlayer.setOnError(() ->
                System.err.println("MusicEngine: error playing " + trackPath)
        );
        currentPlayer.play();
    }
}

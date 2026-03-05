package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles background music with shuffle playback.
 * Tracks are extracted to NUKE/audio/ by Bootstrap on first launch.
 * MediaPlayer requires files on disk — it cannot play from inside a jar.
 */
public class MusicEngine {

    // track filenames — add more here as needed
    private static final List<String> TRACK_NAMES = List.of(
            "track1.mp3",
            "track2.mp3",
            "track3.mp3"
    );

    private static MediaPlayer currentPlayer;
    private static List<String> shuffled = new ArrayList<>();
    private static int index = 0;
    private static java.nio.file.Path audioDir;

    /**
     * Initializes the music engine and starts playback.
     * audioPath should point to the NUKE/audio/ directory.
     *
     * @param audioPath path to the directory containing the audio files
     */
    public static void start(java.nio.file.Path audioPath) {
        audioDir = audioPath;
        shuffled = new ArrayList<>(TRACK_NAMES);
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

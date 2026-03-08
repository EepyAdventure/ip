package ui;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Nuke nuke = new Nuke();

    @Override
    public void start(Stage stage) {
        playSplashScreen(stage);
    }

    /**
     * Displays the splash screen GIF, then transitions to the main window.
     *
     * @param stage the primary stage
     */
    private void playSplashScreen(Stage stage) {
        Image splashGif = new Image(Main.class.getResourceAsStream("/image/Start_Screen.gif"));
        ImageView splashView = new ImageView(splashGif);
        StackPane splashPane = new StackPane(splashView);
        splashPane.setStyle("-fx-background-color: white;");

        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(new Scene(splashPane, splashGif.getWidth(), splashGif.getHeight()));
        splashStage.show();

        // duration should match the length of your splash gif
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            splashStage.close();
            showMainWindow(stage);
        });
        pause.play();
    }

    private void showMainWindow(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            MainWindow controller = fxmlLoader.getController();
            controller.setNuke(nuke);
            controller.setOnExit(() -> playExitAnimation(stage)); // pass callback
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playExitAnimation(Stage stage) {
        MusicEngine.stop();
        VoiceEngine.shutdown();
        stage.hide();

        Image exitGif = new Image(Main.class.getResourceAsStream("/image/End_Screen.gif"));
        ImageView exitView = new ImageView(exitGif);
        StackPane exitPane = new StackPane(exitView);
        exitPane.setStyle("-fx-background-color: white;");

        Stage exitStage = new Stage();
        exitStage.initStyle(StageStyle.UNDECORATED);
        exitStage.setScene(new Scene(exitPane, exitGif.getWidth(), exitGif.getHeight()));
        exitStage.show();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(ev -> Platform.exit());
        pause.play();
    }
}

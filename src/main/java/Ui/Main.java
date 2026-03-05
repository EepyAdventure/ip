package ui;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Application;
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
        Image splashGif = new Image(Main.class.getResourceAsStream("/image/splash.gif"));
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

    /**
     * Loads and displays the main application window.
     *
     * @param stage the primary stage
     */
    private void showMainWindow(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setNuke(nuke);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

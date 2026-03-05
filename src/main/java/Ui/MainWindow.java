package ui;

import java.nio.file.Paths;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import process.AIEngine;
import process.Bootstrap;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private AnchorPane root;

    private Nuke nuke;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/image/DaUser.jpg"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/image/DaNuke.png"));

    /**
     * Initializes the scene
     */
    @FXML
    public void initialize() {
        bindScroll();
        setupBackground();
        setupAnimation();
    }

    private Runnable onExit;

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    private void bindScroll() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    private void setupBackground() {
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());

        ImageView[] clones = {
                new ImageView(backgroundImage.getImage()),
                new ImageView(backgroundImage.getImage()),
                new ImageView(backgroundImage.getImage())
        };

        for (ImageView bg : clones) {
            bg.fitWidthProperty().bind(root.widthProperty());
            bg.fitHeightProperty().bind(root.heightProperty());
            bg.opacityProperty().bind(backgroundImage.opacityProperty());
            bg.effectProperty().bind(backgroundImage.effectProperty());
        }

        ImageView bg2 = clones[0];
        ImageView bg3 = clones[1];
        ImageView bg4 = clones[2];

        root.widthProperty().addListener((obs, oldVal, w) -> {
            bg2.setLayoutX(w.doubleValue());
            bg4.setLayoutX(w.doubleValue());
        });
        root.heightProperty().addListener((obs, oldVal, h) -> {
            bg3.setLayoutY(h.doubleValue());
            bg4.setLayoutY(h.doubleValue());
        });

        backgroundImage.setMouseTransparent(true);

        for (ImageView bg : clones) {
            bg.fitWidthProperty().bind(root.widthProperty());
            bg.fitHeightProperty().bind(root.heightProperty());
            bg.opacityProperty().bind(backgroundImage.opacityProperty());
            bg.effectProperty().bind(backgroundImage.effectProperty());
            bg.setMouseTransparent(true);
        }

        root.getChildren().addAll(clones);
    }

    private void setupAnimation() {
        ImageView[] allBackgrounds = {
                backgroundImage,
                (ImageView) root.getChildren().get(root.getChildren().size() - 3),
                (ImageView) root.getChildren().get(root.getChildren().size() - 2),
                (ImageView) root.getChildren().get(root.getChildren().size() - 1)
        };

        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.07), e -> {
            double maxX = root.getPrefWidth();
            double maxY = root.getPrefHeight();

            double t = Math.random();
            double snapX = -t * maxX;
            double snapY = -t * maxY;

            ColorAdjust effect = new ColorAdjust();
            double r = Math.random();
            r = Math.pow(r, 0.3);
            effect.setSaturation(Math.random() < 0.5 ? r : -r);
            backgroundImage.setEffect(effect);

            for (ImageView bg : allBackgrounds) {
                bg.setTranslateX(snapX);
                bg.setTranslateY(snapY);
            }

            int green = (int) (Math.pow(Math.random(), 20) * 60 + 47);
            root.setStyle("-fx-background-color: rgb(47, " + green + ", 47);");

            for (ImageView bg : allBackgrounds) {
                bg.setTranslateX(snapX);
                bg.setTranslateY(snapY);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Injects the Nuke instance and initializes engines.
     */
    public void setNuke(Nuke n) {
        nuke = n;
        nuke.start(Bootstrap.getConfigPath().toString());
        VoiceEngine.init();
        AIEngine.init(Bootstrap.getConfigPath().getParent().resolve("api.txt"));
        MusicEngine.start(Bootstrap.getConfigPath().getParent().getParent().resolve("audio"));

        // kill TTS and music when window is closed with the X button
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnCloseRequest(e -> {
                            VoiceEngine.shutdown();
                            MusicEngine.stop();
                        });
                    }
                });
            }
        });
    }



    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Duke's reply, then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        userInput.clear();
        userInput.setDisable(true);
        sendButton.setDisable(true);

        // add user dialog immediately
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));

        // process response on background thread
        new Thread(() -> {
            String response;
            try {
                response = nuke.getResponse(input);
            } catch (Exception e) {
                response = e.getMessage();
            }
            final String finalResponse = response;
            final boolean stillRunning = nuke.isRunning();

            // update UI back on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                dialogContainer.getChildren().add(DialogBox.getDukeDialog(finalResponse, dukeImage));
                userInput.setDisable(false);
                sendButton.setDisable(false);
                userInput.requestFocus();
                VoiceEngine.speak(finalResponse);
                if (!stillRunning) {
                    if (onExit != null) onExit.run();
                }
            });
        }).start();
    }
}

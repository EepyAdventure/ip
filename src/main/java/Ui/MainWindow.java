package ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private void bindScroll() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    private void setupBackground() {
        // Bind original background to root size
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());

        // Create clones
        ImageView[] clones = {
            new ImageView(backgroundImage.getImage()),
            new ImageView(backgroundImage.getImage()),
            new ImageView(backgroundImage.getImage())
        };

        // Bind size + opacity
        for (ImageView bg : clones) {
            bg.fitWidthProperty().bind(root.widthProperty());
            bg.fitHeightProperty().bind(root.heightProperty());
            bg.opacityProperty().bind(backgroundImage.opacityProperty());
            bg.effectProperty().bind(backgroundImage.effectProperty());
        }

        ImageView bg2 = clones[0];
        ImageView bg3 = clones[1];
        ImageView bg4 = clones[2];

        // Position clones in grid
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
            bg.setMouseTransparent(true); // add this
        }

        // Add clones behind UI
        root.getChildren().addAll(clones);
    }

    private void setupAnimation() {
        // Collect all backgrounds (original + clones)
        ImageView[] allBackgrounds = {
            backgroundImage,
            (ImageView) root.getChildren().get(root.getChildren().size() - 3),
            (ImageView) root.getChildren().get(root.getChildren().size() - 2),
            (ImageView) root.getChildren().get(root.getChildren().size() - 1)
        };

        Timeline timeline = new Timeline();

        // Fire every few seconds
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.07), e -> {
            double maxX = root.getPrefWidth();
            double maxY = root.getPrefHeight();

            // Pick a random "frame" along the diagonal path
            double t = Math.random(); // 0.0 → start, 1.0 → end
            double snapX = -t * maxX;
            double snapY = -t * maxY;

            // Only need to set on backgroundImage — clones are bound to it
            ColorAdjust effect = new ColorAdjust();
            double r = Math.random();
            r = Math.pow(r, 0.3);
            effect.setSaturation(Math.random() < 0.5 ? r : -r);
            backgroundImage.setEffect(effect);

            for (ImageView bg : allBackgrounds) {
                bg.setTranslateX(snapX);
                bg.setTranslateY(snapY);
            }

            // Flicker background color
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

    /** Injects the Duke instance */
    public void setNuke(Nuke n) {
        nuke = n;
        nuke.start(".\\config\\config.txt");
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = "";
        try {
            response = nuke.getResponse(input);
        } catch (Exception e) {
            response = e.getMessage();
        } finally {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getDukeDialog(response, dukeImage)
            );
            userInput.clear();
            if (!nuke.isRunning()) {
                javafx.application.Platform.exit();
            }
        }
    }
}

package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import template.TemplateGenerator;

import java.io.File;

public class JavaFXApplication extends Application {

    private VBox vbox;
    private String sourceDirectory = "";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        UIHandler.INSTANCE.setApplication(this);

        primaryStage.setTitle("Vulcan");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        StackPane root = new StackPane();
        vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);

        Label label = addLabel("No directory chosen", 20, -15);

        addButton("Choose source directory", 20, -60, (event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open Resource File");
            File file = chooser.showDialog(primaryStage);
            if(file != null) {
                sourceDirectory = file.getAbsolutePath();
                label.setText(sourceDirectory);
            }
        }));

        addButton("Build", 20, -20, (event -> {
            if(!sourceDirectory.isEmpty()) {
                VulcanBuild.INSTANCE.build(sourceDirectory);
            } else {
                alert(Alert.AlertType.ERROR, "Please choose a source directory first.", ButtonType.OK);
            }
        }));

        addButton("Generate settings file", 20, 0, (event -> {
            if(!sourceDirectory.isEmpty()) {
                TemplateGenerator.INSTANCE.makeSettingsFile(sourceDirectory);
                alert(Alert.AlertType.INFORMATION, "Generated settings file.", ButtonType.OK);
            } else {
                alert(Alert.AlertType.ERROR, "Please choose a source directory first.", ButtonType.OK);
            }
        }));

        addButton("Generate example mod", 20, 20, (event -> {
            if(!sourceDirectory.isEmpty()) {
                TemplateGenerator.INSTANCE.makeExampleProject(sourceDirectory);
                alert(Alert.AlertType.INFORMATION, "Generated example project.", ButtonType.OK);
            } else {
                alert(Alert.AlertType.ERROR, "Please choose a source directory first.", ButtonType.OK);
            }
        }));

        root.getChildren().add(vbox);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button addButton(String text, double x, double y, EventHandler<ActionEvent> onPressed) {
        Button button = new Button();
        button.setTranslateX(x);
        button.setTranslateY(y);
        button.setText(text);
        button.setOnAction(onPressed);
        vbox.getChildren().add(button);
        return button;
    }

    private Label addLabel(String text, double x, double y) {
        Label label = new Label();
        label.setTranslateX(x);
        label.setTranslateY(y);
        label.setText(text);
        vbox.getChildren().add(label);
        return label;
    }

    public void alert(Alert.AlertType type, String message, ButtonType... buttons) {
        (new Alert(type, message, buttons)).showAndWait();
    }

    public boolean confirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }
}

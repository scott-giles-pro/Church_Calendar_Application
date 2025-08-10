package org.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Church Calendar");
        Button postEventBtn = new Button("Post Event");
        Button viewCalendarBtn = new Button("View Calendar");

        postEventBtn.setOnAction(e -> PostEvent.showPostEventWindow());
        viewCalendarBtn.setOnAction(e -> ViewCalendar.showCalendarWindow());

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(postEventBtn, viewCalendarBtn);

        VBox layout = new VBox();
        layout.getChildren().addAll(titleLabel, buttonBox);

        Scene scene = new Scene(layout, 1000, 800);
        primaryStage.setTitle("Church Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
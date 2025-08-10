package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostEvent {
    private static final String EVENTS_FILE = "church_events.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void showPostEventWindow() {
        Stage postEventStage = new Stage();
        postEventStage.setTitle("Post New Event");

        Label titleLabel = new Label("Create New Event");
        Label eventNameLabel = new Label("Event Name:");
        TextField eventNameField = new TextField();
        Label eventDateLabel = new Label("Event Date:");
        DatePicker eventDatePicker = new DatePicker();
        Label eventTimeLabel = new Label("Event Time:");
        TextField eventTimeField = new TextField();
        Label eventDescLabel = new Label("Description:");
        TextArea eventDescArea = new TextArea();

        Button saveButton = new Button("Save Event");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            Event newEvent = new Event(
                    eventNameField.getText(),
                    eventDatePicker.getValue().toString(),
                    eventTimeField.getText(),
                    eventDescArea.getText()
            );

            boolean saveSuccess = saveEventToJson(newEvent);

            if (saveSuccess) {
                showSuccessDialog("Event saved");
            } else {
                showErrorDialog("Failed to save");
            }

            postEventStage.close();
        });

        cancelButton.setOnAction(e -> postEventStage.close());

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(titleLabel, eventNameLabel, eventNameField,
                eventDateLabel, eventDatePicker, eventTimeLabel, eventTimeField,
                eventDescLabel, eventDescArea, buttonBox);

        Scene scene = new Scene(mainLayout, 1000, 800);
        postEventStage.setScene(scene);
        postEventStage.show();
    }

    private static void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static boolean saveEventToJson(Event newEvent) {
        try {
            List<Event> events = loadEventsFromJson();
            events.add(newEvent);
            objectMapper.writeValue(new File(EVENTS_FILE), events);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static List<Event> loadEventsFromJson() {
        try {
            File file = new File(EVENTS_FILE);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<List<Event>>() {});
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
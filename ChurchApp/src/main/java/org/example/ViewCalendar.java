package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;

public class ViewCalendar {
    private static final String EVENTS_FILE = "church_events.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static YearMonth currentMonth = YearMonth.now();
    private static Stage calendarStage;

    public static void showCalendarWindow() {
        calendarStage = new Stage();
        calendarStage.setTitle("Church Calendar");
        VBox mainLayout = createCalendarLayout();
        Scene scene = new Scene(mainLayout, 1000, 800);
        calendarStage.setScene(scene);
        calendarStage.show();
    }

    private static VBox createCalendarLayout() {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Church Calendar");
        HBox monthNavigation = createMonthNavigation();
        GridPane calendarGrid = createCalendarGrid();
        VBox eventsList = createEventsList();
        mainLayout.getChildren().addAll(titleLabel, monthNavigation, calendarGrid, eventsList);
        return mainLayout;
    }

    private static HBox createMonthNavigation() {
        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);
        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");
        Label monthLabel = new Label(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        prevButton.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendarDisplay();
        });
        nextButton.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendarDisplay();
        });
        navBox.getChildren().addAll(prevButton, monthLabel, nextButton);
        return navBox;
    }

    private static GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPrefWidth(900);
        grid.setPrefHeight(400);
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < dayHeaders.length; i++) {
            Label dayLabel = new Label(dayHeaders[i]);
            dayLabel.setPrefWidth(120);
            dayLabel.setPrefHeight(30);
            grid.add(dayLabel, i, 0);
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();
        List<Event> monthEvents = loadEventsForMonth(currentMonth);

        int row = 1;
        int col = startDayOfWeek;
        for (int day = 1; day <= daysInMonth; day++) {
            VBox dayBox = createDayBox(day, monthEvents);
            grid.add(dayBox, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    private static VBox createDayBox(int day, List<Event> monthEvents) {
        VBox dayBox = new VBox(2);
        dayBox.setPrefWidth(120);
        dayBox.setPrefHeight(50);
        dayBox.setMinWidth(120);
        dayBox.setMinHeight(50);
        dayBox.setMaxWidth(120);
        dayBox.setMaxHeight(50);

        Label dayLabel = new Label(String.valueOf(day));
        dayBox.getChildren().add(dayLabel);

        LocalDate currentDate = currentMonth.atDay(day);
        int eventCount = 0;
        for (Event event : monthEvents) {
            if (event.getDate().equals(currentDate.toString()) && eventCount < 2) {
                Label eventLabel = new Label(event.getName());
                if (event.getName().length() > 10) {
                    eventLabel.setText(event.getName().substring(0, 10) + "...");
                }
                dayBox.getChildren().add(eventLabel);
                eventCount++;
            }
        }
        return dayBox;
    }

    private static VBox createEventsList() {
        VBox eventsBox = new VBox(10);
        Label eventsTitle = new Label("Events this Month");
        List<Event> monthEvents = loadEventsForMonth(currentMonth);

        if (monthEvents.isEmpty()) {
            Label noEventsLabel = new Label("No events");
            eventsBox.getChildren().addAll(eventsTitle, noEventsLabel);
        } else {
            VBox eventsList = new VBox(5);
            for (Event event : monthEvents) {
                VBox eventBox = createEventBox(event);
                eventsList.getChildren().add(eventBox);
            }
            eventsBox.getChildren().addAll(eventsTitle, eventsList);
        }
        return eventsBox;
    }

    private static VBox createEventBox(Event event) {
        VBox eventBox = new VBox(2);
        Label nameLabel = new Label(event.getName());
        Label dateTimeLabel = new Label(formatDate(event.getDate()) + " at " + event.getTime());
        eventBox.getChildren().addAll(nameLabel, dateTimeLabel);

        if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            Label descLabel = new Label(event.getDescription());
            eventBox.getChildren().add(descLabel);
        }
        return eventBox;
    }

    private static List<Event> loadEventsForMonth(YearMonth month) {
        List<Event> allEvents = loadEventsFromJson();
        List<Event> monthEvents = new ArrayList<>();
        for (Event event : allEvents) {
            try {
                LocalDate eventDate = LocalDate.parse(event.getDate());
                if (YearMonth.from(eventDate).equals(month)) {
                    monthEvents.add(event);
                }
            } catch (Exception e) {
            }
        }
        return monthEvents;
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

    private static String formatDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        } catch (Exception e) {
            return dateStr;
        }
    }

    private static void refreshCalendarDisplay() {
        VBox newLayout = createCalendarLayout();
        Scene currentScene = calendarStage.getScene();
        currentScene.setRoot(newLayout);
    }
}
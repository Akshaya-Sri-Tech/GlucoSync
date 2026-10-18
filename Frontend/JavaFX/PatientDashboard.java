
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PatientDashboard extends Application {

    private static final String TEAL = "#087F8C";
    private static final String DARK_TEAL = "#066570";
    private static final String BACKGROUND = "#EAF6F7";

    @Override
    public void start(Stage stage) {

        Label brand = new Label("GlucoSync");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        brand.setStyle("-fx-text-fill: " + TEAL + ";");

        Label heading = new Label("Patient Dashboard");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        heading.setStyle("-fx-text-fill: #263746;");

        Label subtitle = new Label(
                "Hi! Patient, enter your glucose values"
        );
        subtitle.setStyle(
                "-fx-text-fill: #64748B; -fx-font-size: 14px;"
        );
        subtitle.setWrapText(true);

        // Pre-meal spinner
        Label preLabel = new Label("Pre-meal glucose (mg/dL)");
        styleLabel(preLabel);

        Spinner<Integer> preMeal = createSpinner(100);

        // Post-meal spinner
        Label postLabel = new Label("Post-meal glucose (mg/dL)");
        styleLabel(postLabel);

        Spinner<Integer> postMeal = createSpinner(120);

        Label dateTime = new Label(
                "Date and time will be recorded on submission."
        );
        dateTime.setWrapText(true);
        dateTime.setStyle("-fx-text-fill: #64748B;");

        Label result = new Label("");
        result.setWrapText(true);
        result.setMaxWidth(Double.MAX_VALUE);

        Button submit = new Button("Submit Glucose Values");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setPrefHeight(56);
        submit.setFont(Font.font("Arial", 16));
        submit.setStyle(
                "-fx-background-color: " + TEAL + ";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 10;"
                + "-fx-cursor: hand;"
        );

        submit.setOnMouseEntered(e
                -> submit.setStyle(
                        "-fx-background-color: " + DARK_TEAL + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 10;"
                        + "-fx-cursor: hand;"
                )
        );

        submit.setOnMouseExited(e
                -> submit.setStyle(
                        "-fx-background-color: " + TEAL + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 10;"
                        + "-fx-cursor: hand;"
                )
        );

        submit.setOnAction(e -> {
            try {
                // Read values typed directly into editable spinners
                preMeal.getValueFactory().setValue(
                        Integer.valueOf(preMeal.getEditor().getText())
                );

                postMeal.getValueFactory().setValue(
                        Integer.valueOf(postMeal.getEditor().getText())
                );

                int pre = preMeal.getValue();
                int post = postMeal.getValue();

                if (pre < 0 || pre > 600
                        || post < 0 || post > 600) {
                    result.setText(
                            "Enter values between 0 and 600 mg/dL."
                    );
                    return;
                }

                LocalDateTime submittedAt = LocalDateTime.now();

                DateTimeFormatter formatter
                        = DateTimeFormatter.ofPattern(
                                "dd-MM-yyyy hh:mm a"
                        );

                String formattedTime
                        = submittedAt.format(formatter);

                // Temporary display until database integration
                dateTime.setText("Recorded at: " + formattedTime);

                result.setStyle(
                        "-fx-text-fill: " + TEAL + ";"
                        + "-fx-font-size: 14px;"
                );

                result.setText(
                        "Readings captured successfully!\n"
                        + "Pre-meal: " + pre + " mg/dL\n"
                        + "Post-meal: " + post + " mg/dL"
                );

                // Next step: save values and submittedAt to MySQL.
            } catch (NumberFormatException ex) {
                result.setStyle("-fx-text-fill: #C62828;");
                result.setText(
                        "Please enter valid whole-number glucose readings."
                );
            }
        });

        VBox card = new VBox(
                14,
                brand,
                heading,
                subtitle,
                preLabel,
                preMeal,
                postLabel,
                postMeal,
                dateTime,
                submit,
                result
        );

        card.setMaxWidth(500);
        card.setPadding(new Insets(36));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 22;"
        );

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Scene scene = new Scene(root, 820, 800);

        stage.setTitle("GlucoSync | Patient Dashboard");
        stage.setMinWidth(650);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private Spinner<Integer> createSpinner(int initialValue) {
        Spinner<Integer> spinner = new Spinner<>();

        spinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, 600, initialValue, 1
                )
        );

        spinner.setEditable(true);
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.setPrefHeight(48);

        spinner.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-background-radius: 8;"
        );

        return spinner;
    }

    private void styleLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setStyle("-fx-text-fill: #263746;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

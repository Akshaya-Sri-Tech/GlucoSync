
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorDashboard extends Application {

    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(18);
    private final String purple = "#4C1D95";

    @Override
    public void start(Stage stage) {
        root.setStyle("-fx-background-color: #F7F5FC;");

        Label brand = new Label("GlucoSync");
        brand.setFont(Font.font("Arial", 24));
        brand.setTextFill(Color.WHITE);

        Button registerBtn = navButton("Register Patient");
        Button slotsBtn = navButton("Appointment Slots");

        HBox navbar = new HBox(20, brand, registerBtn, slotsBtn);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(18, 30, 18, 30));
        navbar.setStyle("-fx-background-color: " + purple + ";");

        registerBtn.setOnAction(e -> showRegisterForm());
        slotsBtn.setOnAction(e -> showAppointmentForm());

        root.setTop(navbar);
        root.setCenter(content);
        showRegisterForm();

        Scene scene = new Scene(root, 950, 700);
        stage.setTitle("GlucoSync - Doctor Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #331d57;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 16;"
        );
        return button;
    }

    private void prepareContent(String title, String subtitle) {
        content.getChildren().clear();
        content.setPadding(new Insets(30, 45, 30, 45));
        content.setAlignment(Pos.TOP_LEFT);

        Label heading = new Label(title);
        heading.setFont(Font.font("Arial", 27));
        heading.setTextFill(Color.web("#251842"));

        Label description = new Label(subtitle);
        description.setFont(Font.font("Arial", 14));
        description.setTextFill(Color.web("#6B7280"));

        content.getChildren().addAll(heading, description);
    }

    private TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        field.setStyle(
            "-fx-background-radius: 7;" +
            "-fx-border-radius: 7;" +
            "-fx-border-color: #DDD6FE;" +
            "-fx-padding: 8;"
        );
        return field;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 13));
        label.setTextFill(Color.web("#141f31"));
        return label;
    }

    private VBox field(String label, javafx.scene.Node input) {
        VBox box = new VBox(7, fieldLabel(label), input);
        return box;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setStyle(
            "-fx-background-color: " + purple + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 22;"
        );
        return button;
    }

    private void showRegisterForm() {
        prepareContent("Register Patient",
                "Add a patient and record their glucose readings.");

        TextField name = textField("Enter full name");
        TextField id = textField("Enter patient ID");
        TextField age = textField("Enter age");
        TextField preMeal = textField("Pre-meal glucose (mg/dL)");
        TextField postMeal = textField("Post-meal glucose (mg/dL)");

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton type1 = new RadioButton("Type 1");
        RadioButton type2 = new RadioButton("Type 2");
        type1.setToggleGroup(typeGroup);
        type2.setToggleGroup(typeGroup);
        type1.setSelected(true);

        HBox types = new HBox(25, type1, type2);
        types.setAlignment(Pos.CENTER_LEFT);
        types.setPadding(new Insets(10, 0, 5, 0));

        GridPane grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(18);

        grid.add(field("Patient Name", name), 0, 0);
        grid.add(field("Patient ID", id), 1, 0);
        grid.add(field("Age", age), 0, 1);
        grid.add(field("Diabetes Type", types), 1, 1);
        grid.add(field("Pre-meal Glucose", preMeal), 0, 2);
        grid.add(field("Post-meal Glucose", postMeal), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Label message = new Label();
        message.setWrapText(true);

        Button submit = primaryButton("Register Patient");
        submit.setOnAction(e -> {
            try {
                if (name.getText().isBlank() || id.getText().isBlank()
                        || age.getText().isBlank()
                        || preMeal.getText().isBlank()
                        || postMeal.getText().isBlank()
                        || typeGroup.getSelectedToggle() == null) {
                    throw new IllegalArgumentException(
                            "Please fill in all fields.");
                }

                int patientAge = Integer.parseInt(age.getText().trim());
                double pre = Double.parseDouble(preMeal.getText().trim());
                double post = Double.parseDouble(postMeal.getText().trim());

                if (patientAge <= 0 || patientAge > 120
                        || pre < 0 || post < 0
                        || !Double.isFinite(pre) || !Double.isFinite(post)) {
                    throw new IllegalArgumentException(
                            "Enter valid age and glucose readings.");
                }

                String diabetesType = type1.isSelected()
                        ? "Type 1" : "Type 2";

                message.setText(
                    "Patient registered in this demo!\n" +
                    "Name: " + name.getText().trim() +
                    " | ID: " + id.getText().trim() +
                    " | Age: " + patientAge +
                    " | " + diabetesType +
                    "\nPre-meal: " + pre + " mg/dL" +
                    " | Post-meal: " + post + " mg/dL"
                );
                message.setTextFill(Color.web("#15803D"));

            } catch (NumberFormatException ex) {
                message.setText("Age and glucose readings must be numbers.");
                message.setTextFill(Color.RED);
            } catch (IllegalArgumentException ex) {
                message.setText(ex.getMessage());
                message.setTextFill(Color.RED);
            }
        });

        VBox card = new VBox(20, grid, submit, message);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E9E3F8;" +
            "-fx-border-radius: 14;"
        );

        content.getChildren().add(card);
    }

    private void showAppointmentForm() {
        prepareContent("Appointment Slots",
                "Create an available date and time for patient appointments.");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefHeight(40);
        datePicker.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> timePicker = new ComboBox<>();
        timePicker.getItems().addAll(
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM", "04:00 PM"
        );
        timePicker.setPromptText("Select time");
        timePicker.setPrefHeight(40);
        timePicker.setMaxWidth(Double.MAX_VALUE);

        ListView<String> slots = new ListView<>();
        slots.setPlaceholder(new Label("No appointment slots added yet."));
        slots.setPrefHeight(260);

        Label message = new Label();
        Button addSlot = primaryButton("Add Appointment Slot");

        addSlot.setOnAction(e -> {
            if (datePicker.getValue() == null || timePicker.getValue() == null) {
                message.setText("Please select both a date and time.");
                message.setTextFill(Color.RED);
                return;
            }

            if (datePicker.getValue().isBefore(LocalDate.now())) {
                message.setText("Please select today or a future date.");
                message.setTextFill(Color.RED);
                return;
            }

            String slot = datePicker.getValue() + "  |  "
                    + timePicker.getValue();

            if (slots.getItems().contains(slot)) {
                message.setText("This appointment slot already exists.");
                message.setTextFill(Color.RED);
                return;
            }

            slots.getItems().add(slot);
            message.setText("Appointment slot added.");
            message.setTextFill(Color.web("#15803D"));
        });

        VBox card = new VBox(18,
                field("Appointment Date", datePicker),
                field("Appointment Time", timePicker),
                addSlot,
                message,
                field("Available Slots", slots)
        );

        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E9E3F8;" +
            "-fx-border-radius: 14;"
        );

        content.getChildren().add(card);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

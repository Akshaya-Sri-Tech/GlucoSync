
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PatientLogin extends Application {

    @Override
    public void start(Stage stage) {
        Label logo = new Label("GlucoSync");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        logo.setTextFill(Color.web("#087F8C"));

        Label title = new Label("Patient Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));

        Label subtitle = new Label("Welcome back! Monitor your health.");
        subtitle.setTextFill(Color.GRAY);

        TextField patientId = new TextField();
        patientId.setPromptText("Enter your patient ID");
        patientId.setPrefHeight(42);

        TextField email = new TextField();
        email.setPromptText("Enter your email");
        email.setPrefHeight(42);

        PasswordField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setPrefHeight(42);

        Label message = new Label();
        message.setTextFill(Color.RED);

        Button login = new Button("Login");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setPrefHeight(44);
        login.setStyle(
                "-fx-background-color: #087F8C;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-background-radius: 8px;"
        );

        login.setOnAction(e -> {
            if (patientId.getText().isBlank()
                    || email.getText().isBlank()
                    || password.getText().isBlank()) {

                message.setText("Please fill in all fields.");
                message.setTextFill(Color.RED);

            } else if (!patientId.getText().matches("\\d+")) {

                message.setText("Enter a valid Patient ID.");
                message.setTextFill(Color.RED);

            } else if (!email.getText().matches(
                    "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {

                message.setText("Enter a valid email address.");
                message.setTextFill(Color.RED);

            } else {
                message.setText("Login details validated!");
                message.setTextFill(Color.web("#087F8C"));
            }
        });

        VBox form = new VBox(13,
                logo, title, subtitle,
                new Label("Patient ID"), patientId,
                new Label("Email Address"), email,
                new Label("Password"), password,
                message, login
        );
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(430);
        form.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 18px;"
        );

        StackPane root = new StackPane(form);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #EAF6F7;");

        Scene scene = new Scene(root, 650, 600);
        stage.setTitle("GlucoSync | Patient Login");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

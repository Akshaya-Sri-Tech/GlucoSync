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

public class DoctorLogin extends Application {

    @Override
    public void start(Stage stage) {
        Label logo = new Label("GlucoSync");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        logo.setTextFill(Color.web("#6C4AB6"));

        Label title = new Label("Doctor Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));

        Label subtitle = new Label("Welcome back! Manage your patients.");
        subtitle.setTextFill(Color.GRAY);

        TextField doctorId = new TextField();
        doctorId.setPromptText("Enter your Doctor ID");
        doctorId.setPrefHeight(42);

        PasswordField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setPrefHeight(42);

        Label message = new Label();
        message.setWrapText(true);
        message.setTextFill(Color.RED);

        Button login = new Button("Login");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setPrefHeight(44);
        login.setStyle(
                "-fx-background-color: #6C4AB6;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-background-radius: 8px;"
        );

        login.setOnAction(e -> {
            String id = doctorId.getText().trim();
            String pass = password.getText();

            if (id.isBlank() || pass.isBlank()) {
                message.setText("Please fill in all fields.");
                message.setTextFill(Color.RED);
            } else if (!id.matches("\\d+")) {
                message.setText("Doctor ID must contain digits only.");
                message.setTextFill(Color.RED);
            } else if (!pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$")) {
                message.setText("Password must be 8+ characters with uppercase, lowercase and a digit.");
                message.setTextFill(Color.RED);
            } else {
                message.setText("Login details validated!");
                message.setTextFill(Color.web("#6C4AB6"));
            }
        });

        VBox form = new VBox(13,
                logo, title, subtitle,
                new Label("Doctor ID"), doctorId,
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
        root.setStyle("-fx-background-color: #F1ECFA;");

        Scene scene = new Scene(root, 650, 600);
        stage.setTitle("GlucoSync | Doctor Login");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
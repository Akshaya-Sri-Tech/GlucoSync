import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AlertNotification extends Frame {

    Label title, glucose, warning;
    Button close;

    AlertNotification(double sugarLevel, double threshold) {
        setTitle("GlucoSync - Alert Notification");
        setSize(400, 250);
        setLayout(new GridLayout(4, 1, 10, 10));
        setBackground(Color.WHITE);

        title = new Label("GLUCOSYNC - GLUCOSE ALERT", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.RED);

        glucose = new Label("Sugar Level: " + sugarLevel + " mg/dL", Label.CENTER);
        glucose.setFont(new Font("Arial", Font.BOLD, 14));

        warning = new Label("WARNING: Threshold exceeded!", Label.CENTER);
        warning.setForeground(Color.RED);
        warning.setFont(new Font("Arial", Font.BOLD, 14));

        close = new Button("Acknowledge Alert");

        add(title);
        add(glucose);
        add(warning);
        add(close);

        close.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        double sugarLevel = 250;
        double threshold = 180;

        if (sugarLevel > threshold) {
            EventQueue.invokeLater(() ->
                new AlertNotification(sugarLevel, threshold)
            );
        }
    }
}

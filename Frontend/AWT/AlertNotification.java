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

    Label title, warning;
    Button close;

    AlertNotification() {
        setTitle("GlucoSync - Alert Notification");
        setSize(400, 200);
        setLayout(new GridLayout(4, 1, 10, 10));
        setBackground(Color.WHITE);

        title = new Label("GLUCOSYNC - GLUCOSE ALERT", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.RED);

        warning = new Label("WARNING: Threshold exceeded!", Label.CENTER);
        warning.setForeground(Color.RED);
        warning.setFont(new Font("Arial", Font.BOLD, 14));

        close = new Button("Book Appointment");

        add(title);
        add(warning);
        add(close);

        close.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
            EventQueue.invokeLater(() -> new AlertNotification());
    }
}

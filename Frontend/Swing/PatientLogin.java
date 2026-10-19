
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class PatientLogin extends JFrame {

    private static final Color TEAL = new Color(8, 127, 140);
    private static final Color DARK_TEAL = new Color(6, 101, 112);
    private static final Color BACKGROUND = new Color(234, 246, 247);

    private JTextField patientId;
    private JPasswordField password;
    private JLabel message;

    public PatientLogin() {
        setTitle("GlucoSync | Patient Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);
        setMinimumSize(new Dimension(500, 500));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setPreferredSize(new Dimension(430, 460));

        JLabel logo = new JLabel("GlucoSync");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(TEAL);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Patient Login");
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(new Color(38, 55, 70));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Welcome back! Monitor your health."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        patientId = new JTextField();
        styleInput(patientId);
        patientId.putClientProperty("JTextField.placeholderText",
                "Enter your patient ID");

        password = new JPasswordField();
        styleInput(password);
        password.putClientProperty("JTextField.placeholderText",
                "Enter your password");

        message = new JLabel(" ");
        message.setFont(new Font("Arial", Font.PLAIN, 13));
        message.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton login = new JButton("Login");
        login.setFont(new Font("Arial", Font.BOLD, 15));
        login.setForeground(Color.WHITE);
        login.setBackground(TEAL);
        login.setFocusPainted(false);
        login.setBorderPainted(false);
        login.setOpaque(true);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login.setPreferredSize(new Dimension(370, 44));
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        login.setAlignmentX(Component.LEFT_ALIGNMENT);

        login.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                login.setBackground(DARK_TEAL);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                login.setBackground(TEAL);
            }
        });

        login.addActionListener(e -> validateLogin());

        addGap(form, 5);
        form.add(logo);
        addGap(form, 12);
        form.add(title);
        addGap(form, 5);
        form.add(subtitle);
        addGap(form, 25);

        addField(form, "Patient ID", patientId);
        addGap(form, 15);
        addField(form, "Password", password);
        addGap(form, 12);

        form.add(message);
        addGap(form, 10);
        form.add(login);

        root.add(form);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(370, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 220, 225)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    }

    private void addField(JPanel panel, String text, JTextField field) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(38, 55, 70));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        addGap(panel, 7);
        panel.add(field);
    }

    private void addGap(JPanel panel, int height) {
        panel.add(Box.createRigidArea(new Dimension(0, height)));
    }

    private void validateLogin() {
        String id = patientId.getText().trim();
        String pass = new String(password.getPassword());

        if (id.isBlank() || pass.isBlank()) {
            showMessage("Please fill in all fields.", Color.RED);

        } else if (!id.matches("\\d+")) {
            showMessage("Enter a valid Patient ID.", Color.RED);

        } else if (!pass.matches("\\d+")
               ) {
            showMessage("Enter a valid password.", Color.RED);

        } else {
            // Open Patient Dashboard
            PatientDashboard dashboard
                    = new PatientDashboard(Integer.parseInt(id));

            dashboard.setVisible(true);

            // Close the login window
            this.dispose();
        }
    }

    private void showMessage(String text, Color color) {
        message.setText(text);
        message.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PatientLogin login = new PatientLogin();
            login.setVisible(true);
        });
    }
}

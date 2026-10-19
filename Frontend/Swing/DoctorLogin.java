
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DoctorLogin extends JFrame {

    private final Color PURPLE = new Color(108, 74, 182);
    private final Color BACKGROUND = new Color(241, 236, 250);

    private JTextField doctorId;
    private JPasswordField password;
    private JLabel message;

    public DoctorLogin() {
        setTitle("GlucoSync | Doctor Login");
        setSize(650, 600);
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel logo = new JLabel("GlucoSync");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(PURPLE);

        JLabel title = new JLabel("Doctor Login");
        title.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel subtitle = new JLabel(
            "Welcome back! Manage your patients."
        );
        subtitle.setForeground(Color.GRAY);

        doctorId = new JTextField();
        doctorId.setPreferredSize(new Dimension(350, 42));
        doctorId.setFont(new Font("Arial", Font.PLAIN, 14));

        password = new JPasswordField();
        password.setPreferredSize(new Dimension(350, 42));
        password.setFont(new Font("Arial", Font.PLAIN, 14));

        message = new JLabel(" ");
        message.setForeground(Color.RED);
        message.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton login = new JButton("Login");
        login.setPreferredSize(new Dimension(350, 44));
        login.setBackground(PURPLE);
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Arial", Font.BOLD, 15));
        login.setFocusPainted(false);
        login.setOpaque(true);
        login.setBorderPainted(false);
        login.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        login.addActionListener(e -> validateLogin());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setMaximumSize(new Dimension(430, 500));

        addField(form, "Doctor ID", doctorId);
        addField(form, "Password", password);

        form.add(Box.createVerticalStrut(8));
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(message);

        form.add(Box.createVerticalStrut(8));
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        form.add(login);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(Color.WHITE);
        headingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headingPanel.add(logo);
        headingPanel.add(Box.createVerticalStrut(12));
        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subtitle);
        headingPanel.add(Box.createVerticalStrut(20));

        form.add(headingPanel,0);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(230, 225, 240)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.add(form);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));
        root.add(card);

        add(root);
    }

    private void addField(
        JPanel form, String labelText, JComponent field
    ) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        form.add(label);
        form.add(Box.createVerticalStrut(6));
        form.add(field);
        form.add(Box.createVerticalStrut(13));
    }

    private void validateLogin() {
        String id = doctorId.getText().trim();
        String pass = new String(password.getPassword());

        if (id.isBlank() || pass.isBlank()) {
            showMessage("Please fill in all fields.", false);
        } else if (!id.matches("\\d+")) {
            showMessage("Doctor ID must contain digits only.", false);
        } else if (!pass.matches(
            "\\d+"
        )) {
            showMessage(
                "Password must be 8+ characters with uppercase, lowercase and a digit.",
                false
            );
        } else {
            DoctorDashboard dashboard
                    = new DoctorDashboard(Integer.parseInt(id));

            dashboard.setVisible(true);

            // Close the login window
            this.dispose();
        }
    }

    private void showMessage(String text, boolean success) {
        message.setText(text);
        message.setForeground(
            success ? PURPLE : Color.RED
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoctorLogin login = new DoctorLogin();
            login.setVisible(true);
        });
    }
}

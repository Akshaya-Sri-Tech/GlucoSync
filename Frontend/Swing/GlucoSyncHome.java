
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GlucoSyncHome extends JFrame {

    private final Color brown = new Color(141, 110, 99);       // Medium brown
    private final Color darkBrown = new Color(93, 64, 55);     // Dark brown
    private final Color lightBrown = new Color(239, 235, 233); // Light beige
    private final Color whiteBrown  = new Color(250, 247, 242); // Cream

    public GlucoSyncHome() {
        setTitle("GlucoSync");
        setSize(1000, 700);
        setMinimumSize(new Dimension(750, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(whiteBrown);
        setContentPane(main);

        // Top branding
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(lightBrown);

        JLabel brand = new JLabel("\u2665  GlucoSync");
        brand.setFont(new Font("SansSerif", Font.BOLD, 23));
        brand.setForeground(darkBrown);
        header.add(brand);
        main.add(header, BorderLayout.NORTH);

        // Center content
        JPanel center = new JPanel();
        center.setBackground(whiteBrown);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("\u2665");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 76));
        logo.setForeground(brown);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("GlucoSync");
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(darkBrown);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Track  \u2022  Monitor  \u2022  Stay Healthy");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 19));
        tagline.setForeground(darkBrown);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(logo);
        center.add(Box.createVerticalStrut(5));
        center.add(title);
        center.add(Box.createVerticalStrut(8));
        center.add(tagline);
        center.add(Box.createVerticalStrut(45));

        // Login buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 30, 0));
        buttons.setBackground(whiteBrown);
        buttons.setMaximumSize(new Dimension(560, 100));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton doctor = createButton("Doctor Login");
        JButton patient = createButton("Patient Login");

        doctor.addActionListener(e -> openLogin("Doctor"));
        patient.addActionListener(e -> openLogin("Patient"));

        buttons.add(doctor);
        buttons.add(patient);

        center.add(buttons);
        center.add(Box.createVerticalStrut(45));

        JLabel footer = new JLabel(
                "Better Glucose Control for a Healthier Tomorrow"
        );
        footer.setFont(new Font("SansSerif", Font.PLAIN, 16));
        footer.setForeground(darkBrown);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(footer);
        center.add(Box.createVerticalGlue());

        main.add(center, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 19));
        button.setForeground(darkBrown);
        button.setBackground(lightBrown);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(brown, 2),
                BorderFactory.createEmptyBorder(22, 25, 22, 25)
        ));
        button.setOpaque(true);
        return button;
    }

    private void openLogin(String role) {
        if (role.equals("Doctor")) {
            new DoctorLogin().setVisible(true);
        } else if (role.equals("Patient")) {
            new PatientLogin().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GlucoSyncHome().setVisible(true);
        });
    }
}

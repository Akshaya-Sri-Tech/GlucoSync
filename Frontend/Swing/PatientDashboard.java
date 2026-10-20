
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientDashboard extends JFrame {

    private static final Color TEAL = new Color(8, 127, 140);
    private static final Color DARK_TEAL = new Color(6, 101, 112);
    private static final Color BACKGROUND = new Color(234, 246, 247);
    private static final Color TEXT = new Color(38, 55, 70);
    private static final Color MUTED = new Color(100, 116, 139);

    private final int patientId;
    private JSpinner preMeal;
    private JSpinner postMeal;
    private JLabel dateTime;
    private JLabel result;

    private JLabel riskFlag;
    private JLabel riskDetails;

    public PatientDashboard(int patientId) {
        this.patientId = patientId;
        setTitle("GlucoSync | Patient Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 800);
        setMinimumSize(new Dimension(650, 700));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(36, 36, 36, 36));
        card.setPreferredSize(new Dimension(500, 650));

        JLabel brand = new JLabel("GlucoSync");
        brand.setFont(new Font("Arial", Font.BOLD, 30));
        brand.setForeground(TEAL);

        JLabel patientInfo = new JLabel("Patient ID: " + patientId);
        patientInfo.setFont(new Font("Arial", Font.BOLD, 14));
        patientInfo.setForeground(TEAL);

        JLabel heading = new JLabel("Patient Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "<html>Hi! Patient, enter your glucose values</html>"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        preMeal = createSpinner(100);
        postMeal = createSpinner(120);

        dateTime = new JLabel(
                "<html>Date and time will be recorded on submission.</html>"
        );
        dateTime.setFont(new Font("Arial", Font.PLAIN, 13));
        dateTime.setForeground(MUTED);
        dateTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        result = new JLabel(" ");
        result.setFont(new Font("Arial", Font.PLAIN, 14));
        result.setForeground(TEAL);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Risk status flag
        riskFlag = new JLabel("  Awaiting readings  ");
        riskFlag.setFont(new Font("Arial", Font.BOLD, 15));
        riskFlag.setOpaque(true);
        riskFlag.setBackground(new Color(230, 230, 230));
        riskFlag.setForeground(TEXT);
        riskFlag.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );
        riskFlag.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Risk explanation
        riskDetails = new JLabel(
                "Submit your readings to view the risk status."
        );
        riskDetails.setFont(new Font("Arial", Font.PLAIN, 13));
        riskDetails.setForeground(MUTED);
        riskDetails.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton submit = new JButton("Submit Glucose Values");
        submit.setFont(new Font("Arial", Font.BOLD, 16));
        submit.setForeground(Color.WHITE);
        submit.setBackground(TEAL);
        submit.setFocusPainted(false);
        submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submit.setBorderPainted(false);
        submit.setOpaque(true);
        submit.setPreferredSize(new Dimension(420, 52));
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);

        submit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                submit.setBackground(DARK_TEAL);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                submit.setBackground(TEAL);
            }
        });

        submit.addActionListener(e -> submitReadings());

        card.add(brand);
        addGap(card, 12);
        card.add(heading);
        addGap(card, 8);
        card.add(subtitle);
        addGap(card, 28);

        addField(card, "Pre-meal glucose (mg/dL)", preMeal);
        addGap(card, 18);
        addField(card, "Post-meal glucose (mg/dL)", postMeal);
        addGap(card, 20);

        card.add(dateTime);
        addGap(card, 22);

        card.add(submit);
        addGap(card, 18);
        card.add(riskFlag);
        addGap(card, 8);
        card.add(riskDetails);
        addGap(card, 10);
        card.add(result);

        root.add(card);
        setContentPane(root);
    }

    private JSpinner createSpinner(int initialValue) {
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(initialValue, null, null, 1)
        );

        spinner.setFont(new Font("Arial", Font.PLAIN, 15));
        spinner.setPreferredSize(new Dimension(420, 42));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner.DefaultEditor editor
                = (JSpinner.DefaultEditor) spinner.getEditor();

        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);

        return spinner;
    }

    private String classifyRisk(int pre, int post) {
        // Placeholder thresholds for testing the application.
        boolean high = pre > 130 || post > 180;
        boolean low = pre < 70 || post < 70;

        // High takes priority if either reading is high.
        if (high) {
            return "HIGH";
        }

        if (low) {
            return "LOW";
        }

        return "MODERATE";
    }

    private void addField(JPanel panel, String labelText,
            JComponent input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        addGap(panel, 8);
        panel.add(input);
    }

    private void addGap(JPanel panel, int height) {
        panel.add(Box.createRigidArea(new Dimension(0, height)));
    }

    private long saveReading(
            int value, String status, String mealContext)
            throws IOException, InterruptedException {

        String json = String.format(
                "{\"patientId\":%d,\"glucoseValue\":%d,"
                + "\"status\":\"%s\",\"mealContext\":\"%s\"}",
                patientId, value, status, mealContext
        );

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:8080/api/glucose/readings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 201) {
            throw new IOException(
                    "Backend returned HTTP "
                    + response.statusCode() + ": "
                    + response.body()
            );
        }

        Pattern pattern = Pattern.compile(
                "\"readingId\"\\s*:\\s*(\\d+)"
        );

        Matcher matcher = pattern.matcher(response.body());

        if (!matcher.find()) {
            throw new IOException(
                    "Could not read the generated reading ID."
            );
        }

        return Long.parseLong(matcher.group(1));
    }

    private void submitReadings() {
        try {
            preMeal.commitEdit();
            postMeal.commitEdit();

            int pre = (Integer) preMeal.getValue();
            int post = (Integer) postMeal.getValue();

            if (pre <= 0 || pre > 600) {
                result.setForeground(Color.RED);
                result.setText(
                        "Pre-meal reading must be between 1 and 600 mg/dL."
                );
                return;
            }

            if (post <= 0 || post > 600) {
                result.setForeground(Color.RED);
                result.setText(
                        "Post-meal reading must be between 1 and 600 mg/dL."
                );
                return;
            }

            // Determine the database status for each reading.
            String preStatus = getReadingStatus(pre, 130);
            String postStatus = getReadingStatus(post, 180);

            // Connect the GUI to the existing DAO.
            // Send both readings through the Spring Boot backend.
            long preReadingId = saveReading(
                    pre, preStatus, "Pre-meal"
            );

            long postReadingId = saveReading(
                    post, postStatus, "Post-meal"
            );

            // Both DAO calls succeeded.
            LocalDateTime submittedAt = LocalDateTime.now();

            DateTimeFormatter formatter
                    = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

            dateTime.setText(
                    "Recorded at: " + submittedAt.format(formatter)
            );

            String risk = classifyRisk(pre, post);

            switch (risk) {
                case "HIGH":
                    riskFlag.setText("  HIGH RISK  ");
                    riskFlag.setBackground(new Color(211, 47, 47));
                    riskFlag.setForeground(Color.WHITE);
                    riskDetails.setText(
                            "A reading crossed the configured high threshold."
                    );
                    AlertNotification.showAlert();
                    break;

                case "LOW":
                    riskFlag.setText("  LOW RISK  ");
                    riskFlag.setBackground(new Color(255, 213, 79));
                    riskFlag.setForeground(new Color(70, 50, 0));
                    riskDetails.setText(
                            "A reading fell below the configured low threshold."
                    );
                    break;

                default:
                    riskFlag.setText("  MODERATE  ");
                    riskFlag.setBackground(new Color(46, 125, 50));
                    riskFlag.setForeground(Color.WHITE);
                    riskDetails.setText(
                            "Both readings are within the example target ranges."
                    );
                    break;
            }

            result.setForeground(TEAL);
            result.setText(
                    "<html>Readings saved successfully!<br>"
                    + "Patient ID: " + patientId + "<br>"
                    + "Pre-meal: " + pre + " mg/dL<br>"
                    + "Post-meal: " + post + " mg/dL<br>"
                    + "Reading IDs: " + preReadingId + ", "
                    + postReadingId + "<br>"
                    + "Risk status: " + risk + "</html>"
            );

        } catch (NumberFormatException
                | java.text.ParseException ex) {

            result.setForeground(Color.RED);
            result.setText("Please enter valid whole-number readings.");

        } catch (IllegalArgumentException ex) {

            result.setForeground(Color.RED);
            result.setText("Invalid glucose reading: " + ex.getMessage());

        } catch (IOException ex) {

            result.setForeground(Color.RED);
            result.setText(
                    "<html>Could not save readings.<br>"
                    + "Check that the backend is running.</html>"
            );

            ex.printStackTrace();
        }
        catch (InterruptedException ex) {

            result.setForeground(Color.RED);
            result.setText(
                    "<html>Operation interrupted.<br>"
                    + "Please try again.</html>"
            );

            Thread.currentThread().interrupt();
        }
    }

    private String getReadingStatus(int value, int highThreshold) {
        if (value < 70) {
            return "Low";
        }

        if (value > highThreshold) {
            return "High";
        }

        return "Normal";
    }

}

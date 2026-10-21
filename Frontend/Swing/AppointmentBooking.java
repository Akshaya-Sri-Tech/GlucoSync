import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AppointmentBooking extends JFrame {

    private static final Color TEAL = new Color(8, 127, 140);
    private static final Color BG = new Color(234, 246, 247);
    private static final Color TEXT = new Color(45, 55, 60);
    private static final Color BORDER = new Color(200, 229, 231);
    private static final Color SLOT_BG = new Color(245, 251, 251);

    private static final String API_BASE =
            "http://localhost:8080/api/appointments";

    private final long patientId;

    private final HttpClient client = HttpClient.newHttpClient();

    private final List<DoctorSlot> slots = new ArrayList<>();

    private record DoctorSlot(
            long slotId,
            long doctorId,
            String doctorName,
            String date,
            String startTime,
            String endTime) {
    }

    private final JRadioButton[] choices;
    private final ButtonGroup group = new ButtonGroup();
    private final JLabel status = new JLabel(" ");

    public AppointmentBooking(long patientId) {

        this.patientId = patientId;

        // Fetch available slots from the backend
        loadAvailableSlots();

        choices = new JRadioButton[slots.size()];

        setTitle("GlucoSync | Appointment Booking");
        setSize(650, 620);
        setMinimumSize(new Dimension(550, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 30, 22, 30));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);

        JLabel logo = new JLabel("GlucoSync");
        logo.setFont(new Font("Arial", Font.BOLD, 27));
        logo.setForeground(TEAL);

        JLabel heading = new JLabel("Book an Appointment");
        heading.setFont(new Font("Arial", Font.BOLD, 21));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Choose an available date and time.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        header.add(logo);
        header.add(Box.createVerticalStrut(10));
        header.add(heading);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        // Appointment slot list
        JPanel slotList = new JPanel();
        slotList.setLayout(new BoxLayout(slotList, BoxLayout.Y_AXIS));
        slotList.setBackground(Color.WHITE);
        slotList.setBorder(new EmptyBorder(12, 12, 12, 12));

        if (slots.isEmpty()) {
            JLabel noSlotsLabel = new JLabel(
                    "No available appointment slots found.");
            noSlotsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            noSlotsLabel.setForeground(Color.GRAY);
            noSlotsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            slotList.add(noSlotsLabel);
        }

        for (int i = 0; i < slots.size(); i++) {

            final int index = i;
            DoctorSlot slot = slots.get(i);

            JPanel slotCard = new JPanel(new BorderLayout(8, 0));
            slotCard.setBackground(SLOT_BG);
            slotCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(7, 12, 7, 10)
            ));

            // Compact, consistent card height
            slotCard.setPreferredSize(new Dimension(500, 64));
            slotCard.setMinimumSize(new Dimension(300, 64));
            slotCard.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 64));
            slotCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Date and time details
            JPanel details = new JPanel();
            details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
            details.setBackground(SLOT_BG);

            JLabel slotLabel = new JLabel(
                    "Slot " + String.format("%02d", i + 1)
                            + " | Dr. " + slot.doctorName());
            slotLabel.setFont(new Font("Arial", Font.BOLD, 10));
            slotLabel.setForeground(TEAL);

            String formattedDate = java.time.Instant.parse(slot.date())
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .format(java.time.format.DateTimeFormatter.ofPattern(
                "dd MMM yyyy", java.util.Locale.ENGLISH));

        JLabel dateLabel = new JLabel("Date: " + formattedDate);
            dateLabel.setFont(new Font("Arial", Font.BOLD, 13));
            dateLabel.setForeground(TEXT);

            JLabel timeLabel = new JLabel(
                    "Time: " + slot.startTime() + " - " + slot.endTime());
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            timeLabel.setForeground(new Color(90, 100, 105));

            details.add(slotLabel);
            details.add(dateLabel);
            details.add(timeLabel);

            // Radio button
            choices[i] = new JRadioButton();
            choices[i].setBackground(SLOT_BG);
            choices[i].setFocusPainted(false);
            choices[i].setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            group.add(choices[i]);

            slotCard.add(details, BorderLayout.CENTER);
            slotCard.add(choices[i], BorderLayout.EAST);

            slotCard.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            slotCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    choices[index].setSelected(true);
                }
            });

            slotList.add(slotCard);

            if (i < slots.size() - 1) {
                slotList.add(Box.createVerticalStrut(8));
            }
        }

        if (choices.length > 0) {
            choices[0].setSelected(true);
        }

        JScrollPane scrollPane = new JScrollPane(slotList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // Booking section
        JButton bookButton = new JButton("Book Appointment");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(TEAL);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookButton.setPreferredSize(new Dimension(200, 40));
        bookButton.setMaximumSize(new Dimension(200, 40));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bookButton.setBackground(new Color(6, 101, 112));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bookButton.setBackground(TEAL);
            }
        });

        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(TEAL);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookButton.addActionListener(e -> {

            for (int i = 0; i < choices.length; i++) {

                if (choices[i].isSelected()) {

                    DoctorSlot selectedSlot = slots.get(i);

                    bookButton.setEnabled(false);
                    status.setText("Booking appointment...");

                    try {
                        long appointmentId =
                                bookAppointment(selectedSlot.slotId());

                        String confirmation =
                                "Appointment booked successfully!\n\n"
                                + "Appointment ID: " + appointmentId
                                + "\nDoctor: " + selectedSlot.doctorName()
                                + "\nDate: " + selectedSlot.date()
                                + "\nTime: " + selectedSlot.startTime()
                                + " - " + selectedSlot.endTime();

                        status.setText("Appointment booked successfully!");

                        JOptionPane.showMessageDialog(
                                this,
                                confirmation,
                                "GlucoSync",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        // Refresh the slots after a successful booking
                        dispose();

                    } catch (IOException ex) {

                        status.setText("Unable to connect to the backend.");

                        JOptionPane.showMessageDialog(
                                this,
                                "Could not connect to the backend.\n"
                                + "Make sure the GlucoSync backend is running "
                                + "on port 8080.\n\n"
                                + "Details: " + ex.getMessage(),
                                "Connection Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                    } catch (InterruptedException ex) {

                        Thread.currentThread().interrupt();
                        status.setText("Booking was interrupted.");

                        JOptionPane.showMessageDialog(
                                this,
                                "The booking request was interrupted.",
                                "GlucoSync",
                                JOptionPane.ERROR_MESSAGE
                        );

                    } catch (RuntimeException ex) {

                        status.setText("Unable to book appointment.");

                        JOptionPane.showMessageDialog(
                                this,
                                ex.getMessage(),
                                "Booking Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                    } finally {
                        bookButton.setEnabled(true);
                    }

                    return;
                }
            }

            status.setText("Please select a slot.");

        });

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(BG);
        bottom.add(bookButton);
        bottom.add(Box.createVerticalStrut(6));
        bottom.add(status);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadAvailableSlots() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/slots"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                status.setText("Unable to load available slots.");
                return;
            }

            String json = response.body();

            // Extract each slot object from the JSON array
            Pattern objectPattern = Pattern.compile("\\{[^{}]*\\}");
            Matcher objectMatcher = objectPattern.matcher(json);

            while (objectMatcher.find()) {

                String object = objectMatcher.group();

                long slotId = readLong(object, "slotId");
                long doctorId = readLong(object, "doctorId");

                String doctorName = readString(object, "doctorName");
                String date = readString(object, "slotDate");
                String startTime = readString(object, "startTime");
                String endTime = readString(object, "endTime");

                if (slotId > 0
                        && doctorId > 0
                        && doctorName != null
                        && date != null
                        && startTime != null
                        && endTime != null) {

                    slots.add(new DoctorSlot(
                            slotId,
                            doctorId,
                            doctorName,
                            date,
                            startTime,
                            endTime
                    ));
                }
            }

        } catch (IOException ex) {

            status.setText("Cannot connect to the backend.");

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();
            status.setText("Loading slots was interrupted.");

        } catch (RuntimeException ex) {

            status.setText("Unable to load available slots.");
        }
    }

    private long bookAppointment(long slotId)
            throws IOException, InterruptedException {

        String json = String.format(
                "{\"patientId\":%d,\"slotId\":%d}",
                patientId,
                slotId
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 201) {

            Matcher matcher = Pattern.compile(
                    "\"appointmentId\"\\s*:\\s*(\\d+)"
            ).matcher(response.body());

            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }

            throw new RuntimeException(
                    "The appointment was created, but its ID "
                    + "could not be read from the backend response."
            );
        }

        if (response.statusCode() == 400) {
            throw new RuntimeException(
                    "Invalid booking details. Check the patient ID "
                    + "and selected slot."
            );
        }

        if (response.statusCode() == 404) {
            throw new RuntimeException(
                    "The patient does not exist in the database."
            );
        }

        if (response.statusCode() == 409) {
            throw new RuntimeException(
                    "This slot is no longer available. "
                    + "Please refresh and select another slot."
            );
        }

        throw new RuntimeException(
                "Booking failed. HTTP status: "
                + response.statusCode()
        );
    }

    private long readLong(String json, String field) {

        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(field)
                + "\"\\s*:\\s*(\\d+)"
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return -1;
    }

    private String readString(String json, String field) {

        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(field)
                + "\"\\s*:\\s*\"([^\"]*)\""
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new AppointmentBooking(1).setVisible(true));

    }
}
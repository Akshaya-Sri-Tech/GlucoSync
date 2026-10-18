
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DoctorDashboard extends JFrame {

    private final Color PURPLE = new Color(92, 65, 180);
    private final Color LIGHT_BG = new Color(246, 247, 251);
    private final Color TEXT = new Color(45, 45, 60);
    private final Color BORDER = new Color(225, 227, 235);

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private JTextField nameField, ageField;
    private JTextField minGlucoseField, maxGlucoseField;
    private JRadioButton type1Button, type2Button;
    private ButtonGroup diabetesGroup;

    private JLabel selectedDateLabel;
    private JComboBox<String> timeBox;
    private DefaultListModel<String> slotModel;
    private JList<String> slotList;

    private LocalDate selectedDate = LocalDate.now();
    private final Set<String> bookedSlots = new HashSet<>();

    private final String[] TIMES = {
        "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM"
    };

    public DoctorDashboard() {
        setTitle("GlucoSync - Doctor Dashboard");
        setSize(1000, 760);
        setMinimumSize(new Dimension(850, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.add(createRegistrationPage(), "register");
        contentPanel.add(createAppointmentPage(), "appointments");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "register");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PURPLE);
        header.setBorder(new EmptyBorder(15, 24, 15, 24));

        JLabel title = new JLabel("GlucoSync | Doctor Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton registerButton = createHeaderButton("Register Patient");
        JButton appointmentButton = createHeaderButton("Appointment Slots");

        registerButton.addActionListener(e ->
            cardLayout.show(contentPanel, "register")
        );

        appointmentButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "appointments");
            refreshSlots();
        });

        buttons.add(registerButton);
        buttons.add(appointmentButton);

        header.add(title, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(Color.WHITE);
        button.setForeground(PURPLE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(155, 38));
        return button;
    }

    private JPanel createRegistrationPage() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(LIGHT_BG);
        page.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel card = createCard();
        card.setPreferredSize(new Dimension(560, 590));
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(22, 28, 22, 28));

        JLabel heading = new JLabel("Register New Patient");
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
        heading.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nameField = new JTextField();
        ageField = new JTextField();
        minGlucoseField = new JTextField();
        maxGlucoseField = new JTextField();

        type1Button = new JRadioButton("Type 1 Diabetes");
        type2Button = new JRadioButton("Type 2 Diabetes");

        type1Button.setOpaque(false);
        type2Button.setOpaque(false);
        type1Button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        type2Button.setFont(new Font("SansSerif", Font.PLAIN, 14));

        diabetesGroup = new ButtonGroup();
        diabetesGroup.add(type1Button);
        diabetesGroup.add(type2Button);
        type1Button.setSelected(true);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(type1Button);
        radioPanel.add(type2Button);

        addFormRow(form, gbc, 0, "Patient Name", nameField);
        addFormRow(form, gbc, 1, "Age", ageField);
        addFormRow(form, gbc, 2, "Diabetes Type", radioPanel);
        addFormRow(form, gbc, 3, "Minimum Glucose Threshold (mg/dL)",
                   minGlucoseField);
        addFormRow(form, gbc, 4, "Maximum Glucose Threshold (mg/dL)",
                   maxGlucoseField);

        minGlucoseField.setText("70");
        maxGlucoseField.setText("180");

        JLabel note = new JLabel(
            "Set the patient's individual alert thresholds."
        );
        note.setForeground(Color.GRAY);
        note.setFont(new Font("SansSerif", Font.ITALIC, 12));

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(form, BorderLayout.CENTER);
        center.add(note, BorderLayout.SOUTH);

        JButton submitButton = createPrimaryButton("Register Patient");
        submitButton.setPreferredSize(new Dimension(190, 42));
        submitButton.addActionListener(e -> registerPatient());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);

        card.add(heading, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        page.add(card);
        return page;
    }

    private void addFormRow(
        JPanel form, GridBagConstraints gbc,
        int row, String labelText, JComponent field
    ) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT);
        form.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 34));
        form.add(field, gbc);
    }

    private void registerPatient() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String minText = minGlucoseField.getText().trim();
        String maxText = maxGlucoseField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty()
                || minText.isEmpty() || maxText.isEmpty()) {
            showMessage("Please fill in all patient details.");
            return;
        }

        int age;
        int minGlucose;
        int maxGlucose;

        try {
            age = Integer.parseInt(ageText);
            minGlucose = Integer.parseInt(minText);
            maxGlucose = Integer.parseInt(maxText);
        } catch (NumberFormatException ex) {
            showMessage("Age and glucose thresholds must be whole numbers.");
            return;
        }

        if (age < 1 || age > 120) {
            showMessage("Age must be between 1 and 120.");
            return;
        }

        if (minGlucose < 0 || maxGlucose > 600
                || minGlucose >= maxGlucose) {
            showMessage(
                "Enter glucose thresholds from 0 to 600 mg/dL.\n" +
                "Minimum must be less than maximum."
            );
            return;
        }

        String diabetesType = type1Button.isSelected()
                ? "Type 1 Diabetes" : "Type 2 Diabetes";

        showMessage(
            "Patient registered successfully!\n\n" +
            "Name: " + name +
            "\nAge: " + age +
            "\nDiabetes: " + diabetesType +
            "\nMinimum threshold: " + minGlucose + " mg/dL" +
            "\nMaximum threshold: " + maxGlucose + " mg/dL"
        );

        nameField.setText("");
        ageField.setText("");
        minGlucoseField.setText("70");
        maxGlucoseField.setText("180");
        type1Button.setSelected(true);
    }

    private JPanel createAppointmentPage() {
        JPanel page = new JPanel(new BorderLayout(18, 0));
        page.setBackground(LIGHT_BG);
        page.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel calendarCard = createCard();
        calendarCard.setLayout(new BorderLayout(0, 12));
        calendarCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel calendarHeading = new JLabel("Select Appointment Date");
        calendarHeading.setFont(new Font("SansSerif", Font.BOLD, 19));
        calendarHeading.setForeground(TEXT);

        CalendarPanel calendar = new CalendarPanel(date -> {
            selectedDate = date;
            selectedDateLabel.setText(
                date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            );
            refreshSlots();
        });

        calendarCard.add(calendarHeading, BorderLayout.NORTH);
        calendarCard.add(calendar, BorderLayout.CENTER);

        JPanel bookingCard = createCard();
        bookingCard.setLayout(new BorderLayout(0, 15));
        bookingCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        bookingCard.setPreferredSize(new Dimension(320, 500));

        JLabel heading = new JLabel("Manage Slots");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setForeground(TEXT);

        JPanel topFields = new JPanel();
        topFields.setLayout(new BoxLayout(topFields, BoxLayout.Y_AXIS));
        topFields.setOpaque(false);

        JLabel dateLabel = new JLabel("Selected Date");
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        selectedDateLabel = new JLabel(
            selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        );
        selectedDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        selectedDateLabel.setForeground(PURPLE);

        JLabel timeLabel = new JLabel("Appointment Time");
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        timeBox = new JComboBox<>(TIMES);
        timeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        timeBox.setPreferredSize(new Dimension(250, 38));

        topFields.add(dateLabel);
        topFields.add(Box.createVerticalStrut(5));
        topFields.add(selectedDateLabel);
        topFields.add(Box.createVerticalStrut(18));
        topFields.add(timeLabel);
        topFields.add(Box.createVerticalStrut(6));
        topFields.add(timeBox);

        JLabel availableLabel = new JLabel("Added Slots");
        availableLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        slotModel = new DefaultListModel<>();
        slotList = new JList<>(slotModel);
        slotList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotList.setFixedCellHeight(32);

        JScrollPane scrollPane = new JScrollPane(slotList);

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setOpaque(false);
        middle.add(availableLabel, BorderLayout.NORTH);
        middle.add(scrollPane, BorderLayout.CENTER);

        JButton addSlotButton = createPrimaryButton("Add Selected Slot");
        addSlotButton.setPreferredSize(new Dimension(250, 42));
        addSlotButton.addActionListener(e -> addSlot());

        JButton submitButton = createPrimaryButton("Submit");
        submitButton.setPreferredSize(new Dimension(250, 42));
        submitButton.addActionListener(e -> submitSlots());

        JPanel bottomButtons = new JPanel();
        bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.Y_AXIS));
        bottomButtons.setOpaque(false);
        addSlotButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomButtons.add(addSlotButton);
        bottomButtons.add(Box.createVerticalStrut(10));
        bottomButtons.add(submitButton);

        JPanel bookingContent = new JPanel(new BorderLayout(0, 15));
        bookingContent.setOpaque(false);
        bookingContent.add(topFields, BorderLayout.NORTH);
        bookingContent.add(middle, BorderLayout.CENTER);
        bookingContent.add(bottomButtons, BorderLayout.SOUTH);

        bookingCard.add(heading, BorderLayout.NORTH);
        bookingCard.add(bookingContent, BorderLayout.CENTER);

        page.add(calendarCard, BorderLayout.CENTER);
        page.add(bookingCard, BorderLayout.EAST);

        refreshSlots();
        return page;
    }

    private void addSlot() {
        String time = (String) timeBox.getSelectedItem();
        if (time == null) return;

        if (selectedDate.isBefore(LocalDate.now())) {
            showMessage("Please select today or a future date.");
            return;
        }

        String key = selectedDate + "|" + time;

        if (!bookedSlots.add(key)) {
            showMessage("This slot has already been added.");
            return;
        }

        refreshSlots();
    }

    private void submitSlots() {
        if (bookedSlots.isEmpty()) {
            showMessage("Please add at least one appointment slot first.");
            return;
        }

        StringBuilder message =
            new StringBuilder("Appointment slots submitted:\n\n");

        for (String slot : bookedSlots) {
            String[] parts = slot.split("\\|");
            message.append(parts[0]).append(" - ")
                   .append(parts[1]).append("\n");
        }

        showMessage(message.toString());
    }

    private void refreshSlots() {
        if (slotModel == null) return;

        slotModel.clear();

        for (String key : bookedSlots) {
            String[] parts = key.split("\\|");
            if (parts[0].equals(selectedDate.toString())) {
                slotModel.addElement(parts[1]);
            }
        }
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            this, message, "GlucoSync",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private class CalendarPanel extends JPanel {
        private YearMonth displayedMonth = YearMonth.now();
        private final Consumer<LocalDate> onDateSelected;
        private final JPanel daysPanel =
            new JPanel(new GridLayout(0, 7, 5, 5));
        private final JLabel monthLabel =
            new JLabel("", SwingConstants.CENTER);

        CalendarPanel(Consumer<LocalDate> onDateSelected) {
            this.onDateSelected = onDateSelected;
            setLayout(new BorderLayout(0, 12));
            setBackground(Color.WHITE);

            JPanel monthHeader = new JPanel(new BorderLayout(8, 0));
            monthHeader.setOpaque(false);

            JButton previous = new JButton("<");
            JButton next = new JButton(">");

            previous.addActionListener(e -> {
                displayedMonth = displayedMonth.minusMonths(1);
                renderCalendar();
            });

            next.addActionListener(e -> {
                displayedMonth = displayedMonth.plusMonths(1);
                renderCalendar();
            });

            monthLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
            monthLabel.setForeground(TEXT);

            monthHeader.add(previous, BorderLayout.WEST);
            monthHeader.add(monthLabel, BorderLayout.CENTER);
            monthHeader.add(next, BorderLayout.EAST);

            JPanel calendarBody = new JPanel(new BorderLayout(0, 8));
            calendarBody.setOpaque(false);

            JPanel weekdays = new JPanel(new GridLayout(1, 7, 5, 5));
            weekdays.setOpaque(false);

            String[] names = {
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
            };

            for (String day : names) {
                JLabel label = new JLabel(day, SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 12));
                weekdays.add(label);
            }

            daysPanel.setOpaque(false);
            calendarBody.add(weekdays, BorderLayout.NORTH);
            calendarBody.add(daysPanel, BorderLayout.CENTER);

            add(monthHeader, BorderLayout.NORTH);
            add(calendarBody, BorderLayout.CENTER);

            renderCalendar();
        }

        private void renderCalendar() {
            monthLabel.setText(
                displayedMonth.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy")
                )
            );

            daysPanel.removeAll();

            int offset = displayedMonth.atDay(1)
                .getDayOfWeek().getValue() - 1;

            for (int i = 0; i < offset; i++) {
                daysPanel.add(new JLabel(""));
            }

            for (int day = 1;
                 day <= displayedMonth.lengthOfMonth(); day++) {

                LocalDate date = displayedMonth.atDay(day);
                JButton dateButton = new JButton(String.valueOf(day));

                dateButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
                dateButton.setFocusPainted(false);
                dateButton.setMargin(new Insets(2, 2, 2, 2));

                if (date.isBefore(LocalDate.now())) {
                    dateButton.setEnabled(false);
                    dateButton.setForeground(Color.LIGHT_GRAY);
                } else if (date.equals(selectedDate)) {
                    dateButton.setBackground(PURPLE);
                    dateButton.setForeground(Color.WHITE);
                    dateButton.setOpaque(true);
                    dateButton.setBorderPainted(false);
                } else {
                    dateButton.setBackground(Color.WHITE);
                    dateButton.setForeground(TEXT);
                }

                dateButton.addActionListener(e -> {
                    selectedDate = date;
                    onDateSelected.accept(date);
                    renderCalendar();
                });

                daysPanel.add(dateButton);
            }

            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoctorDashboard dashboard = new DoctorDashboard();
            dashboard.setVisible(true);
        });
    }
}
